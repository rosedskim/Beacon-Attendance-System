package com.project.kdh.beacon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class RecordActivity1 extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("lectures");
    ArrayList<RecordViewItem> list=new ArrayList<RecordViewItem>();

    String user_id="";
    String lecture_id="";
    String lecture_name="";

    TextView txt_lecture_name;
    ListView listView;

    Calendar cal;
    int nowYear;
    int nowMonth;
    int nowDay;
    int nowHour;
    int nowMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record1);

        txt_lecture_name=(TextView)findViewById(R.id.txt_lecture_name);
        listView = (ListView)findViewById(R.id.list_item);

        cal=Calendar.getInstance();
        nowYear=cal.get(Calendar.YEAR);
        nowMonth=cal.get(Calendar.MONTH);
        nowDay=cal.get(Calendar.DATE);
        nowHour=cal.get(Calendar.HOUR_OF_DAY);
        nowMinute=cal.get(Calendar.MINUTE);


        Intent intent=this.getIntent();
        user_id=intent.getStringExtra("u_id");
        lecture_id=intent.getStringExtra("lid");
        lecture_name=intent.getStringExtra("l_name");
        txt_lecture_name.setText(lecture_name);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("aaa",snapshot.getKey());
                    //강의 아이디가 같은거일때만
                    if(lecture_id.equals(snapshot.getKey()))
                    {
                        Log.d("bbb",snapshot.child("record").child(LocalDB.getUserId()).getValue().toString());
                        Iterator it=snapshot.child("record").child(LocalDB.getUserId()).getChildren().iterator();

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
        listView.setAdapter(adapter);
    }
}