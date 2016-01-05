package com.daemin.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.daemin.adapter.EnrollAdapter;
import com.daemin.common.Common;
import com.daemin.common.Convert;
import com.daemin.data.EnrollData;
import com.daemin.enumclass.Dates;
import com.daemin.enumclass.TimePos;
import com.daemin.event.CreateDialEvent;
import com.daemin.event.FinishDialogEvent;
import com.daemin.event.RemoveEnrollEvent;
import com.daemin.event.SetColorEvent;
import com.daemin.repository.MyTimeRepo;
import com.daemin.timetable.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import timedao.MyTime;

/**
 * Created by hernia on 2015-09-08.
 */
public class DialEnroll extends Activity {
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new CreateDialEvent(true));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_enroll);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));
        //window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        layoutParams.width = dm.widthPixels * 7 / 9;
        layoutParams.height = layoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
        window.setGravity(Gravity.CENTER);
        setLayout();
    }
    private void setLayout() {
        int endMin =0;
        if(getIntent()!=null) {
            this.xth = getIntent().getIntExtra("xth", 1);
            this.yth = getIntent().getIntExtra("yth", 1);
            this.startMin = getIntent().getIntExtra("startMin", 1);
            this.dayOfWeek = Convert.XthToDayOfWeek(xth);
            endMin = getIntent().getIntExtra("endMin", 1);
        }
        mtList = new ArrayList<>();
        enrollList = new HashMap<>();
        startHour = Integer.parseInt(Convert.YthToHourOfDay(yth));
        String wMonthDay = Dates.NOW.getwMonthDay(xth);
        String [] tmp = wMonthDay.split("\\.");
        int year;
        int titleMonth = Dates.NOW.month;
        int monthOfYear = Integer.parseInt(tmp[0]);
        if (monthOfYear != titleMonth && titleMonth==1) year = Dates.NOW.year - 1;
        else year = Dates.NOW.year;
        int dayOfMonth = Integer.parseInt(tmp[1]);
        enrollMyTime(MyTimeRepo.getHourTimes(DialEnroll.this,
                Dates.NOW.getDateMillis(year, monthOfYear, dayOfMonth, startHour, 0),//startmillis
                Dates.NOW.getDateMillis(year, monthOfYear, dayOfMonth, startHour+1, 0)-1,//endmillis
                xth, startHour, endMin));
        btDialCancel = (Button) findViewById(R.id.btDialCancel);
        tvMonthDay = (TextView) findViewById(R.id.tvMonthDay);
        if(xth==1){
            tvMonthDay.setTextColor(getResources().getColor(R.color.red));
        }else if(xth==13){
            tvMonthDay.setTextColor(getResources().getColor(R.color.blue));
        }
        tvMonthDay.setText(Dates.NOW.getwMonthDay(xth)+" "+dayOfWeek);
        llNewEnroll = (LinearLayout) findViewById(R.id.llNewEnroll);
        llNewEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new FinishDialogEvent());
                Intent i = new Intent(DialEnroll.this, DialSchedule.class);
                i.putExtra("overlapEnrollFlag", true);
                i.putExtra("xth", xth);
                i.putExtra("yth", yth);
                startActivity(i);
                finish();
            }
        });
        btDialCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.gc();
                finish();
            }
        });
        lv = (ListView) findViewById(R.id.lv);
        enrollAdapter = new EnrollAdapter(DialEnroll.this, mtList);
        lv.setAdapter(enrollAdapter);/*
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DialEnroll.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                *//*TextView tv = (TextView) view.findViewById(R.id.tvId);
                Intent i = new Intent(DialEnroll.this, DialSchedule.class);
                i.putExtra("enrollFlag", true);
                i.putExtra("overlapEnrollFlag", true);
                i.putExtra("_id",tv.getText().toString());
                i.putExtra("xth", xth);
                i.putExtra("yth", yth);
                i.putExtra("startMin", startMin);
                startActivity(i);
                finish();*//*
            }
        });*/
    }
    private void enrollMyTime(List<MyTime> mt){
        for(MyTime m : mt){
            String time = m.getStarthour()+":"+Convert.IntToString(m.getStartmin())+" ~ "
                    +m.getEndhour()+ ":"+Convert.IntToString(m.getEndmin());
            int timeType = m.getTimetype();
            if(timeType==0){
                String timeCode = m.getTimecode();
                EnrollData ed = new EnrollData(time,m.getName(),m.getMemo(),timeCode,
                        String.valueOf(timeType),
                        "0",m.getColor(),m.getPlace(),m.getId());
                mtList.add(ed);
                enrollList.put(timeCode,ed);
            }else{
                String timeCode = m.getTimecode();
                String memo = m.getMemo();
                EnrollData ed =new EnrollData(time, m.getName(), memo, timeCode,
                        String.valueOf(timeType),
                        memo.split("/")[1].substring(0, 1),m.getColor(), m.getPlace(), m.getId());
                mtList.add(ed);
                enrollList.put(timeCode, ed);
            }
        }
    }
    private int xth,yth,startHour,startMin;
    private TextView tvMonthDay;
    private String dayOfWeek;
    private Button btDialCancel;
    private ListView lv;
    private List<EnrollData> mtList;
    private HashMap enrollList;
    private LinearLayout llNewEnroll;
    private EnrollAdapter enrollAdapter;
    public void onEventMainThread(RemoveEnrollEvent e) {
        mtList.remove(enrollList.get(e.getTimeCode()));
        if(mtList.size()==0){
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }else {
            enrollAdapter.notifyDataSetChanged();
            for (TimePos ETP : TimePos.values()) {
                ETP.setInitTitle();
            }
            Common.fetchWeekData();
        }
    }
}