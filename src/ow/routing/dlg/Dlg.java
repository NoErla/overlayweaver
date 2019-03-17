package ow.routing.dlg;

import org.apache.commons.collections4.map.MultiValueMap;
import ow.id.ID;
import ow.id.IDAddressPair;
import ow.messaging.Message;
import ow.messaging.MessageHandler;
import ow.routing.RoutingAlgorithmConfiguration;
import ow.routing.RoutingContext;
import ow.routing.RoutingService;
import ow.routing.impl.AbstractRoutingAlgorithm;
import ow.routing.dlg.message.RepSuccAndPredMessage;
import ow.routing.dlg.message.ReqSuccAndPredMessage;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Set;
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
    public void join(IDAddressPair joiningNode, IDAddressPair lastHop, boolean isFinalNode) {
        this.successorList.add(joiningNode);
        System.out.println("join2");

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

        Message msg = new ReqSuccAndPredMessage();
        try{
            sender.sendAndReceive(joiningNode.getAddress(), msg);
        }catch (IOException e){
            logger.log(Level.WARNING, "Failed to send/receive a REQ/REP_SUCC_AND_PRED msg.", e);
        }
        // parse the reply
        //IDAddressPair[] succs = ((RepSuccAndPredMessage)msg).successors;
        //IDAddressPair pred = ((RepSuccAndPredMessage)msg).lastPredecessor;

        // update routing table
        //this.successorList.addAll(succs);
        //this.successorList.add(pred);

    }

    @Override
    public void touch(IDAddressPair from) {
        this.successorList.add(from);
        System.out.println("touch");
    }

    @Override
    public void forget(IDAddressPair node) {
        System.out.println("forget");
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
        System.out.println(this.successorList.toString());
        System.out.println("getRoutingTableString");
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

    public String creareNewNodeId(String ori, String pre){
        int oriLength = ori.length();
        int preLength = pre.length();
        String str = ori.substring(preLength - oriLength, preLength - oriLength + 1);
        return str + pre;
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

    // MessageHandler for REQ_SUCC_AND_PRED
    public class ReqSuccAndPredMessageHandler implements MessageHandler {
        public Message process(Message msg) {
            Dlg.this.touch((IDAddressPair)msg.getSource());	// notify the algorithm

            IDAddressPair[] lastSuccessors = Dlg.this.successorList.toArrayExcludingSelf();
            IDAddressPair lastPredecessor = Dlg.this.predecessorList.first();

            // update successor and predecessor
            IDAddressPair msgSrc = (IDAddressPair)msg.getSource();

            Dlg.this.successorList.add(msgSrc);
            // try to add the predecessor to successor list

            synchronized (Dlg.this) {
                if (config.getAggressiveJoiningMode()) {
                    Dlg.this.predecessor = msgSrc;
                }
                else {
                    // check the received predecessor
                    if (msgSrc.getID().equals(predecessorList.first().getID()) ||	// just perf optimization
                            towardSelfComparator.compare(predecessorList.first().getID(), msgSrc.getID()) > 0) {
                        Dlg.this.predecessor = msgSrc;
                    }
                }
            }

            // reply
            Message repMsg = new RepSuccAndPredMessage(
                    lastSuccessors, lastPredecessor);

            return repMsg;
        }
    }
}
