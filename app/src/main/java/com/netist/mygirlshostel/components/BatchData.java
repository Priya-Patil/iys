package com.netist.mygirlshostel.components;

/**
 * Created by Administrator on 12/1/2017.
 */

public class BatchData {
    public String name;
    public Double charge;

    public BatchData() {
        name = "";
        charge = 0.0;
    }

    public BatchData(String _name, Double _charge) {
        name = _name;
        charge = _charge;
    }

}
