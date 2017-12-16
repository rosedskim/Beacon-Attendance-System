package com.project.kdh.beacon;

import java.util.ArrayList;

public class Lecture { // 강의 클래스
    private String lctId;
    private String lctName;
    private String prfName;
    private ArrayList<LctInfo> lctInfos = new ArrayList<LctInfo>(); //강의 정보 리스트(수업 시간, 진행 요일, 진행 강의실)

    //===========================================================================================

    Lecture(String lctId, String lctName, String prfName, ArrayList<LctInfo> lctInfos){
        this.lctId = lctId;
        this.lctName = lctName;
        this.prfName = prfName;
        this.lctInfos = lctInfos;
    }

    //===========================================================================================

    public String getLctId() {
        return lctId;
    }

    public void setLctId(String lctId) {
        this.lctId = lctId;
    }

    public String getLctName() {
        return lctName;
    }

    public void setLctName(String lctName) {
        this.lctName = lctName;
    }

    public String getPrfName() {
        return prfName;
    }

    public void setPrfName(String prfName) {
        this.prfName = prfName;
    }

    public ArrayList<LctInfo> getLctInfos() {
        return lctInfos;
    }

    public void setLctInfos(ArrayList<LctInfo> lctInfos) {
        this.lctInfos = lctInfos;
    }
}
