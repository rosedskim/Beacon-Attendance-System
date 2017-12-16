package com.project.kdh.beacon;


import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;


// 학생 권한
public class MenuActivity1 extends AppCompatActivity implements View.OnClickListener{

    private ImageView chkBtn,rcdBtn, lctBtn, ntcBtn; // 출석체크, 출석기록, 수강내역, 공지사항 버튼
    ProgressBar prgBar;

    //==============================================================================================================
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
    DatabaseReference myRef = database.getReference("students");
    String uuid;
    String user_id;
    private int selectItem=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu1);

        // ProgressBar
        prgBar = (ProgressBar)findViewById(R.id.menu1PrgBar);

        initLocalDB(); // 학생용 LocalDB 초기화

        // ImageView
        chkBtn = (ImageView)findViewById(R.id.chkBtn1);
        rcdBtn = (ImageView)findViewById(R.id.rcdBtn1);
        lctBtn = (ImageView)findViewById(R.id.lctBtn1);
        ntcBtn = (ImageView)findViewById(R.id.ntcBtn1);

        // OnClickListener
        chkBtn.setOnClickListener(this);
        rcdBtn.setOnClickListener(this);
        lctBtn.setOnClickListener(this);
        ntcBtn.setOnClickListener(this);
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Log.i("MainActivity", "The location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is not granted.");
                this.requestLocationPermission();
            } else {
                //Log.i("MainActivity", "The location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is already granted.");
            }
        }
    }


    //==============================================================================================================

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.chkBtn1: // 출석체크
                Intent intent = new Intent(getApplicationContext(), CheckActivity.class);
                String rid = searchLct(intent);
                if (!rid.equals("")) {
                    searchRoom(intent,rid);
                } else {
                    toast.makeText(getApplicationContext(),"현재 수업이 없습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }

                break;
            case R.id.rcdBtn1: // 출석기록
                selectLct();
                break;
            case R.id.lctBtn1: // 수강내역
                lecture();
                break;
            case R.id.ntcBtn1: // 공지사항
                notice();
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
        Log.d("SIZE1", LocalDB.getLectures().size() + "");

        for (int i = 0; i < LocalDB.getLectures().size(); i++) {
            infoList = LocalDB.getLectures().get(i).getLctInfos();
            Log.d("STEP1","진입 완료");
            Log.d("SIZE2",infoList.size() +"");
            for (int j = 0; j < infoList.size(); j++) {
                Log.d("STEP2","진입 완료");
                if (infoList.get(j).getLctDay().equals(curDay)) { // 요일 일치
                    String[] stime = infoList.get(j).getLctSTime().split(":");
                    String[] etime = infoList.get(j).getLctETime().split(":");

                    int shour = Integer.parseInt(stime[0]);
                    int smin = Integer.parseInt(stime[1]);
                    int sTime = shour * 60 + smin;

                    int ehour = Integer.parseInt(etime[0]);
                    int emin = Integer.parseInt(etime[1]);
                    int eTime = ehour * 60 + emin;

                    Log.d("TIME", shour + ":" + smin + " ~ " + ehour + ":" + emin);

                    if (sTime <= curTime && curTime <= eTime) { // 시간 해당
                        String rid = infoList.get(j).getRoomId();

                        intent.putExtra("LNAME",LocalDB.getLectures().get(i).getLctName());
                        intent.putExtra("LID", LocalDB.getLectures().get(i).getLctId());
                        intent.putExtra("DAY",curDay);
                        intent.putExtra("u_id",user_id);
                        intent.putExtra("TIME",stime[0]+":"+stime[1] + " ~ " + etime[0] + ":" + etime[1]);
                        return rid;
                    }
                }
            }
        }

        return "";
    }

    void searchRoom(final Intent intent, final String id) {
        myRef = database.getReference("rooms");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String rid = snapshot.child("id").getValue().toString();

                    //강의실 이름과 같으면 비콘ID를 가져오고
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

    void selectLct()
    {
        final ArrayList<String> list=new ArrayList<String>();  //사용자가 강의듣는 강의 ID 저장 리스트
        final ArrayList<String> s_list = new ArrayList<String>(); //사용자가 듣는 강의 이름 저장
        for(int i=0; i<LocalDB.getLectures().size(); i++) {
            list.add(LocalDB.getLectures().get(i).getLctId());
            s_list.add(LocalDB.getLectures().get(i).getLctName());
        }
        final String[] mList=new String[s_list.size()];
        for(int i=0; i<s_list.size(); i++)
        {
            mList[i]= s_list.get(i);
        }

        AlertDialog.Builder dlg=new AlertDialog.Builder(this);
        dlg.setTitle("강의목록");
        dlg.setIcon(R.drawable.lecture);
        dlg.setCancelable(false);
        dlg.setSingleChoiceItems(mList, selectItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectItem=i;
            }
        });
        dlg.setPositiveButton("선택", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String temp=mList[selectItem];

                Intent intent1 = new Intent(getApplicationContext(),RecordActivity1.class);
                intent1.putExtra("u_id",LocalDB.getUserId());
                intent1.putExtra("lid",list.get(selectItem));
                intent1.putExtra("l_name",temp);

                startActivity(intent1);
            }
        });
        dlg.setNegativeButton("취소",null);
        dlg.show();

    }


    //==============================================================================================================
    public void initLocalDB(){

        prgBar.setVisibility(View.VISIBLE);

        // 학생이 수강하는 강의 id  검색
        LocalDB.setmRef(LocalDB.getmDatabase()
                .getReference("learnings")
                .child(LocalDB.getUserId()));

        final ArrayList<String> lctNums = new ArrayList<String>();
        LocalDB.getmRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    lctNums.add(snapshot.getValue().toString()); // 수강하는 강의 id 저장
                }

                // 검색한 id들을 바탕으로 각 강의의 정보 LocalDB에 저장
                LocalDB.setmRef(LocalDB.getmDatabase()
                        .getReference("lectures"));

                LocalDB.getmRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) { // 콜백 실행 순서 제어를 위해 내부에 메소드 삽입
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){ // 강의 리스트 검색
                            String id = snapshot.child("id").getValue().toString();

                            if(lctNums.contains(id)) { // 학생이 수강하는 강의라면
                                String name = snapshot.child("name").getValue().toString(); // 강의명
                                String prfName = snapshot.child("prfName").getValue().toString(); // 교수명
                                Log.d("HELLO","ID : " + id  + " Name : " + name + " prfName : " + prfName);
                                ArrayList<LctInfo> infos = new ArrayList<LctInfo>(); // 해당 강의의 정보
                                for(DataSnapshot snapshot1 : snapshot.child("info").getChildren()){
                                    String stime = snapshot1.child("stime").getValue().toString();
                                    String etime = snapshot1.child("etime").getValue().toString();
                                    String day = snapshot1.child("day").getValue().toString();
                                    String roomId = snapshot1.child("rid").getValue().toString();

                                    infos.add(new LctInfo(day,stime,etime,roomId)); // 해당 강의에 대한 정보들을 저장
                                }

                                LocalDB.getLectures().add(new Lecture(id, name,prfName,infos)); // LocalDB에 해당 강의 저장
                            }
                        }
                        prgBar.setVisibility(View.GONE);
                        chkBtn.setClickable(true);rcdBtn.setClickable(true); lctBtn.setClickable(true); ntcBtn.setClickable(true); // LocalDB 초기화 후에 버튼 클릭 가능
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    //===========================================================================================================================================================================

    public void check(){

    }
    public void record(){

        String[] lectures = new String[LocalDB.getLectures().size()];
        final int selectItem = 0;

        for(int i=0;i<lectures.length;i++){
            lectures[i] = LocalDB.getLectures().get(i).getLctName();
        }

        new AlertDialog.Builder(this)
                .setTitle("수업 선택")
                .setSingleChoiceItems(lectures, selectItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),RecordActivity1.class);
                        intent.putExtra("LCTID",LocalDB.getLectures().get(which).getLctId());
                        startActivity(intent);
                    }
                })
                .setNegativeButton("취소",null)
                .show();
    }
    public void lecture(){
        startActivity(new Intent(getApplicationContext(),LectureActivity.class));
    }
    public void notice(){

        String[] lectures = new String[LocalDB.getLectures().size()];
        final int selectItem = 0;

        for(int i=0;i<lectures.length;i++){
            lectures[i] = LocalDB.getLectures().get(i).getLctName();
        }

        new AlertDialog.Builder(this)
                .setTitle("수업 선택")
                .setSingleChoiceItems(lectures, selectItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),NoticeActivity1.class);
                        intent.putExtra("LCTID",LocalDB.getLectures().get(which).getLctId());
                        startActivity(intent);
                    }
                })
                .setNegativeButton("취소",null)
                .show();
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

    //===========================================================================================================================================================================
}
