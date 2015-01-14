package com.antilost.Listener;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.antilost.activity.MainActivity;
import com.antilost.components.SwitchButton;
import com.example.antiLost.R;

public class SBOnchangedListener implements OnCheckedChangeListener {
	public static boolean isProtectionON = false;
	public static boolean isFind = false;
	public static boolean startNoti = false;
	static Handler msgHandler = null;
	public final static int NOTIFICATIONISON = 10;
	public final static int NOTIFICATIONISOFF = 100;
	public final static int PERTECTIONON = 20;
	public final static int PERTECTIONOFF = 200;

	public static void setHandler(Handler h) {
		msgHandler = h;
	}

	@Override
	public void onCheckedChanged(CompoundButton sb, boolean state) {
		// TODO Auto-generated method stub
		switch (sb.getId()) {
		case R.id.sb_protection:
			if (state) {
				isProtectionON = true;
				Message msg = msgHandler
						.obtainMessage(PERTECTIONON, msgHandler);
				msg.sendToTarget();
			} else {
				isProtectionON = false;
				Message msg = msgHandler.obtainMessage(PERTECTIONOFF,
						msgHandler);
				msg.sendToTarget();
			}
			break;

		case R.id.sb_alert:
			if (state) {
				startNoti = true;
				Message msg = msgHandler.obtainMessage(NOTIFICATIONISON,
						msgHandler);
				msg.sendToTarget();
				
			} else {
				startNoti = false;
				Message msg = msgHandler.obtainMessage(NOTIFICATIONISOFF,
						msgHandler);
				msg.sendToTarget();
			}
			break;
		case R.id.sb_find:
			if (state) {
				startNoti = false;
				MainActivity.startFindAntiLostor();
			} else {
				startNoti=true;
				MainActivity.cancalFindAntiLostor();
			}
			break;
		}
	}
	private void isSettingUrgentSituation(){}
}
