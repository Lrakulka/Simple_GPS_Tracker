package com.task.krabiysok.simple_gps_tracker;

import android.app.Activity;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {
    private LocationManager mLocationManager;
    private MapPainter mMapPainter;
    private MyLocationListener mMyLocationListener;
    private boolean mTrackerStatus;
    private SurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mMyLocationListener = new MyLocationListener(mMapPainter);
        mSurfaceView = (android.view.SurfaceView) findViewById(R.id.tracker_map);
        mMapPainter = new MapPainter(mSurfaceView, this);
        mSurfaceView.getHolder().addCallback(mMapPainter);
    }

    public void onStopOrStart(View v) {
        if (mTrackerStatus) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000, 5, mMyLocationListener);
            ((Button) findViewById(R.id.bt_start_stop)).
                    setText(getResources().getString(R.string.button_start));
            mTrackerStatus = false;
        } else {
            mLocationManager.removeUpdates(mMyLocationListener);
            ((Button) findViewById(R.id.bt_start_stop)).
                    setText(getResources().getString(R.string.button_stop));
            mTrackerStatus = true;
        }
    }

    public void onReset(View v) { mMapPainter.cleanAll();}
}
