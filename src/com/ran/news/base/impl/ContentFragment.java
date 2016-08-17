package com.ran.news.base.impl;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ran.news.MainActivity;
import com.ran.news.R;
import com.ran.news.base.BaseFragment;
import com.ran.news.base.BasePager;

/**
 * ��ҳ��Fragment
 * 
 * @author rlp
 *
 */
public class ContentFragment extends BaseFragment {
	@ViewInject(R.id.vp_content)
	private ViewPager mViewPager;
	private List<BasePager> mPagers;
	@ViewInject(R.id.content_group)
	private RadioGroup contentGroup;

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.fragment_content, null);
		ViewUtils.inject(this, view);// ע��view���¼�
		return view;
	}

	/**
	 * ��ʼ��5��ҳ��
	 * 
	 * @author asus
	 *
	 */
	@Override
	public void initData() {
		mPagers = new ArrayList<BasePager>();
		mPagers.add(new HomePager(mActivity));
		mPagers.add(new NewsCenterPager(mActivity));
		mPagers.add(new SmartServicePager(mActivity));
		mPagers.add(new GovAffairsPager(mActivity));
		mPagers.add(new SettingPager(mActivity));

		mViewPager.setAdapter(new ContentAdapter());
		contentGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_home:
					mViewPager.setCurrentItem(0, false);
					mPagers.get(0).initData();
					setSlidingEnable(false);
					break;
				case R.id.rb_news:
					mViewPager.setCurrentItem(1, false);
					mPagers.get(1).initData();
					setSlidingEnable(true);
					break;
				case R.id.rb_smart:
					mViewPager.setCurrentItem(2, false);
					mPagers.get(2).initData();
					setSlidingEnable(true);
					break;
				case R.id.rb_gov:
					mViewPager.setCurrentItem(3, false);
					mPagers.get(3).initData();
					setSlidingEnable(true);
					break;
				case R.id.rb_setting:
					mViewPager.setCurrentItem(4, false);
					mPagers.get(4).initData();
					setSlidingEnable(false);
					break;

				default:
					break;
				}

			}
		});
		mPagers.get(0).initData();// ��ҳ��һ�γ�ʼ��
		setSlidingEnable(false);
	}

	/**
	 * ������
	 * 
	 * @author asus
	 *
	 */
	class ContentAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mPagers.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			BasePager pager = mPagers.get(position);
			container.addView(pager.mRootview);// ��ҳ�沼����ӵ�������
			// pager.initData();
			return pager.mRootview;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
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
	/**
	 * ��ȡ��������ҳ��
	 * @return
	 */
	public NewsCenterPager getNewsCenterPager(){
		return (NewsCenterPager) mPagers.get(1);
	}

}
