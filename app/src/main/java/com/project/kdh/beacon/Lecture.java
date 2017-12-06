package com.project.kdh.beacon;

import java.util.ArrayList;

/**
 * Created by 동현 on 2017-12-06.
 */

public class Lecture {
    private String lid;
    private String name;
    private ArrayList<LctInfo> infoList;

    Lecture(String lid, String name, ArrayList<LctInfo> infoList){
        this.lid = lid;
        this.name = name;
        this.infoList = infoList;
    }

    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<LctInfo> getInfoList() {
        return infoList;
    }

    public void setInfoList(ArrayList<LctInfo> infoList) {
        this.infoList = infoList;
    }
}