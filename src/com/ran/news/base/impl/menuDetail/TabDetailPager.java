package com.ran.news.base.impl.menuDetail;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
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
import com.ran.news.NewsDetailActivity;
import com.ran.news.R;
import com.ran.news.base.BaseMenuDetailPager;
import com.ran.news.domain.NewsData;
import com.ran.news.domain.NewsData.News;
import com.ran.news.domain.NewsData.TopNews;
import com.ran.news.domain.NewsMenuData.NewsTabData;
import com.ran.news.global.Constants;
import com.ran.news.utils.CacheUtils;
import com.ran.news.utils.PrefUtils;
import com.ran.news.view.HorizontalViewPager;
import com.ran.news.view.RefreshListView;
import com.ran.news.view.RefreshListView.OnRefreshListener;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * 详情页面的12个页签对象的封装
 * 
 * @author rlp
 *
 */
public class TabDetailPager extends BaseMenuDetailPager {
	//自定义viewpager
	@ViewInject(R.id.vp_tab_detail)
	private HorizontalViewPager mViewPager;

	@ViewInject(R.id.lv_tab_detail)
	private RefreshListView mListView;// 新闻列表

	@ViewInject(R.id.indicator)
	private CirclePageIndicator mIndicator;// 指示器

	@ViewInject(R.id.tv_title_top)
	private TextView tvTopNewsTitle;// 头条新闻标题

	private NewsTabData mTabDta;// 页签分类网络信息
	private String mUrl;// 加载新闻列表的url
	private NewsData newsData;// 新闻列表数据
	private ArrayList<TopNews> mTopNewsList;// 头条新闻的网络数据
	private TopNewsAdapter mTopNewsAdapter;// 头条新闻的适配器

	private ArrayList<News> mNewsList;// 新闻列表的List集合
	private NewsAdapter newsAdapter;

	private String moreUrl;// 下一页数据的链接

	private Handler handler = null;

	public TabDetailPager(Activity activity, NewsTabData tabData) {
		super(activity);
		mTabDta = tabData;
		mUrl = Constants.SERVER_URL + mTabDta.url;
	}

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.pager_tab_detail, null);
		ViewUtils.inject(this, view);
		View header = View.inflate(mActivity, R.layout.list_header_topnews,
				null);
		// 将头布局也要注入
		ViewUtils.inject(this, header);
		// 头条新闻作为listview的头布局
		mListView.addHeaderView(header);
		// listview设置刷新监听
		mListView.setOnRefreshListener(new OnRefreshListener() {
			// 刷新
			@Override
			public void onRefresh() {
				getDataFromServer();
			}

			// 加载下一页
			@Override
			public void loadMore() {
				if (moreUrl != null) {
					System.out.println("加载下一页数据");
					getMoreDataFromServer();
				} else {
					mListView.onRefreshCompleted(true);
					Toast.makeText(mActivity, "没有更多数据了", Toast.LENGTH_SHORT)
							.show();
				}

			}

		});
		// 列表项点击事件(RefreshListView作为代理已经将头布局数量减去)
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				News news = mNewsList.get(position);
				TextView tvTitle = (TextView) view
						.findViewById(R.id.tv_title_item);
				tvTitle.setTextColor(Color.GRAY);// 颜色设置成灰色
				// 并且将已读状态持久化到本地
				String readIds = PrefUtils.getString("read_ids", "", mActivity);
				if (!readIds.contains(news.id)) {// 添加过就不添加了
					readIds = readIds + news.id + ",";
					PrefUtils.putString("read_ids", readIds, mActivity);
				}

				// 跳转到新闻详情页面
				Intent intent = new Intent(mActivity, NewsDetailActivity.class);
				intent.putExtra("url", news.url);
				mActivity.startActivity(intent);

			}
		});
		return view;
	}

	@Override
	public void initData() {
		String cache = CacheUtils.getCache(mUrl, mActivity);
		if (!TextUtils.isEmpty(cache)) {
			processResult(cache, false);
		}
		getDataFromServer();
	}

	/**
	 * 加载更多数据
	 */
	private void getMoreDataFromServer() {
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.GET, moreUrl, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = responseInfo.result;
				processResult(result, true);
				mListView.onRefreshCompleted(true);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				mListView.onRefreshCompleted(false);
			}
		});
	}

	private void getDataFromServer() {
		HttpUtils httpUtils = new HttpUtils();

		httpUtils.send(HttpMethod.GET, mUrl, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = responseInfo.result;
				processResult(result, false);// 解析数据
				// System.out.println("访问网络成功");
				CacheUtils.setCache(mUrl, result, mActivity);
				mListView.onRefreshCompleted(true);// 刷新完成
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				error.printStackTrace();
				Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
				mListView.onRefreshCompleted(false);// 刷新完成
			}
		});
	}

	private void processResult(String result, boolean isMore) {
		Gson gson = new Gson();
		newsData = gson.fromJson(result, NewsData.class);
		if (!TextUtils.isEmpty(newsData.data.more)) {
			moreUrl = Constants.SERVER_URL + newsData.data.more;// 下一页链接地址
		} else {
			moreUrl = null;// 没有下一页
		}

		if (!isMore) {// 初始化头条新闻
			mTopNewsList = newsData.data.topnews;
			if (mTopNewsList != null) {
				mTopNewsAdapter = new TopNewsAdapter();
				mViewPager.setAdapter(mTopNewsAdapter);

				mIndicator.setViewPager(mViewPager);// 绑定指示器和ViewPager
				mIndicator.setSnap(true);// 圆点变成跳动模式(快照)
				// mIndicator.onPageSelected(0);// 将小圆点位置归零，否则记录着页面销毁前的位置
				// 会回调多次
				mIndicator.setOnPageChangeListener(new OnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						TopNews topNew = mTopNewsList.get(position);
						tvTopNewsTitle.setText(topNew.title);
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {

					}

					@Override
					public void onPageScrollStateChanged(int arg0) {

					}
				});

				mIndicator.onPageSelected(0);
				tvTopNewsTitle.setText(mTopNewsList.get(0).title);// 不等回调重新设置一下
			}
			// 初始化新闻列表
			mNewsList = newsData.data.news;
			if (mNewsList != null) {
				newsAdapter = new NewsAdapter();
				mListView.setAdapter(newsAdapter);
			}
			if (handler == null) {
				handler = new Handler() {
					public void handleMessage(android.os.Message msg) {
						int currentItem = mViewPager.getCurrentItem();
						if (currentItem < mTopNewsList.size() - 1) {
							currentItem++;
						} else {
							currentItem = 0;
						}
						mViewPager.setCurrentItem(currentItem);
						handler.sendEmptyMessageDelayed(0, 2000);
					};
				};
				handler.sendEmptyMessageAtTime(0, 2000);// 延迟2秒钟执行
				mViewPager.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							handler.removeCallbacksAndMessages(null);
							// 删除所有的消息
							break;
						case MotionEvent.ACTION_CANCEL:
							//时间取消，也需要2秒轮播
							handler.sendEmptyMessageDelayed(0, 2000);
							break;
						case MotionEvent.ACTION_UP:
							handler.sendEmptyMessageDelayed(0, 2000);
							// 2秒轮播
							break;
						default:
							break;
						}

						return false;
					}

				});
			}

		} else {// 加载更多
			ArrayList<News> moreData = newsData.data.news;
			mNewsList.addAll(moreData);
			newsAdapter.notifyDataSetChanged();
		}

	}

	class TopNewsAdapter extends PagerAdapter {
		BitmapUtils mBitmapUtils;

		public TopNewsAdapter() {
			mBitmapUtils = new BitmapUtils(mActivity);
			// 设置默认加载的图片
			mBitmapUtils
					.configDefaultLoadingImage(R.drawable.pic_item_list_default);
		}

		@Override
		public int getCount() {
			return mTopNewsList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView view = new ImageView(mActivity);// 网络的图片
			// 获取图片链接然后下载图片。将图片设置给ImageView,考虑内存溢出，本地缓存
			view.setScaleType(ScaleType.FIT_XY);// 填充父窗体
			mBitmapUtils.display(view, mTopNewsList.get(position).topimage);
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	class NewsAdapter extends BaseAdapter {
		public BitmapUtils bitmapUtils;
		private ViewHolder holder;

		public NewsAdapter() {
			bitmapUtils = new BitmapUtils(mActivity);
			bitmapUtils
					.configDefaultLoadingImage(R.drawable.pic_item_list_default);
		}

		@Override
		public int getCount() {
			return mNewsList.size();
		}

		@Override
		public News getItem(int position) {
			return mNewsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(mActivity, R.layout.list_item_news,
						null);

				holder = new ViewHolder();
				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.tv_title_item);
				holder.tvDate = (TextView) convertView
						.findViewById(R.id.tv_date_item);
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			News news = getItem(position);
			holder.tvTitle.setText(news.title);
			holder.tvDate.setText(news.pubdate);
			bitmapUtils.display(holder.ivIcon, news.listimage);

			String readIds = PrefUtils.getString("read_ids", "", mActivity);
			if (readIds.contains(news.id)) {
				// 已读
				holder.tvTitle.setTextColor(Color.GRAY);
			} else {
				// 未读
				holder.tvTitle.setTextColor(Color.BLACK);
			}
			return convertView;
		}

	}

	static class ViewHolder {
		public TextView tvTitle;
		public TextView tvDate;
		public ImageView ivIcon;
	}
}
