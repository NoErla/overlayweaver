package ow.routing.dlg;

import ow.id.ID;
import ow.id.IDAddressPair;
import ow.routing.RoutingAlgorithmConfiguration;
import ow.routing.RoutingContext;
import ow.routing.RoutingService;
import ow.routing.impl.AbstractRoutingAlgorithm;

import java.math.BigInteger;

/**
 * Created by muyuchen on 2018/11/12.
 */
public class Dlg extends AbstractRoutingAlgorithm {

    // routing table
    protected SuccessorList successorList;//outneighbor
    protected PredecessorList predecessorList;//inneighbor
    //static Integer num = 0;
    //private Integer label;
    static BigInteger distan = new BigInteger("0");


    public Dlg(RoutingAlgorithmConfiguration config, RoutingService routingSvc) {
        super(config, routingSvc);

        // initialize routing table
        this.successorList = new SuccessorList(this, selfIDAddress);
        this.predecessorList = new PredecessorList(this,selfIDAddress);
        //this.label = this.num ++;
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
}
