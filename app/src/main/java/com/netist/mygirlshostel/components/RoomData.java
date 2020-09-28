package com.netist.mygirlshostel.components;

/**
 * Created by Administrator on 12/1/2017.
 */

public class RoomData {
    public  String name;
    public Integer total;
    public Integer occupancy;
    public Integer price;

    public RoomData()
    {
        name = "";
        total = occupancy =price= 0;
    }

    public RoomData(String _name, Integer _total, Integer _occupancy,Integer price)
    {
        name = _name;
        total = _total;
        occupancy = _occupancy;
        this.price=price;
    }

    public Integer getAvailability()
    {
        if (total >= occupancy)
            return total - occupancy;

        return -1;
    }
}
