package com.antilost.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.antilost.util.MyApplication;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatus.Builder;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.antiLost.R;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.webkit.WebView.FindListener;
import android.widget.TextView;
import android.widget.Toast;

public class LocationActivity extends Activity {
	private MapView mMapView;
	private static BaiduMap mBaiduMap;
	private static BaiduMapOptions mbaiduMapOptions;
	private static MapStatus.Builder mMapStatusBuilder;
	private static MapStatus mMapStatus;
	public static LocationClient mLocationClient;
	private LatLng centerPoint;
	private String tempcoor = "bd09ll";
	private static MapStatusUpdate mMapStatusUpdate;
	private static String address;
	private Date curDate;
	private boolean enter = true;
	static boolean initedMapFlag=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		mMapStatusBuilder = new Builder();
		mbaiduMapOptions = new BaiduMapOptions();
		mMapView = new MapView(this, mbaiduMapOptions);
		setContentView(mMapView);
		mLocationClient = ((MyApplication) getApplication()).mLocationClient;
		MyLocationListener myLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(myLocationListener);
		InitLocation();
		// mLocationClient.start();
		// ��ȡ��ͼ�ؼ�����
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mBaiduMap.setTrafficEnabled(true);
		mBaiduMap.setOnMarkerClickListener(makerOnclickListener);
		initedActivity();
	}

	private void initedActivity() {
		// TODO Auto-generated method stub
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				TabHostActivity.tabHost.setCurrentTabByTag("home");
				Toast.makeText(LocationActivity.this, "��ͼ��Դ��ʼ�����",
						Toast.LENGTH_SHORT).show();
				initedMapFlag=true;
				MainActivity.locationHandler.sendEmptyMessage(1);
			}
		}, 3200);
	}

	public void updateMapStatus() {
		mBaiduMap.clear();
		mMapStatusBuilder.target(centerPoint);
		mMapStatusBuilder.zoom(17.0f);
		mMapStatus = mMapStatusBuilder.build();
		mbaiduMapOptions.mapStatus(mMapStatus);
		mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		addCoverMaker();
		addCoverText();
		mBaiduMap.setMapStatus(mMapStatusUpdate);
		mLocationClient.stop();
		Log.e("tag", "changed mapStatus");
	}

	private void addCoverMaker() {
		// ����Maker�����
		LatLng point = centerPoint;
		// ����Markerͼ��
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_gcoding);
		// ����MarkerOption�������ڵ�ͼ�����Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap);
		// �ڵ�ͼ�����Marker������ʾ
		mBaiduMap.addOverlay(option);
	}

	private void addCoverText() {
		// ������������ʾ�������
		LatLng llText = centerPoint;
		// ��������Option���������ڵ�ͼ���������
		OverlayOptions textOption = new TextOptions().bgColor(0xAAFFFF00)
				.fontSize(24).fontColor(0xFFFF00FF).text(address).rotate(0)
				.position(llText);
		// �ڵ�ͼ����Ӹ����ֶ�����ʾ
		mBaiduMap.addOverlay(textOption);
	}

	private void showInfoWindonw() {
		TextView tv = new TextView(getApplicationContext());
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER);
		tv.setText(obtainLastLocateTime());
		InfoWindow infoWindow = new InfoWindow(tv, centerPoint, -80);
		mBaiduMap.showInfoWindow(infoWindow);
	}

	private String obtainLastLocateTime() {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"���λʱ��\nyyyy��MM��dd�� HH:mm    ");
		String str = formatter.format(curDate);
		return str;

	}

	OnMarkerClickListener makerOnclickListener = new OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker arg0) {
			// TODO Auto-generated method stub
			showInfoWindonw();
			return false;
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// ��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// ��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onPause();
	}

	protected void onStop() {
		// TODO Auto-generated method stub
		mLocationClient.stop();
		super.onStop();
	}

	private void InitLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// ���ö�λģʽ
		option.setCoorType(tempcoor);// ���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);

	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			Log.e("tag", "��ͼ��Ϣ�ص�����");
			centerPoint = new LatLng(location.getLatitude(),
					location.getLongitude());
			address = location.getAddrStr();
			curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			if (address == null || curDate == null) {
				Toast.makeText(LocationActivity.this, "λ����Ϣ��ȡʧ��",
						Toast.LENGTH_SHORT).show();
			} else {
				updateMapStatus();
				MainActivity.locationHandler.sendEmptyMessage(0);
			}
		}
	}
}
