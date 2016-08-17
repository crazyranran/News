package com.ran.news.base;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.ran.news.MainActivity;
import com.ran.news.R;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public abstract class BasePager {
	public Activity mActivity;
	public View mRootview;
	public TextView tvTitle;
	public ImageButton btnMenu;
	public FrameLayout flContent;
	
	public ImageButton btnDisplay;

	public BasePager(Activity activity) {
		mActivity = activity;
		initView();
	}

	/**
	 * 初始化布局
	 */
	public void initView() {
		mRootview = View.inflate(mActivity, R.layout.base_pager, null);
		tvTitle = (TextView) mRootview.findViewById(R.id.tv_title);
		btnMenu = (ImageButton) mRootview.findViewById(R.id.img_menu);
		flContent = (FrameLayout) mRootview.findViewById(R.id.fl_content);
		btnDisplay = (ImageButton) mRootview.findViewById(R.id.btn_display);
		btnMenu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toggle();
			}
		});
	}
	
	public void toggle(){
		MainActivity mainUI = (MainActivity) mActivity;
		SlidingMenu slidingMenu = mainUI.getSlidingMenu();
		
		slidingMenu.toggle();//开关
	}
	/**
	 * 初始化数据
	 */
	public abstract void initData();
}
