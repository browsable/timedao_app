package com.daemin.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daemin.common.Convert;
import com.daemin.enumclass.User;
import com.daemin.timetable.R;
import com.daemin.main.MainActivity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by hernia on 2015-09-08.
 */
public class DialSettingWeekPicker extends Dialog {
    private Button btDialCancel;
    private Button btDialSetting;
    private int startDay, endDay;
    private ArrayList<String> dayList;
    private TextView tvWeek;
    ArrayAdapter<String> endAdapter;
    private Context context;
    public DialSettingWeekPicker(Context context, TextView tvWeek, int startDay, int endDay) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);
        dayList = new ArrayList<>();
        Collections.addAll(dayList, context.getResources().getStringArray(R.array.dayArray));
        this.context = context;
        this.tvWeek = tvWeek;
        this.startDay = startDay;
        this.endDay = endDay;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_setting_time_week);
        setCancelable(true);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        layoutParams.width = dm.widthPixels * 7/10;
        layoutParams.height = dm.heightPixels*3/7;
        window.setAttributes(layoutParams);
        window.setGravity(Gravity.CENTER);
        btDialCancel = (Button) findViewById(R.id.btDialCancel);
        btDialSetting = (Button) findViewById(R.id.btDialSetting);
        final Spinner sStartDay = (Spinner)findViewById(R.id.sStartDay);
        ArrayAdapter<String> startAdapter = new ArrayAdapter<>(context,
                R.layout.spinner, dayList);
        startAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        sStartDay.setAdapter(startAdapter);
        sStartDay.setSelection(startDay);
        final Spinner sEndDay = (Spinner)findViewById(R.id.sEndDay);
        endAdapter = new ArrayAdapter<>(context,
                R.layout.spinner, dayList);
        endAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        sEndDay.setAdapter(endAdapter);
        sEndDay.setSelection(endDay);


        sStartDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                startDay = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sEndDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                endDay= position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btDialCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        btDialSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDay = sStartDay.getSelectedItemPosition();
                endDay = Convert.DayOfWeekToInt(sEndDay.getSelectedItem().toString());
                if(startDay>endDay){
                    Toast.makeText(context, context.getString(R.string.setting_time_day_error), Toast.LENGTH_SHORT).show();
                }else {
                    int start = context.getResources().getIdentifier("day" + startDay, "string", context.getPackageName());
                    int end = context.getResources().getIdentifier("day" + endDay, "string", context.getPackageName());
                    String week = context.getString(start) + " ~ " + context.getString(end);
                    tvWeek.setText(week);
                    User.INFO.getEditor().putInt("startDay", startDay).commit();
                    User.INFO.getEditor().putInt("endDay", endDay).commit();
                    MainActivity.getInstance().getInitSurfaceView().setDay(startDay,endDay);
                    cancel();
                }
            }
        });
    }
}
