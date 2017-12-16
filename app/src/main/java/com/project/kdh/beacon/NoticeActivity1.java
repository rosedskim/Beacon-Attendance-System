package com.project.kdh.beacon;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/*
* -> 처음에 제목만 보이고 클릭하였을 때 내용이 나타나도록 수정
* -> 어떤 공지가 선택되는지 직관적으로 보일 수 있게 수정
*
*/

public class NoticeActivity1 extends AppCompatActivity {

    private final String[] days = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
    private NtcAdapter adapter;
    private ListView listView;
    private String lctId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice1);

        lctId = getIntent().getStringExtra("LCTID");

        // init
        init();

    }

    //==============================================================================================

    public void init() {
        //
        listView = (ListView) findViewById(R.id.ntcListView);
        adapter = new NtcAdapter();
        listView.setAdapter(adapter);

        // Firebase
        LocalDB.setmRef(LocalDB.getmDatabase().
                getReference("lectures").
                child(lctId).child("notice"));


        LocalDB.getmRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String id = snapshot.getKey();
                    //
                    String title = snapshot.child("title").getValue().toString();
                    String date = snapshot.child("date").getValue().toString();
                    String content = snapshot.child("content").getValue().toString();

                    adapter.addItem(title, date, content);
                    Notice notice = (Notice) (adapter.getItem(adapter.getCount() - 1));
                    notice.setId(id);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
