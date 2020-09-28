package com.netist.mygirlshostel.components;

/**
 * Created by Administrator on 12/1/2017.
 */

public class HallData {
    public  String name;
    public Integer total;
    public Integer occupancy;

    public HallData()
    {
        name = "";
        total = occupancy = 0;
    }

    public HallData(String _name, Integer _total, Integer _occupancy)
    {
        name = _name;
        total = _total;
        occupancy = _occupancy;
    }

    public Integer getAvailability()
    {
        if (total >= occupancy)
            return total - occupancy;

        return -1;
    }
}
