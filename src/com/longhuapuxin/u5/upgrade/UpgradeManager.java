package com.longhuapuxin.u5.upgrade;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.u5.R;
import com.squareup.okhttp.Request;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

@SuppressLint("SdCardPath") 
	public class UpgradeManager {
	private final String TAG = "Upgrade Package";
	private static int LocalVersion;
	private static String LocalVersionName;
	private static int ServerVersion;
	private static String ServerVersionName;
	private static String Apk;
	private Context context;
	private final int UPDATA_CLIENT = 1;
	private final int GET_UNDATAINFO_ERROR = 2;
	private final int DOWN_ERROR = 3;
	private final int DOWN_UPDATE = 4;
	private final int DOWN_OVER = 5;
	private ProgressBar mProgress;
	private static final String savePath = "/sdcard/U5/";// 保存apk的文件夹
	private static final String saveFileName = savePath
			+ "UpdateDemoRelease.apk";
	private int progress = 0;
	private Dialog downloadDialog;// 下载对话框
	private Boolean interceptFlag = false;

	public UpgradeManager(Context context) {
		this.context = context;

	}

	private void GetLocalVersion() {
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			LocalVersion = packInfo.versionCode;
			LocalVersionName = packInfo.versionName;

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());

		}
	}

	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			URL url;
			try {
				url = new URL(UpgradSetting.UpdateSite + Apk);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream ins = conn.getInputStream();
				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream outStream = new FileOutputStream(ApkFile);
				int count = 0;
				byte buf[] = new byte[1024];
				do {
					int numread = ins.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 下载进度
					handler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成通知安装
						handler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					outStream.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消停止下载
				outStream.close();
				ins.close();
			} catch (Exception e) {
				Message msg = new Message();
				msg.what = DOWN_ERROR;
				handler.sendMessage(msg);
			}
		}
	};

	public void CheckAndRefreshApp() {
		new Handler().postDelayed(new CheckVersionTask(), 100);
	}

	private class VersionInfo {
		public int Version;
		public String VersionName;
		public String Apk;
	}

	public class CheckVersionTask implements Runnable {

		public void run() {
			try {
				GetLocalVersion();
				OkHttpClientManager.getAsyn(UpgradSetting.UpdateSite
						+ "version.txt",
						new OkHttpClientManager.ResultCallback<String>() {
							@Override
							public void onError(Request request, Exception e) {
								Toast.makeText(context, e.getMessage(),
										Toast.LENGTH_LONG).show();
								e.printStackTrace();
							}

							@Override
							public void onResponse(String u) {
								try {
									Gson gson = new Gson();
									VersionInfo res = gson.fromJson(u,
											VersionInfo.class);
									ServerVersion = res.Version;
									ServerVersionName = res.VersionName;
									Apk = res.Apk;
									if (ServerVersion > LocalVersion) {
										Log.i(TAG, "版本号不同 ,提示用户升级 ");
										Message msg = new Message();
										msg.what = UPDATA_CLIENT;
										handler.sendMessage(msg);
									}
								} catch (Exception ex) {
									Toast.makeText(context, ex.getMessage(),
											Toast.LENGTH_LONG).show();
									Log.d("AA", ex.getMessage());
								}
							}
						});

			} catch (Exception e) {
				// 待处理
				Message msg = new Message();
				msg.what = GET_UNDATAINFO_ERROR;
				handler.sendMessage(msg);
				e.printStackTrace();
			}
		}
	}

	Handler handler = new Handler() {

		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATA_CLIENT:

				doNewVersionUpdate();
				break;
			case GET_UNDATAINFO_ERROR:

				Toast.makeText(context, "获取服务器更新信息失败", Toast.LENGTH_SHORT)
						.show();

				break;
			case DOWN_ERROR:

				Toast.makeText(context, "下载新版本失败", Toast.LENGTH_SHORT).show();

				break;
			case DOWN_UPDATE:

				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				downloadDialog.dismiss(); // 结束掉进度条对话框
				installApk();
			}
		}
	};

	private void doNewVersionUpdate() {

		StringBuffer sb = new StringBuffer();
		sb.append("当前版本:");
		sb.append(LocalVersionName);

		sb.append(", 发现新版本:");
		sb.append(ServerVersionName);

		sb.append(", 是否更新?");
		Dialog dialog = new AlertDialog.Builder(context)
				.setTitle("软件更新")
				.setMessage(sb.toString())
				// 设置内容
				.setPositiveButton("更新",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								try {
									android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
											context);
									builder.setTitle("软件版本更新");
									final LayoutInflater inflater = LayoutInflater
											.from(context);
									View v = inflater.inflate(
											R.layout.progress, null);
									mProgress = (ProgressBar) v
											.findViewById(R.id.progress);
									builder.setView(v);// 设置对话框的内容为一个View
									builder.setNegativeButton("取消",
											new OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();
													interceptFlag = true;
												}
											});
									downloadDialog = builder.create();
									downloadDialog.show();
									Thread downLoadThread = new Thread(
											mdownApkRunnable);
									downLoadThread.start();

								} catch (Exception e) {
									Message msg = new Message();
									msg.what = DOWN_ERROR;
									handler.sendMessage(msg);
									e.printStackTrace();
								}

							}
						})
				.setNegativeButton("暂不更新",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// 点击"取消"按钮之后退出程序
								dialog.dismiss();
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
	}

	protected void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");// File.toString()会返回路径信息
		context.startActivity(i);

	}
}
