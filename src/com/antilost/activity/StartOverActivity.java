package com.antilost.activity;

import com.antilost.util.BluetoothOpenCheck;
import com.example.antiLost.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;

public class StartOverActivity extends Activity {
	boolean firstEnter = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startover);
		new BluetoothOpenCheck();
		final SharedPreferences shares = getSharedPreferences("FirstEnter",
				MODE_PRIVATE);
		firstEnter = shares.getBoolean("FirstEnter", true);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub

				if (firstEnter) {
					Editor editor = shares.edit();
					editor.putBoolean("FirstEnter", false);
					editor.commit();
					Intent it = new Intent(StartOverActivity.this,
							GuideActivity.class);
					startActivity(it);
					finish();
				} else {
					if (BluetoothOpenCheck.isBTOpen()) {
						Intent it = new Intent(StartOverActivity.this,
								ScanningActivity.class);
						startActivity(it);
						finish();
					}
					else{
							Intent  it=new Intent(StartOverActivity.this,OpenBTActivity.class);
							startActivity(it);
							finish();
					}
				}
			}
		}, 3000);
	}
}
