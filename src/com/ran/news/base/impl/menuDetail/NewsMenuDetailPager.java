package com.ran.news.base.impl.menuDetail;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnChildClick;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ran.news.MainActivity;
import com.ran.news.R;
import com.ran.news.base.BaseMenuDetailPager;
import com.ran.news.domain.NewsMenuData.NewsTabData;
import com.viewpagerindicator.TabPageIndicator;

/**
 * ���Ų˵�����ҳ
 * ViewPagerIndicator��ʹ�ã�
 * 1.����Library��
 * 2.�ڲ����ļ�������TabPageIndicator
 * 3.��ViewPager��ָʾ����������
 * 4.��дPageAdapter��getPageTitle����������ÿ��ҳ��ı���
 * 5.�޸���ʽ
 * @author asus
 *
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager implements
		OnPageChangeListener {
	@ViewInject(R.id.vp_news_detail)
	private ViewPager mViewPager;
	private ArrayList<NewsTabData> mTabList;// ҳǩ�������ݼ���
	private ArrayList<TabDetailPager> mTabPagers;// ҳǩҳ�漯��
	@ViewInject(R.id.indicator)
	private TabPageIndicator mIndicator;// ҳ��ָʾ��

	// �ڹ��췽���н�NewsData������
	public NewsMenuDetailPager(Activity activity,
			ArrayList<NewsTabData> children) {
		super(activity);
		mTabList = children;
	}

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.pager_menu_detail, null);
		ViewUtils.inject(this, view);
		return view;
	}

	public void initData() {
		// �������ϣ���ʼ��12ҳǩ�����������ݾ����������ڹ��췽���н�NewsData��������
		mTabPagers = new ArrayList<TabDetailPager>();
		for (NewsTabData newsTabData : mTabList) {
			TabDetailPager pager = new TabDetailPager(mActivity, newsTabData);
			mTabPagers.add(pager);// ��ҳǩҳ����ӵ�ҳ�漯����
		}
		mViewPager.setAdapter(new NewsMenuAdapter());
		//mViewPager.setOnPageChangeListener(this);// ��ViewPager���������ݺ���ã���ҳ��ָʾ����Viewpager��������
		mIndicator.setViewPager(mViewPager);
		mIndicator.setOnPageChangeListener(this);	//���߰󶨺���Ҫ��ҳ���л��ļ������ø�ָʾ��
	
	}

	class NewsMenuAdapter extends PagerAdapter {
		// ����ҳ��ָʾ���ı���
		@Override
		public CharSequence getPageTitle(int position) {
			return mTabList.get(position).title;
		}

		@Override
		public int getCount() {
			return mTabPagers.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			TabDetailPager pager = mTabPagers.get(position);
			container.addView(pager.mRootView);
			// ��ǰ��pager����ҳǩҳ��
			pager.initData();
			return pager.mRootView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	@Override
	public void onPageScrollStateChanged(int position) {
		
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		
	}

	@Override
	public void onPageSelected(int position) {
		System.out.println("position:" + position);
		//û�н��óɹ���,indicator��Ӱ��,��ҳ��������ø�ָʾ��
		if (position == 0) {// �ڵ�һ��ҳǩ,������������
			// ���������
			setSlidingEnable(true);
		} else {// ����ҳǩ,���ò����, ��֤viewpager�����������һ���
			// �رղ����
			setSlidingEnable(false);
		}
	}

	/***
	 * �Ƿ���ò����
	 * 
	 * @param enable
	 */
	private void setSlidingEnable(boolean enable) {
		// ��BaseFragment���Ի��MainActivity�Ķ���
		MainActivity mainUI = (MainActivity) mActivity;
		SlidingMenu slidingMenu = mainUI.getSlidingMenu();
		if (enable) {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}
	@OnClick(R.id.iv_next_page)
	public void nextPage(View view){
		int currentItem = mViewPager.getCurrentItem();
		currentItem++;
		mViewPager.setCurrentItem(currentItem);
	}

}
