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
	private int mCurrentPosition;// ��ǰ��ѡ�еĲ˵�λ��

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.fragment_left_menu, null);
		ViewUtils.inject(this, view);
		return view;
	}

	/**
	 * ����������������ݡ���NewsCenterPager��������������
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

				// �����֪ͨ���������л�ҳ��
				setCurrentMenuDetailPager(position);
				toggle();
			}

		});
		mCurrentPosition = 0;// ���õ�ǰҳ��Ϊ����ҳ��
	}

	private void setCurrentMenuDetailPager(int position) {
		// ��ȡ��������ҳ��
		// ��ȡMainActivity---ContentFragment---NewsCenterPager
		MainActivity mainUi = (MainActivity) mActivity;
		ContentFragment contentFragment = mainUi.getContentFragmet();
		NewsCenterPager newsCenterPager = contentFragment.getNewsCenterPager();
		// ����������ҳ���Framelayout��䲼��
		newsCenterPager.setCurrentMenuDetailPager(position);
	}

	/**
	 * չ���������ز����
	 * 
	 * @author asus
	 *
	 */
	public void toggle() {
		MainActivity mainUI = (MainActivity) mActivity;
		SlidingMenu slidingMenu = mainUI.getSlidingMenu();

		slidingMenu.toggle();// ����
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
				// �����ǰҪ���Ƶ�item�պ��Ǳ�ѡ�е�, ��Ҫ����Ϊ��ɫ
				tvMenu.setEnabled(true);
			} else {
				// ����item���ǰ�ɫ
				tvMenu.setEnabled(false);
			}
			return view;
		}

	}
}
