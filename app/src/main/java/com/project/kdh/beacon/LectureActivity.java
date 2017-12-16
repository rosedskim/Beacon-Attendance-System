package com.project.kdh.beacon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class LectureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture);

        ListView listView = (ListView)findViewById(R.id.lctListView);
        LctAdapter adapter = new LctAdapter();

        listView.setAdapter(adapter);
    }
}
