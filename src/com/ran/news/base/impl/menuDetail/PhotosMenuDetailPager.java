package com.ran.news.base.impl.menuDetail;

import java.util.ArrayList;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ran.news.R;
import com.ran.news.base.BaseMenuDetailPager;
import com.ran.news.domain.PhotoBean;
import com.ran.news.domain.PhotoBean.PhotoNewsData;
import com.ran.news.global.Constants;
import com.ran.news.utils.CacheUtils;

public class PhotosMenuDetailPager extends BaseMenuDetailPager implements
		OnClickListener {
	@ViewInject(R.id.lv_photo)
	private ListView lvList;
	@ViewInject(R.id.gv_photo)
	private GridView gvGrid;
	private ArrayList<PhotoNewsData> mPhotoList;
	private boolean isList = true;// 当前界面状态
	private ImageButton mbtnDisplay;

	public PhotosMenuDetailPager(Activity activity, ImageButton btnDisplay) {
		super(activity);
		// btnDisplay.setVisibility(View.VISIBLE);//新闻中心页面控制显示否
		mbtnDisplay = btnDisplay;
		mbtnDisplay.setOnClickListener(this);
	}

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.pager_menu_detail_photo,
				null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData() {
		String cache = CacheUtils.getCache(Constants.PHOTOS_URL, mActivity);
		if (!TextUtils.isEmpty(cache)) {
			processResult(cache);
		}
		getDataFromServer();
	}

	/**
	 * 从服务器获取数据
	 */
	private void getDataFromServer() {
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.GET, Constants.PHOTOS_URL,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						processResult(responseInfo.result);
						CacheUtils.setCache(Constants.PHOTOS_URL,
								responseInfo.result, mActivity);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						error.printStackTrace();
						Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT)
								.show();
					}
				});
	}

	/**
	 * 解析json数据
	 * 
	 * @param result
	 */
	protected void processResult(String result) {
		Gson gson = new Gson();
		PhotoBean photoBean = gson.fromJson(result, PhotoBean.class);
		mPhotoList = photoBean.data.news;

		lvList.setAdapter(new PhotoAdapter());// listview设置适配器
		gvGrid.setAdapter(new PhotoAdapter());
	}

	class PhotoAdapter extends BaseAdapter {

		private BitmapUtils mBitmapUtils;

		public PhotoAdapter() {
			mBitmapUtils = new BitmapUtils(mActivity);
			mBitmapUtils.configDefaultLoadingImage(R.drawable.news_pic_default);

		}

		@Override
		public int getCount() {
			return mPhotoList.size();
		}

		@Override
		public PhotoNewsData getItem(int position) {
			return mPhotoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(mActivity, R.layout.list_item_photo,
						null);
				holder = new ViewHolder();
				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.tv_title);
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			PhotoNewsData item = mPhotoList.get(position);
			holder.tvTitle.setText(item.title);
			mBitmapUtils.display(holder.ivIcon, item.listimage);
			return convertView;
		}

	}

	static class ViewHolder {
		public TextView tvTitle;
		public ImageView ivIcon;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_display:
			if (isList) {
				isList = false;
				lvList.setVisibility(View.GONE);
				gvGrid.setVisibility(View.VISIBLE);
				mbtnDisplay.setImageResource(R.drawable.icon_pic_list_type);
			} else {
				isList = true;
				lvList.setVisibility(View.VISIBLE);
				gvGrid.setVisibility(View.GONE);
				mbtnDisplay.setImageResource(R.drawable.icon_pic_grid_type);

			}
			break;

		default:
			break;
		}
	}

}
