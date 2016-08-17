package com.ran.news;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * 新闻详情页面
 * 
 * @author asus
 *
 */
public class NewsDetailActivity extends Activity implements OnClickListener {
	@ViewInject(R.id.ll_controller)
	LinearLayout llController;// 控制分析和字体大小的ll
	@ViewInject(R.id.btn_back)
	// 返回按钮
	private ImageButton btnBack;
	@ViewInject(R.id.img_menu)
	// 菜单按钮
	private ImageButton btnMenu;
	@ViewInject(R.id.btn_share)
	// 分享按钮
	private ImageButton btnShare;
	@ViewInject(R.id.btn_textsize)
	// 字体大小按钮
	private ImageButton btnTextSize;
	@ViewInject(R.id.wv_webview)
	private WebView mWebView;

	@ViewInject(R.id.pb_loading)
	private ProgressBar pbLoading;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_news_detail);
		ViewUtils.inject(this);

		btnBack.setVisibility(View.VISIBLE);
		btnMenu.setVisibility(View.INVISIBLE);
		llController.setVisibility(View.VISIBLE);

		btnBack.setOnClickListener(this);
		btnShare.setOnClickListener(this);
		btnTextSize.setOnClickListener(this);

		url = getIntent().getStringExtra("url");

		mWebView.loadUrl(url);// 加载网页

		WebSettings settings = mWebView.getSettings();
		settings.setBuiltInZoomControls(true);// 显示放大缩小
		settings.setUseWideViewPort(true);// 显示双击缩放
		settings.setJavaScriptEnabled(true);// 打开JS功能

		// 给WebView设置监听
		mWebView.setWebViewClient(new WebViewClient() {
			// 网页加载开始
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				System.out.println("网页加载开始");
				pbLoading.setVisibility(View.VISIBLE);

			}

			// 网页加载结束
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				System.out.println("网页加载结束");
				// pbLoading.setVisibility(View.INVISIBLE);
			}

			// 网页跳转调用这个方法
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				System.out.println("网页跳转");
				view.loadUrl(url);// 强制在当前页面加载，不跳转浏览器
				return true;
			}
		});

		mWebView.setWebChromeClient(new WebChromeClient() {
			// 加载进度的回调
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress >= 80) {
					pbLoading.setVisibility(View.INVISIBLE);
				}
				System.out.println("加载进度" + newProgress);
			}

			// 获取网页的标题
			@Override
			public void onReceivedTitle(WebView view, String title) {
				// TODO Auto-generated method stub
				super.onReceivedTitle(view, title);
			}

			// 获取网页的图标
			@Override
			public void onReceivedIcon(WebView view, Bitmap icon) {
				// TODO Auto-generated method stub
				super.onReceivedIcon(view, icon);
			}
		});
	}

	// E/Web Console(2403): Uncaught Code.PhotoSwipe.createInstance: No images
	// to passed. at
	// http://10.0.2.2:8080/zhbj/10007/724D6A55496A11726628_files/code.photoswipe-3.0.5.min.js:133

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_share:
			showShare();
			break;
		case R.id.btn_textsize:
			showChooseDialog();
			break;
		default:
			break;
		}
	}

	private int mChooseItem;// 用户选择的选项
	private int mSelectItem = 2;// 最终选择的选项

	private void showChooseDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("字体设置");
		String[] items = new String[] { "超大字体", "大号字体", "正常字体", "小号字体", "超小号字体" };
		builder.setSingleChoiceItems(items, mSelectItem,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mChooseItem = which;

					}
				});

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			WebSettings settings = mWebView.getSettings();

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (mChooseItem) {
				case 0:
					settings.setTextSize(TextSize.LARGEST);
					break;
				case 1:
					settings.setTextSize(TextSize.LARGER);
					break;
				case 2:
					settings.setTextSize(TextSize.NORMAL);
					break;
				case 3:
					settings.setTextSize(TextSize.SMALLER);
					break;
				case 4:
					settings.setTextSize(TextSize.SMALLEST);
					break;

				default:
					break;
				}
				mSelectItem = which;
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	/**
	 * 确保SDcard下面存在此张图片
	 */
	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();

		// 修改主题
		oks.setTheme(OnekeyShareTheme.SKYBLUE);

		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("我是分享文本");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");

		// 启动分享GUI
		oks.show(this);
	}

}
