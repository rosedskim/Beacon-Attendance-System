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

public class CheckActivity extends RecoActivity implements View.OnClickListener, RECORangingListener {


    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference myRef;
    private ArrayList<RECOBeacon> mRangedBeacons;

    String uuid = "";
    String lid = "";

    Button realChkBtn;
    TextView curLctTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        Intent intent = new Intent(this.getIntent());
        uuid = intent.getStringExtra("UUID");
        lid = intent.getStringExtra("LID");

        Log.d("CHECKACTI", "받았다 : " + uuid);
        Log.d("CHECKACTI", "받았다 : " + lid);

        realChkBtn = (Button)findViewById(R.id.realChkBtn);
        realChkBtn.setOnClickListener(this);
        curLctTxt=(TextView)findViewById(R.id.curLctTxt);
        String curLct = "현재 수업 : " + intent.getStringExtra("LNAME") + "\n" +
                "수업 시간 : " + "00:00 ~ 12:00" +"\n" +
                "강의실 : " + intent.getStringExtra("RID");
        curLctTxt.setText(curLct);

    }
    //수업날 출석이닞 지각인지 결석인지 체크
    void record(){
        SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
        final String date = fm.format(new Date());
        Log.d("DATE",date);
        myRef = database.getReference("lctList").child(lid);
        myRef.child("record").child(LocalDB.sid).child(date).setValue(1);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.realChkBtn:
            {
                check();
                break;
            }
        }
    }

    void check(){
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
        Log.i("RECORangingActivity", "onServiceConnect()");
        mRecoManager.setDiscontinuousScan(MenuActivity.DISCONTINUOUS_SCAN);

        this.start(mRegions);
        //Write the code when RECOBeaconManager is bound to RECOBeaconService
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<RECOBeacon> recoBeacons, RECOBeaconRegion recoRegion) {
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
                Toast.makeText(this, "출석체크 되었습니다.", Toast.LENGTH_SHORT).show();
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