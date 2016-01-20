package com.daemin.common;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.daemin.enumclass.Dates;
import com.daemin.enumclass.DayOfMonthPos;
import com.daemin.enumclass.DayOfMonthPosState;
import com.daemin.enumclass.PosState;
import com.daemin.enumclass.TimePos;
import com.daemin.enumclass.User;
import com.daemin.repository.MyTimeRepo;

import java.util.ArrayList;
import java.util.List;

import timedao.MyTime;


public class Common {

	/* BroadCastReceiver Filter */
	public static final String ACTION_HOME5_5 = "com.daemin.widget.ACTION_HOME5_5";
	public static final String ACTION_DIAL5_5 = "com.daemin.widget.ACTION_DIAL5_5";
	public static final String ACTION_WEEK5_5 = "com.daemin.widget.ACTION_WEEK5_5";
	public static final String ACTION_MONTH5_5 = "com.daemin.widget.ACTION_MONTH5_5";
	public static final String ACTION_BACK5_5 = "com.daemin.widget.ACTION_BACK5_5";
	public static final String ACTION_FORWARD5_5 = "com.daemin.widget.ACTION_FORWARD5_5";
	public static final String ACTION_HOME4_4 = "com.daemin.widget.ACTION_HOME4_4";
	public static final String ACTION_WEEK4_4 = "com.daemin.widget.ACTION_WEEK4_4";
	public static final String ACTION_MONTH4_4 = "com.daemin.widget.ACTION_MONTH4_4";
	public static final String ACTION_BACK4_4 = "com.daemin.widget.ACTION_BACK4_4";
	public static final String ACTION_FORWARD4_4 = "com.daemin.widget.ACTION_FORWARD4_4";
	public static final String ACTION_DIAL4_4 = "com.daemin.widget.ACTION_DIAL4_4";
	public static final String ALARM_PUSH = "com.daemin.common.ALARM_PUSH";
	public static final String MAIN_COLOR = "#73C8BA";
	public static final String TRANS_COLOR = "#00000000";
	public static boolean firstEnrollFlag = false;
	public static boolean isOnline() { // network 연결 상태 확인
		try {
			ConnectivityManager conMan = (ConnectivityManager) AppController.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState(); // wifi
			if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
				return true;
			}
			NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState(); // mobile ConnectivityManager.TYPE_MOBILE
			if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
				return true;
			}
		} catch (NullPointerException e) {
			return false;
		}
		return false;
	}
	public static String calCredit(){
		int sum=0;
		String timecode="";
		try {
			List<MyTime> mtList = MyTimeRepo.getCreditSum(AppController.getInstance());
			if (mtList!= null) {
				for (MyTime m : mtList) {
					if (!timecode.equals(m.getTimecode())) {
						sum += m.getDayofmonth(); // 과목에서는 dayOfMonth에 학점을 저장중임
						timecode = m.getTimecode();
					}
				}
			}
		}catch (NullPointerException e){
			e.printStackTrace();
			sum=0;
		}
		return String.valueOf(sum);
	}
	public static void fetchWeekData(){
		for (TimePos ETP : TimePos.values()) {
			ETP.setPosState(PosState.NO_PAINT);
			ETP.setMin(0, 60);
			ETP.setInitText();
		}
		int week_startMonth = Dates.NOW.monthOfSun;
		int week_startDay = Dates.NOW.dayOfSun;
		int week_endMonth = Dates.NOW.monthOfSat;
		int week_endDay = Dates.NOW.dayOfSat;
		int week_startYear;
		int week_endYear;
		if(week_startMonth==12&&week_endMonth==1){
			week_endYear= Dates.NOW.year;
			week_startYear=week_endYear-1;
		}else
			week_endYear=week_startYear=Dates.NOW.year;
		long week_startMillies = Dates.NOW.getDateMillis(week_startYear, week_startMonth, week_startDay, 8, 0);
		long week_endMillies = Dates.NOW.getDateMillis(week_endYear, week_endMonth, week_endDay, 23, 0);
		User.INFO.monthData.clear();
		User.INFO.weekData.clear();
		User.INFO.weekData.addAll(MyTimeRepo.getWeekTimes(AppController.getInstance(), week_startMillies, week_endMillies));
		for(MyTime mt :User.INFO.weekData){
			addWeek(mt.getName(),mt.getPlace(),mt.getTimetype(),mt.getDayofweek(),mt.getStarthour(),mt.getStartmin(), mt.getEndhour(), mt.getEndmin());
		}
	}
	public static void addWeek(String title,String place,int timeType, int xth, int startHour,int startMin, int endHour, int endMin){
		firstEnrollFlag=false;
		if(endMin!=0) ++endHour;
		else endMin = 60;
		TimePos[] tp = new TimePos[endHour - startHour];
		int j = 0;
		for (int i = startHour; i < endHour; i++) {
			int yth = Convert.HourOfDayToYth(i);
			tp[j] = TimePos.valueOf(Convert.getxyMerge(xth,yth));
			if (tp[j].getPosState() == PosState.NO_PAINT) {
				if (i == startHour) {
					tp[j].setText(title, place);
					tp[j].setRealStart(yth, startMin);
					if (startMin != 0) tp[j].setMin(startMin, 60);
				}
				if (i == endHour - 1) {
					tp[j].setMin(0, endMin);
				}
				tp[j].setPosState(PosState.ENROLL);
			}else if(tp[j].getPosState() == PosState.ENROLL) {
					if (i == startHour) {
						if (!firstEnrollFlag||timeType==1) {
							tp[j].setText(title, place);
							if(timeType ==0) {
								tp[j].setText(title, place);
								tp[j].setRealStart(yth,tp[j].realStartMin);
							}else tp[j].setRealStart(yth, startMin);
							if (startMin != 0) tp[j].setMin(startMin, 60);
							firstEnrollFlag = true;
						}
					}
					if (i == endHour - 1) {
						tp[j].setMin(0, endMin);
					} else if (i != startHour) {
						tp[j].setMin(startMin, endMin);
					}
			}
			++j;
		}
	}
	public static void fetchMonthData(){
		for (DayOfMonthPos DOMP : DayOfMonthPos.values()) {
			DOMP.setPosState(DayOfMonthPosState.NO_PAINT);
			DOMP.setInitTitle();
		}
		int year = Dates.NOW.year;
		int month = Dates.NOW.month;
		long month_startMillies = Dates.NOW.getDateMillis(year, month, 1, 8, 0);
		long month_endMillies = Dates.NOW.getDateMillis(year, month, Dates.NOW.dayNumOfMonth, 23, 0);
		User.INFO.weekData.clear();
		User.INFO.monthData.clear();
		User.INFO.monthData.addAll(MyTimeRepo.getMonthTimes(AppController.getInstance(), month_startMillies, month_endMillies));
		for (MyTime mt :User.INFO.monthData){
			addMonth(mt.getName(), mt.getColor(), mt.getDayofweek(), mt.getDayofmonth());
		}
	}
	public static void addMonth(String title, String color, int xth, int dayOfMonth){
		int dayCnt = dayOfMonth + Dates.NOW.dayOfWeek;
		int yth = dayCnt/7+1;
		xth = Convert.wXthTomXth(xth);
		DayOfMonthPos DOMP = DayOfMonthPos.valueOf(Convert.getxyMergeForMonth(xth, yth));
		if (DOMP.getPosState() == DayOfMonthPosState.NO_PAINT) {
			DOMP.setPosState(DayOfMonthPosState.ENROLL);
			DOMP.setTitleAndColor(title, color);
			DOMP.setEnrollCnt();
		}else if(DOMP.getPosState() == DayOfMonthPosState.ENROLL){
			DOMP.setTitleAndColor(title,color);
			DOMP.setEnrollCnt();
		}
	}
	public static boolean isTableEmpty(){
		boolean empty = true;
		for (TimePos ETP : TimePos.values()) {
			if(ETP.getPosState()!=PosState.NO_PAINT) empty =false;
		}
		return empty;
	}
	static ArrayList<String> tempTimePos=new ArrayList<>();
	public static ArrayList<String> getTempTimePos(){
		return tempTimePos;
	}
	public static void stateFilter(int viewMode){
		if(tempTimePos!=null) {
			switch(viewMode){
				case 0:
					for(String t : tempTimePos){
						TimePos tp= TimePos.valueOf(t);
						tp.setMin(0, 60);
						if(tp.getPosState()==PosState.PAINT) {
							tp.setPosState(PosState.NO_PAINT);
						}else if(tp.getPosState()==PosState.OVERLAP){
							tp.setPosState(PosState.ENROLL);
						}
					}
					break;
				case 1:
					for(String t : tempTimePos){
						DayOfMonthPos.valueOf(t).setPosState(DayOfMonthPosState.NO_PAINT);
					}
					break;
			}
		}
		Common.getTempTimePos().clear();
		return;
	}
	public static void registerAlarm(Context context, int requestCode, Long triggerTime)
	{
		Log.i("alarm", " register");
		Intent intent = new Intent(context, NotificationReceiver.class);
		intent.setAction(ALARM_PUSH);
		PendingIntent sender
				= PendingIntent.getBroadcast(context, requestCode, intent, 0);
		AlarmManager manager
				= (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		manager.set(AlarmManager.RTC_WAKEUP, triggerTime, sender);
	}
	public static void unregisterAlarm(Context context,int requestCode)
	{
		Log.i("alarm", "unregister");
		Intent intent = new Intent();
		PendingIntent sender
				= PendingIntent.getBroadcast(context, requestCode, intent, 0);
		AlarmManager manager =
				(AlarmManager)context
						.getSystemService(Context.ALARM_SERVICE);
		manager.cancel(sender);
	}
}
