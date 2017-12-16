package com.project.kdh.beacon;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


// 교수&조교 권한
public class MenuActivity0 extends AppCompatActivity implements View.OnClickListener {

    private ImageView rcdBtn, ntcBtn; // 출석기록, 공지사항 버튼
    ProgressBar prgBar;

    //==============================================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu0);

        // ProgressBar
        prgBar = (ProgressBar)findViewById(R.id.menu0PrgBar);

        initLocalDB(); // 교수용 LocalDB 초기화

        // ImageView
        rcdBtn = (ImageView) findViewById(R.id.rcdBtn0);
        ntcBtn = (ImageView) findViewById(R.id.ntcBtn0);

        // OnClickListener
        rcdBtn.setOnClickListener(this);
        ntcBtn.setOnClickListener(this);

    }

    //==============================================================================================================

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rcdBtn0: // 출석기록
                record();
                break;
            case R.id.ntcBtn0: // 공지사항
                notice();
                break;
        }
    }

    //==============================================================================================================
    public void initLocalDB() {
        prgBar.setVisibility(View.VISIBLE);

        // 교수가 맡은 강의 id  검색
        LocalDB.setmRef(LocalDB.getmDatabase()
                .getReference("teachings")
                .child(LocalDB.getUserId()));

        final ArrayList<String> lctNums = new ArrayList<String>();
        LocalDB.getmRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    lctNums.add(snapshot.getValue().toString()); // 맡은 강의 id 저장
                }

                // 검색한 id들을 바탕으로 각 강의의 정보 LocalDB에 저장
                LocalDB.setmRef(LocalDB.getmDatabase()
                        .getReference("lectures"));

                LocalDB.getmRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) { // 콜백 실행 순서 제어를 위해 내부에 메소드 삽입
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 강의 리스트 검색
                            String id = snapshot.child("id").getValue().toString();
                            if (lctNums.contains(id)) { // 교수가 맡은 강의라면

                                String name = snapshot.child("name").getValue().toString(); // 강의명
                                String prfName = snapshot.child("prfName").getValue().toString(); // 교수명

                                ArrayList<LctInfo> infos = new ArrayList<LctInfo>(); // 해당 강의의 정보
                                for (DataSnapshot snapshot1 : snapshot.child("info").getChildren()) {
                                    String stime = snapshot1.child("stime").getValue().toString();
                                    String etime = snapshot1.child("etime").getValue().toString();
                                    String day = snapshot1.child("day").getValue().toString();
                                    String roomId = snapshot1.child("rid").getValue().toString();

                                    infos.add(new LctInfo(day, stime, etime, roomId)); // 해당 강의에 대한 정보들을 저장
                                }
                                LocalDB.getLectures().add(new Lecture(id, name, prfName, infos)); // LocalDB에 해당 강의 저장
                            }
                        }
                        prgBar.setVisibility(View.GONE);
                        rcdBtn.setClickable(true);ntcBtn.setClickable(true); // LocalDB 초기화 후에 버튼 클릭 가능
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

                        selectStudentInit(LocalDB.getLectures().get(which).getLctId(),LocalDB.getLectures().get(which).getLctName());
                    }
                })
                .setNegativeButton("취소",null)
                .show();
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
                        Intent intent = new Intent(getApplicationContext(),NoticeActivity0.class);
                        intent.putExtra("LCTID",LocalDB.getLectures().get(which).getLctId());
                        startActivity(intent);
                    }
                })
                .setNegativeButton("취소",null)
                .show();
    }

    //===========================================================================================================================================================================

    public void selectStudentInit(final String LctId, final String LctName){
        prgBar.setVisibility(View.VISIBLE);

        // 해당 수업 수강생 id 검색
        LocalDB.setmRef(LocalDB.getmDatabase()
                .getReference("lectures")
                .child(LctId).child("record"));

        final ArrayList<String> tempArr = new ArrayList<>(); // id를 담기위한 임시 컨테이너
        LocalDB.getmRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    tempArr.add(snapshot.getKey().toString()); // 출석부의 수강생 id 추가
                }

                //
                LocalDB.setStudentIds(tempArr);

                prgBar.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(),RecordActivity0.class);
                intent.putExtra("LCTID",LctId);
                intent.putExtra("LCTNAME",LctName);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    //===========================================================================================================================================================================
}
