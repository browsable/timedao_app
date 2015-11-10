package com.daemin.main;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.daemin.common.GPSInfo;
import com.daemin.enumclass.User;
import com.daemin.timetable.R;

public class SplashActivity extends Activity{
	static SplashActivity singleton;

	public static SplashActivity getInstance() {
		return singleton;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		singleton = this;
		setContentView(R.layout.activity_splash);
		initialize();
	}
	private void initialize() {
		/*if(Common.isOnline()) {
			if (User.USER.isGroupListDownloadState()) {
				Toast.makeText(this, "그룹리스트다운로드되있는상태", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, "그룹리스트다운로드해야함", Toast.LENGTH_SHORT).show();
				//MyRequest.getGroupList();
			}
		}
		else{
			Toast.makeText(this, this.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
		}*/
		if(User.INFO.getFirstFlag()) {
			Toast.makeText(
					this,
					"앱 처음 설치",
					Toast.LENGTH_LONG).show();
			firstSetting();
		}
		GPSInfo gps = new GPSInfo(this);
		// GPS 사용유무 가져오기
		if (gps.isGetLocation()) {
			User.INFO.setLatitude(gps.getLatitude());
			User.INFO.setLongitude(gps.getLongitude());
			Toast.makeText(
					this,
					"내 위치 - \n위도: " + gps.getLatitude() + "\n경도: " + gps.getLongitude(),
					Toast.LENGTH_LONG).show();
			gps.stopUsingGPS();
		} else {
			// GPS 를 사용할수 없으므로
			Toast.makeText(
					this,
					"현재 GPS가 켜져있지 않습니다.",
					Toast.LENGTH_LONG).show();
			gps.stopUsingGPS();
		}

		//MyRequest.test(singleton);
		Handler handler = new Handler() {

			public void handleMessage(Message msg) {
				finish();

				Intent i = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(i);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		};
		handler.sendEmptyMessageDelayed(0, 1000);
	}
	//앱 설치시 맨 처음 한 번만 셋팅
	public void firstSetting(){
		//기기 해상도 너비,높이
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int deviceWidth = displayMetrics.widthPixels;
		int deviceHeight = displayMetrics.heightPixels;
		//폰번호
		TelephonyManager systemService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNum = systemService.getLine1Number();
        if(phoneNum!=null) {
			phoneNum = phoneNum.substring(phoneNum.length() - 10, phoneNum.length());
			phoneNum = "0" + phoneNum;
			phoneNum = PhoneNumberUtils.formatNumber(phoneNum);
        }else{
			phoneNum="00000000000";
		}
		//MAC Address
		WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		String macAddress = info.getMacAddress();
		if (macAddress == null) {
			macAddress = "00:00:00:00:00";//device has no macaddress or wifi is disabled
		}
		SharedPreferences.Editor editor = User.INFO.getEditor();
		editor.putBoolean("firstFlag", false);
		editor.putInt("deviceWidth", deviceWidth);
		editor.putInt("deviceHeight", deviceHeight);
		editor.putString("userPK", phoneNum + macAddress);
		editor.commit();
		Log.i("start", phoneNum + macAddress);
	}
}
