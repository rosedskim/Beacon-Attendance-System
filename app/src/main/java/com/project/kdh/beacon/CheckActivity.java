package com.project.kdh.beacon;


import android.content.Intent;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOErrorCode;
import com.perples.recosdk.RECORangingListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import static android.R.attr.key;

public class CheckActivity extends RecoActivity implements View.OnClickListener, RECORangingListener {


    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference myRef;
    private ArrayList<RECOBeacon> mRangedBeacons;

    String uuid = "";
    String lid = "";
    String day="";
    String user_id="";

    Button checkBtn;
    TextView curLctTxt;
    ArrayList<String> arr=new ArrayList<String>();
    ArrayList<String> arr2=new ArrayList<String>();
    boolean check=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        Intent intent = new Intent(this.getIntent());
        uuid = intent.getStringExtra("UUID");
        lid = intent.getStringExtra("LID");
        user_id=intent.getStringExtra("u_id");

        Log.d("CHECKACTI", "받았다 : " + uuid);
        Log.d("CHECKACTI", "받았다 : " + lid);

        checkBtn = (Button)findViewById(R.id.checkBtn);
        checkBtn.setOnClickListener(this);
        curLctTxt=(TextView)findViewById(R.id.curLctTxt);
        String curLct = "현재 수업 : " + intent.getStringExtra("LNAME") + "\n" +
                "수업 시간 : " + intent.getStringExtra("TIME") +"\n" +
                "강의실 : " + intent.getStringExtra("RID");
        curLctTxt.setText(curLct);
        day=intent.getStringExtra("DAY");

        myRef = database.getReference("lectures").child(lid).child("record");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dateSnapShot : dataSnapshot.getChildren())
                {
                    Iterator it = dataSnapshot.child(LocalDB.getUserId()).getChildren().iterator();
                    while(it.hasNext())
                    {
                        String temp=it.next().toString();
                        int start=temp.indexOf("key");
                        String _year=temp.substring(start+6,start+10);
                        String _month=temp.substring(start+10,start+12);
                        String _day=String.valueOf(temp.charAt(start+12))+String.valueOf(temp.charAt(start+13));
                        Log.d("ㅂㅂㅂㅂ", _year);
                        Log.d("ㅂㅂㅂ", _month);
                        Log.d("ㅂㅂㅂ", _day);
                        //String _hour=temp.substring(start+14,start+16);
                        //String _min=temp.substring(start+16,start+18);
                        String _date=_year+_month+_day;
                        Log.d("ㅂㅂㅂ", _date);
                        arr.add(_date);
                        Log.d("ㅂㅂㅂ",dataSnapshot.child(LocalDB.getUserId()).child(_date).getValue().toString());
                        String str1=dataSnapshot.child(LocalDB.getUserId()).child(_date).getValue().toString();
                        String str2=str1.substring(6,11);
                        arr2.add(str2);
                        Log.d("ㅂㅂㅂ", str2);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    //수업날 출석이닞 지각인지 결석인지 체크
    void record(){
        //현재 날짜

        SimpleDateFormat fm_date = new SimpleDateFormat("yyyy:MM:dd:HH:mm");
        final String date = fm_date.format(new Date());
        String[] s=date.split(":");
        String y=s[0];
        String m=s[1];
        String d=s[2];
        String h=s[3];
        String mi=s[4];
        String result=y+m+d;    //연월일

        for(int i=0; i<arr.size(); i++)
        {
            if(result.equals(arr.get(i))) //날짜 같은데 시간이 00:00이면은 체크안한 상태였다는거다
            {
                if (arr2.get(i).equals("00:00"))
                {
                    check = false;
                }
            }
        }
        myRef = database.getReference("lectures");
        if(check)//한번 출첵된상태라면
        {
            Toast.makeText(this, "이미 체크했습니다.", Toast.LENGTH_SHORT).show();
        }
        else {
            ArrayList<LctInfo> lc = new ArrayList<LctInfo>();
            for (int i = 0; i < LocalDB.getLectures().size(); i++) {
                if (LocalDB.getLectures().get(i).getLctId().equals(lid)) {
                    lc = LocalDB.getLectures().get(i).getLctInfos();
                    break;
                }
            }
            String[] stime = null;
            String[] etime = null;
            for (int i = 0; i < lc.size(); i++) {
                //요일 같은날에꺼의 시간을 가져옴
                if (lc.get(i).getLctDay().equals(day)) {
                    stime = lc.get(i).getLctSTime().split(":");
                    etime = lc.get(i).getLctETime().split(":");
                }
            }

            int shour = Integer.parseInt(stime[0]);
            Log.d("토탈시간", "" + shour);
            int smin = Integer.parseInt(stime[1]);
            Log.d("토탈시간", "" + smin);
            int sTime = shour * 60 + smin;
            Log.d("토탈시간", "" + sTime);

            int ehour = Integer.parseInt(etime[0]);
            int emin = Integer.parseInt(etime[1]);
            int eTime = ehour * 60 + emin;

            int total_time = Integer.parseInt(h) * 60 + Integer.parseInt(mi);
            myRef = database.getReference("lectures").child(lid);

            //시작시간부터 10분안에 있을경우에는 출석
            if (total_time <= sTime + 10) {
                myRef.child("record").child(LocalDB.getUserId()).child(result).child("time").setValue(""+h+":"+mi);
                myRef.child("record").child(LocalDB.getUserId()).child(result).child("type").setValue(1);
                Toast.makeText(this, "출석체크 되었습니다.", Toast.LENGTH_SHORT).show();
            }
            //시작시간 10분후부터 ~ 시작시간 30분까지
            else if (sTime + 10 < total_time && total_time <= sTime + 30) {
                myRef.child("record").child(LocalDB.getUserId()).child(result).child("time").setValue(""+h+":"+mi);
                myRef.child("record").child(LocalDB.getUserId()).child(result).child("type").setValue(2);
                Toast.makeText(this, "지각체크 되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                myRef.child("record").child(LocalDB.getUserId()).child(result).child("time").setValue("00:00");
                myRef.child("record").child(LocalDB.getUserId()).child(result).child("type").setValue(0);
                Toast.makeText(this, "결석체크 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.checkBtn:
            {
                check();
                break;
            }
        }
    }

    void check(){
        Log.d("check함수", "start" );
        mRecoManager.setRangingListener(this);
        //Log.i("RECORangingActivity", "onCreate()");
        mRecoManager.bind(this);  //RECOBeaconManager의 bind() 함수 호출을 통해 RECOBeaconManager의 인스턴스가 RECOBeaconService에 연결
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("RECORangingActivity", "onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.stop(mRegions);
        this.unbind();
    }

    private void unbind() {
        try {
            mRecoManager.unbind();
        } catch (RemoteException e) {
            Log.i("RECORangingActivity", "Remote Exception");
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceConnect() {

        mRecoManager.setDiscontinuousScan(MenuActivity1.DISCONTINUOUS_SCAN);

        this.start(mRegions);
        //Write the code when RECOBeaconManager is bound to RECOBeaconService
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<RECOBeacon> recoBeacons, RECOBeaconRegion recoRegion) {
        Log.d("didRangeBeacons함수", "start" );
        Log.i("RECORangingActivity", "didRangeBeaconsInRegion() region: " + recoRegion.getUniqueIdentifier() + ", number of beacons ranged: " + recoBeacons.size());

        synchronized (recoBeacons) {
            mRangedBeacons = new ArrayList<RECOBeacon>(recoBeacons);
        }


        for(int i=0; i<recoBeacons.size(); i++)
        {
            RECOBeacon recoBeacon = mRangedBeacons.get(i);
            String uuid_major=recoBeacon.getProximityUuid()+recoBeacon.getMajor();
            Log.d("uuid_uuid",uuid);
            Log.d("uuid_major",uuid_major);
            if(uuid.equals(uuid_major))
            {
                record();
                //Toast.makeText(this, "출석체크 되었습니다.", Toast.LENGTH_SHORT).show();
                try {
                    mRecoManager.unbind();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(500);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return;
            }

        }

        Toast.makeText(this,"현재 강의실이 아닙니다.",Toast.LENGTH_LONG).show();
        try {
            mRecoManager.unbind();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //mRangingListAdapter.updateAllBeacons(recoBeacons);
        //mRangingListAdapter.notifyDataSetChanged();
        //Write the code when the beacons in the region is received

    }

    @Override
    protected void start(ArrayList<RECOBeaconRegion> regions) {

        for(RECOBeaconRegion region : regions) {
            try {
                Log.d("RECORangingActivity",""+""+region.getProximityUuid());
                mRecoManager.startRangingBeaconsInRegion(region);
            } catch (RemoteException e) {
                Log.i("RECORangingActivity", "Remote Exception");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.i("RECORangingActivity", "Null Pointer Exception");
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void stop(ArrayList<RECOBeaconRegion> regions) {
        for(RECOBeaconRegion region : regions) {
            try {
                mRecoManager.stopRangingBeaconsInRegion(region);
            } catch (RemoteException e) {
                Log.i("RECORangingActivity", "Remote Exception");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.i("RECORangingActivity", "Null Pointer Exception");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onServiceFail(RECOErrorCode errorCode) {
        //Write the code when the RECOBeaconService is failed.
        //See the RECOErrorCode in the documents.
        return;
    }

    @Override
    public void rangingBeaconsDidFailForRegion(RECOBeaconRegion region, RECOErrorCode errorCode) {
        Log.i("RECORangingActivity", "error code = " + errorCode);
        //Write the code when the RECOBeaconService is failed to range beacons in the region.
        //See the RECOErrorCode in the documents.
        return;
    }

}