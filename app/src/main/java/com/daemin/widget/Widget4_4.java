package com.daemin.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.daemin.common.Common;
import com.daemin.enumclass.Dates;
import com.daemin.enumclass.User;
import com.daemin.main.MainActivity;

public class Widget4_4 extends AppWidgetProvider {
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		registerAlarm(context);
		User.INFO.getEditor().putBoolean("widget4_4", true).commit();

	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		Log.i("widget", "service end");
		unregisterAlarm(context);
		context.stopService(new Intent(context, WidgetUpdateService.class));
		User.INFO.getEditor().putBoolean("widget4_4", false).commit();
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
						 int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		switch(intent.getAction()){
			case AppWidgetManager.ACTION_APPWIDGET_UPDATE:
				Intent init = new Intent(context, WidgetUpdateService.class);
				init.putExtra("action","update4_4");
				context.startService(init);
				break;
			case Common.ACTION_WEEK4_4:
				Intent week = new Intent(context, WidgetUpdateService.class);
				week.putExtra("action","week4_4");
				context.startService(week);
				break;
			case Common.ACTION_MONTH4_4:
				Intent month = new Intent(context, WidgetUpdateService.class);
				month.putExtra("action","month4_4");
				context.startService(month);
				break;
			case Common.ACTION_BACK4_4:
				Intent back = new Intent(context, WidgetUpdateService.class);
				back.putExtra("action","back4_4");
				context.startService(back);
				break;
			case Common.ACTION_FORWARD4_4:
				Intent forward = new Intent(context, WidgetUpdateService.class);
				forward.putExtra("action", "forward4_4");
				context.startService(forward);
				break;
			case Common.ACTION_HOME4_4:
				Intent main5 = new Intent(context, MainActivity.class);
				main5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				main5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(main5);
				break;
			case Common.ACTION_DUMMY4_4:
				break;
		}
	}

	public static void registerAlarm(Context context)
	{
		Log.i("widget", "alarm register");
		Intent init = new Intent(context, WidgetUpdateService.class);
		init.putExtra("action","update4_4");
		PendingIntent sender
				= PendingIntent.getService(context, 0, init, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager manager
				= (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		manager.setRepeating(AlarmManager.RTC_WAKEUP, Dates.NOW.getMidnight()+5000, AlarmManager.INTERVAL_DAY, sender);
	}
	public static void unregisterAlarm(Context context)
	{
		Log.i("widget", "alarm unregister");
		Intent intent = new Intent();
		PendingIntent sender
				= PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager manager =
				(AlarmManager)context
						.getSystemService(Context.ALARM_SERVICE);
		manager.cancel(sender);
	}
}