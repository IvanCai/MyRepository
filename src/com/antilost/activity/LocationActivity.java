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
		// 获取地图控件引用
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
				Toast.makeText(LocationActivity.this, "地图资源初始化完成",
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
		// 定义Maker坐标点
		LatLng point = centerPoint;
		// 构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_gcoding);
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap);
		// 在地图上添加Marker，并显示
		mBaiduMap.addOverlay(option);
	}

	private void addCoverText() {
		// 定义文字所显示的坐标点
		LatLng llText = centerPoint;
		// 构建文字Option对象，用于在地图上添加文字
		OverlayOptions textOption = new TextOptions().bgColor(0xAAFFFF00)
				.fontSize(24).fontColor(0xFFFF00FF).text(address).rotate(0)
				.position(llText);
		// 在地图上添加该文字对象并显示
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
				"最后定位时间\nyyyy年MM月dd日 HH:mm    ");
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
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

	protected void onStop() {
		// TODO Auto-generated method stub
		mLocationClient.stop();
		super.onStop();
	}

	private void InitLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType(tempcoor);// 返回的定位结果是百度经纬度，默认值gcj02
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);

	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			Log.e("tag", "地图信息回调触发");
			centerPoint = new LatLng(location.getLatitude(),
					location.getLongitude());
			address = location.getAddrStr();
			curDate = new Date(System.currentTimeMillis());// 获取当前时间
			if (address == null || curDate == null) {
				Toast.makeText(LocationActivity.this, "位置信息获取失败",
						Toast.LENGTH_SHORT).show();
			} else {
				updateMapStatus();
				MainActivity.locationHandler.sendEmptyMessage(0);
			}
		}
	}
}
