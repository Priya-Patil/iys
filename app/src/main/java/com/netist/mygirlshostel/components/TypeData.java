package com.netist.mygirlshostel.components;

/**
 * Created by Administrator on 12/1/2017.
 */

public class TypeData {
    public String name;
    public Double charge;

    public TypeData() {
        name = "";
        charge = 0.0;
    }

    public TypeData(String _name, Double _charge) {
        name = _name;
        charge = _charge;
    }

}
