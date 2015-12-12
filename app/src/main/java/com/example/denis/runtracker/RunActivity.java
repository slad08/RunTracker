package com.example.denis.runtracker;

import android.app.Fragment;

public class RunActivity extends SingleFragmentActivity {
    /**Ключ для передачи идентификатора серии в формате long*/
    public static final String EXTRA_RUN_ID=
            "com.example.denis.runtracker.run_id";
    @Override
  protected Fragment createFragment(){
       long runId=getIntent().getLongExtra(EXTRA_RUN_ID,-1);
        if (runId!=-1){
            return RunFragment.newInstance(runId);
        }else {
            return new RunFragment();
        }
    }
}
