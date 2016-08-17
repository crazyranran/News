package com.ran.news.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

import com.ran.news.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView;

/**
 * 自制下拉刷新的ListView
 * 
 * @author asus
 *
 */
public class RefreshListView extends ListView implements
		AbsListView.OnScrollListener,
		android.widget.AdapterView.OnItemClickListener {

	private static final int STATE_PUUL_TO_REFRESH = 1;// 下拉刷新
	private static final int STATE_RELISE_TO_REFRESH = 2;// 松开刷新
	private static final int STATE_REFRESHING = 3;// 正在刷新

	private int measuredHeight;// 头布局高度
	private int startY = -1;
	private View mHeaderView;// 头布局
	private View mFooterView;// 脚布局

	private OnRefreshListener mListener;// 回调接口
	private int footViewHeight;// 脚布局高度

	private int mCurrentState = STATE_PUUL_TO_REFRESH;// 默认是下拉刷新的状态

	private TextView tvTitle;// 状态
	private ImageView ivArrow;// 箭头
	private TextView tvTime;// 时间
	private ProgressBar pbRefresh;// 进度条

	private RotateAnimation animUp;// 箭头向上动画
	private RotateAnimation animDown;// 箭头向下动画

	private boolean isLoadingMore;// 标记是否正在加载更多

	public RefreshListView(Context context) {
		super(context);
		initHeaderView();
		initFooterView();
	}

	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initHeaderView();
		initFooterView();
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeaderView();
		initFooterView();
	}

	public void initHeaderView() {
		mHeaderView = View.inflate(getContext(), R.layout.list_header_refresh,
				null);
		this.addHeaderView(mHeaderView);// 添加头布局

		// 隐藏头布局，paddingTop=-布局高度，就会向上走，隐藏起来
		// 拿不到高度，还没有绘制完
		// 绘制之前就要获取高度，手动测量

		mHeaderView.measure(0, 0);
		measuredHeight = mHeaderView.getMeasuredHeight();// 测量的头布局的高度
		mHeaderView.setPadding(0, -measuredHeight, 0, 0);

		tvTitle = (TextView) mHeaderView.findViewById(R.id.tv_title_refresh);
		ivArrow = (ImageView) mHeaderView.findViewById(R.id.iv_arrow);
		tvTime = (TextView) mHeaderView.findViewById(R.id.tv_time_refresh);
		pbRefresh = (ProgressBar) mHeaderView.findViewById(R.id.pb_refresh);
		initAnim();// 初始化动画

		setCurrentTime();// 设置当前时间
	}

	/**
	 * 初始化脚布局
	 */
	private void initFooterView() {
		mFooterView = View.inflate(getContext(), R.layout.list_footer_refresh,
				null);
		this.addFooterView(mFooterView);
		mFooterView.measure(0, 0);
		footViewHeight = mFooterView.getMeasuredHeight();
		mFooterView.setPadding(0, -footViewHeight, 0, 0);// 隐藏脚布局
		// 设置滑动监听
		this.setOnScrollListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			// 如果按住头条新闻滑动，Listview拿不到down事件，重新获取
			if (startY == -1) {
				startY = (int) ev.getY();
			}
			// 状态是正在刷新，不做任何处理，让listview处理
			if (mCurrentState == STATE_REFRESHING) {
				break;
			}
			int endY = (int) ev.getY();
			int dy = endY - startY;

			if (dy > 0 && getFirstVisiblePosition() == 0) {
				// 向下滑动并且显示的是第一位置才改变padding，显示下拉刷新
				int paddingTop = dy - measuredHeight;
				// 根据padding的值切换状态
				if (paddingTop >= 0 && mCurrentState != STATE_RELISE_TO_REFRESH) {
					// 松开刷新
					mCurrentState = STATE_RELISE_TO_REFRESH;
					refreshState();
				} else if (paddingTop < 0
						&& mCurrentState != STATE_PUUL_TO_REFRESH) {
					// 下拉刷新
					mCurrentState = STATE_PUUL_TO_REFRESH;
					refreshState();
				}
				mHeaderView.setPadding(0, paddingTop, 0, 0);
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			startY = -1;// 起始坐标归零
			if (mCurrentState == STATE_RELISE_TO_REFRESH) {
				// 如果当前的状态是松开刷新，那就切换成正在刷新，并且显示头布局
				mCurrentState = STATE_REFRESHING;
				mHeaderView.setPadding(0, 0, 0, 0);
				refreshState();
				// 刷新回调
				if (mListener != null) {
					mListener.onRefresh();
				}
			} else if (mCurrentState == STATE_PUUL_TO_REFRESH) {
				// 如果当前的状态就是下拉刷新，隐藏头布局
				mHeaderView.setPadding(0, -measuredHeight, 0, 0);
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 根据状态刷新数据
	 */
	private void refreshState() {
		switch (mCurrentState) {
		// 下拉刷新状态
		case STATE_PUUL_TO_REFRESH:
			tvTitle.setText("下拉刷新");
			// 箭头向上移动动画
			ivArrow.startAnimation(animDown);// 箭头向下移动
			pbRefresh.setVisibility(View.INVISIBLE);// 隐藏进度条
			break;
		// 松开刷新状态
		case STATE_RELISE_TO_REFRESH:
			tvTitle.setText("松开刷新");
			ivArrow.startAnimation(animUp);// 箭头向上移动
			pbRefresh.setVisibility(View.INVISIBLE);// 隐藏进度条
			break;
		// 正在刷新状态
		case STATE_REFRESHING:
			tvTitle.setText("正在刷新...");
			pbRefresh.setVisibility(View.VISIBLE);// 显示进度条
			ivArrow.clearAnimation();// 清除动画后才能隐藏控件
			ivArrow.setVisibility(View.INVISIBLE);// 隐藏箭头
			break;
		default:
			break;
		}
	}

	public void initAnim() {
		animUp = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);// 箭头向上动画
		animUp.setDuration(500);
		animUp.setFillAfter(true);

		animDown = new RotateAnimation(-180, -0, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animDown.setDuration(500);
		animDown.setFillAfter(true);// 保持状态
	}

	public void setOnRefreshListener(OnRefreshListener listener) {
		mListener = listener;
	}

	public void setCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		String time = format.format(new Date());
		tvTime.setText(time);
	}

	// 刷新完成后回调这个方法
	public void onRefreshCompleted(boolean success) {
		if (!isLoadingMore) {
			// 隐藏头布局
			mHeaderView.setPadding(0, -measuredHeight, 0, 0);
			mCurrentState = STATE_PUUL_TO_REFRESH;
			pbRefresh.setVisibility(View.INVISIBLE);
			ivArrow.setVisibility(View.INVISIBLE);
			tvTitle.setText("下拉刷新");
			// 刷新成功，设置时间
			if (success) {
				setCurrentTime();
			}
		} else {
			// 隐藏脚布局
			mFooterView.setPadding(0, -footViewHeight, 0, 0);
			isLoadingMore = false;
		}
	}

	public interface OnRefreshListener {
		// 下拉刷新回调
		public void onRefresh();

		public void loadMore();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {// 最后一个idle位置和集合大小相等就是最后一个idle
			int lastVisiblePosition = getLastVisiblePosition();// 当前显示的最后一个位置
			if (lastVisiblePosition >= getCount() - 1 && !isLoadingMore) {

				isLoadingMore = true;
				mFooterView.setPadding(0, 0, 0, 0);// 加载更多。。。。显示脚布局
				// 设置当前要展示的item的位置，直接显示加载更多这个item的位置显示
				setSelection(getCount() - 1);
				if (mListener != null) {
					mListener.loadMore();
				}
			}

		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}

	private OnItemClickListener mItemOnClickListener;

	// 重写点击事件的)方法
	@Override
	public void setOnItemClickListener(
			android.widget.AdapterView.OnItemClickListener listener) {
		mItemOnClickListener = listener;
		super .setOnItemClickListener(this);// 将点击事件设置给RefreshListView
	}

	// 将position减去头布局数量
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mItemOnClickListener != null) {
			mItemOnClickListener.onItemClick(parent, view, position
					- getHeaderViewsCount(), id);
		}
	}

}
