package com.antilost.activity;

import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

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
	// ����㲥����
	public final static String ACTION_GATT_CONNECTED = "ble.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "ble.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "ble.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "ble.ACTION_DATA_AVAILABLE";
	public final static String ACTION_CDATA_AVAILABLE = "ble.ACTION_CDATA_AVAILABLE";
	public final static String EXTRA_DATA = "ble.EXTRA_DATA";
	public final static String EXTRA_CDATA = "ble.EXTRA_CDATA";
	public final static String ACTION_NOFITICATION = "ble.ACTION_NOFITICATION";
	public static String PUSH_BUTTON_SERVICE_UUID = "0000FFF0-0000-1000-8000-00805f9b34fb";
	public static String PUSH_BUTTON_SERVICE_CHARACTERISTIC = "0000FFF1-0000-1000-8000-00805f9b34fb";// ��д��uuid
	public static String PUSH_BUTTON_SERVICE_CHARACTERISTIC2 = "0000FFF2-0000-1000-8000-00805f9b34fb";// �ɿ���֪ͨuuid
	public static String PUSH_BUTTON_SERVICE_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";// �Զ�������2������uuid
	@SuppressLint("NewApi")
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, // ����״̬�ı�ʱ�ص�
				int newState) {
			// TODO Auto-generated method stub

			if (newState == BluetoothProfile.STATE_CONNECTED) {
				boolean b = mBluetoothGatt.discoverServices();
				broadcastUpdate(ACTION_GATT_CONNECTED);

				Log.e("tag", "connected");
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				broadcastUpdate(ACTION_GATT_DISCONNECTED);
			}
		}

		public void onServicesDiscovered(BluetoothGatt gatt, int status) {// ���ֵķ���ʱ�ص�
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
			}

		};

		public void onCharacteristicRead(
				BluetoothGatt gatt, // ����ȡ����ֵʱ�ص�
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

		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {// ����ȡrssiֵʱ�ص�
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

	public boolean initialize() { // ��ʼ������������
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

	private void close() { // �ر�BluetoothGatt
		// TODO Auto-generated method stub
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	private void broadcastUpdaterssi(final String action, int rssi) { // ����rssi�㲥
		// TODO Auto-generated method stub

		Intent intent = new Intent(action);
		intent.putExtra(EXTRA_DATA, String.valueOf(rssi));
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action) { // ���¹㲥
		Intent intent = new Intent(action);
		sendBroadcast(intent);
	}
	private void broadcastUpdate(final String action,
			BluetoothGattCharacteristic charc) {
		byte b[]=charc.getValue();
		String encodedHexStr=new String(Hex.encodeHex(b));
		Intent intent = new Intent(action);
		intent.putExtra("encodedHexStr", encodedHexStr);
		sendBroadcast(intent);
	}

	public boolean connect(final String address) {
		if (mBluetoothGatt != null)
			{
				mBluetoothGatt.close();
			}
		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		Log.w(TAG, "trying to create a new connection");
		return true;
	}

	public void disconnect() {// �ر�
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
	}

	public boolean reconnect() {// ��������
		if (mBluetoothAdapter == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return false;
		}
		return mBluetoothGatt.connect();
	}

	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {// ��ȡ����ֵ
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

	public void getCharacteristicDescriptor(BluetoothGattDescriptor descriptor) {// ��ȡ��������
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}

		mBluetoothGatt.readDescriptor(descriptor);
	}

	public void readRssi() { // ��ȡrssiֵ
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readRemoteRssi();
	}

	public List<BluetoothGattService> getSupportedBluetoothGattServices() { // ��ȡװ��������list����
		if (mBluetoothGatt == null) {
			return null;
		}
		return mBluetoothGatt.getServices();
	}

	public boolean writeCharacteristic(String serviceUuid,
			String charatisticUuid, String content) {
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
		byte[] value = null;
		try {
			value = Hex.decodeHex(content.toCharArray());
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
