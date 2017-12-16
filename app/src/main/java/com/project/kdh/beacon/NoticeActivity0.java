package com.project.kdh.beacon;


import android.content.DialogInterface;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;


/*
* -> 처음에 제목만 보이고 클릭하였을 때 내용이 나타나도록 수정
* -> 어떤 공지가 선택되는지 직관적으로 보일 수 있게 수정
*
*/

public class NoticeActivity0 extends AppCompatActivity implements View.OnClickListener {

    private final String[] days = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
    private NtcAdapter adapter;
    private ListView listView;
    private Toast toast;
    private String lctId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice0);

        // Toast & Intent
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        lctId = getIntent().getStringExtra("LCTID");

        // init
        init();

        //
        Button ntcAddBtn = (Button) findViewById(R.id.ntcAddBtn);
        Button ntcModifyBtn = (Button) findViewById(R.id.ntcModifyBtn);
        Button ntcDeleteBtn = (Button) findViewById(R.id.ntcDeleteBtn);

        // OnClickListener
        ntcAddBtn.setOnClickListener(this);
        ntcModifyBtn.setOnClickListener(this);
        ntcDeleteBtn.setOnClickListener(this);
    }

    //==============================================================================================

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ntcAddBtn: // 추가
                addNotice();
                break;
            case R.id.ntcModifyBtn: // 수정
                modifyNotice();
                break;
            case R.id.ntcDeleteBtn: // 삭제
                deleteNotice();
                break;
        }
    }

    //==============================================================================================

    public void addNotice() {
        final LinearLayout dlg_layout =
                (LinearLayout) View.inflate(this, R.layout.notice_dlg, null);

        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle("공지사항 등록")
                .setView(dlg_layout)
                .setPositiveButton("작성", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText ntcTitleEdit = (EditText) dlg_layout.findViewById(R.id.ntcTitleEdit);
                        EditText ntcContentEdit = (EditText) dlg_layout.findViewById(R.id.ntcContentEdit);

                        // 제목
                        String title = ntcTitleEdit.getText().toString();
                        // 내용
                        String content = ntcContentEdit.getText().toString();

                        // 시간
                        Calendar cal = Calendar.getInstance();
                        int year = cal.get(cal.YEAR);
                        int month = cal.get(cal.MONTH) + 1;
                        int date = cal.get(cal.DATE);
                        String curDay = days[((cal.get(Calendar.DAY_OF_WEEK) - 1))];
                        int curHour = cal.get(cal.HOUR_OF_DAY);
                        int curMin = cal.get(cal.MINUTE);

                        String dateInfo = year + "." + month + "." + date + " " + curDay + " " + curHour + ":" + curMin;

                        adapter.addItem(title, dateInfo, content);
                        adapter.notifyDataSetChanged();

                        toast.setText("새 공지가 등록되었습니다.");
                        toast.show();

                        // Firebase
                        String id = new String(hashCode() + ""); // 수정,삭제를 위한 해쉬함수로 아이디 생성
                        LocalDB.setmRef(LocalDB.getmDatabase().
                                getReference("lectures").
                                child(lctId).child("notice").child(id));

                        LocalDB.getmRef().child("title").setValue(title);
                        LocalDB.getmRef().child("date").setValue(dateInfo);
                        LocalDB.getmRef().child("content").setValue(content);

                        Notice notice =  (Notice)(adapter.getItem(adapter.getCount() - 1));
                        notice.setId(id);

                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    public void modifyNotice() {
        int count = adapter.getCount();

        if (count > 0) {
            final int checked = listView.getCheckedItemPosition();
            if (checked > -1 && checked < count) {
                final LinearLayout dlg_layout =
                        (LinearLayout) View.inflate(this, R.layout.notice_dlg, null);

                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setTitle("공지사항 수정")
                        .setView(dlg_layout)
                        .setPositiveButton("작성", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText ntcTitleEdit = (EditText) dlg_layout.findViewById(R.id.ntcTitleEdit);
                                EditText ntcContentEdit = (EditText) dlg_layout.findViewById(R.id.ntcContentEdit);

                                // 제목
                                String title = ntcTitleEdit.getText().toString();
                                // 내용
                                String content = ntcContentEdit.getText().toString();

                                // 시간
                                Calendar cal = Calendar.getInstance();
                                int year = cal.get(cal.YEAR);
                                int month = cal.get(cal.MONTH) + 1;
                                int date = cal.get(cal.DATE);
                                String curDay = days[((Calendar.DAY_OF_WEEK) - 1)];
                                int curHour = cal.get(Calendar.HOUR_OF_DAY);
                                int curMin = cal.get(Calendar.MINUTE);

                                String dateInfo = year + "." + month + "." + date + " " + curDay + " " + curHour + ":" + curMin;

                                adapter.modifyItem(title, dateInfo, content, checked);
                                adapter.notifyDataSetChanged();

                                toast.setText("공지가 수정되었습니다.");
                                toast.show();

                                // Firebase
                                String id = ((Notice) adapter.getItem(checked)).getId();
                                LocalDB.setmRef(LocalDB.getmDatabase().
                                        getReference("lectures").
                                        child(lctId).child("notice").child(id));

                                LocalDB.getmRef().child("title").setValue(title);
                                LocalDB.getmRef().child("date").setValue(dateInfo);
                                LocalDB.getmRef().child("content").setValue(content);

                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
            }
        }
    }

    public void deleteNotice() {
        int count = adapter.getCount();

        if (count > 0) {
            int checked = listView.getCheckedItemPosition();
            Log.d("HELLO", "chk : " + checked);
            if (checked > -1 && checked < count) {
                Notice notice =  (Notice)(adapter.getItem(checked));
                String id = notice.getId();

                adapter.deleteItem(checked);


                adapter.notifyDataSetChanged();

                toast.setText("공지가 삭제되었습니다.");
                toast.show();

                // Firebase
                LocalDB.setmRef(LocalDB.getmDatabase().
                        getReference("lectures").
                        child(lctId).child("notice").child(id));
                LocalDB.getmRef().removeValue();
            }
        }
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
                    Notice notice =  (Notice)(adapter.getItem(adapter.getCount() - 1));
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
