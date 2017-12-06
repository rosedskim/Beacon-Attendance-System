package com.project.kdh.beacon;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    EditText idEdit; EditText pwEdit;
    Toast toast;
    ProgressBar loginPrgBar;
    String sid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idEdit = (EditText)findViewById(R.id.idEdit);
        pwEdit = (EditText)findViewById(R.id.pwEdit);
        Button loginBtn = (Button)findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT);

        loginPrgBar =(ProgressBar)findViewById(R.id.loginPrgBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.loginBtn:
                loginPrgBar.setVisibility(View.VISIBLE);
                login();
                break;
        }
    }

    public void login(){
        String id = idEdit.getText().toString();
        String pw = pwEdit.getText().toString();

        sid = id;

        mAuth.signInWithEmailAndPassword(id + "@khu.ac.kr",pw)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            LocalDB.init(sid);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            toast.setText("로그인 성공");
                            toast.show();
                            loginPrgBar.setVisibility(View.GONE);

                            Intent intent = new Intent(getApplicationContext(),MenuActivity.class);
                            startActivity(intent);

                        }else{
                            toast.setText("로그인 실패");
                            toast.show();
                            loginPrgBar.setVisibility(View.GONE);

                            sid = null;
                        }
                    }
                });
    }
}