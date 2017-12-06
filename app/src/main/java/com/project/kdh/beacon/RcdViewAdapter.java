package com.project.kdh.beacon;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jsh on 2017-11-20.
 */

public class RcdViewAdapter extends BaseAdapter {

    private ArrayList<RecordViewItem> rcdViewList
            = new ArrayList<RecordViewItem>();

    public RcdViewAdapter(){}

    @Override
    public int getCount() {
        return rcdViewList.size();
    }

    @Override
    public Object getItem(int position) {
        return rcdViewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int idx = position;
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.record_view,parent,false);
        }
        ImageView rcdImg = (ImageView)convertView.findViewById(R.id.rcdImg);
        TextView rcdTxt = (TextView)convertView.findViewById(R.id.rcdTxt);
        //
        RecordViewItem item = rcdViewList.get(idx);
        //
        rcdTxt.setText(item.getDate());
        rcdImg.setImageResource(item.getSrc());
        return convertView;
    }

    public void addItem(String date, int type){
        RecordViewItem item = new RecordViewItem();
        item.setDate(date);
        item.setType(type);

        rcdViewList.add(item);
    }
}