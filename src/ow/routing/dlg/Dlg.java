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
        int i = 0;
        for(;;i++){
            if(toString.length() == 0 || fromString.length() == 0){
                break;
            }
            if(toString.equals(fromString)){
                break;
            } else{
                toString = toString.substring(1);
                fromString = fromString.substring(0,fromString.length() - 1);
            }
        }
        BigInteger distance = BigInteger.valueOf(from_length - (to_length - i));
        return distance;
    }

    @Override
    public IDAddressPair[] nextHopCandidates(ID target, ID lastHop, boolean joining, int maxNumber, RoutingContext context) {
        String targetStr = utils.IdDeleteZero(target);
        IDAddressPair[] results = new IDAddressPair[0];
        String suffix = "";
        if(this.findCommon(targetStr).length() > 0) {
            suffix = targetStr.substring(this.findCommon(targetStr).length() - 1, this.findCommon(targetStr).length());
        } else {
            suffix = targetStr.substring(0, 1);
        }
        results[0] = this.successorList.closestTo(suffix);

        return results;
    }

    @Override
    public IDAddressPair[] responsibleNodeCandidates(ID target, int maxNumber) {
        return new IDAddressPair[0];
    }

    @Override
    public void join(IDAddressPair[] neighbors) {
        // do nothing
        System.out.println("join1");
    }

    @Override
    public void join(IDAddressPair joiningNode, IDAddressPair lastHop, boolean isFinalNode) {
        System.out.println("join2");
    }

    @Override
    public void touch(IDAddressPair from) {
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
        return null;
    }

    @Override
    public String getRoutingTableHTMLString() {
        return null;
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
