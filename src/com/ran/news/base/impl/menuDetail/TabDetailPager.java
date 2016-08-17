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
 * ����ҳ���12��ҳǩ����ķ�װ
 * 
 * @author rlp
 *
 */
public class TabDetailPager extends BaseMenuDetailPager {
	//�Զ���viewpager
	@ViewInject(R.id.vp_tab_detail)
	private HorizontalViewPager mViewPager;

	@ViewInject(R.id.lv_tab_detail)
	private RefreshListView mListView;// �����б�

	@ViewInject(R.id.indicator)
	private CirclePageIndicator mIndicator;// ָʾ��

	@ViewInject(R.id.tv_title_top)
	private TextView tvTopNewsTitle;// ͷ�����ű���

	private NewsTabData mTabDta;// ҳǩ����������Ϣ
	private String mUrl;// ���������б��url
	private NewsData newsData;// �����б�����
	private ArrayList<TopNews> mTopNewsList;// ͷ�����ŵ���������
	private TopNewsAdapter mTopNewsAdapter;// ͷ�����ŵ�������

	private ArrayList<News> mNewsList;// �����б��List����
	private NewsAdapter newsAdapter;

	private String moreUrl;// ��һҳ���ݵ�����

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
		// ��ͷ����ҲҪע��
		ViewUtils.inject(this, header);
		// ͷ��������Ϊlistview��ͷ����
		mListView.addHeaderView(header);
		// listview����ˢ�¼���
		mListView.setOnRefreshListener(new OnRefreshListener() {
			// ˢ��
			@Override
			public void onRefresh() {
				getDataFromServer();
			}

			// ������һҳ
			@Override
			public void loadMore() {
				if (moreUrl != null) {
					System.out.println("������һҳ����");
					getMoreDataFromServer();
				} else {
					mListView.onRefreshCompleted(true);
					Toast.makeText(mActivity, "û�и���������", Toast.LENGTH_SHORT)
							.show();
				}

			}

		});
		// �б������¼�(RefreshListView��Ϊ�����Ѿ���ͷ����������ȥ)
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				News news = mNewsList.get(position);
				TextView tvTitle = (TextView) view
						.findViewById(R.id.tv_title_item);
				tvTitle.setTextColor(Color.GRAY);// ��ɫ���óɻ�ɫ
				// ���ҽ��Ѷ�״̬�־û�������
				String readIds = PrefUtils.getString("read_ids", "", mActivity);
				if (!readIds.contains(news.id)) {// ��ӹ��Ͳ������
					readIds = readIds + news.id + ",";
					PrefUtils.putString("read_ids", readIds, mActivity);
				}

				// ��ת����������ҳ��
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
	 * ���ظ�������
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
				processResult(result, false);// ��������
				// System.out.println("��������ɹ�");
				CacheUtils.setCache(mUrl, result, mActivity);
				mListView.onRefreshCompleted(true);// ˢ�����
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				error.printStackTrace();
				Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
				mListView.onRefreshCompleted(false);// ˢ�����
			}
		});
	}

	private void processResult(String result, boolean isMore) {
		Gson gson = new Gson();
		newsData = gson.fromJson(result, NewsData.class);
		if (!TextUtils.isEmpty(newsData.data.more)) {
			moreUrl = Constants.SERVER_URL + newsData.data.more;// ��һҳ���ӵ�ַ
		} else {
			moreUrl = null;// û����һҳ
		}

		if (!isMore) {// ��ʼ��ͷ������
			mTopNewsList = newsData.data.topnews;
			if (mTopNewsList != null) {
				mTopNewsAdapter = new TopNewsAdapter();
				mViewPager.setAdapter(mTopNewsAdapter);

				mIndicator.setViewPager(mViewPager);// ��ָʾ����ViewPager
				mIndicator.setSnap(true);// Բ��������ģʽ(����)
				// mIndicator.onPageSelected(0);// ��СԲ��λ�ù��㣬�����¼��ҳ������ǰ��λ��
				// ��ص����
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
				tvTopNewsTitle.setText(mTopNewsList.get(0).title);// ���Ȼص���������һ��
			}
			// ��ʼ�������б�
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
				handler.sendEmptyMessageAtTime(0, 2000);// �ӳ�2����ִ��
				mViewPager.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							handler.removeCallbacksAndMessages(null);
							// ɾ�����е���Ϣ
							break;
						case MotionEvent.ACTION_CANCEL:
							//ʱ��ȡ����Ҳ��Ҫ2���ֲ�
							handler.sendEmptyMessageDelayed(0, 2000);
							break;
						case MotionEvent.ACTION_UP:
							handler.sendEmptyMessageDelayed(0, 2000);
							// 2���ֲ�
							break;
						default:
							break;
						}

						return false;
					}

				});
			}

		} else {// ���ظ���
			ArrayList<News> moreData = newsData.data.news;
			mNewsList.addAll(moreData);
			newsAdapter.notifyDataSetChanged();
		}

	}

	class TopNewsAdapter extends PagerAdapter {
		BitmapUtils mBitmapUtils;

		public TopNewsAdapter() {
			mBitmapUtils = new BitmapUtils(mActivity);
			// ����Ĭ�ϼ��ص�ͼƬ
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
			ImageView view = new ImageView(mActivity);// �����ͼƬ
			// ��ȡͼƬ����Ȼ������ͼƬ����ͼƬ���ø�ImageView,�����ڴ���������ػ���
			view.setScaleType(ScaleType.FIT_XY);// ��丸����
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
				// �Ѷ�
				holder.tvTitle.setTextColor(Color.GRAY);
			} else {
				// δ��
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
