package com.project.kdh.beacon;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class LocalDB {
    public static String sid;

    public static ArrayList<Lecture> lctList = new ArrayList<Lecture>();

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference myRef;

    public static void init(String id){
        sid = id;
        final ArrayList<String> lctNumList = new ArrayList<String>();
        //로그인한 사람이 듣는 강의 번호를 찾음
        myRef = database.getReference("learningList").child(sid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String str = snapshot.getValue().toString();
                    lctNumList.add(str);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // 듣는 강의 번호에 해당하는 강의 정보를 로컬DB에 담음
        myRef = database.getReference("lctList");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String lid = snapshot.child("lid").getValue().toString();
                    if(lctNumList.contains(lid)){
                        String str = snapshot.getValue().toString();
                        String name = snapshot.child("name").getValue(String.class);

                        ArrayList<LctInfo> infoList = new ArrayList<LctInfo>();
                        for(DataSnapshot snapshot1 : snapshot.child("info").getChildren()){
                            String stime = snapshot1.child("stime").getValue().toString();
                            String etime = snapshot1.child("etime").getValue().toString();
                            String day = snapshot1.child("day").getValue().toString();
                            String rid = snapshot1.child("rid").getValue().toString();

                            infoList.add(new LctInfo(day,stime,etime,rid));
                        }
                        Log.d("SAVE","SAVE");
                        lctList.add(new Lecture(lid,name,infoList)); // 로컬 강의 리스트 저장완료

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
