package com.antilost.activity;

import java.util.List;
import java.util.UUID;

import com.antilost.Listener.SBOnchangedListener;
import com.antilost.util.Parser;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

@SuppressLint("NewApi")
public class BleAdapterService extends Service {
	private final static String TAG = "BluetoothLeService";
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	public BluetoothGatt mBluetoothGatt;
	private int alertValueI = 286331153;
	private int cancleAlertValueI = 572662306;
	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;
	private int mConnectionState = STATE_DISCONNECTED;
	// 定义广播动作
	public final static String ACTION_GATT_CONNECTED = "ble.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "ble.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "ble.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "ble.ACTION_DATA_AVAILABLE";
	public final static String ACTION_CDATA_AVAILABLE = "ble.ACTION_CDATA_AVAILABLE";
	public final static String EXTRA_DATA = "ble.EXTRA_DATA";
	public final static String EXTRA_CDATA = "ble.EXTRA_CDATA";
	public final static String ACTION_NOFITICATION = "ble.ACTION_NOFITICATION";
	public static String PUSH_BUTTON_SERVICE_UUID = "00001000-0000-1000-8000-00805f9b34fb";
	public static String PUSH_BUTTON_SERVICE_CHARACTERISTIC = "00001003-0000-1000-8000-00805f9b34fb";// 可写入uuid
	public static String PUSH_BUTTON_SERVICE_CHARACTERISTIC2 = "00001002-0000-1000-8000-00805f9b34fb";// 可开启通知uuid
	public static String PUSH_BUTTON_SERVICE_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";// 自定义特征2的描述uuid
	@SuppressLint("NewApi")
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, // 连接状态改变时回调
				int newState) {
			// TODO Auto-generated method stub

			if (newState == BluetoothProfile.STATE_CONNECTED) {
			boolean b= 	mBluetoothGatt.discoverServices();
				broadcastUpdate(ACTION_GATT_CONNECTED);
				
				Log.e("tag", "connected");
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				broadcastUpdate(ACTION_GATT_DISCONNECTED);

			}
		}

		public void onServicesDiscovered(BluetoothGatt gatt, int status) {// 发现的服务时回调
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
			}

		};

		public void onCharacteristicRead(
				BluetoothGatt gatt, // 当读取特征值时回调
				android.bluetooth.BluetoothGattCharacteristic characteristic,
				int status) {
			broadcastUpdate(ACTION_CDATA_AVAILABLE, characteristic);
		};

		public void onCharacteristicChanged(BluetoothGatt gatt,
				android.bluetooth.BluetoothGattCharacteristic characteristic) {
			broadcastUpdate(ACTION_NOFITICATION, characteristic);

		};

		public void onCharacteristicWrite(BluetoothGatt gatt,
				android.bluetooth.BluetoothGattCharacteristic characteristic,
				int status) {
		};

		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {// 当读取rssi值时回调
			broadcastUpdaterssi(ACTION_DATA_AVAILABLE, rssi);
		};

		public void onDescriptorRead(BluetoothGatt gatt,
				android.bluetooth.BluetoothGattDescriptor descriptor, int status) {
			byte[] desc = descriptor.getValue();
		};

		public void onDescriptorWrite(BluetoothGatt gatt,
				android.bluetooth.BluetoothGattDescriptor descriptor, int status) {

		};

		@Override
		public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
			// TODO Auto-generated method stub
			super.onReliableWriteCompleted(gatt, status);
		}
	};

	public class LocalBinder extends Binder {
		BleAdapterService getService() {
			return BleAdapterService.this;
		}
	}

	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.w("service process id:", String.valueOf(android.os.Process.myPid()));
		return mBinder;
	}

	public boolean onUnbind(Intent intent) {
		close();
		return super.onUnbind(intent);

	}

	public boolean initialize() { // 初始化蓝牙适配器
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
			mBluetoothAdapter = mBluetoothManager.getAdapter();
			if (mBluetoothAdapter == null) {
				Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
				return false;

			}

		}

		return true;

	}

	private void close() { // 关闭BluetoothGatt
		// TODO Auto-generated method stub
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	private void broadcastUpdaterssi(final String action, int rssi) { // 更新rssi广播
		// TODO Auto-generated method stub

		Intent intent = new Intent(action);
		intent.putExtra(EXTRA_DATA, String.valueOf(rssi));
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action) { // 更新广播
		Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action,
			BluetoothGattCharacteristic charc) {
		byte b[] = charc.getValue();
		int i = Parser.byteToInt2(b);
		if(i==alertValueI||i==cancleAlertValueI)
			return;
		Intent intent = new Intent(action);
		byte c[] = charc.getValue();
		intent.putExtra("CharValue", b);
		sendBroadcast(intent);
	}

	public boolean connect(final String address) {
		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		if (mBluetoothGatt != null)
			return true;
		Log.w(TAG, "trying to create a new connection");
		return false;
	}

	public void disconnect() {// 关闭
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
	}

	public boolean reconnect() {// 重新连接
		if (mBluetoothAdapter == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return false;
		}
		return mBluetoothGatt.connect();
	}

	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {// 读取特征值
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	public void setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
		if (PUSH_BUTTON_SERVICE_CHARACTERISTIC2.equals(characteristic.getUuid()
				.toString())) {

			BluetoothGattDescriptor descriptor = characteristic
					.getDescriptor(UUID
							.fromString(PUSH_BUTTON_SERVICE_DESCRIPTOR));
			descriptor
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}
	}

	public void setCharacteristicNotification() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		BluetoothGattService mService = mBluetoothGatt.getService(UUID
				.fromString(PUSH_BUTTON_SERVICE_UUID));
		if (mService == null)
			return;

		BluetoothGattCharacteristic mCharacteristic = mService
				.getCharacteristic(UUID
						.fromString(PUSH_BUTTON_SERVICE_CHARACTERISTIC2));
		if (mCharacteristic == null)
			return;
		mBluetoothGatt.setCharacteristicNotification(mCharacteristic, true);
		BluetoothGattDescriptor mDescriptor = mCharacteristic
				.getDescriptor(UUID.fromString(PUSH_BUTTON_SERVICE_DESCRIPTOR));
		if (mDescriptor == null)
			return;
		mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		mBluetoothGatt.writeDescriptor(mDescriptor);
	}

	public void getCharacteristicDescriptor(BluetoothGattDescriptor descriptor) {// 读取特征描述
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}

		mBluetoothGatt.readDescriptor(descriptor);
	}

	public void readRssi() { // 读取rssi值
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readRemoteRssi();
	}

	public List<BluetoothGattService> getSupportedBluetoothGattServices() { // 获取装服务对象的list集合
		if (mBluetoothGatt == null) {
			return null;
		}
		return mBluetoothGatt.getServices();
	}

	public boolean writeCharacteristic(String serviceUuid,
			String charatisticUuid, byte[] value) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return false;
		}
		BluetoothGattService gattService = mBluetoothGatt
				.getService(java.util.UUID.fromString(serviceUuid));
		if (gattService == null) {
			return false;
		}
		BluetoothGattCharacteristic gattCharacteristic = gattService
				.getCharacteristic(java.util.UUID.fromString(charatisticUuid));
		if (gattCharacteristic == null) {
			return false;
		}
		gattCharacteristic.setValue(value);

		return mBluetoothGatt.writeCharacteristic(gattCharacteristic);
	}

	public boolean writeGattCharacteristic(
			BluetoothGattCharacteristic characteristic, byte[] value) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return false;
		}
		if (characteristic == null) {
			return false;
		}
		characteristic.setValue(value);
		return mBluetoothGatt.writeCharacteristic(characteristic);

	}

	public BluetoothGattCharacteristic getCurrentChar() {
		BluetoothGattService mDifinedSerice = mBluetoothGatt.getService(UUID
				.fromString(PUSH_BUTTON_SERVICE_UUID));
		BluetoothGattCharacteristic mWriteableChar = mDifinedSerice
				.getCharacteristic(UUID
						.fromString(PUSH_BUTTON_SERVICE_CHARACTERISTIC));
		return mWriteableChar;
	}

}
