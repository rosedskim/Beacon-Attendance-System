package com.project.kdh.beacon;

/**
 * Created by 동현 on 2017-12-17.
 */

public class LctInfo {
    private String lctDay; // 수업 진행 요일
    private String lctSTime; // 수업 시작 요일
    private String lctETime; // 수업 종료 요일
    private String roomId; // 강의실 아이디

    //===========================================================================================

    public LctInfo(String lctDay, String lctSTime, String lctETime, String roomId){
        this.lctDay =lctDay;
        this.lctSTime = lctSTime;
        this.lctETime = lctETime;
        this.roomId = roomId;
    }
    //===========================================================================================


    public String getLctDay() {
        return lctDay;
    }

    public void setLctDay(String lctDay) {
        this.lctDay = lctDay;
    }

    public String getLctSTime() {
        return lctSTime;
    }

    public void setLctSTime(String lctSTime) {
        this.lctSTime = lctSTime;
    }

    public String getLctETime() {
        return lctETime;
    }

    public void setLctETime(String lctETime) {
        this.lctETime = lctETime;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
