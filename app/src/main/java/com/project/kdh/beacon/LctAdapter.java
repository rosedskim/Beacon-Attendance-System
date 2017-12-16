package com.project.kdh.beacon;


import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LctAdapter extends BaseAdapter{

    private final String[] days = {"일요일","월요일","화요일","수요일","목요일","금요일","토요일"};

    public LctAdapter(){}

    @Override
    public int getCount() {
        return LocalDB.getLectures().size();
    }

    @Override
    public Object getItem(int position) {
        return LocalDB.getLectures().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lct_item,parent,false);
        }
        TextView lctNameTxt  = (TextView) convertView.findViewById(R.id.lctNameTxt);
        TextView prfNameTxt = (TextView) convertView.findViewById(R.id.prfNameTxt);
        TextView infoTxt = (TextView)convertView.findViewById(R.id.infoTxt);
        //
        Lecture lecture = LocalDB.getLectures().get(position);
        //
        lctNameTxt.setText(lecture.getLctName());
        prfNameTxt.setText(lecture.getPrfName());

        String info = "";
        ArrayList<LctInfo> infos = lecture.getLctInfos();
        for(int i=0;i<infos.size();i++){
            String day = infos.get(i).getLctDay().toString();
            day = days[Integer.parseInt(day)];
            String stime = infos.get(i).getLctSTime().toString();
            String etime = infos.get(i).getLctETime();
            String roomId = infos.get(i).getRoomId();

            info += ("(" + day + ") " + stime + " ~ " + etime + "/"+roomId+"\n");
        }
        infoTxt.setText(info);


        return convertView;
    }
}
