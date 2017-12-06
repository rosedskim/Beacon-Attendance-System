package com.project.kdh.beacon;

/**
 * Created by 동현 on 2017-12-06.
 */
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new splashHandler(), 2000);
    }

    private class splashHandler implements Runnable{

        @Override
        public void run() {

            startActivity(new Intent(getApplication(),MainActivity.class));
            Splash.this.finish();
        }
    }
}
