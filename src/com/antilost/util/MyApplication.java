package com.antilost.util;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
	private static Context context;
	public static LocationClient mLocationClient;
	public static LocationClient mSmsLocationClient;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mLocationClient = new LocationClient(this.getApplicationContext());
		mSmsLocationClient = new LocationClient(this.getApplicationContext());
		context = getApplicationContext();
		initSmsLocation();
	}

	private void initSmsLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// ���ö�λģʽ
		option.setCoorType("bd09ll");// ���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
		option.setIsNeedAddress(true);
		mSmsLocationClient.setLocOption(option);
	}

	public static Context getContext() {

		return context;

	}
}
