package com.ran.news.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.ran.news.base.BasePager;

public class SettingPager extends BasePager {

	public SettingPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		tvTitle.setText("…Ë÷√");
		btnMenu.setVisibility(View.GONE);
		TextView view = new TextView(mActivity);
		view.setText("…Ë÷√");
		view.setTextColor(Color.RED);
		view.setTextSize(22);
		
		view.setGravity(Gravity.CENTER);
		
		flContent.addView(view);
	}

}
