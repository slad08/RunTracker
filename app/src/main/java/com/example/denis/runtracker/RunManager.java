package com.example.denis.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by Denis on 07.12.2015.
 */
public class RunManager {
    private static final String TAG="RunManager";

    public static final String ACTION_LOCATION="com.example.denis.runtracker.ACTION_LOCATION";

    private static final String TEST_PROVIDER="TEST_PROVIDER";
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
        //Если имеется поставщик данных и он активен,используем его
        if (mLocationManager.getProvider(TEST_PROVIDER)!=null &&
                mLocationManager.isProviderEnabled(TEST_PROVIDER)){
            provider=TEST_PROVIDER;
        }
        Log.d(TAG, "Using provider " + provider);


        //Запуск обновлений из LocationManager
        Location lastKnown = mLocationManager.getLastKnownLocation(provider);
        if (lastKnown != null) {
        // Время инициализируется текущим значением
            lastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnown);
        }

        PendingIntent pi=getLocationPendingIntent(true);
        mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
    }
    private void broadcastLocation(Location location){
        Intent broadcast=new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED,location);
        mAppContext.sendBroadcast(broadcast);
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
