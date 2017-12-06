package com.project.kdh.beacon;


import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jsh on 2017-11-19.
 */

public class LctInfo {
    String day;
    String stime;
    String etime;
    String rid;

    public  LctInfo(){    }
    public LctInfo(String day, String stime, String etime, String rid){
        this.day = day;
        this.stime = stime;
        this.etime = etime;
        this.rid = rid;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public String getEtime() {
        return etime;
    }

    public void setEtime(String etime) {
        this.etime = etime;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }
}