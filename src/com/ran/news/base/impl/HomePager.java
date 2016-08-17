package com.ran.news.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.ran.news.base.BasePager;

public class HomePager extends BasePager {

	public HomePager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		tvTitle.setText("�ǻ���Ѷ");
		
		btnMenu.setVisibility(View.GONE);
		TextView view = new TextView(mActivity);
		view.setText("��ҳ");
		view.setTextColor(Color.RED);
		view.setTextSize(22);
		
		view.setGravity(Gravity.CENTER);
		
		flContent.addView(view);
	}

}
