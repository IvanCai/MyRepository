package com.antilost.util;

import java.io.IOException;

import com.antilost.activity.MainActivity;
import com.example.antiLost.R;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

public class PlayerMedia {
	static Context context;
	public static MediaPlayer mp1, mp2, mp3, mp4;

	public PlayerMedia(Context context) {
		this.context = context;
		initMediaplayer();
	}
	 public  void initMediaplayer() {
	 mp1 = new MediaPlayer();
	 mp2 = new MediaPlayer();
	 mp3 = new MediaPlayer();
	 mp4 = new MediaPlayer();
	
	 }

	public static void playMedia1() {
		try {
			AssetFileDescriptor afd = context.getResources().openRawResourceFd(
					R.raw.alert);
			mp1.reset();
			mp1.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
					afd.getLength());
			mp1.prepare();
			mp1.start();
			afd.close();

		} catch (IllegalArgumentException e) {
		} catch (IllegalStateException e) {
		} catch (IOException e) {
		}
	}

	public static void playMedia2() {
		try {
			AssetFileDescriptor afd = context.getResources().openRawResourceFd(
					R.raw.warn);
			mp2.reset();
			mp2.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
					afd.getLength());
			mp2.prepare();
			mp2.start();
			afd.close();

		} catch (IllegalArgumentException e) {
		} catch (IllegalStateException e) {
		} catch (IOException e) {
		}

	}

	public static void playMedia3() {
		try {
			AssetFileDescriptor afd = context.getResources().openRawResourceFd(
					R.raw.warn2);
			mp3.reset();
			mp3.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
					afd.getLength());
			mp3.prepare();
			mp3.start();
			afd.close();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void playMedia4() {
		try {
			AssetFileDescriptor afd = context.getResources().openRawResourceFd(
					R.raw.chime);
			mp4.reset();
			mp4.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
					afd.getLength());
			mp4.prepare();
			mp4.start();
			afd.close();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
