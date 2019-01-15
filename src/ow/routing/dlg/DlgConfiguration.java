package ow.routing.dlg;

import ow.routing.RoutingAlgorithmConfiguration;

/**
 * Created by muyuchen on 2018/11/15.
 */
public class DlgConfiguration extends RoutingAlgorithmConfiguration{
    public final static int DEFAULT_ROUTING_TABLE_SIZE = 160;
    protected DlgConfiguration() {}
    private int routingTableSize = DEFAULT_ROUTING_TABLE_SIZE;
    public int getRoutingTableSize(){
        return this.routingTableSize;
    }
    public int setRoutingTableSize(int size){
        int old = this.getRoutingTableSize();
        this.routingTableSize = size;
        return old;
    }
}
