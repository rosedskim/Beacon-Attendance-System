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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText idEdit;
    private EditText pwEdit;
    private ProgressBar loginPrgBar;

    @Override
    protected void onStart() {
        super.onStart();
        // LocalDB default setting
        LocalDB.defaultSetting(); // 다시 로그인할 때 LocalDB 기본 값으로 초기화
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //
        idEdit = (EditText) findViewById(R.id.idEdit);
        pwEdit = (EditText) findViewById(R.id.pwEdit);
        loginPrgBar = (ProgressBar) findViewById(R.id.loginPrgBar);
        final Button loginBtn = (Button) findViewById(R.id.loginBtn);

        //==================================================================================================

        // 로그인 버튼 클릭 리스너
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginBtn.setClickable(false); // 여러 번 누르는 것 방지

                // 프로그레스 바
                loginPrgBar.setVisibility(View.VISIBLE);

                // FirebaseAuth 연결
                LocalDB.setmAuth(FirebaseAuth.getInstance());

                final String id = idEdit.getText().toString();
                final String pw = pwEdit.getText().toString();
                //
                LocalDB.setUserId(id);
                //
                LocalDB.getmAuth().signInWithEmailAndPassword(id + "@khu.ac.kr", pw)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) { // 로그인 성공

                                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();

                                    LocalDB.setmDatabase(FirebaseDatabase.getInstance()); // 파이어베이스 데이터베이스 연결

                                    if(LocalDB.getUserAuth() == 0) // 교수&조교 권한
                                        startActivity(new Intent(getApplicationContext(),MenuActivity0.class));
                                    else // 학생 권한
                                        startActivity(new Intent(getApplicationContext(),MenuActivity1.class));

                                    loginPrgBar.setVisibility(View.GONE);
                                    loginBtn.setClickable(true);

                                } else { // 로그인 실패
                                    //LocalDB.setUserId(null);
                                    Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();

                                    loginPrgBar.setVisibility(View.GONE);
                                    loginBtn.setClickable(true);
                                }

                            }
                        });

            }
        });

    }
}