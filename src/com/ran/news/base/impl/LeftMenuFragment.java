package com.ran.news.base.impl;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ran.news.MainActivity;
import com.ran.news.R;
import com.ran.news.base.BaseFragment;
import com.ran.news.domain.NewsMenuData;
import com.ran.news.domain.NewsMenuData.NewsData;

public class LeftMenuFragment extends BaseFragment {
	@ViewInject(R.id.lv_list)
	private ListView lvList;
	private ArrayList<NewsData> mMenuList;
	private MenuAdapter mAdapter;
	private int mCurrentPosition;// 当前被选中的菜单位置

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.fragment_left_menu, null);
		ViewUtils.inject(this, view);
		return view;
	}

	/**
	 * 设置来自网络的数据。由NewsCenterPager调用来传递数据
	 * 
	 * @param data
	 */
	public void setData(ArrayList<NewsData> data) {
		mMenuList = data;
		mAdapter = new MenuAdapter();
		lvList.setAdapter(mAdapter);

		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mCurrentPosition = position;
				mAdapter.notifyDataSetChanged();

				// 侧边栏通知新闻中心切换页面
				setCurrentMenuDetailPager(position);
				toggle();
			}

		});
		mCurrentPosition = 0;// 重置当前页面为新闻页面
	}

	private void setCurrentMenuDetailPager(int position) {
		// 获取新闻中心页面
		// 获取MainActivity---ContentFragment---NewsCenterPager
		MainActivity mainUi = (MainActivity) mActivity;
		ContentFragment contentFragment = mainUi.getContentFragmet();
		NewsCenterPager newsCenterPager = contentFragment.getNewsCenterPager();
		// 给新闻中心页面的Framelayout填充布局
		newsCenterPager.setCurrentMenuDetailPager(position);
	}

	/**
	 * 展开或者隐藏侧边栏
	 * 
	 * @author asus
	 *
	 */
	public void toggle() {
		MainActivity mainUI = (MainActivity) mActivity;
		SlidingMenu slidingMenu = mainUI.getSlidingMenu();

		slidingMenu.toggle();// 开关
	}

	class MenuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mMenuList.size();
		}

		@Override
		public NewsData getItem(int position) {
			return mMenuList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(mActivity, R.layout.list_item_left_menu,
					null);
			TextView tvMenu = (TextView) view.findViewById(R.id.tv_menu);

			NewsData data = getItem(position);
			tvMenu.setText(data.title);

			if (mCurrentPosition == position) {
				// 如果当前要绘制的item刚好是被选中的, 需要设置为红色
				tvMenu.setEnabled(true);
			} else {
				// 其他item都是白色
				tvMenu.setEnabled(false);
			}
			return view;
		}

	}
}
