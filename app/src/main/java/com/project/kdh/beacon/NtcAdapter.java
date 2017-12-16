package com.project.kdh.beacon;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NtcAdapter extends BaseAdapter {

    private ArrayList<Notice> ntcViewList
            = new ArrayList<Notice>();

    public NtcAdapter() {}

    @Override
    public int getCount() {
        return ntcViewList.size();
    }

    @Override
    public Object getItem(int position) {
        return ntcViewList.get(position);
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
            convertView = inflater.inflate(R.layout.ntc_item,parent,false);
        }
        TextView ntcTitleTxt = (TextView)convertView.findViewById(R.id.ntcTitleTxt);
        TextView ntcDateTxt = (TextView)convertView.findViewById(R.id.ntcDateTxt);
        TextView ntcContentTxt = (TextView)convertView.findViewById(R.id.ntcContentTxt);
        //
        Notice notice = ntcViewList.get(position);
        //
        ntcTitleTxt.setText(notice.getTitle());
        ntcDateTxt.setText(notice.getDate());
        ntcContentTxt.setText(notice.getContent());

        return convertView;
    }

    public void addItem(String title, String date, String content){
        Notice item = new Notice();
        item.setTitle(title);
        item.setDate(date);
        item.setContent(content);
        //
        ntcViewList.add(item);
    }

    public void modifyItem(String title, String date, String content, int position){
        Notice item = ntcViewList.get(position);
        item.setTitle(title);
        item.setDate(date);
        item.setContent(content);
    }

    public void deleteItem(int position){
        ntcViewList.remove(position);
    }
}
