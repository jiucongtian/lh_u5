package com.longhuapuxin.u5;

import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.entity.ResponseReplyFeeling;
import com.longhuapuxin.u5.pullable.PullToRefreshLayout;
import com.longhuapuxin.u5.pullable.PullToRefreshLayout.OnRefreshListener;
import com.squareup.okhttp.Request;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FeelingActivity extends BaseActivity {
	private WebView webView1;
	private int mOwnerType = 1;
	private int mFeelingId = 0;
	private String mUrl = "";
	private PullToRefreshLayout pullToRefreshLayout;
	private TextView txtContent;
	private final int MSG_RefreshFinsihed = 1;
	private final int MSG_LoadFinoshed = 2;
	private final int MSG_NewFeeling = 3;
	private final int MSG_ShowReply = 4;
	private final int MSG_HideReply = 5;
	private final int MSG_ReplyFinished = 6;
	private final int MSG_ShowGallery = 7;
	private final int MSG_ShowFeeling = 8;

	private int mReplyId = 0;
	private RelativeLayout inputLayout;
	private Button btnSend;
	private InputMethodManager imm;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RefreshFinsihed:
				pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
				break;
			case MSG_LoadFinoshed:
				pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
				break;
			case MSG_NewFeeling:
				Intent intent = new Intent(FeelingActivity.this,
						NewFeelingActivity.class);
				intent.putExtra("OwnerType", mOwnerType);
				startActivityForResult(intent, 102);
				break;
			case MSG_ShowReply:
				inputLayout.setVisibility(View.VISIBLE);
				txtContent.requestFocus();
				imm.showSoftInput(txtContent, InputMethodManager.SHOW_FORCED);
				break;

			case MSG_HideReply:
				inputLayout.setVisibility(View.INVISIBLE);
				imm.hideSoftInputFromWindow(txtContent.getWindowToken(), 0);
				break;
			case MSG_ReplyFinished:
				String txt = txtContent.getText().toString()
						.replace("\"", "\\\"");
				webView1.loadUrl("javascript:ReplyFinish(" + mFeelingId + ","
						+ mReplyId + "," + msg.arg1 + ",\"" + txt + "\")");
				txtContent.setText("");
				break;
			case MSG_ShowGallery:
				String[] urls = (String[]) msg.obj;
				int position = msg.arg1;
				FeelingActivity.this.ShowGalleryActivity(urls, position);
				break;
			case MSG_ShowFeeling:
				Intent feelingintent = new Intent(FeelingActivity.this,
						FeelingActivity.class);
				feelingintent.putExtra("FeelingId", msg.arg1);
				feelingintent.putExtra("OwnerType", mOwnerType);
				feelingintent.putExtra("Url", msg.obj.toString());
				startActivityForResult(feelingintent, 0);
				break;
			}

		}
	};
	@SuppressLint("HandlerLeak")
	private OnRefreshListener myListener = new OnRefreshListener() {

		@Override
		public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
			// TODO Auto-generated method stub
			webView1.loadUrl("javascript:Refresh()");

		}

		@Override
		public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
			// TODO Auto-generated method stub
			webView1.loadUrl("javascript:LoadMore()");
		}

	};

	@SuppressLint("HandlerLeak")
	@JavascriptInterface
	public void RefreshFinished() {
		handler.sendEmptyMessageDelayed(MSG_RefreshFinsihed, 10);
	}

	@SuppressLint("HandlerLeak")
	@JavascriptInterface
	public void LoadMoreFinished() {
		handler.sendEmptyMessageDelayed(MSG_LoadFinoshed, 10);
	}

	@SuppressLint("HandlerLeak")
	@JavascriptInterface
	public void NewFeeling() {
		handler.sendEmptyMessageDelayed(MSG_LoadFinoshed, 10);
	}

	@SuppressLint("HandlerLeak")
	@JavascriptInterface
	public void ShowReply(int feelingId, int replyId) {
		this.mFeelingId = feelingId;
		this.mReplyId = replyId;
		handler.sendEmptyMessageDelayed(MSG_ShowReply, 10);
	}

	@SuppressLint("HandlerLeak")
	@JavascriptInterface
	public void HideReply() {
		handler.sendEmptyMessageDelayed(MSG_HideReply, 10);
	}

	@SuppressLint("HandlerLeak")
	@JavascriptInterface
	public void ShowGallery(String[] urls, int pos) {
		Message msg = new Message();
		msg.arg1 = pos;
		msg.obj = urls;
		msg.what = MSG_ShowGallery;
		handler.sendMessageDelayed(msg, 10);

	}

	@JavascriptInterface
	public void PreventPullUp() {
		pullToRefreshLayout.PreventPullUp();
	}

	@JavascriptInterface
	public void AllowPull() {
		pullToRefreshLayout.AllowPull();
	}

	@SuppressLint("HandlerLeak")
	@JavascriptInterface
	public void ShowFeeling(int feelingId, String url) {

		Message msg = new Message();
		msg.obj = url;
		msg.arg1 = feelingId;
		msg.what = MSG_ShowFeeling;
		handler.sendMessageDelayed(msg, 10);
	}

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feeling);

		Intent intent = getIntent();
		if (intent != null) {

			mOwnerType = intent.getIntExtra("OwnerType", 0);
			mFeelingId = intent.getIntExtra("FeelingId", 0);
			mUrl = intent.getStringExtra("Url");

		}
		switch (mOwnerType) {
		case 1:
			initHeader(R.string.personalFeeling);
			break;
		case 2:
			initHeader(R.string.shopFeeling);
			break;
		case 3:
			initHeader(R.string.myFeeling);
			break;
		case 4:
			initHeader(R.string.feelingAboutMe);
			break;

		}
		if (mOwnerType == 1 || mOwnerType == 3) {
			this.enableRightImage(R.drawable.space_camera_icon,
					new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							handler.sendEmptyMessageDelayed(MSG_NewFeeling, 10);
						}

					});
		}
		imm = (InputMethodManager) getSystemService(FeelingActivity.this.INPUT_METHOD_SERVICE);

		pullToRefreshLayout = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
		pullToRefreshLayout.setOnRefreshListener(myListener);
		inputLayout = (RelativeLayout) findViewById(R.id.layoutInput);
		txtContent = (TextView) findViewById(R.id.txtContent);
		btnSend = (Button) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SaveFeeling();
			}

		});

		webView1 = (WebView) findViewById(R.id.content_view);
		WebSettings webSettings = webView1.getSettings();
		webSettings.setDefaultTextEncodingName("UTF-8");// 设置字符编码
		webView1.setScrollBarStyle(0);

		webView1.addJavascriptInterface(this, "AndoridInterface");

		// User settings

		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setUseWideViewPort(true);// 关键点

		webSettings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);

		webSettings.setDisplayZoomControls(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setAllowFileAccess(true); // 允许访问文件
		webSettings.setBuiltInZoomControls(false); // 设置显示缩放按钮
		webSettings.setSupportZoom(false); // 支持缩放
		// webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

		webSettings.setLoadWithOverviewMode(true);

		webView1.requestFocusFromTouch();
		webView1.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
				if (progress == 100) {
					// handler.sendEmptyMessage(1);// 如果全部载入,隐藏进度对话框
					WaitDialog.instance().hideWaitNote();
				}
				super.onProgressChanged(view, progress);
			}

		});
		webView1.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

		});

		// clearCacheFolder(getCacheDir(), System.currentTimeMillis());
		String url = Settings.instance().getApiUrl() + "/feeling/index?UserId="
				+ Settings.instance().getUserId() + "&Token="
				+ Settings.instance().getToken() + "&NickName="
				+ Settings.instance().User.getNickName() + "&FeelingId="
				+ String.valueOf(mFeelingId) + "&OwnerType=" + mOwnerType;
		if (mOwnerType == 3) {
			url = Settings.instance().getApiUrl()
					+ "/feeling/myfeelings?UserId="
					+ Settings.instance().getUserId() + "&Token="
					+ Settings.instance().getToken() + "&NickName="
					+ Settings.instance().User.getNickName() + "&FeelingId="
					+ String.valueOf(mFeelingId) + "&OwnerType=" + mOwnerType;
		}
		if (mOwnerType == 4) {
			url = Settings.instance().getApiUrl() + "/feeling/aboutme?UserId="
					+ Settings.instance().getUserId() + "&Token="
					+ Settings.instance().getToken() + "&NickName="
					+ Settings.instance().User.getNickName() + "&FeelingId="
					+ String.valueOf(mFeelingId) + "&OwnerType=" + mOwnerType+"#/feeling/aboutme";
		}
		if (mUrl != null && mUrl.length() > 0) {
			url = Settings.instance().getApiUrl() + mUrl + "?UserId="
					+ Settings.instance().getUserId() + "&Token="
					+ Settings.instance().getToken() + "&NickName="
					+ Settings.instance().User.getNickName() + "&FeelingId="
					+ String.valueOf(mFeelingId) + "&OwnerType=" + mOwnerType + "#/feeling/one";
		}
		WaitDialog.instance().showWaitNote(this);
		if (isNetworkAvailable()) {
			webView1.loadUrl(url);
		} else {
			webView1.loadData("当前网络不可用，请稍后再试", "text/html; charset=UTF-8", null);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// 根据上面发送过去的请求吗来区别
		if (requestCode == 102) {
			switch (resultCode) {
			case 1:
				String FeelingId = data.getStringExtra("FeelingId");
				webView1.loadUrl("javascript:Refresh()");
				break;
			case 0:

				break;
			default:
				break;
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void SaveFeeling() {
		if (txtContent.getText().toString().length() <= 0) {
			handler.sendEmptyMessage(MSG_HideReply);
			return;
		}
		String ids = "";
		WaitDialog.instance().showWaitNote(this);
		Param[] params = new Param[7];
		params[0] = new Param("UserId", Settings.instance().getUserId());
		params[1] = new Param("Token", Settings.instance().getToken());
		params[2] = new Param("Content", txtContent.getText().toString());
		params[3] = new Param("OwnerType", "1");
		params[4] = new Param("FeelingId", String.valueOf(mFeelingId));
		params[5] = new Param("ReplyId", String.valueOf(mReplyId));
		params[6] = new Param("PhotoIds", ids);

		OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
				+ "/feeling/ReplyFeeling", params,
				new OkHttpClientManager.ResultCallback<ResponseReplyFeeling>() {

					@Override
					public void onError(Request request, Exception e) {
						Logger.info("editLabel.onError" + e.toString());
						WaitDialog.instance().hideWaitNote();
						handler.sendEmptyMessage(MSG_HideReply);
					}

					@Override
					public void onResponse(ResponseReplyFeeling response) {
						WaitDialog.instance().hideWaitNote();
						if (response.isSuccess()) {
							Message msg = new Message();
							msg.what = MSG_ReplyFinished;
							msg.arg1 = response.ReplyId;
							handler.sendMessage(msg);
						} else {

							Logger.info("editLabel onResponse. message is: "
									+ response.getErrorMessage());
						}
						handler.sendEmptyMessage(MSG_HideReply);
					}

				});
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isConnected()) {
				// 当前网络是连接的
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					// 当前所连接的网络可用
					return true;
				}
			}
		}
		return false;
	}
}
