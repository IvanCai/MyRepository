package com.antilost.activity;

import com.example.antiLost.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TabHost;

public class TabHostActivity extends TabActivity {
	static TabHost tabHost;
	Intent intent;
static	ImageView home,help,about;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab);
		init();
		addActivity();
		MainActivity.setHandler(mHander);
	}
	public void init(){
		home=(ImageView) findViewById(R.id.img_home);
		help=(ImageView) findViewById(R.id.img_locate);
		about=(ImageView) findViewById(R.id.img_about);
		home.setOnClickListener( new MyOnClickListener());
		help.setOnClickListener(new MyOnClickListener());
		about.setOnClickListener(new MyOnClickListener());
	}
	public void addActivity() {
		tabHost = this.getTabHost();
		TabHost.TabSpec spec;
		intent = new Intent().setClass(this, MainActivity.class);
		spec = tabHost.newTabSpec("home").setIndicator("home").setContent(intent);
		tabHost.addTab(spec);
		intent = new Intent().setClass(this, LocationActivity.class);
		spec = tabHost.newTabSpec("locate").setIndicator("locate").setContent(intent);
		tabHost.addTab(spec);
		intent = new Intent().setClass(this,AboutActivity.class);
		spec = tabHost.newTabSpec("about").setIndicator("about").setContent(intent);
		tabHost.addTab(spec);
	}
	public class MyOnClickListener implements OnClickListener {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.img_home:
				tabHost.setCurrentTabByTag("home");
				home.setImageDrawable(getResources().getDrawable(
						R.drawable.home_press));
				help.setImageDrawable(getResources().getDrawable(
						R.drawable.locate_normal));
				about.setImageDrawable(getResources().getDrawable(
						R.drawable.aboutus_normal));
			
				break;
			case R.id.img_locate:
				tabHost.setCurrentTabByTag("locate");
				help.setImageDrawable(getResources().getDrawable(
						R.drawable.locate_pressed));
				home.setImageDrawable(getResources().getDrawable(
						R.drawable.home_normal));
				about.setImageDrawable(getResources().getDrawable(
						R.drawable.aboutus_normal));
				
				break;
			case R.id.img_about:
				tabHost.setCurrentTabByTag("about");
				about.setImageDrawable(getResources().getDrawable(
						R.drawable.aboutus_press));
				home.setImageDrawable(getResources().getDrawable(
						R.drawable.home_normal));
				help.setImageDrawable(getResources().getDrawable(
						R.drawable.locate_normal));
				
				break;
		
			}
		}
	}
	 Handler mHander= new Handler(){
		 public void handleMessage(android.os.Message msg) {
			 switch (msg.what) {
			case 0:
				TabHostActivity.tabHost.setCurrentTabByTag("locate");
				help.setImageDrawable(getResources().getDrawable(
						R.drawable.locate_pressed));
				home.setImageDrawable(getResources().getDrawable(
						R.drawable.home_normal));
				about.setImageDrawable(getResources().getDrawable(
						R.drawable.aboutus_normal));
				break;

			case 1:
				tabHost.setCurrentTabByTag("home");
				home.setImageDrawable(getResources().getDrawable(
						R.drawable.home_press));
				help.setImageDrawable(getResources().getDrawable(
						R.drawable.locate_normal));
				about.setImageDrawable(getResources().getDrawable(
						R.drawable.aboutus_normal));
				break;
			}
		 };
	 };
}