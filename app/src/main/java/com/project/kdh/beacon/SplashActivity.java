package com.project.kdh.beacon;


import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    // 로딩 화면

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        handler.postDelayed(new splashHandler(),1500); // 1.5초 로딩 후 종료
    }

    private class splashHandler implements  Runnable{

        @Override
        public void run() {
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            SplashActivity.this.finish();
        }
    }
}
