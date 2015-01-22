package com.antilost.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.codec.binary.Hex;

import com.antilost.Listener.LocationCallbackListener;
import com.antilost.Listener.SBOnchangedListener;
import com.antilost.components.SwitchButton;
import com.antilost.util.Community;
import com.antilost.util.MyApplication;
import com.antilost.util.Parser;
import com.antilost.util.PlayerMedia;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.example.antiLost.R;

import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	private ImageView imageView1, imageView2, imageView3, img_connectedState;
	private SwitchButton sb_protection, sb_alert, sb_find;
	private TextView tv_rssi;
	Animation animation, animation2, animation3;
	private MediaPlayer mp;
	private ArrayList<Integer> Rssi_gather = new ArrayList<Integer>();
	Vibrator vibrator;
	private boolean nearDistance = true;
	private boolean midDistance = false;
	private boolean farDistance = false;
	private boolean isConnected = false;
	private Timer readRssiTimer;
	private Timer animationTimer;
	private static BleAdapterService mbleAdapterService;
	private final static String TAG = "initializeBluetooth";
	private int rssi;
	private boolean delay = true;
	private boolean isFrist = true;
	private Boolean isSoundOn = false;
	private Boolean isVirateOn = false;
	private boolean manualConnectionFlag = false;
	public static int whichRing = 1;
	public static Handler locationHandler;
	private static final int SENDSUCCESSFLU = 10086;
	private static final int URGENTSITUATIONNOTSAVED = 110;
	private boolean initMapresFalg = false;
	private boolean autoConnectedFlag = false;
	private static String alertOnContent = "ac11111111";
	private static String alertOffContent = "ac00000000";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		init();
		new PlayerMedia(getApplicationContext());
		Intent gattServiceIntent = new Intent(MainActivity.this,
				BleAdapterService.class);
		System.out.println(this.getApplicationContext().bindService(
				gattServiceIntent, serviceConnection, BIND_AUTO_CREATE));
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	private void init() {
		imageView1 = (ImageView) findViewById(R.id.img_wava1);
		imageView2 = (ImageView) findViewById(R.id.img_wava2);
		imageView3 = (ImageView) findViewById(R.id.img_wava3);
		sb_protection = (SwitchButton) findViewById(R.id.sb_protection);
		sb_alert = (SwitchButton) findViewById(R.id.sb_alert);
		sb_find = (SwitchButton) findViewById(R.id.sb_find);
		tv_rssi = (TextView) findViewById(R.id.tv_rssi);
		img_connectedState = (ImageView) findViewById(R.id.img_connected_state);
		animation = AnimationUtils.loadAnimation(MainActivity.this,
				R.anim.sp_shrink_from_bottom);
		animation.setDuration(3000);
		imageView1.setAnimation(animation);
		animation2 = AnimationUtils.loadAnimation(MainActivity.this,
				R.anim.sp_shrink_from_bottom2);
		animation2.setDuration(3000);
		animation3 = AnimationUtils.loadAnimation(MainActivity.this,
				R.anim.sp_shrink_from_bottom2);
		animation3.setDuration(3000);
		animationTimer = new Timer();
		animationTimer.schedule(task, 1000);
		animationTimer.schedule(task2, 2000);
		sb_protection.setOnCheckedChangeListener(new SBOnchangedListener());
		sb_find.setOnCheckedChangeListener(new SBOnchangedListener());
		sb_alert.setOnCheckedChangeListener(new SBOnchangedListener());
		sb_protection.setEnabled(false);
		sb_alert.setEnabled(false);
		sb_find.setEnabled(false);

	}

	private final ServiceConnection serviceConnection = new ServiceConnection() { // ��BleAdapterService����

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mbleAdapterService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mbleAdapterService = ((BleAdapterService.LocalBinder) service)
					.getService();
			if (!mbleAdapterService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			SettingActivity.setAcctivityHandler(mMsgHandler);
			SBOnchangedListener.setHandler(mMsgHandler);
			RingSettingActivity.setHandler(mMsgHandler);
		}
	};
	TimerTask task = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			imageView2.setAnimation(animation2);
		}
	};
	TimerTask task2 = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			imageView3.setAnimation(animation3);
		}
	};

	public void connectWithServer(View view) {
		if (!isConnected) {

			if (mbleAdapterService.connect(Community.address)) {
				manualConnectionFlag = true;
				return;
			}
		} else {
			mbleAdapterService.disconnect();
			manualConnectionFlag = false;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.w("Activity process id:",
				String.valueOf(android.os.Process.myPid()));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mGattUpdateReceiver);
		stopTimer();
		mbleAdapterService = null;
		super.onDestroy();
	}

	private void startReadRssiTimer() {
		readRssiTimer = new Timer();
		readRssiTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mbleAdapterService.readRssi();
			}
		}, 0, 300);
	}

	private void stopTimer() {
		if (readRssiTimer != null) {
			readRssiTimer.cancel();
			readRssiTimer = null;
		}
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {// �㲥���������ڸ���UI

		@SuppressWarnings("deprecation")
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			final String action = intent.getAction();
			if (BleAdapterService.ACTION_GATT_CONNECTED.equals(action)) {
				Log.e("tag", "���ӳɹ�");
				startShake();
				if (readRssiTimer == null) {
					MainActivity.this.startReadRssiTimer();
				}
				img_connectedState.setBackgroundResource(R.drawable.connected4);
				sb_protection.setEnabled(true);
				sb_alert.setEnabled(true);
				sb_find.setEnabled(true);
				isConnected = true;
				if (!initMapresFalg) {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							initLocationActivity(); // ��ʼ��λ�ý����ͼ��Դ
						}
					}, 2500);
					initMapresFalg = true;
				}
				if (mp != null)
					mp.setLooping(false);
				if (isFrist) {
					isFrist = false;
				} else {
					delay = false;
					autoConnectedFlag = true;
				}
			}
			if (BleAdapterService.ACTION_GATT_DISCONNECTED.equals(action)) {
				mbleAdapterService.disconnect();
				Toast.makeText(MainActivity.this, "�Ͽ�����", 1000).show();
				img_connectedState
						.setBackgroundResource(R.drawable.disconnected);
				isConnected = false;
				// �ж϶Ͽ���ʽ�����Ϊ�ֶ��Ͽ����򲻻ᴥ���Զ�����
				if (manualConnectionFlag) {
					delay = true;
					mbleAdapterService.reconnect();
					new Handler().postDelayed(myRunnable, 10000);
				}
			}
			if (BleAdapterService.ACTION_DATA_AVAILABLE.equals(action)) {
				if (addRssi(intent.getStringExtra(BleAdapterService.EXTRA_DATA))) {
					rssi = rssiFilter();
					tv_rssi.setText(String.valueOf(rssi));
					ifAlert(rssi);
				}
			}
			if (BleAdapterService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				mbleAdapterService.setCharacteristicNotification();
			}
			if (BleAdapterService.ACTION_CDATA_AVAILABLE.equals(action)) {

			}

			if (BleAdapterService.ACTION_NOFITICATION.equals(action)) {
				String encodedHexStr = intent.getStringExtra("encodedHexStr");
				// һ���������������¼�
				if (encodedHexStr.equals("aa03ad")) {
					playSound();
				}
				if (SBOnchangedListener.startNoti) {
					// �����������������¼�
					if (encodedHexStr.equals("aa02ac")) {
						SharedPreferences contactShares = getSharedPreferences(
								"contact", MODE_PRIVATE);
						final String contact1 = contactShares.getString(
								"contact1", null);
						final String contact2 = contactShares.getString(
								"contact2", null);
						final String contact3 = contactShares.getString(
								"contact3", null);
						SharedPreferences msgShares = getSharedPreferences(
								"MsgContent", MODE_PRIVATE);
						final String content = msgShares.getString("Content",
								null);
						final SmsManager sms = SmsManager.getDefault();
						MyApplication.mSmsLocationClient
								.registerLocationListener(new BDLocationListener() {

									@Override
									public void onReceiveLocation(
											BDLocation location) {
										// TODO Auto-generated method stub
										Message msg = mMsgHandler
												.obtainMessage();
										String currentposition = "γ����:"
												+ location.getLatitude() + "\n"
												+ "������:"
												+ location.getLongitude();
										String appendContent = content
												+ "\n�ҵ�λ����:"
												+ location.getAddrStr() + "("
												+ currentposition + ")";
										if ((contact1 == null
												&& contact2 == null && contact3 == null)
												|| content == null) {
											msg.what = URGENTSITUATIONNOTSAVED;
											mMsgHandler.sendMessage(msg);
										}

										else {
											if (contact1 != null)
												sms.sendTextMessage(contact1,
														null, appendContent,
														null, null);
											if (contact2 != null)
												sms.sendTextMessage(contact2,
														null, appendContent,
														null, null);
											if (contact3 != null)
												sms.sendTextMessage(contact3,
														null, appendContent,
														null, null);
											msg.what = SENDSUCCESSFLU;
											mMsgHandler.sendMessage(msg);
										}
										// ���ҵ���߼����ֶ�ֹͣ��λ
										MyApplication.mSmsLocationClient.stop();
									}
								});
						MyApplication.mSmsLocationClient.start();

					}

				}
				// ���Ĺ��ܰ��������¼�
				if (encodedHexStr.equals("aa01ab")) {
					if (CameraActivity.mCamera != null) {
						CameraActivity.mCamera.takePicture(
								CameraActivity.shutter, null,
								CameraActivity.mPictureCallback);
					} else {
						Toast.makeText(MainActivity.this, "�������Ͻǿ�������������ʹ�ô˹���",
								Toast.LENGTH_SHORT).show();
						;
					}
				}
			}
		}

		private void initLocationActivity() {
			// TODO Auto-generated method stub
			if (!LocationActivity.initedMapFlag) {
				locationHandler.sendEmptyMessage(0);
				Toast.makeText(MainActivity.this, "��ʼ����ͼ��Դ", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	public static void setHandler(Handler h) {
		locationHandler = h;
	}

	private static IntentFilter makeGattUpdateIntentFilter() {// ������
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BleAdapterService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(BleAdapterService.ACTION_CDATA_AVAILABLE);
		intentFilter
				.addAction(BleAdapterService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BleAdapterService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BleAdapterService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BleAdapterService.ACTION_NOFITICATION);

		return intentFilter;
	}

	private void ifAlert(int rssi) {
		if (SBOnchangedListener.isProtectionON) {
			byte CharCacheValue[] = mbleAdapterService.getCurrentChar()
					.getValue();
			String encodedStrVal = "";
			if (CharCacheValue != null) {
				encodedStrVal = new String(Hex.encodeHex(CharCacheValue));
			}
			if (nearDistance) {
				if (rssi > -55 && encodedStrVal.equals(alertOnContent)) {
					mbleAdapterService
							.writeCharacteristic(
									BleAdapterService.PUSH_BUTTON_SERVICE_UUID,
									BleAdapterService.PUSH_BUTTON_SERVICE_CHARACTERISTIC,
									alertOffContent);

				}
				if (rssi < -55) {
					if (mbleAdapterService
							.writeCharacteristic(
									BleAdapterService.PUSH_BUTTON_SERVICE_UUID,
									BleAdapterService.PUSH_BUTTON_SERVICE_CHARACTERISTIC,
									alertOnContent))
						;
					;
					if (isSoundOn)
						playSound();
					if (isVirateOn)
						startShake();
				}

			}
			if (midDistance) {
				if (rssi > -68 && encodedStrVal.equals(alertOnContent)) {
					mbleAdapterService
							.writeCharacteristic(
									BleAdapterService.PUSH_BUTTON_SERVICE_UUID,
									BleAdapterService.PUSH_BUTTON_SERVICE_CHARACTERISTIC,
									alertOffContent);
				}
				if (rssi < -68) {
					if (mbleAdapterService
							.writeCharacteristic(
									BleAdapterService.PUSH_BUTTON_SERVICE_UUID,
									BleAdapterService.PUSH_BUTTON_SERVICE_CHARACTERISTIC,
									alertOnContent))
						if (isSoundOn)
							playSound();
					if (isVirateOn)
						startShake();
				}

			}
			if (farDistance) {
				if (rssi > -78 && encodedStrVal.equals(alertOnContent)) {
					mbleAdapterService
							.writeCharacteristic(
									BleAdapterService.PUSH_BUTTON_SERVICE_UUID,
									BleAdapterService.PUSH_BUTTON_SERVICE_CHARACTERISTIC,
									alertOffContent);
				}
				if (rssi < -78) {
					if (mbleAdapterService
							.writeCharacteristic(
									BleAdapterService.PUSH_BUTTON_SERVICE_UUID,
									BleAdapterService.PUSH_BUTTON_SERVICE_CHARACTERISTIC,
									alertOnContent))
						if (isSoundOn)
							playSound();
					if (isVirateOn)
						startShake();
				}

			}
		}
	}

	public static void startFindAntiLostor() {
		System.out.println("isWriteSuccessfully:"
				+ mbleAdapterService.writeCharacteristic(
						BleAdapterService.PUSH_BUTTON_SERVICE_UUID,
						BleAdapterService.PUSH_BUTTON_SERVICE_CHARACTERISTIC,
						alertOnContent));
	}

	public static void cancalFindAntiLostor() {
		mbleAdapterService.writeCharacteristic(
				BleAdapterService.PUSH_BUTTON_SERVICE_UUID,
				BleAdapterService.PUSH_BUTTON_SERVICE_CHARACTERISTIC,
				alertOffContent);
	}

	private void playSound() {
		if (whichRing == 1)
			PlayerMedia.playMedia1();
		if (whichRing == 2)
			PlayerMedia.playMedia2();
		if (whichRing == 3)
			PlayerMedia.playMedia3();
		if (whichRing == 4)
			PlayerMedia.playMedia4();
	}

	// ��
	private void startShake() {
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(1000);
		// vibrator.cancel();
	}

	private boolean addRssi(String rssi) {

		Rssi_gather.add(Integer.valueOf(rssi));
		if (Rssi_gather.size() == 10) {
			return true;
		}
		return false;
	}

	private int rssiFilter() {
		int chMinVal, chMaxVal, chTemp;
		int nCnt, nSum, length = Rssi_gather.size();
		chMaxVal = chMinVal = Rssi_gather.get(0);
		nSum = 0;
		for (nCnt = 0; nCnt < Rssi_gather.size(); ++nCnt) {
			chTemp = Rssi_gather.get(nCnt);
			nSum += chTemp;

			if (chTemp > chMaxVal) {
				chMaxVal = chTemp;
			} else if (chTemp < chMinVal) {
				chMinVal = chTemp;
			}
		}
		nSum -= (chMaxVal + chMinVal);
		nSum /= (Rssi_gather.size() - 2);
		Rssi_gather.clear();
		return (byte) nSum;
	}

	Runnable myRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (delay) {
				LocationActivity.mLocationClient.start();
				mbleAdapterService.disconnect();
				vibrator.vibrate(1500);
				mp = new MediaPlayer();
				AssetFileDescriptor afd = getResources().openRawResourceFd(
						R.raw.alert);
				sb_protection.setEnabled(false);
				sb_alert.setEnabled(false);
				sb_find.setEnabled(false);
				try {
					mp.setDataSource(afd.getFileDescriptor(),
							afd.getStartOffset(), afd.getLength());
					mp.setLooping(true);
					mp.prepare();
					mp.start();
					afd.close();
				} catch (IllegalArgumentException e) {
				} catch (IllegalStateException e) {
				} catch (IOException e) {
				}
			}
		}
	};

	/**
	 * �˵������ؼ���Ӧ
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // ����˫���˳�����
		}
		return true;
	}

	/**
	 * ˫���˳�����
	 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // ׼���˳�
			Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // ȡ���˳�
				}
			}, 1000); // ���1������û�а��·��ؼ�����������ʱ��ȡ�����ղ�ִ�е�����

		} else {
			finish();
			System.exit(0);
		}
	}

	public void setting(View v) {
		Intent it = new Intent(MainActivity.this, SettingActivity.class);
		startActivity(it);
	}

	Handler mMsgHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SettingActivity.NEARISCHOSE:
				Toast.makeText(MainActivity.this, "��ǰ��ȫ��������Ϊ:��", 1000).show();
				nearDistance = true;
				midDistance = false;
				farDistance = false;
				break;

			case SettingActivity.NEARISUNCHOSE:
				nearDistance = false;

				break;
			case SettingActivity.MIDRISCHOSE:
				midDistance = true;
				nearDistance = false;
				farDistance = false;
				Toast.makeText(MainActivity.this, "��ȫ��������Ϊ:��", 1000).show();
				if (mbleAdapterService.mBluetoothGatt != null) {
					byte[] charValue = mbleAdapterService.getCurrentChar()
							.getValue();
					if (charValue != null) {
						String encodedStrVal = new String(
								Hex.encodeHex(charValue));

						if (encodedStrVal.equals(alertOnContent)) {
							mbleAdapterService
									.writeCharacteristic(
											BleAdapterService.PUSH_BUTTON_SERVICE_UUID,
											BleAdapterService.PUSH_BUTTON_SERVICE_CHARACTERISTIC,
											alertOffContent);
						}
					}
				}

				break;
			case SettingActivity.MIDRISUNCHOSE:
				midDistance = false;

				break;
			case SettingActivity.FARISCHOSE:
				Toast.makeText(MainActivity.this, "��ȫ��������Ϊ:Զ", 1000).show();
				farDistance = true;
				midDistance = false;
				nearDistance = false;
				if (mbleAdapterService.mBluetoothGatt != null) {
					byte[] charValue = mbleAdapterService.getCurrentChar()
							.getValue();
					if (charValue != null) {
						String encodedStrVal = new String(
								Hex.encodeHex(charValue));

						if (encodedStrVal.equals(alertOnContent)) {
							mbleAdapterService
									.writeCharacteristic(
											BleAdapterService.PUSH_BUTTON_SERVICE_UUID,
											BleAdapterService.PUSH_BUTTON_SERVICE_CHARACTERISTIC,
											alertOffContent);
						}
					}
				}
				break;
			case SettingActivity.FARISUNCHOSE:
				farDistance = false;

				break;
			case SBOnchangedListener.PERTECTIONON:
				sb_find.setEnabled(false);
				break;
			case SBOnchangedListener.PERTECTIONOFF:

				if (mbleAdapterService.mBluetoothGatt != null) {
					byte[] charValue = mbleAdapterService.getCurrentChar()
							.getValue();
					if (charValue != null) {
						String encodedStrVal = new String(
								Hex.encodeHex(charValue));

						if (encodedStrVal.equals(alertOnContent)) {
							mbleAdapterService
									.writeCharacteristic(
											BleAdapterService.PUSH_BUTTON_SERVICE_UUID,
											BleAdapterService.PUSH_BUTTON_SERVICE_CHARACTERISTIC,
											alertOffContent);
						}
					}
				}
				sb_find.setEnabled(true);
				break;
			case SettingActivity.SOUNDISCHOSE:
				isSoundOn = true;
				break;
			case SettingActivity.SOUNDISUNCHOSE:
				isSoundOn = false;
				break;
			case SettingActivity.VIBRATEISCHOSE:
				isVirateOn = true;
				break;
			case SettingActivity.VIBRATEISUNCHOSE:
				isVirateOn = false;
				break;
			case SBOnchangedListener.NOTIFICATIONISON:
				mbleAdapterService.setCharacteristicNotification();
				break;
			case SBOnchangedListener.NOTIFICATIONISOFF:
				mbleAdapterService.setCharacteristicNotification();
				break;
			case RingSettingActivity.RING1:
				whichRing = 1;
				break;
			case RingSettingActivity.RING2:
				whichRing = 2;
				break;
			case RingSettingActivity.RING3:
				whichRing = 3;
				break;
			case RingSettingActivity.RING4:
				whichRing = 4;
				break;
			case SENDSUCCESSFLU:
				Toast.makeText(getApplicationContext(), "���ŷ��ͳɹ�",
						Toast.LENGTH_SHORT).show();
				break;
			case URGENTSITUATIONNOTSAVED:
				Toast.makeText(getApplicationContext(), "���ȱ༭���������е���ϵ���Լ�����!",
						Toast.LENGTH_SHORT).show();
				break;
			}
		};

	};

	public void openCamera(View v) {
		Intent intent = new Intent(MainActivity.this, CameraActivity.class);
		startActivity(intent);
	}
}
