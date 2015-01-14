package com.antilost.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.example.antiLost.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class OpenBTActivity extends Activity {
	BluetoothManager bluetoothManager;
	BluetoothAdapter mBluetoothAdapter;
	private ImageView button, wave;
	private Animation animation;
	private int what = 1;
	boolean ifClick = false;
	Timer timer = new Timer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.open_bt);
		bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		button = (ImageView) findViewById(R.id.button);
		wave = (ImageView) findViewById(R.id.wave);
		wave.setVisibility(View.INVISIBLE);

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				button.setImageDrawable(getResources().getDrawable(
						R.drawable.newbutton_start2));
				wave.setVisibility(View.VISIBLE);
				wave.setImageDrawable(getResources().getDrawable(
						R.drawable.wave_loading));
				animation = AnimationUtils.loadAnimation(OpenBTActivity.this,
						R.anim.anim_roating);
				animation.setDuration(2000);
				wave.setAnimation(animation);

			}
		};
	};

	public void onShow(View view) {

		if (!ifClick) {
			mBluetoothAdapter.enable();
			handler.sendEmptyMessage(what);
			timer.schedule(task, 3000);
			ifClick = true;
			return;
		}

		if (ifClick == true) {
			wave.clearAnimation();
			wave.setVisibility(View.INVISIBLE);
			ifClick = false;

		}

	}

	TimerTask task = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Intent intent = new Intent(OpenBTActivity.this,
					ScanningActivity.class);
			startActivity(intent);
			OpenBTActivity.this.finish();
		}
	};

}
