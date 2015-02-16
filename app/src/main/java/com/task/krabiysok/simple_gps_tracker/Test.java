package com.task.krabiysok.simple_gps_tracker;

import android.location.Location;

/**
 * Created by KrabiySok on 2/16/2015.
 */
public class Test extends Thread {
    private static final double[] longitude = {30.4502757, 30.4516373, 30.4516373, 30.4517103, 30.4502757};
    private static final double[] latitude = {50.4482463, 50.4492463, 50.447442, 50.447442, 50.4482463};
    private MapPainter mMapPointer;

    Test(MapPainter mapPointer) {
        mMapPointer = mapPointer;
    }

    public void run() {
        for (int p = 0; p < 5; ++p) {
            for (int i = 0; i < longitude.length; ++i) {
                Location location = new Location("12");
                location.setLongitude(longitude[i]);
                location.setLatitude(latitude[i]);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mMapPointer.drawNewPoint(location);
            }
        }
    }
}
