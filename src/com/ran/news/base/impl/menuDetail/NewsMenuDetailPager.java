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
 * 新闻菜单详情页
 * ViewPagerIndicator的使用：
 * 1.引入Library库
 * 2.在布局文件中配置TabPageIndicator
 * 3.将ViewPager和指示器关联起来
 * 4.重写PageAdapter的getPageTitle方法，返回每个页面的标题
 * 5.修改样式
 * @author asus
 *
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager implements
		OnPageChangeListener {
	@ViewInject(R.id.vp_news_detail)
	private ViewPager mViewPager;
	private ArrayList<NewsTabData> mTabList;// 页签网络数据集合
	private ArrayList<TabDetailPager> mTabPagers;// 页签页面集合
	@ViewInject(R.id.indicator)
	private TabPageIndicator mIndicator;// 页面指示器

	// 在构造方法中将NewsData传过来
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
		// 变量集合，初始化12页签（由网络数据决定，所以在构造方法中将NewsData传过来）
		mTabPagers = new ArrayList<TabDetailPager>();
		for (NewsTabData newsTabData : mTabList) {
			TabDetailPager pager = new TabDetailPager(mActivity, newsTabData);
			mTabPagers.add(pager);// 将页签页面添加到页面集合中
		}
		mViewPager.setAdapter(new NewsMenuAdapter());
		//mViewPager.setOnPageChangeListener(this);// 在ViewPager设置完数据后调用，将页面指示器和Viewpager关联起来
		mIndicator.setViewPager(mViewPager);
		mIndicator.setOnPageChangeListener(this);	//二者绑定后，需要将页面切换的监听设置给指示器
	
	}

	class NewsMenuAdapter extends PagerAdapter {
		// 返回页面指示器的标题
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
			// 当前的pager代表页签页面
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
		//没有禁用成功？,indicator的影响,将页面监听设置给指示器
		if (position == 0) {// 在第一个页签,允许侧边栏出现
			// 开启侧边栏
			setSlidingEnable(true);
		} else {// 其他页签,禁用侧边栏, 保证viewpager可以正常向右滑动
			// 关闭侧边栏
			setSlidingEnable(false);
		}
	}

	/***
	 * 是否禁用侧边栏
	 * 
	 * @param enable
	 */
	private void setSlidingEnable(boolean enable) {
		// 从BaseFragment可以获得MainActivity的对象
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
