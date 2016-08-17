package com.ran.news;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.ran.news.base.impl.ContentFragment;
import com.ran.news.base.impl.LeftMenuFragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

public class MainActivity extends SlidingFragmentActivity {

	private static final String TAG_LEFT_MENU = "TAG_LEFT_MENU";
	private static final String TAG_CONTENT = "TAG_CONTENT";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setBehindContentView(R.layout.left_menu);
		SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setBehindOffset(200);
		initFragment();
	}
/**
 * ��ʼ��Fragment
 */
	private void initFragment() {
		FragmentManager fragManager = getSupportFragmentManager();
		FragmentTransaction trasaction = fragManager.beginTransaction();
		// FrameLayout��ռλ��Ȼ����fragment�滻
		trasaction.add(R.id.fl_content, new ContentFragment(), TAG_CONTENT);
		trasaction
				.add(R.id.fl_left_menu, new LeftMenuFragment(), TAG_LEFT_MENU);
		trasaction.commit();
	}

	/**
	 * ��ȡ�����Fragment
	 * 
	 * @return LeftMenuFragment
	 */
	public LeftMenuFragment getLeftMenuFragment() {
		FragmentManager fragManager = getSupportFragmentManager();
		LeftMenuFragment fragement = (LeftMenuFragment) fragManager
				.findFragmentByTag(TAG_LEFT_MENU);
		return fragement;
	}
	
	/**
	 * ��ȡ��ҳ��Fragment
	 * 
	 * @return ContentFragment
	 */
	public ContentFragment getContentFragmet() {
		FragmentManager fragManager = getSupportFragmentManager();
		ContentFragment fragement = (ContentFragment) fragManager
				.findFragmentByTag(TAG_CONTENT);
		return fragement;
	}

}
