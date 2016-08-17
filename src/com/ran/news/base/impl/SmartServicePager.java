package com.ran.news.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.ran.news.base.BasePager;

public class SmartServicePager extends BasePager {

	public SmartServicePager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		tvTitle.setText("智能服务");
		TextView view = new TextView(mActivity);
		view.setText("服务");
		view.setTextColor(Color.RED);
		view.setTextSize(22);
		
		view.setGravity(Gravity.CENTER);
		
		flContent.addView(view);
	}

}
