package com.project.kdh.beacon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {


    //This is a default proximity uuid of the RECO
    public static final String RECO_UUID = "24DDF411-8CF1-440C-87CD-E368DAF9C931";


    public static final boolean SCAN_RECO_ONLY = true;

    public static final boolean ENABLE_BACKGROUND_RANGING_TIMEOUT = true;
    public static final boolean DISCONTINUOUS_SCAN = false;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION = 10;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;


    private Toast toast;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("stdList");
    String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        uuid = "";
        final int[] iBtns = {R.id.chkBtn, R.id.rcdBtn, R.id.timeBtn, R.id.notiBtn};
        ImageView[] rBtns = new ImageView[iBtns.length];

        for (int i = 0; i < iBtns.length; i++) {
            rBtns[i] = (ImageView) findViewById(iBtns[i]);
            rBtns[i].setOnClickListener(this);

        }

        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);



        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i("MainActivity", "The location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is not granted.");
                this.requestLocationPermission();
            } else {
                Log.i("MainActivity", "The location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is already granted.");
            }
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case REQUEST_LOCATION : {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "false", Toast.LENGTH_SHORT).show();
                }
            }
            default :
                break;
        }


    }

    private void requestLocationPermission() {
        if(!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chkBtn:
                Intent intent = new Intent(getApplicationContext(), CheckActivity.class);
                String rid = searchLct(intent);
                if (!rid.equals("")) {
                    searchRoom(intent,rid);
                } else {
                    toast.setText("현재 수업이 없습니다.");
                    toast.show();
                    return;
                }
                break;

            case R.id.rcdBtn:

                Intent intent1 = new Intent(getApplicationContext(),RecordActivity.class);
                startActivity(intent1);
                break;

            case R.id.timeBtn:
                break;

            case R.id.notiBtn:
                break;
        }
    }

    String searchLct(final Intent intent) { // 현재 시각에 해당하는 수업이 있는지 확인

        // 현재 시각
        Calendar cal = Calendar.getInstance();
        String curDay = Integer.toString(cal.get(Calendar.DAY_OF_WEEK) - 1);
        Log.d("CUR_DAY", curDay);
        int curHour = cal.get(Calendar.HOUR_OF_DAY);
        int curMin = cal.get(Calendar.MINUTE);
        int curTime = curHour * 60 + curMin;
        Log.d("CUR_TIME", curHour + ":" + curMin);

        ArrayList<LctInfo> infoList;
        Log.d("SIZE1", LocalDB.lctList.size() + "");

        for (int i = 0; i < LocalDB.lctList.size(); i++) {
            infoList = LocalDB.lctList.get(i).getInfoList();
            Log.d("STEP1","진입 완료");
            Log.d("SIZE2",infoList.size() +"");
            for (int j = 0; j < infoList.size(); j++) {
                Log.d("STEP2","진입 완료");
                if (infoList.get(j).day.equals(curDay)) { // 요일 일치
                    String[] stime = infoList.get(j).stime.split(":");
                    String[] etime = infoList.get(j).etime.split(":");

                    int shour = Integer.parseInt(stime[0]);
                    int smin = Integer.parseInt(stime[1]);
                    int sTime = shour * 60 + smin;

                    int ehour = Integer.parseInt(etime[0]);
                    int emin = Integer.parseInt(etime[1]);
                    int eTime = ehour * 60 + emin;

                    Log.d("TIME", shour + ":" + smin + " ~ " + ehour + ":" + emin);

                    if (sTime <= curTime && curTime <= eTime) { // 시간 해당
                        String rid = infoList.get(j).rid;

                        intent.putExtra("LNAME",LocalDB.lctList.get(i).getName());
                        intent.putExtra("LID", LocalDB.lctList.get(i).getLid());
                        intent.putExtra("TIME",stime[0]+":"+stime[1] + " ~ " + etime[0] + ":" + etime[1]);
                        return rid;
                    }
                }
            }
        }

        return "";
    }

    void searchRoom(final Intent intent, final String id) {
        myRef = database.getReference("roomList");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String rid = snapshot.child("rid").getValue().toString();

                    if (rid.equals(id)) {
                        uuid = snapshot.child("uuid").getValue().toString();
                        uuid += snapshot.child("major").getValue().toString();
                        intent.putExtra("UUID",uuid);
                        intent.putExtra("RID",rid);
                        Log.d("?????",""+1);
                        startActivity(intent);

                        return;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void selectLct(){

    }
}