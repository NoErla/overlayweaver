package ow.routing.dlg;

import org.apache.commons.collections4.map.MultiValueMap;
import ow.id.ID;
import ow.id.IDAddressPair;
import ow.messaging.Message;
import ow.messaging.MessageHandler;
import ow.routing.RoutingAlgorithmConfiguration;
import ow.routing.RoutingContext;
import ow.routing.RoutingService;
import ow.routing.dlg.message.ReqSplitMessage;
import ow.routing.impl.AbstractRoutingAlgorithm;
import ow.routing.dlg.message.RepSuccAndPredMessage;
import ow.routing.dlg.message.ReqSuccAndPredMessage;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

/**
 * Created by muyuchen on 2018/11/12.
 */
public class Dlg extends AbstractRoutingAlgorithm {

    protected DlgConfiguration config;

    // routing table
    protected SuccessorList successorList;//outneighbor
    protected PredecessorList predecessorList;//inneighbor
    //static Integer num = 0;
    //private Integer label;
    protected ArrayList weight;//


    protected Dlg(RoutingAlgorithmConfiguration config, RoutingService routingSvc) {
        super(config, routingSvc);

        // initialize routing table
        this.successorList = new SuccessorList(this, selfIDAddress);
        this.predecessorList = new PredecessorList(this,selfIDAddress);
        //this.label = this.num ++;
        this.weight = new ArrayList();


        this.config = (DlgConfiguration)config;

        this.prepareHandlers();
    }




    @Override
    public BigInteger distance(ID to, ID from) {
        String toString = utils.IdDeleteZero(to);

        String fromString = utils.IdDeleteZero(from);

        //first subpath
        ;

        //second subpath
        int to_length = toString.length();
        int from_length = fromString.length();
        int i=0;
        for(;;i++){
            if(toString.length() == 0 || fromString.length() == 0){
                break;
            }
            if(toString.length() >= fromString.length()){
                if(toString.substring(toString.length() - fromString.length()).equals(fromString))
                    break;
            }
            if(toString.length() < fromString.length()){
                if(toString.equals(fromString.substring(0,toString.length())))
                    break;
            }
            toString = toString.substring(1);
            fromString = fromString.substring(0,fromString.length() - 1);

        }
        //BigInteger distance = BigInteger.valueOf(from_length - (to_length - i));
        BigInteger distance = BigInteger.valueOf(fromString.length());
        //distan = distan.add(new BigInteger("1"));
        return distance;
    }

    @Override
    public IDAddressPair[] nextHopCandidates(ID target, ID lastHop, boolean joining, int maxNumber, RoutingContext context) {

        System.out.println("nextHopCandidates");
        String targetStr = utils.IdDeleteZero(target);
        IDAddressPair[] results = new IDAddressPair[1];
        String suffix = "";
        if(joining){

            results[0] = this.selfIDAddress;
            return results;
        } else {
            if(this.findCommon(targetStr).length() > 0) {
                suffix = targetStr.substring(this.findCommon(targetStr).length() - 1, this.findCommon(targetStr).length());
            } else {
                suffix = targetStr.substring(0, 1);
            }
            //suffix = "2";
            results[0] = this.successorList.closestTo(suffix);

            return results;
        }
    }

    @Override
    public IDAddressPair[] responsibleNodeCandidates(ID target, int maxNumber) {
        System.out.println("responsibleNodeCandidates");
        return new IDAddressPair[0];
    }

    @Override
    public void join(IDAddressPair[] neighbors) {
        // do nothing
        System.out.println(neighbors.length);
        this.successorList.addAll(neighbors);

        System.out.println("join1");
    }

    @Override
    //顺序
    public void join(IDAddressPair joiningNode, IDAddressPair lastHop, boolean isFinalNode) {

        System.out.println("join2");
        Integer joiningNodeId = utils.IdtoInt(joiningNode.getID());
        //异常
        if(this.selfIDAddress.getID().equals(joiningNode.getID()))
            return;
        //初始图
        if(joiningNodeId < 10){
            this.predecessorList.add(joiningNode);
            Message msg = new ReqSuccAndPredMessage();
            try{
                RepSuccAndPredMessage reply= (RepSuccAndPredMessage)sender.sendAndReceive(joiningNode.getAddress(), msg);
                if(reply != null){
                    this.selfIDAddress.setID(new ID(reply.newId.getBytes(), 20));
                }
            }catch (IOException e){
                logger.log(Level.WARNING, "Failed to send/receive a REQ/REP_SUCC_AND_PRED msg.", e);
            }
        }else{
            String selfId = utils.IdDeleteZero(this.selfIDAddress.getID());

            MultiValueMap multiValueMap = new MultiValueMap();

            for(IDAddressPair successor:this.successorList.toArray()){
                //需要记录outnei的分组
                String newNodeId = createNewNodeId(selfId, utils.IdDeleteZero(successor.getID()));
                multiValueMap.put(newNodeId, newNodeId);
            }
            MultiValueMap newNodeMap = union(multiValueMap);
            //object[] to string[]
            String[] keySet = new String[newNodeMap.keySet().size()];
            newNodeMap.keySet().toArray(keySet);


            //新建id的方式
            //this.selfIDAddress.setID(ID.getID(keySet[0].getBytes(), 16));
            //十进制转十六进制
            BigInteger hex = new BigInteger(keySet[0], 16);
            this.selfIDAddress.setID(ID.getID(hex, 16));
            this.weight = (ArrayList) newNodeMap.get(keySet[0]);
            IDAddressPair[] divide = this.predecessorList.divide(keySet);
            //this.predecessorList.remove(this.predecessorList.first());
            //joiningNode.setID(new ID(newNodeMap[1].getBytes(), 16));//joiningNode的weight无法获得 msg

            Message msg = new ReqSuccAndPredMessage();

            ((ReqSuccAndPredMessage) msg).successors = this.successorList.toArrayExcludingSelf();
            //PredecessorList newPred = new PredecessorList(this, selfIDAddress);
            ((ReqSuccAndPredMessage) msg).predecessors = divide;
            //((ReqSuccAndPredMessage) msg).predecessors = newPred.toArrayExcludingSelf();
            ((ReqSuccAndPredMessage) msg).newID = keySet[1];

            try{
                sender.sendAndReceive(joiningNode.getAddress(), msg);
            }catch (IOException e){
                logger.log(Level.WARNING, "Failed to send/receive a REQ/REP_SPLIT msg.", e);
            }
        }
        /*
        String selfId = utils.IdDeleteZero(this.selfIDAddress.getID());

        MultiValueMap multiValueMap = new MultiValueMap();

        for(IDAddressPair successor:this.successorList.toArray()){
            //需要记录outnei的分组
            String newNodeId = creareNewNodeId(selfId, utils.IdDeleteZero(successor.getID()));
            multiValueMap.put(newNodeId,utils.IdDeleteZero(successor.getID()) );
        }
        MultiValueMap newNodeMap = union(multiValueMap);
        String[] keySet = (String[]) newNodeMap.keySet().toArray();


        //新建id的方式
        this.selfIDAddress.setID(new ID(keySet[0].getBytes(), 16));
        this.weight = (ArrayList) newNodeMap.get(keySet[0]);
        //joiningNode.setID(new ID(newNodeMap[1].getBytes(), 16));//joiningNode的weight无法获得 msg
        */

        // parse the reply
        //IDAddressPair[] succs = ((RepSuccAndPredMessage)msg).successors;
        //IDAddressPair pred = ((RepSuccAndPredMessage)msg).lastPredecessor;

        // update routing table
        //this.successorList.addAll(succs);
        //this.successorList.add(pred);


    }

    @Override
    public void touch(IDAddressPair from) {
        //this.successorList.add(from);
        System.out.println("touch");
    }

    @Override
    public void forget(IDAddressPair node) {
        System.out.println("forget");
        Message msg = new ReqSuccAndPredMessage();//delete message
        try{
            sender.sendAndReceive(node.getAddress(), msg);
        }catch (IOException e){
            logger.log(Level.WARNING, "Failed to send/receive a REQ/REP_SUCC_AND_PRED msg.", e);
        }
        IDAddressPair[] succs = ((RepSuccAndPredMessage)msg).successors;
        IDAddressPair[] pred = ((RepSuccAndPredMessage)msg).predecessors;
        this.successorList.addAll(succs);
        this.predecessorList.addAll(pred);
        //this.weight.addAll()
    }


    @Override
    public boolean toReplace(IDAddressPair existingEntry, IDAddressPair newEntry) {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void suspend() {

    }

    @Override
    public void resume() {

    }

    @Override
    public String getRoutingTableString(int verboseLevel) {
        System.out.println("outneighbours:");
        System.out.println(this.successorList.toString());
        System.out.println("inneighbours");
        System.out.println(this.predecessorList.toString());
        System.out.println(this.selfIDAddress.getID().toString());
        return "";
    }

    @Override
    public String getRoutingTableHTMLString() {
        return "";
    }

    public String findCommon(String targetId){

        String selfId = utils.IdDeleteZero(selfIDAddress.getID());

        for(int i=0;;i++){
            if(selfId.length() == 0 || targetId.length() == 0){
                return "";
            }
            if(selfId.length() >= targetId.length()){
                if(selfId.substring(selfId.length() - targetId.length()).equals(targetId))
                    return targetId;
            }
            if(selfId.length() < targetId.length()){
                if(selfId.equals(targetId.substring(0,selfId.length())))
                    return selfId;
            }
            selfId = selfId.substring(1);
            targetId = targetId.substring(0,targetId.length() - 1);

        }


    }

    public String createNewNodeId(String ori, String pre){
        int oriLength = ori.length();
        int preLength = pre.length();
        String str = ori.substring(preLength - oriLength, preLength - oriLength + 1);
        return pre + str;
    }
    //合并分组
    public MultiValueMap union(MultiValueMap mvm){
        int size = mvm.size();
        int half = (int)Math.rint(size / 2);
        MultiValueMap newMVM = new MultiValueMap();
        Set<String> keySet = mvm.keySet();
        int count = 0;
        String str1 = "";
        String str2 = "";
        for(String str : keySet){
            if(count == 0){
                str1 = str;
            }
            if(count == half){
                str2 = str;
            }
            if(count < half){
                newMVM.put(str1, mvm.get(str));
            }else{
                newMVM.put(str2, mvm.get(str));
            }
            count++;
        }
        return newMVM;

    }

    // REQ_SUCC_AND_PRED
    protected void prepareHandlers() {
        this.prepareHandlers(false);
    }

    protected void prepareHandlers(boolean ignoreReqSuccAndPredMessage) {
        MessageHandler handler;

        // REQ_SUCC_AND_PRED
        // first half of init_finger_table(n')
        if (!ignoreReqSuccAndPredMessage) {
            handler = new ReqSuccAndPredMessageHandler();
            this.runtime.addMessageHandler(ReqSuccAndPredMessage.class, handler);
        }
    }

    // MessageHandler for REQ_SUCC_AND_PRED
    public class ReqSuccAndPredMessageHandler implements MessageHandler {
        public Message process(Message msg) {
            ReqSuccAndPredMessage message = (ReqSuccAndPredMessage)msg;

            Dlg.this.touch((IDAddressPair)message.getSource());	// notify the algorithm

            // update successor and predecessor
            IDAddressPair msgSrc = (IDAddressPair)message.getSource();
            if(message.newID == null ){
                try{
                    Dlg.this.successorList.add(msgSrc);
                }catch (Exception e){
                    System.out.println(e);
                }
                return null;
            }else {
                BigInteger hex = new BigInteger(message.newID, 16);
                Dlg.this.selfIDAddress.setID(ID.getID(hex, 16));
                Dlg.this.successorList.addAll(message.successors);
                Dlg.this.predecessorList.addAll(message.predecessors);
                return null;
            }

        }
    }

    public static byte[] hexStringToBytes(String hexString, int size) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        int length = hexString.length();
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[size];
        int i = 0;
        while(i != length){
            d[size - i - 1] = (byte)Integer.parseInt(String.valueOf(hexChars[length - i - 1 ]));
            i++;
        }
        return d;
    }





}



