package com.antilost.activity;

import com.antilost.util.Community;
import com.antilost.util.PlayerMedia;
import com.example.antiLost.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingActivity extends Activity {
	static Handler mhandler = null;
	CheckBox cb_near, cb_mid, cb_far, cb_sound, cb_vibrate;
	public static final int NEARISUNCHOSE = 0;
	public static final int NEARISCHOSE = 1;
	public static final int MIDRISCHOSE = 2;
	public static final int MIDRISUNCHOSE = 3;
	public static final int FARISUNCHOSE = 4;
	public static final int FARISCHOSE = 5;
	public static final int SOUNDISCHOSE = 6;
	public static final int SOUNDISUNCHOSE = 7;
	public static final int VIBRATEISCHOSE = 8;
	public static final int VIBRATEISUNCHOSE = 9;

	ImageView back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		cb_far = (CheckBox) findViewById(R.id.cb_far);
		cb_mid = (CheckBox) findViewById(R.id.cb_mid);
		cb_near = (CheckBox) findViewById(R.id.cb_near);
		cb_sound = (CheckBox) findViewById(R.id.cb_sound);
		cb_vibrate = (CheckBox) findViewById(R.id.cb_vibrate);
		back = (ImageView) findViewById(R.id.img_back);
		initCheckBox();
		cb_far.setOnCheckedChangeListener(mCheckedChangeListener);
		cb_near.setOnCheckedChangeListener(mCheckedChangeListener);
		cb_mid.setOnCheckedChangeListener(mCheckedChangeListener);
		cb_sound.setOnCheckedChangeListener(mCheckedChangeListener);
		cb_vibrate.setOnCheckedChangeListener(mCheckedChangeListener);
		new PlayerMedia(getApplicationContext());
	}

	public void initCheckBox() {
		if (Community.near)
			cb_near.setChecked(true);
		if (Community.mid)
			cb_mid.setChecked(true);
		if (Community.far)
			cb_far.setChecked(true);
		if (Community.sound)
			cb_sound.setChecked(true);
		if (Community.vibrate)
			cb_vibrate.setChecked(true);
	}

	public static void setAcctivityHandler(Handler h) {
		mhandler = h;
	}

	OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			switch (buttonView.getId()) {
			case R.id.cb_near:
				if (isChecked) {
					Message msg = Message.obtain(mhandler, NEARISCHOSE);
					msg.sendToTarget();
					cb_mid.setChecked(false);
					cb_far.setChecked(false);
					Community.near = true;
					Community.mid = false;
					Community.far = false;
				} else if (!isChecked) {
					Message msg = Message.obtain(mhandler, NEARISUNCHOSE);
					msg.sendToTarget();
					Community.near = false;

				}
				break;
			case R.id.cb_mid:
				if (isChecked) {
					Message msg = Message.obtain(mhandler, MIDRISCHOSE);
					msg.sendToTarget();
					cb_near.setChecked(false);
					cb_far.setChecked(false);
					Community.mid = true;
					Community.near = false;
					Community.far = false;
				} else if (!isChecked) {
					Message msg = Message.obtain(mhandler, MIDRISUNCHOSE);
					msg.sendToTarget();
					Community.mid = false;

				}
				break;
			case R.id.cb_far:
				if (isChecked) {
					Message msg = Message.obtain(mhandler, FARISCHOSE);
					msg.sendToTarget();
					cb_near.setChecked(false);
					cb_mid.setChecked(false);
					Community.far = true;
					Community.near = false;
					Community.mid = false;
				} else if (!isChecked) {
					Message msg = Message.obtain(mhandler, FARISUNCHOSE);
					msg.sendToTarget();
					Community.far = false;
				}
				break;
			case R.id.cb_sound:
				if (isChecked) {
					Message msg = Message.obtain(mhandler, SOUNDISCHOSE);
					msg.sendToTarget();
					Community.sound = true;
				} else if (!isChecked) {
					Message msg = Message.obtain(mhandler, SOUNDISUNCHOSE);
					msg.sendToTarget();
					Community.sound = false;
				}
				break;
			case R.id.cb_vibrate:
				if (isChecked) {
					Message msg = Message.obtain(mhandler, VIBRATEISCHOSE);
					msg.sendToTarget();
					Community.vibrate = true;
				} else if (!isChecked) {
					Message msg = Message.obtain(mhandler, VIBRATEISUNCHOSE);
					msg.sendToTarget();
					Community.vibrate = false;
				}
				break;
			}
		}
	};
	public void back(View v) {
		finish();
	}

	public void ringSetting(View v) {
		Intent it = new Intent(SettingActivity.this, RingSettingActivity.class);
		startActivity(it);
	}
	public void addContact(View v) {
		Intent it = new Intent(SettingActivity.this, AddContactActivity.class);
		startActivity(it);
	}
	public void addMsgContent(View v) {
		Intent it = new Intent(SettingActivity.this, AddMsgActivity.class);
		startActivity(it);
	}
}
