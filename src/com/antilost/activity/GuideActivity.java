package com.antilost.activity;

import java.util.ArrayList;

import com.antilost.util.BluetoothOpenCheck;
import com.example.antiLost.R;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GuideActivity extends Activity {
	private ViewPager viewgPager;
	private ArrayList<View> views = new ArrayList<View>();
	private View view1, view2, view3;
	private LayoutInflater inflater;
	private PagerAdapter padapter;
	private int currIndex = 0;
	private ImageView[] points;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_activity);
		viewgPager = (ViewPager) findViewById(R.id.tabpager);
		viewgPager.setOnPageChangeListener(new Listener());
		inflater = LayoutInflater.from(this);
		view1 = inflater.inflate(R.layout.guide_01, null);
		view2 = inflater.inflate(R.layout.guide_02, null);
		view3 = inflater.inflate(R.layout.guide_03, null);
		views.add(view1);
		views.add(view2);
		views.add(view3);
		initPoint();
		padapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}
		};
		viewgPager.setAdapter(padapter);
	}

	private void initPoint() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);

		points = new ImageView[3];

		// 循环取得小点图片
		for (int i = 0; i < 3; i++) {
			// 得到一个LinearLayout下面的每一个子元素
			points[i] = (ImageView) linearLayout.getChildAt(i);
			// 默认都设为灰色
			points[i].setEnabled(false);
			// 给每个小点设置监听
			// 设置位置tag，方便取出与当前位置对应
			points[i].setTag(i);
		}
		points[currIndex].setEnabled(true);
	}

	public void onStart(View view) {
		if (BluetoothOpenCheck.isBTOpen()) {
			Intent intent = new Intent(GuideActivity.this, ScanningActivity.class);
			startActivity(intent);
			finish();
		}
		else{
			Intent intent = new Intent(GuideActivity.this, OpenBTActivity.class);
			startActivity(intent);
			finish();
		}
	}

	class Listener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			setCurDot(arg0);

		}
	}

	private void setCurDot(int positon) {
		if (positon < 0 || positon > 2 || currIndex == positon) {
			return;
		}
		points[positon].setEnabled(true);
		points[currIndex].setEnabled(false);

		currIndex = positon;
	}

}
