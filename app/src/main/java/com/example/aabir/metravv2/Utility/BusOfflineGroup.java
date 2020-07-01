package com.example.aabir.metravv2.Utility;

import java.util.ArrayList;

/**
 * Created by abir on 4/6/2017.
 */

public class BusOfflineGroup {

    private String busName="";
    private ArrayList<BusOffline> busOfflineArrayList=new ArrayList<>();

    public BusOfflineGroup(String busName, ArrayList<BusOffline> busOfflineArrayList) {
        this.busName = busName;
        this.busOfflineArrayList = busOfflineArrayList;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public ArrayList<BusOffline> getBusOfflineArrayList() {
        return busOfflineArrayList;
    }

    public void setBusOfflineArrayList(ArrayList<BusOffline> busOfflineArrayList) {
        this.busOfflineArrayList = busOfflineArrayList;
    }
}
