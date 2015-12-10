package com.example.denis.runtracker;

import android.app.Fragment;

/**
 * Created by Denis on 09.12.2015.
 */
public class RunListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new RunListFragment();
    }
}
