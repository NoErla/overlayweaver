package ow.routing.dlg;

import ow.id.IDAddressPair;

import java.util.Comparator;

/**
 * Created by MUYUCHEN on 2019/1/25.
 */
public final class DlgComparator implements Comparator<IDAddressPair> {

    public int compare(IDAddressPair i1, IDAddressPair i2) {
        int num = utils.IdtoInt(i1.getID()) < utils.IdtoInt(i2.getID()) ? 1 : (utils.IdtoInt(i1.getID())==utils.IdtoInt(i2.getID())?0:-1);
        return num;
    }
}
