package com.example.denis.runtracker;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Denis on 09.12.2015.
 */
public class RunListFragment extends ListFragment {

    private static final int REQUEST_NEW_RUN=0;

    private RunDatabaseHelper.RunCursor mCursor;
    @Override
    public void onCreate(Bundle savedInstandeStat){
        super.onCreate(savedInstandeStat);
        setHasOptionsMenu(true);
        //Запрос на получение списка серий
        mCursor=RunManager.get(getActivity()).queryRuns();
    //Создание адаптера , ссылающегося на этот курсор
        RunCursorAdapter adapter=new RunCursorAdapter(getActivity(),mCursor);
        setListAdapter(adapter);
    }
    @Override
    public void onDestroy(){
        mCursor.close();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.run_list_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_new_run:
                Intent i=new Intent(getActivity(),RunActivity.class);
                startActivityForResult(i,REQUEST_NEW_RUN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data){
        if(REQUEST_NEW_RUN==requestCode){
            mCursor.requery();
            ((RunCursorAdapter)getListAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public void onListItemClick(ListView l,View v,int position,long id){
        //Аргумент id  содержит идентификатор серии;
        //CursorAdapter автоматически предоставляет эту информацию
        Intent i=new Intent(getActivity(),RunActivity.class);
        i.putExtra(RunActivity.EXTRA_RUN_ID,id);
        startActivity(i);
    }

    private static class RunCursorAdapter extends CursorAdapter {

        private RunDatabaseHelper.RunCursor mRunCursor;
        private RunCursorAdapter(Context context,RunDatabaseHelper.RunCursor cursor){
            super(context,cursor,0);
            mRunCursor=cursor;
        }
        @Override
        public View newView(Context context,Cursor cursor,ViewGroup parent){
            //Использование заполнителя макета для получени представления строки
            LayoutInflater inflater=(LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater
                    .inflate(android.R.layout.simple_list_item_1,parent,false);
        }
        @Override
        public void bindView(View view,Context context,Cursor cursor){
            //Получение серии для текущей строки
            Run run=mRunCursor.getRun();

            //Создание текстового представления начадьной даты
            TextView startDateTextView=(TextView)view;
            String cellText=context.getString(R.string.cell_text,run.getStartDate());
            startDateTextView.setText(cellText);
        }
    }
}
