package com.project.kdh.beacon;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class RecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);


        ListView listView = (ListView)findViewById(R.id.list_item);
        RcdViewAdapter adapter = new RcdViewAdapter();

        listView.setAdapter(adapter);
        adapter.addItem("2017.11.14 11:23 정상 출석",1);
        adapter.addItem("2017.11.15 14:23 지각",2);
        adapter.addItem("2017.11.16 10:23 정상 출석",1);
        adapter.addItem("2017.11.17 9:49 정상 출석",1);
        adapter.addItem("2017.11.18 15:11 지각 ",2);
        adapter.addItem("2017.11.19 16:21 지각 ",2);
        adapter.addItem("2017.11.20 결석 ",0);
    }
}
