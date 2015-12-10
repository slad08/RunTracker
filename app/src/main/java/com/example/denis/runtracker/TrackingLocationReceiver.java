package com.example.denis.runtracker;

import android.content.Context;
import android.location.Location;

/**
 * Created by Denis on 09.12.2015.
 */
public class TrackingLocationReceiver extends LocationReceiver {
    @Override
    protected void onLocationReceived(Context c,Location loc){
        RunManager.get(c).insetrLocation(loc);
    }
}
