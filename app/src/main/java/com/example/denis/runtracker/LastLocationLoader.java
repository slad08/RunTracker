package com.example.denis.runtracker;

import android.content.Context;
import android.location.Location;

/**
 * Created by Denis on 12.12.2015.
 */
public class LastLocationLoader extends DataLoader<Location> {
    private long mRunId;

    public LastLocationLoader(Context context,long runId){
        super(context);
        mRunId=runId;
    }
    @Override
    public Location loadInBackground(){
        return RunManager.get(getContext()).getLastLocationForRun(mRunId);
    }
}
