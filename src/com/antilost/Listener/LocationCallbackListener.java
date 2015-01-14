package com.antilost.Listener;

public interface LocationCallbackListener {
		void onFinish(String address);
		void onError(Exception e);
}
