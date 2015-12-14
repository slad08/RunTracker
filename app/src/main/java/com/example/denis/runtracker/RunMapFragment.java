package com.example.denis.runtracker;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by Denis on 14.12.2015.
 */
public class RunMapFragment extends SupportMapFragment {
    private static final String ARG_RUN_ID="RUN_ID";

    private GoogleMap mGoogleMap;
    public static RunMapFragment newInstance(long runId){
        Bundle args=new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunMapFragment rf=new RunMapFragment();
        rf.setArguments(args);
        return rf;
    }
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup parent,
                             Bundle savedInstanceState){
       View v=super.onCreateView(inflater,parent,savedInstanceState);
        //Сохранение ссылки на GoogleMap
        mGoogleMap=getMap();
        //Вывод местоположения пользователя
        mGoogleMap.setMyLocationEnabled(true);
    return v;
    }

}
