package com.example.denis.runtracker;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Loader;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.LoaderManager.LoaderCallbacks;
/**
 * Created by Denis on 07.12.2015.
 */
public class RunFragment extends Fragment {

    private Button mStartButton, mStopButton;
    private TextView mStartedTextView, mLatitudeTextView,
            mLongitudeTextView, mAltitudeTextView, mDurationTextView;

    private static final String ARG_RUN_ID="RUN_ID";
    private static final int LOAD_RUN=0;
    private static final int LOAD_LOCATION=1;
    private RunManager mRunManager;

    private Run mRun;
    private Location mLastLocation;

    private BroadcastReceiver mLocationReceiver = new LocationReceiver() {


        @Override
        protected void onLocationReceived(Context context, Location loc) {
            if (!mRunManager.isTrackingRun(mRun))
                return;
            mLastLocation = loc;
            if (isVisible())
                updateUI();
        }
    @Override
    protected void onProviderEnabledChanged(boolean enabled) {
        int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
        Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
    }
};


    public static RunFragment newInstance(long runId){
        Bundle args=new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunFragment rf=new RunFragment();
        rf.setArguments(args);
        return rf;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        mRunManager=RunManager.get(getActivity());

        //Проверить идентификатор Run  и получить объект серии
        Bundle args=getArguments();
        if (args!=null){
            long runId=args.getLong(ARG_RUN_ID,-1);
            if (runId !=-1){
                //mRun=mRunManager.getRun(runId);
                LoaderManager lm=getLoaderManager();
                lm.initLoader(LOAD_RUN,args,new RunLoaderCallbacks());
                //mLastLocation=mRunManager.getLastLocationForRun(runId);
                lm.initLoader(LOAD_LOCATION,args,new LocationLoaderCallbacks());
            }
        }
}
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_run,container,false);

        mStartedTextView=(TextView)view.findViewById(R.id.run_startedTextView);
        mLatitudeTextView=(TextView)view.findViewById(R.id.run_latitudeTextView);
        mLongitudeTextView=(TextView)view.findViewById(R.id.run_longitudeTextView);
        mAltitudeTextView=(TextView)view.findViewById(R.id.run_altitudeTextView);
        mDurationTextView=(TextView)view.findViewById(R.id.run_durationTextView);

        mStartButton=(Button)view.findViewById(R.id.run_startButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRun = mRunManager.startNewRun();
                if (mRun==null){
                    mRun=mRunManager.startNewRun();
                }else {
                    mRunManager.startTrackingRun(mRun);
                }
                updateUI();
            }
        });

        mStopButton=(Button)view.findViewById(R.id.run_stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRunManager.stopRun();
                updateUI();
            }
        });
        return view;
    }
    @Override
    public void onStart(){
        super.onStart();
        getActivity().registerReceiver(mLocationReceiver,
                new IntentFilter(RunManager.ACTION_LOCATION));
    }
    @Override
    public void onStop(){
        getActivity().unregisterReceiver(mLocationReceiver);
        super.onStop();
    }


    private void updateUI(){
        boolean started=mRunManager.isTrackingRun(mRun);
        boolean trackingThisRun=mRunManager.isTrackingRun(mRun);

        if (mRun !=null)
            mStartedTextView.setText(mRun.getStartDate().toString());

        int durationSeconds=0;
        if (mRun !=null && mLastLocation!=null){
            durationSeconds=mRun.getDurationSeconds(mLastLocation.getTime());
            mLatitudeTextView.setText(Double.toString(mLastLocation.getLatitude()));
            mLongitudeTextView.setText(Double.toString(mLastLocation.getLongitude()));
            mAltitudeTextView.setText(Double.toString(mLastLocation.getAltitude()));
                    }
        mDurationTextView.setText(Run.formatDuration(durationSeconds));

        mStartButton.setEnabled(!started);

        mStopButton.setEnabled(started && trackingThisRun);
}
    private class RunLoaderCallbacks implements LoaderCallbacks<Run>{

        @Override
        public Loader<Run> onCreateLoader(int id, Bundle args) {
            return new RunLoader(getActivity(),args.getLong(ARG_RUN_ID));
        }

        @Override
        public void onLoadFinished(Loader<Run> loader, Run run) {
            mRun=run;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Run> loader) {
            //Ничего не делать
        }
    }
    private class LocationLoaderCallbacks implements LoaderCallbacks<Location>{

        @Override
        public Loader<Location> onCreateLoader(int id, Bundle args) {
            return new LastLocationLoader(getActivity(),args.getLong(ARG_RUN_ID));
        }

        @Override
        public void onLoadFinished(Loader<Location> loader, Location location) {
            mLastLocation=location;
            updateUI();
        }
        @Override
        public void onLoaderReset(Loader<Location> loader) {
            //Ничего не делаем
        }
    }
}

