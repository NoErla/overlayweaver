package ow.routing.dlg;

import ow.id.ID;

/**
 * Created by Administrator on 2019/1/15.
 */
public class utils {

    static public String IdDeleteZero(ID id){
        return id.toString().replace("0","");
    }
}
