package com.example.denis.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by Denis on 07.12.2015.
 */
public class RunManager {
    private static final String TAG="RunManager";

    private static final String PREFS_FILE="runs";
    private static final String PREF_CURRENT_RUN_ID="RunManager.currentRunId";

    public static final String ACTION_LOCATION="com.example.denis.runtracker.ACTION_LOCATION";

    private static final String TEST_PROVIDER="TEST_PROVIDER";
    private static RunManager sRunManager;
    private Context mAppContext;
    private LocationManager mLocationManager;
    private RunDatabaseHelper mHelper;
    private SharedPreferences mPrefs;
    private long mCurrentRunId;

    private RunManager(Context AppContext){
        mAppContext=AppContext;
        mLocationManager=(LocationManager)mAppContext.getSystemService(Context.LOCATION_SERVICE);
        mHelper=new RunDatabaseHelper(mAppContext);
        mPrefs=mAppContext.getSharedPreferences(PREFS_FILE,Context.MODE_PRIVATE);
        mCurrentRunId=mPrefs.getLong(PREF_CURRENT_RUN_ID,-1);
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
    public Run startNewRun(){
        //Вставка объекта Run в базу данных
        Run run=insertRun();
        //Запуск отслеживания серии
        startTrackingRun(run);
        return run;
    }
    public void startTrackingRun(Run run){
        //Получение идентификатора
        mCurrentRunId=run.getId();
        //Сохранение его в общих настройках
        mPrefs.edit().putLong(PREF_CURRENT_RUN_ID,mCurrentRunId).commit();
        //Запуск обновления данных местоположения
        startLocationUpdates();
    }
    public void stopRun(){
        stopLocationUpdates();
        mCurrentRunId=-1;
        mPrefs.edit().remove(PREF_CURRENT_RUN_ID).commit();
    }
    private Run insertRun(){
        Run run =new Run();
        run.setId(mHelper.insertRun(run));
        return run;
    }
    public RunDatabaseHelper.RunCursor queryRuns(){
        return mHelper.queryRuns();
    }
    public void insetrLocation(Location loc){
        if (mCurrentRunId !=-1){
            mHelper.insertLocation(mCurrentRunId,loc);
        }
        else {
            Log.e(TAG,"Location received with no tracking run; ignoring. ");
        }
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
