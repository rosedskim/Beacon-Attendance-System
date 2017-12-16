package com.project.kdh.beacon;


public class RecordViewItem {
    String date = "";
    int src;
    int type;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        if(type == 0){
            src = R.drawable.x_img;
        }else if(type == 1){
            src = R.drawable.check_icon_material;
        }else{
            src = R.drawable.late_img;
        }
    }

    public int getSrc() {
        return src;
    }
}