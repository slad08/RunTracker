package com.example.denis.runtracker;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by Denis on 12.12.2015.
 */
public abstract class DataLoader<D> extends AsyncTaskLoader<D> {
    private D mDate;
    public DataLoader(Context context){
        super(context);
    }
    @Override
    protected void onStartLoading(){
        if (mDate!=null){
            deliverResult(mDate);
        }else {
            forceLoad();
        }
    }
@Override
    public void deliverResult(D data){
    mDate=data;
    if (isStarted())
        super.deliverResult(data);
}
}
