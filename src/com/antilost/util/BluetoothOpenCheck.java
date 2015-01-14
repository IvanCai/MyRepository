package com.antilost.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

public class BluetoothOpenCheck {
	private Context context;
	private 	BluetoothManager	bluetoothManager ;
	private static BluetoothAdapter mBluetoothAdapter;
	public BluetoothOpenCheck() {
		context = MyApplication.getContext();
		bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
	}

	public static boolean isBTOpen() {
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			return false;
		} else
			return true;

	}
}
