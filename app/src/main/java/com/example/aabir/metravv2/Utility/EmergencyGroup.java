package com.example.aabir.metravv2.Utility;

import java.util.ArrayList;

/**
 * Created by abir on 4/6/2017.
 */

public class EmergencyGroup {
    private String name;
    private ArrayList<Emergency> emergencyArrayList=new ArrayList<>();

    public EmergencyGroup(String name, ArrayList<Emergency> emergencyArrayList) {
        super();
        this.name = name;
        this.emergencyArrayList = emergencyArrayList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Emergency> getEmergencyArrayList() {
        return emergencyArrayList;
    }

    public void setEmergencyArrayList(ArrayList<Emergency> emergencyArrayList) {
        this.emergencyArrayList = emergencyArrayList;
    }
}
