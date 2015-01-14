package com.antilost.activity;

import java.io.IOException;

import com.antilost.util.PlayerMedia;
import com.example.antiLost.R;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class RingSettingActivity extends Activity {

	private CheckBox cb_r1, cb_r2, cb_r3, cb_r4;
	public final static int RING1 = 11;
	public final static int RING2 = 12;
	public final static int RING3 = 13;
	public final static int RING4 = 14;
	public static Handler mHander;
	public static Resources resource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_ring);
		cb_r1 = (CheckBox) findViewById(R.id.cb_ring1);
		cb_r2 = (CheckBox) findViewById(R.id.cb_ring2);
		cb_r3 = (CheckBox) findViewById(R.id.cb_ring3);
		cb_r4 = (CheckBox) findViewById(R.id.cb_ring4);
		initCheckBoxState();
		cb_r1.setOnCheckedChangeListener(cbOnCheckedChangeListener);
		cb_r2.setOnCheckedChangeListener(cbOnCheckedChangeListener);
		cb_r3.setOnCheckedChangeListener(cbOnCheckedChangeListener);
		cb_r4.setOnCheckedChangeListener(cbOnCheckedChangeListener);
		// initMediaplayer();
		// resource=getResources();
		new PlayerMedia(getApplicationContext());
	}


	OnCheckedChangeListener cbOnCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			switch (buttonView.getId()) {
			case R.id.cb_ring1:
				if (isChecked) {
					cb_r2.setChecked(false);
					cb_r3.setChecked(false);
					cb_r4.setChecked(false);
					if (PlayerMedia.mp2.isPlaying())
						PlayerMedia.mp2.stop();
					if (PlayerMedia.mp3.isPlaying())
						PlayerMedia.mp3.stop();
					if (PlayerMedia.mp4.isPlaying())
						PlayerMedia.mp4.stop();
					PlayerMedia.playMedia1();
					Message msg = mHander.obtainMessage(RING1, mHander);
					msg.sendToTarget();
				}
				break;

			case R.id.cb_ring2:
				if (isChecked) {
					cb_r1.setChecked(false);
					cb_r3.setChecked(false);
					cb_r4.setChecked(false);
					if (PlayerMedia.mp2.isPlaying())
						PlayerMedia.mp2.stop();
					if (PlayerMedia.mp3.isPlaying())
						PlayerMedia.mp3.stop();
					if (PlayerMedia.mp4.isPlaying())
						PlayerMedia.mp4.stop();
					PlayerMedia.playMedia2();
					Message msg = mHander.obtainMessage(RING2, mHander);
					msg.sendToTarget();
				}
				break;

			case R.id.cb_ring3:
				if (isChecked) {
					if (PlayerMedia.mp1.isPlaying())
						PlayerMedia.mp1.stop();
					if (PlayerMedia.mp2.isPlaying())
						PlayerMedia.mp2.stop();
					if (PlayerMedia.mp4.isPlaying())
						PlayerMedia.mp4.stop();
					PlayerMedia.playMedia3();
					cb_r1.setChecked(false);
					cb_r2.setChecked(false);
					cb_r4.setChecked(false);
					Message msg = mHander.obtainMessage(RING3, mHander);
					msg.sendToTarget();
				}
				break;

			case R.id.cb_ring4:
				if (isChecked) {
					if (PlayerMedia.mp1.isPlaying())
						PlayerMedia.mp1.stop();
					if (PlayerMedia.mp2.isPlaying())
						PlayerMedia.mp2.stop();
					if (PlayerMedia.mp3.isPlaying())
						PlayerMedia.mp3.stop();
					PlayerMedia.playMedia4();
					cb_r1.setChecked(false);
					cb_r2.setChecked(false);
					cb_r3.setChecked(false);
					Message msg = mHander.obtainMessage(RING4, mHander);
					msg.sendToTarget();
				}
				break;
			}
		}
	};
	
	public static void setHandler(Handler h) {
		mHander = h;
	}
	private void initCheckBoxState(){
		if(MainActivity.whichRing==1)
			cb_r1.setChecked(true);
		if(MainActivity.whichRing==2)
			cb_r2.setChecked(true);
		if(MainActivity.whichRing==3)
			cb_r3.setChecked(true);
		if(MainActivity.whichRing==4)
			cb_r4.setChecked(true);
}
}
