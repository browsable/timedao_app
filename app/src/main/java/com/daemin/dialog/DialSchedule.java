package com.daemin.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daemin.adapter.BottomNormalListAdapter;
import com.daemin.common.Common;
import com.daemin.common.Convert;
import com.daemin.common.CurrentTime;
import com.daemin.common.HorizontalListView;
import com.daemin.data.BottomNormalData;
import com.daemin.enumclass.DayOfMonthPos;
import com.daemin.enumclass.DayOfMonthPosState;
import com.daemin.enumclass.PosState;
import com.daemin.enumclass.TimePos;
import com.daemin.event.ClearNormalEvent;
import com.daemin.event.ExcuteMethodEvent;
import com.daemin.event.SetAlarmEvent;
import com.daemin.event.SetPlaceEvent;
import com.daemin.event.SetRepeatEvent;
import com.daemin.event.SetShareEvent;
import com.daemin.event.UpdateNormalEvent;
import com.daemin.main.MainActivity;
import com.daemin.map.MapActivity;
import com.daemin.timetable.InitMonthThread;
import com.daemin.timetable.InitSurfaceView;
import com.daemin.timetable.InitWeekThread;
import com.daemin.timetable.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import de.greenrobot.event.EventBus;

public class DialSchedule extends Activity implements View.OnClickListener{
    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_schedule);
        window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        lp = window.getAttributes();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels * 5 / 9;
        lp.width = lp.MATCH_PARENT;
        lp.height = screenHeight*4/5;
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        setLayout();
        colorButtonSetting();
        makeNormalList();
    }

    public void colorButtonSetting() {
        gd = (GradientDrawable) btColor.getBackground().mutate();
        String[] dialogColorBtn = getResources().getStringArray(R.array.dialogColorBtn);
        for (int i = 0; i < dialogColorBtn.length; i++) {
            int resID = getResources().getIdentifier(dialogColorBtn[i], "id", getPackageName());
            final int resColor = getResources().getIdentifier(dialogColorBtn[i], "color", getPackageName());
            ImageButton B = (ImageButton) findViewById(resID);
            B.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    llColor.setVisibility(View.INVISIBLE);
                    colorFlag = false;
                    colorName = getResources().getString(resColor);
                    gd.setColor(getResources().getColor(resColor));
                    gd.invalidateSelf();
                }
            });
        }
    }
    public void makeNormalList(){
        normalList = new ArrayList<>();
        lvTime = (HorizontalListView) findViewById(R.id.lvTime);
        normalAdapter = new BottomNormalListAdapter(this, normalList);
        lvTime.setAdapter(normalAdapter);
        lvTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (viewMode) {
                    case 0:
                        String xth = ((TextView) view.findViewById(R.id.tvXth)).getText().toString();
                        String startHour = ((TextView) view.findViewById(R.id.tvStartHour)).getText().toString();
                        String startMin = ((TextView) view.findViewById(R.id.tvStartMin)).getText().toString();
                        String endHour = ((TextView) view.findViewById(R.id.tvEndHour)).getText().toString();
                        String endMin = ((TextView) view.findViewById(R.id.tvEndMin)).getText().toString();
                        DialWeekPicker dwp = new DialWeekPicker(DialSchedule.this, position, xth, startHour, startMin, endHour, endMin);
                        dwp.show();
                        break;
                    case 2:
                        DialMonthPicker dmp = new DialMonthPicker(DialSchedule.this);
                        dmp.show();
                        break;
                }
            }
        });
        lvTime.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           int pos, long id) {
                switch (viewMode) {
                    case 0:
                        String startHour = ((TextView) view.findViewById(R.id.tvStartHour)).getText().toString();
                        String endHour = ((TextView) view.findViewById(R.id.tvEndHour)).getText().toString();
                        String endMin = ((TextView) view.findViewById(R.id.tvEndMin)).getText().toString();
                        String xth = ((TextView) view.findViewById(R.id.tvXth)).getText().toString();
                        removeWeek(Integer.parseInt(xth), Integer.parseInt(startHour), Integer.parseInt(endHour), Integer.parseInt(endMin));
                        break;
                    case 2:
                        String tvYMD = ((TextView) view.findViewById(R.id.tvYMD)).getText().toString();
                        String xth2 = ((TextView) view.findViewById(R.id.tvXth)).getText().toString();
                        removeMonth(Integer.parseInt(xth2), Integer.parseInt(tvYMD.split("/")[1]));
                        break;
                }
                normalList.remove(pos);
                normalAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }
    public void removeWeek(int xth, int startHour, int endHour, int endMin){
        if(startHour!=endHour) {
            if(endMin!=0)++endHour;
            TimePos[] tp = new TimePos[endHour - startHour];
            int j = 0;
            for (int i = startHour; i < endHour; i++) {
                tp[j] = TimePos.valueOf(Convert.getxyMerge(xth, Convert.HourOfDayToYth(i)));
                if (tp[j].getPosState() != PosState.NO_PAINT) {
                    tp[j].setMin(0, 60);
                    tp[j].setPosState(PosState.NO_PAINT);
                }
                ++j;
            }
        }else{
            TimePos tp = TimePos.valueOf(Convert.getxyMerge(xth, Convert.HourOfDayToYth(startHour)));
            tp.setMin(0, 60);
            tp.setPosState(PosState.NO_PAINT);
        }
    }
    public void removeMonth(int xth, int day){
        InitMonthThread im = (InitMonthThread)initSurfaceView.getInitThread();
        int yth = (im.getDayOfWeekOfLastMonth()+day)/7+1;
        DayOfMonthPos DOMP = DayOfMonthPos.valueOf(Convert.getxyMergeForMonth(xth, yth));
        if (DOMP.getPosState() != DayOfMonthPosState.NO_PAINT) {
            DOMP.setPosState(DayOfMonthPosState.NO_PAINT);
        }
    }
    public void updateWeekList(){
        normalList.clear();
        int startYth=0,startMin=0,endYth=0,endMin=0,tmpXth=0;
        int tmpStartYth=0, tmpStartMin=0, tmpEndYth=0, tmpEndMin=0;
        String YMD="";
        for (TimePos ETP : TimePos.values()) {
            if(ETP.getPosState()==PosState.PAINT||ETP.getPosState()==PosState.ADJUST){
                if(tmpXth!=ETP.getXth()){
                    tmpXth = ETP.getXth();
                    YMD = initSurfaceView.getInitThread().getMonthAndDay(tmpXth);
                    tmpStartYth=tmpStartMin=tmpEndYth=tmpEndMin=0;
                }
                if(tmpEndYth!=ETP.getYth()) {
                    tmpStartYth = startYth = ETP.getYth();
                    tmpStartMin = startMin = ETP.getStartMin();
                    tmpEndMin = endMin = ETP.getEndMin();
                    if(endMin!=0) tmpEndYth = endYth = startYth;
                    else tmpEndYth = endYth = startYth + 2;
                    normalList.add(new BottomNormalData(YMD,
                            Convert.YthToHourOfDay(startYth),
                            Convert.IntToString(startMin),
                            Convert.YthToHourOfDay(endYth),
                            Convert.IntToString(endMin),
                            tmpXth
                    ));
                }else if(tmpEndYth== ETP.getYth()&&tmpEndMin==ETP.getStartMin()){ //
                    normalList.remove(normalList.size()-1);
                    startYth = tmpStartYth;
                    startMin = tmpStartMin;
                    tmpEndMin = endMin = ETP.getEndMin();
                    if(endMin!=0) tmpEndYth = endYth = ETP.getYth();
                    else tmpEndYth = endYth = ETP.getYth() + 2;
                    normalList.add(new BottomNormalData(YMD,
                            Convert.YthToHourOfDay(startYth),
                            Convert.IntToString(startMin),
                            Convert.YthToHourOfDay(endYth),
                            Convert.IntToString(endMin),
                            tmpXth
                    ));
                }
            }
        }
        normalAdapter.notifyDataSetChanged();

    }
    public void updateMonthList(){
        normalList.clear();
        int tmpXth=0,tmpYth=0;
        String YMD="";
        for (DayOfMonthPos DOMP : DayOfMonthPos.values()) {
            if (DOMP.getPosState() == DayOfMonthPosState.PAINT) {
                tmpXth = DOMP.getXth();
                tmpYth = DOMP.getYth();
                YMD = CurrentTime.getTitleMonth()+"/"+initSurfaceView.getInitThread().getMonthAndDay(tmpXth - 1, 7 * (tmpYth - 1));
                normalList.add(new BottomNormalData(YMD,"8","00","9","00",tmpXth));
            }
        }
        normalAdapter.notifyDataSetChanged();
    }
    public void clearView(){
        normalList.clear();
        normalAdapter.notifyDataSetChanged();
        Common.stateFilter(Common.getTempTimePos(), viewMode);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btNormal:
                btColor.setVisibility(View.VISIBLE);
                llNormal.setVisibility(View.VISIBLE);
                llUniv.setVisibility(View.GONE);
                btColor.setVisibility(View.VISIBLE);
                btNormal.setTextColor(getResources().getColor(
                        android.R.color.white));
                btUniv.setTextColor(getResources().getColor(
                        R.color.gray));
                break;
            case R.id.btUniv:
                llNormal.setVisibility(View.GONE);
                llUniv.setVisibility(View.VISIBLE);
                btNormal.setTextColor(getResources().getColor(
                        R.color.gray));
                btUniv.setTextColor(getResources().getColor(
                        android.R.color.white));
                break;
            case R.id.btColor:
                if (!colorFlag) {
                    llColor.setVisibility(View.VISIBLE);
                    colorFlag = true;
                } else {
                    llColor.setVisibility(View.INVISIBLE);
                    colorFlag = false;
                }
                break;
            case R.id.btAddSchedule:
                finish();
                break;
            case R.id.btCancel:
                finish();
                break;
            case R.id.btCommunity:
                break;
            case R.id.btInvite:
                break;
            case R.id.btRemove:
                break;
            case R.id.btShowDep:
                break;
            case R.id.btNew1:case R.id.btNew2:
                DialAddTimePicker datp = null;
                switch(viewMode) {
                    case 0:
                        InitWeekThread iw = (InitWeekThread) initSurfaceView.getInitThread();
                        datp = new DialAddTimePicker(DialSchedule.this, iw.getAllMonthAndDay());
                        break;
                    case 2:
                        InitMonthThread im = (InitMonthThread) initSurfaceView.getInitThread();
                        datp = new DialAddTimePicker(DialSchedule.this, im.getMonthData(),im.getDayOfWeekOfLastMonth());
                        break;
                }
                datp.show();
                break;
            case R.id.btPlace:
                Intent in = new Intent(DialSchedule.this, MapActivity.class);
                startActivity(in);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.btShare:
                DialShare ds = new DialShare(DialSchedule.this);
                ds.show();
                break;
            case R.id.btAlarm:
                DialAlarm da = new DialAlarm(DialSchedule.this);
                da.show();
                break;
            case R.id.btRepeat:
                dayIndex.clear();
                for(BottomNormalData d : normalList){
                    if(!dayIndex.containsKey(d.getXth()))
                        dayIndex.put(d.getXth(),d.getXth());
                }
                DialRepeat dr = new DialRepeat(DialSchedule.this,dayIndex);
                dr.show();
                break;
        }
    }

    private void setLayout() {
        main = MainActivity.getInstance();
        initSurfaceView = main.getInitSurfaceView();
        viewMode = main.getViewMode();
        btNormal = (Button) findViewById(R.id.btNormal);
        btUniv = (Button) findViewById(R.id.btUniv);
        btColor = (Button) findViewById(R.id.btColor);
        btAddSchedule = (Button) findViewById(R.id.btAddSchedule);
        btCancel = (Button) findViewById(R.id.btCancel);
        btCommunity = (Button) findViewById(R.id.btCommunity);
        btInvite = (Button) findViewById(R.id.btInvite);
        btRemove = (Button) findViewById(R.id.btRemove);
        btShowDep = (Button) findViewById(R.id.btShowDep);
        llColor = (LinearLayout) findViewById(R.id.llColor);
        llNormal = (LinearLayout) findViewById(R.id.llNormal);
        llButtonArea = (LinearLayout) findViewById(R.id.llButtonArea);
        llUniv = (LinearLayout) findViewById(R.id.llUniv);
        btNew1 = (LinearLayout) findViewById(R.id.btNew1);
        btNew2 = (LinearLayout) findViewById(R.id.btNew2);
        btPlace = (LinearLayout) findViewById(R.id.btPlace);
        btShare = (LinearLayout) findViewById(R.id.btShare);
        btAlarm = (LinearLayout) findViewById(R.id.btAlarm);
        btRepeat = (LinearLayout) findViewById(R.id.btRepeat);
        llIncludeDep = (LinearLayout) findViewById(R.id.llIncludeDep);
        dragToggle = findViewById(R.id.dragToggle);
        tvShare = (TextView) findViewById(R.id.tvShare);
        tvAlarm = (TextView) findViewById(R.id.tvAlarm);
        tvRepeat = (TextView) findViewById(R.id.tvRepeat);
        etName = (EditText) findViewById(R.id.etName);
        etPlace = (EditText) findViewById(R.id.etPlace);
        etMemo = (EditText) findViewById(R.id.etMemo);
        lvTime = (HorizontalListView) findViewById(R.id.lvTime);
        btNormal.setOnClickListener(this);
        btUniv.setOnClickListener(this);
        btColor.setOnClickListener(this);
        btAddSchedule.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        btCommunity.setOnClickListener(this);
        btInvite.setOnClickListener(this);
        btRemove.setOnClickListener(this);
        btShowDep.setOnClickListener(this);
        btNew1.setOnClickListener(this);
        btNew2.setOnClickListener(this);
        btPlace.setOnClickListener(this);
        btShare.setOnClickListener(this);
        btAlarm.setOnClickListener(this);
        btRepeat.setOnClickListener(this);
        dy =mPosY = 0;
        colorFlag = false;
        colorName = Common.MAIN_COLOR;
        dayIndex = new HashMap<>();
        dragToggle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dy = mPosY + (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mPosY = (int) (dy - event.getRawY());
                        lp.y = mPosY;
                        if(lp.y<0) {
                            lp.y = mPosY = 0;
                        }if(lp.y>screenHeight){
                            lp.y = mPosY = screenHeight;
                        }
                        window.setAttributes(lp);
                        break;
                }
                return true;
            }
        });
    }
    private Button btNormal, btUniv, btColor, btAddSchedule, btCancel, btCommunity, btInvite, btRemove, btShowDep;
    private LinearLayout llColor, llNormal, llButtonArea, llUniv, llIncludeDep, btNew1, btNew2, btPlace, btShare, btAlarm, btRepeat;
    private TextView tvShare, tvAlarm, tvRepeat;
    private EditText etName, etPlace, etMemo;
    private View dragToggle;
    private HorizontalListView lvTime;
    private int dy, mPosY, screenHeight,viewMode;
    private boolean colorFlag;
    private Window window;
    private GradientDrawable gd;
    private String colorName;
    private InitSurfaceView initSurfaceView;
    private MainActivity main;
    private WindowManager.LayoutParams lp;
    private ArrayList<BottomNormalData> normalList;
    private ArrayAdapter normalAdapter;
    private HashMap<Integer,Integer> dayIndex;//어느 요일이 선택됬는지
    public void onEventMainThread(SetAlarmEvent e) {
        tvAlarm.setText(e.getTime());
    }
    public void onEventMainThread(SetShareEvent e) {
        tvShare.setText(e.getShare());
    }
    public void onEventMainThread(SetRepeatEvent e) {
        tvRepeat.setText(e.toString());
    }
    public void onEventMainThread(SetPlaceEvent e){
        etPlace.setText(e.getPlace());
    }
    public void onEventMainThread(BottomNormalData e){
        normalList.add(e);
        normalAdapter.notifyDataSetChanged();
    }
    public void onEventMainThread(ClearNormalEvent e) {
        normalList.clear();
        normalAdapter.notifyDataSetChanged();
    }
    public void onEventMainThread(UpdateNormalEvent e){
        normalList.remove(e.getPosition());
        normalList.add(e.getPosition(), new BottomNormalData(
                initSurfaceView.getInitThread().getMonthAndDay(e.getXth()), e.getStartHour(), e.getStartMin(),
                e.getEndHour(), e.getEndMin(), e.getXth()));
        normalAdapter.notifyDataSetChanged();
    }
    public void onEventMainThread(ExcuteMethodEvent e){
        try {
            Method m = DialSchedule.this.getClass().getDeclaredMethod(e.getMethodName());
            m.invoke(DialSchedule.this);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

}