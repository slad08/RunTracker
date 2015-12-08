package com.example.denis.runtracker;

import java.util.Date;

/**
 * Created by Denis on 08.12.2015.
 */
public class Run {
    private Date mStartDate;
    public Run(){
        mStartDate=new Date();
    }
    public Date getStartDate(){
        return mStartDate;
    }
    public void setStartDate(Date startDate){
        mStartDate=startDate;
    }
    public int getDurationSeconds(long endMillis){
        return (int)((endMillis-mStartDate.getTime())/1000);
    }
    public static String formatDuration(int durationSeconds){
        int seconds=durationSeconds%60;
        int minutes=((durationSeconds-seconds)/60)%60;
        int hour=(durationSeconds-(minutes*60)-seconds)/3600;
        return String.format("%02d:%02d:%02d",hour,minutes,seconds);
    }

}
