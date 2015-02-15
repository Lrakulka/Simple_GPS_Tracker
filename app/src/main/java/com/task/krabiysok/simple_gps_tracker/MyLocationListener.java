package com.task.krabiysok.simple_gps_tracker;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by KrabiySok on 2/15/2015.
 */
public class MyLocationListener implements LocationListener {
    private MapPainter mMapPointer;

    public MyLocationListener(MapPainter mapPainter) {
        mMapPointer = mapPainter;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Log", "onLocationChanged " + location.getLatitude() + " " + location.getLongitude());
        mMapPointer.drawNewPoint(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Log", "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Log", "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Log", "onProviderDisabled");
    }
}
