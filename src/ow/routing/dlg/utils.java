package ow.routing.dlg;

import ow.id.ID;

/**
 * Created by Administrator on 2019/1/15.
 */
public class utils {

    static public String IdDeleteZero(ID id){
        String string = id.toString().replace("0","");
        if(string.length() == 0)
            return "0";
        else
            return string;
    }

    static public Integer IdtoInt(ID id){
        return Integer.valueOf(IdDeleteZero(id));
    }
}
