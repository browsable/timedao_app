package com.daemin.enumclass;

import android.content.Context;
import android.content.SharedPreferences;

import com.daemin.common.AppController;
import com.daemin.timetable.R;

import java.util.ArrayList;
import java.util.List;

import timedao.MyTime;

/**
 * Created by hernia on 2015-06-27.
 */
public enum User {
    INFO(AppController.getInstance());
    User(Context context){
        pref = context.getSharedPreferences("USERINFO", context.MODE_PRIVATE);
        editor = pref.edit();
        latitude=0;
        longitude=0;
        userPK = getUserPK();
        groupPK = getGroupPK();
        weekData=new ArrayList<>();
        overlapFlag = false;
        titleSize = context.getResources().getDimensionPixelSize(R.dimen.textsize_xs);
        dateSize = context.getResources().getDimensionPixelSize(R.dimen.textsize_s);
        editor.putInt("titleSize", titleSize).commit();
        editor.putInt("dateSize", dateSize).commit();
    }
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public double latitude, longitude;
    public String userPK;
    public String appVer;//어플버전;
    public List<MyTime> weekData;
    public int groupPK, titleSize, dateSize;
    public boolean overlapFlag;
    public SharedPreferences.Editor getEditor() {
        return editor;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public double getLatitude() {return latitude;}

    //Pref Getter

    public boolean getFirstFlag(){
        return pref.getBoolean("firstFlag", true);
    }
    public String getUserPK(){
        String userPK = pref.getString("userPK", "0");
        return userPK.substring(0, userPK.length() / 2);
    }
    public String getKorGroupName(){
        return pref.getString("korGroupName", "");
    }
    public String getEngGroupName(){
        return pref.getString("engGroupName", "");
    }
    public int getGroupPK(){
        return pref.getInt("groupPK", 0);
    }
    public boolean getSubjectDownFlag(){
        return pref.getBoolean("subjectDown", false);
    }
    public int getViewMode(){
        return pref.getInt("viewMode", 0);
    }
    public String getCreditSum(){
        return pref.getString("creditSum", "0");
    }
    public boolean getWidget5_5(){
        return pref.getBoolean("widget5_5", false);
    }
    public boolean getWidget4_4(){
        return pref.getBoolean("widget4_4",false);
    }
}
