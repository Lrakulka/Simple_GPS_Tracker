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
        mSurfaceView = (android.view.SurfaceView) findViewById(R.id.tracker_map);
        mMapPainter = new MapPainter(mSurfaceView, this);
        mMyLocationListener = new MyLocationListener(mMapPainter);
        mSurfaceView.getHolder().addCallback(mMapPainter);
    }

    public void onStopOrStart(View v) {
        if (!mTrackerStatus) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, mMyLocationListener);//1000, 5, mMyLocationListener);
            ((Button) findViewById(R.id.bt_start_stop)).
                    setText(getResources().getString(R.string.button_stop));
            mTrackerStatus = true;
        } else {
            mLocationManager.removeUpdates(mMyLocationListener);
            ((Button) findViewById(R.id.bt_start_stop)).
                    setText(getResources().getString(R.string.button_start));
            mTrackerStatus = false;
        }
    }

    public void onReset(View v) { mMapPainter.cleanAll();}
}
