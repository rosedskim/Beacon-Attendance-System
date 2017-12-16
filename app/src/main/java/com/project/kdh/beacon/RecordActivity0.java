package com.project.kdh.beacon;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class RecordActivity0 extends AppCompatActivity implements View.OnClickListener {

    private String[] students = new String[LocalDB.getStudentIds().size()];

    TextView txt_lecture_name0;
    ListView list_item0;
    String LctId;
    String LctName;
    ArrayList<RecordViewItem> list=new ArrayList<RecordViewItem>();

    Calendar cal;
    int nowYear;
    int nowMonth;
    int nowDay;
    int nowHour;
    int nowMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record0);

        // init
        LctId = getIntent().getStringExtra("LCTID");
        LctName = getIntent().getStringExtra("LCTNAME");

        for(int i=0;i<students.length;i++){
            students[i] = LocalDB.getStudentIds().get(i);
        }

        cal=Calendar.getInstance();
        nowYear=cal.get(Calendar.YEAR);
        nowMonth=cal.get(Calendar.MONTH);
        nowDay=cal.get(Calendar.DATE);
        nowHour=cal.get(Calendar.HOUR_OF_DAY);
        nowMinute=cal.get(Calendar.MINUTE);

        // connect
        txt_lecture_name0 = (TextView)findViewById(R.id.txt_lecture_name0);
        txt_lecture_name0.setText(LctName);
        list_item0 = (ListView)findViewById(R.id.list_item0);

        Button stu_selectBtn = (Button)findViewById(R.id.stu_selectBtn);
        stu_selectBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.stu_selectBtn:
                selectStudent();
                break;
        }
    }

    public void selectStudent()
    {
        list.clear();
        final int selectItem = 0;

        new AlertDialog.Builder(this)
                .setTitle("학생 선택")
                .setSingleChoiceItems(students, selectItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Log.d("학번",""+students[selectItem]);
                                LocalDB.setmRef(LocalDB.getmDatabase().getReference("lectures"));
                                LocalDB.getmRef().addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            Log.d("aaa",snapshot.getKey());
                                            //강의 아이디가 같은거일때만
                                            if(LctId.equals(snapshot.getKey()))
                                            {
                                                Iterator it=snapshot.child("record").child(students[selectItem]).getChildren().iterator();

                                                while(it.hasNext())
                                                {
                                                    String str=it.next().toString();
                                                    Log.d("ccc",str);
                                                    int date_index=str.indexOf("key");
                                                    int hour_index=str.indexOf("time");
                                                    int type_index=str.indexOf("type");

                                                    String date=str.substring(date_index+6, date_index+14);
                                                    String time=str.substring(hour_index+5,hour_index+10);
                                                    int type=Integer.parseInt(String.valueOf(str.charAt(type_index+5)));
                                                    Log.d("12345",date);
                                                    Log.d("12345",time);
                                                    Log.d("12345",""+type);

                                                    String total=date+time;
                                                    RecordViewItem record=new RecordViewItem();
                                                    record.setDate(total);
                                                    record.setType(type);
                                                    list.add(record);
                                                }
                                            }


                                        }
                                        dataSetting();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                        }
                )
                .setNegativeButton("취소",null)
                .show();
    }
    public void dataSetting()
    {
        RcdViewAdapter adapter = new RcdViewAdapter();
        Log.d("ddd","   ?");

        for(int i=0; i<list.size(); i++)
        {

            String nowTime= String.format("%04d", nowYear) + String.format("%02d", nowMonth+1) + String.format("%02d", nowDay) + String.format("%02d",nowHour)+String.format("%02d",nowMinute);

            String _date=list.get(i).getDate();
            String _year=_date.substring(0,4);
            String _month= _date.substring(4,6);
            String _day=_date.substring(6,8);
            String _hour=_date.substring(8,10);
            String _min=String.valueOf(_date.charAt(11))+String.valueOf(_date.charAt(12));
            String _temp=_year+_month+_day+_hour+_min;
            //오늘 시간보다 작은 날짜들에 대해서만 기록 출력
            Log.d("?????",_temp);
            Log.d("?????",nowTime);
            if(Long.parseLong(_temp)<= Long.parseLong(nowTime))
            {
                String total_date = _year + "." + _month + "." + _day + ". " + _hour + ":" + _min;
                Log.d("total_date",nowTime);
                adapter.addItem(total_date, list.get(i).getType());
            }
        }
        list_item0.setAdapter(adapter);
    }
}
