package com.ran.news.base.impl.menuDetail;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.ran.news.base.BaseMenuDetailPager;

public class InteractMenuDetailPager extends BaseMenuDetailPager {

	public InteractMenuDetailPager(Activity activity) {
		super(activity);
	}

	@Override
	public View initView() {
		
		TextView view = new TextView(mActivity);
		view.setText("≤Àµ•œÍ«È“≥--ª•∂Ø");
		view.setTextColor(Color.RED);
		view.setTextSize(22);
		view.setGravity(Gravity.CENTER);
		return view;
		
	}

}
