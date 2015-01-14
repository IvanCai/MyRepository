package com.antilost.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.antilost.Listener.LocationCallbackListener;

import android.location.Address;
import android.location.Location;
import android.os.Handler;
import android.os.Message;

public class LocationManager {
	public static final int locationMsg = 12530;

	public void showLocation(final Location location,final LocationCallbackListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					StringBuilder url = new StringBuilder();
					url.append("http://api.map.baidu.com/geocoder?location=");
					url.append(location.getLatitude()).append(",")
							.append(location.getLongitude());
					url.append("&output=json&key=8DdwAw1z5AqhGhaCBXNfNGFd");
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(url.toString());
					// 在请求消息头中指定语言，保证服务器会返回中文数据
					httpGet.addHeader("Accept-Lan0guage", "zh-CN");
					HttpResponse httpResponse = httpClient.execute(httpGet);
//					int a = httpResponse.getStatusLine().getStatusCode();
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity, "utf-8");
						JSONObject jsonObject = new JSONObject(response);
						String result = jsonObject.getString("result");
						JSONObject jsonResultObject = new JSONObject(result);
						String address = jsonResultObject
								.getString("formatted_address");
						if(listener!=null){
								listener.onFinish(address);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					listener.onError(e);
				}
			}
		}).start();
	}


}
