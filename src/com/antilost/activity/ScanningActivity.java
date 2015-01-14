package com.antilost.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.antilost.util.Community;
import com.example.antiLost.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ScanningActivity extends Activity {
	private ImageView wave, bluetoothdevice, bluetoohdevice2, bluetoohdevice3;
	private Animation animation, animation2, animation3, animation4,
			animation5;
	private TextView tv_dvName1, tv_dvName2, tv_dvName3, tv_dv_count;
	private String address;
	BluetoothManager bluetoothManager;
	BluetoothAdapter mBluetoothAdapter;
	ArrayList<BluetoothDevice> mBluetoothDevices;
	Timer scanTimer, UItimer;
	Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scanble);
		bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		mBluetoothDevices = new ArrayList<BluetoothDevice>();
		init();
		mHandler = new Handler();
		scanTimer = new Timer();
		UItimer = new Timer();
		scanTimer.schedule(scanTimerTask, 0, 5500);
		UItimer.schedule(uITimerTask, 0, 1000);
	}

	private void init() {
		wave = (ImageView) findViewById(R.id.wave2);
		bluetoothdevice = (ImageView) findViewById(R.id.bluetoothdevice);
		bluetoohdevice2 = (ImageView) findViewById(R.id.bluetoothdevice2);
		bluetoohdevice3 = (ImageView) findViewById(R.id.bluetoothdevice3);
		tv_dvName1 = (TextView) findViewById(R.id.device_name1);
		tv_dvName2 = (TextView) findViewById(R.id.device_name2);
		tv_dvName3 = (TextView) findViewById(R.id.device_name3);
		tv_dv_count = (TextView) findViewById(R.id.device_count);
		animation = AnimationUtils.loadAnimation(ScanningActivity.this,
				R.anim.wave_anim);
		animation2 = AnimationUtils.loadAnimation(ScanningActivity.this,
				R.anim.bluetoothdevice_anim);
		animation3 = AnimationUtils.loadAnimation(ScanningActivity.this,
				R.anim.bluetoothdevice_anim);
		animation4 = AnimationUtils.loadAnimation(ScanningActivity.this,
				R.anim.bluetoothdevice_anim);
		animation5 = AnimationUtils.loadAnimation(ScanningActivity.this,
				R.anim.bluetoothdevice_anim);
		animation.setDuration(1000);
		animation2.setFillAfter(true);
		animation2.setFillBefore(false);
		animation2.setStartOffset(3000);
		animation2.setDuration(1000);
		animation3.setFillAfter(true);
		animation3.setFillBefore(false);
		animation3.setStartOffset(3500);
		animation3.setDuration(1000);
		animation4.setFillAfter(true);
		animation4.setFillBefore(false);
		animation4.setStartOffset(4000);
		animation4.setDuration(1000);
		animation5.setFillAfter(true);
		animation5.setFillBefore(false);
		animation5.setStartOffset(2500);
		animation5.setDuration(1000);
		bluetoothdevice.setOnClickListener(deviceOnClickListener);
		bluetoohdevice2.setOnClickListener(deviceOnClickListener);
		bluetoohdevice3.setOnClickListener(deviceOnClickListener);
	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (!mBluetoothDevices.contains(device)) {
						mBluetoothDevices.add(device);
						tv_dv_count.setText("发现" + mBluetoothDevices.size()
								+ "个设备");
						setText(device);
					}
				}
			});
		}
	};

	private void ScanLEDevice() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// setScanState(enable);
				mBluetoothAdapter.stopLeScan(mLeScanCallback);// 停止搜索
			}
		}, 5000);// 五秒后执行
		mBluetoothAdapter.startLeScan(mLeScanCallback);// 开始搜索
	}

	private void setText(BluetoothDevice device) {
		if (mBluetoothDevices.size() == 1) {

			bluetoothdevice.startAnimation(animation2);
			bluetoothdevice.setVisibility(View.VISIBLE);
			tv_dvName1.setAnimation(animation2);
			tv_dvName1.setText(device.getName());
			address = device.getAddress();
		}
		if (mBluetoothDevices.size() == 2) {
			bluetoohdevice2.startAnimation(animation3);
			bluetoohdevice2.setVisibility(View.VISIBLE);
			tv_dvName2.setAnimation(animation3);
			tv_dvName2.setText(device.getName());
			address = device.getAddress();
		}
		if (mBluetoothDevices.size() == 3) {
			bluetoohdevice3.startAnimation(animation4);
			bluetoohdevice3.setVisibility(View.VISIBLE);
			tv_dvName3.setAnimation(animation4);
			tv_dvName3.setText(device.getName());
			address = device.getAddress();
		}
	}

	OnClickListener deviceOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.bluetoothdevice:
				Intent intent = new Intent(ScanningActivity.this,
						TabHostActivity.class);
				Community.address = mBluetoothDevices.get(0).getAddress();
				startActivity(intent);
				finish();
				break;

			case R.id.bluetoothdevice2:
				Intent intent2 = new Intent(ScanningActivity.this,
						TabHostActivity.class);
				Community.address = mBluetoothDevices.get(1).getAddress();
				startActivity(intent2);
				finish();
				break;
			case R.id.bluetoothdevice3:
				Intent intent3 = new Intent(ScanningActivity.this,
						TabHostActivity.class);
				Community.address = mBluetoothDevices.get(2).getAddress();
				startActivity(intent3);
				finish();
				break;
			}
		}
	};
	TimerTask scanTimerTask = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			tv_dv_count.setAnimation(animation5);
			ScanLEDevice();
		}
	};
	TimerTask uITimerTask = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (mBluetoothDevices.size() == 0) {
				runOnUiThread(new Runnable() {
					public void run() {
						wave.startAnimation(animation);
					}
				});
			}
		}
	};
}
