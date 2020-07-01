package com.example.aabir.metravv2.Utility;

/**
 * Created by abir on 4/6/2017.
 */

public class BusOffline {
    private String busName="";
    private String busRoute="";

    public BusOffline(String busName, String busRoute) {
        super();
        this.busName = busName;
        this.busRoute = busRoute;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getBusRoute() {
        return busRoute;
    }

    public void setBusRoute(String busRoute) {
        this.busRoute = busRoute;
    }
}

