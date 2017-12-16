package com.project.kdh.beacon;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOServiceConnectListener;

import java.util.ArrayList;

public abstract class RecoActivity extends AppCompatActivity implements RECOServiceConnectListener {
    protected RECOBeaconManager mRecoManager;
    protected ArrayList<RECOBeaconRegion> mRegions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecoManager = RECOBeaconManager.getInstance(getApplicationContext(), MenuActivity1.SCAN_RECO_ONLY, MenuActivity1.ENABLE_BACKGROUND_RANGING_TIMEOUT);
        //mRegions = this.generateBeaconRegion();
        mRegions = this.generateBeaconRegion();
        Log.d("mRegions_count", ""+mRegions.size());
    }

    private ArrayList<RECOBeaconRegion>  generateBeaconRegion() {
        Log.i("BackRangingService", "generateBeaconRegion()");
        ArrayList<RECOBeaconRegion> regions = new ArrayList<RECOBeaconRegion>();

        RECOBeaconRegion recoRegion;
        recoRegion = new RECOBeaconRegion(MenuActivity1.RECO_UUID, "RECO Sample Region");
        regions.add(recoRegion);

        return regions;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    protected abstract void start(ArrayList<RECOBeaconRegion> regions);
    protected abstract void stop(ArrayList<RECOBeaconRegion> regions);
}