package com.example.denis.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

/**
 * Created by Denis on 07.12.2015.
 */
public class RunManager {
    private static final String TAG="RunManager";

    public static final String ACTION_LOCATION="com.example.denis.runtracker.ACTION_LOCATION";

    private static RunManager sRunManager;
    private Context mAppContext;
    private LocationManager mLocationManager;

    private RunManager(Context AppContext){
        mAppContext=AppContext;
        mLocationManager=(LocationManager)mAppContext.getSystemService(Context.LOCATION_SERVICE);

    }
    public static RunManager get(Context c){
        if (sRunManager==null){
            //Использование контекста приложения для предотвращения утечки активностей
            sRunManager=new RunManager(c.getApplicationContext());
        }
        return sRunManager;
    }
    private PendingIntent getLocationPendingIntent(boolean shouldCreate){
        Intent broadcast=new Intent(ACTION_LOCATION);
        int flags=shouldCreate ?0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext,0,broadcast,flags);

    }
    public void startLocationUpdates(){
        String provider=LocationManager.GPS_PROVIDER;
        //Запуск обновлений из LocationManager
        PendingIntent pi=getLocationPendingIntent(true);
        mLocationManager.requestLocationUpdates(provider, 0, 0, pi);

    }
    public void stopLocationUpdates(){
        PendingIntent pi=getLocationPendingIntent(false);
        if (pi!=null){
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }
    public boolean isTrackingRun(){
        return getLocationPendingIntent(false)!=null;
    }

}
