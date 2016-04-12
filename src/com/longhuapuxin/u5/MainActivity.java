package com.longhuapuxin.u5;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseBindDevice;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.longhuapuxin.entity.ResponseKeptLabel;
import com.longhuapuxin.entity.ResponseLabelCategory;
import com.longhuapuxin.fragment.FragmentLeft;
import com.longhuapuxin.fragment.FragmentListener;
import com.longhuapuxin.fragment.FragmentRight;
import com.longhuapuxin.service.IMService;
import com.longhuapuxin.slidingmenu.SlidingMenuActivity;
import com.longhuapuxin.u5.upgrade.UpgradeManager;
import com.squareup.okhttp.Request;

/**
 * @author zh
 * @date 2015-8-26
 */
public class MainActivity extends SlidingMenuActivity implements
        FragmentListener {

    private ServiceConnection sc;
    private IMService myService;
    private boolean isBind = false;
    private Fragment fragmentRight;
    private Fragment fragmentLeft;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.debug("MainActivity->onCreate");
        // setBackground(getResources().getDrawable(R.drawable.profile_bg));
        fragmentRight = new FragmentRight();
        fragmentLeft = new FragmentLeft();
        if (Settings.instance().User != null) {
            getSlidingMenu().setBackground(
                    getResources().getDrawable(R.drawable.profile_bg));
            initFragments(fragmentLeft, fragmentRight);
            // MainActivity.setBackground(getResources().getDrawable(R.drawable.profile_bg));
        }
        U5Application u5app = (U5Application) getApplication();
        if (!u5app.ismCheckedUpgrade()) {
            u5app.setmCheckedUpgrade(true);
            UpgradeManager upgradeManager = new UpgradeManager(this);
            upgradeManager.CheckAndRefreshApp();
        }

        ((U5Application) getApplication()).addActivity(this);
        Settings.instance().load(getApplicationContext());
        if (!isBind) {
            httpRequestBinddeviceWebApi();
            isBind = true;
        }
        if (Settings.instance().User == null) {
            httpRequestGetAccount();
        }
        checkFirstUsed();
        loadLowPriorityData();

    }

    private void checkFirstUsed() {
        if(Settings.instance().getIsFirstUsed()) {
            Settings.instance().setIsFirstUsed(false);
            Settings.instance().save(this);

            Intent intent = new Intent(MainActivity.this, FirstUsedActivity.class);
            startActivity(intent);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MainActivity.this, IMService.class);
        if (sc == null) {
            // getScanCode(realCode);
            sc = new ServiceConnection() {
                /*
                 * 只有在MyService中的onBind方法中返回一个IBinder实例才会在Bind的时候
                 * 调用onServiceConnection回调方法
                 * 第二个参数service就是MyService中onBind方法return的那个IBinder实例
                 * ，可以利用这个来传递数据
                 */
                @Override
                public void onServiceConnected(ComponentName name,
                                               IBinder service) {
                    myService = ((IMService.LocalBinder) service).getService();
                    myService.UploadLocation();
                    myService.ConnectToMQTT();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            unbindService(sc);
                        }
                    }, 200);

                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                    sc = null;
//					Log.i("TAG",
//							"onServiceDisconnected : ServiceConnection --->"
//									+ sc);
                    Logger.info("onServiceDisconnected : ServiceConnection --->"
                            + sc);
                }

            };
            bindService(intent, sc, Context.BIND_AUTO_CREATE);
        }

    }

    private void loadLowPriorityData() {
        fetchLabelCategory();
        fetchAllLabels();
    }

    public void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState(outState);
        // //将这一行注释掉，阻止activity保存fragment的状态,防止页面重叠
    }

    // private void initBaiDuPush() {
    // PushManager.startWork(getApplicationContext(),
    // PushConstants.LOGIN_TYPE_API_KEY, "yEvekElDInuVbpseSB4he743");
    //
    // }

    // @Override
    // protected void onNewIntent(Intent intent) {
    // // if (intent.getStringExtra("msgUserId") != null
    // // && intent.getStringExtra("msgChannelId") != null) {
    // Log.d("", "----msgUserId" + intent.getStringExtra("msgUserId"));
    // Log.d("", "----msgChannelId" + intent.getStringExtra("msgChannelId"));
    // Settings.instance().setMsgChannelId(
    // intent.getStringExtra("msgChannelId"));
    // Settings.instance().setMsgUserId(intent.getStringExtra("msgUserId"));
    // Settings.instance().save(getApplicationContext());
    // requestBinddeviceWebApi();
    // // handler.sendEmptyMessageDelayed(0, 1000);
    // // }
    // }

    private void fetchAllLabels() {
        Param[] params = new Param[2];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/label/getlabelList", params,
                new OkHttpClientManager.ResultCallback<ResponseKeptLabel>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("fetchAllLabels.onError" + e.toString());

                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseKeptLabel response) {
                        Logger.info("fetchAllLabels.onResponse" + response);

                        if (response.isSuccess()) {
                            Settings.instance().setKeptLabels(
                                    response.getLabels());
                        }

                    }

                });
    }

    private void fetchLabelCategory() {
        WaitDialog.instance().showWaitNote(this);
        Param[] params = new Param[2];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());

        OkHttpClientManager
                .postAsyn(
                        Settings.instance().getApiUrl()
                                + "/label/getLabelCategory",
                        params,
                        new OkHttpClientManager.ResultCallback<ResponseLabelCategory>() {

                            @Override
                            public void onError(Request request, Exception e) {
                                Logger.info("fetchLabelCategory.onError"
                                        + e.toString());

                                WaitDialog.instance().hideWaitNote();
                            }

                            @Override
                            public void onResponse(
                                    ResponseLabelCategory response) {
                                Logger.info("fetchLabelCategory.onResponse"
                                        + response);
                                if (response.isSuccess()) {
                                    Settings.instance().setLabelCategories(
                                            response.getLabelCategories());
                                    WaitDialog.instance().hideWaitNote();
                                } else {
                                    WaitDialog.instance().hideWaitNote();
                                }
                            }

                        });
    }

    private void httpRequestBinddeviceWebApi() {
        WaitDialog.instance().showWaitNote(this);
        Param[] params = new Param[4];
        params[0] = new Param("Token", Settings.instance().getToken());
        params[1] = new Param("UserId", Settings.instance().getUserId());
        params[2] = new Param("PushChannel", Settings.instance()
                .getMsgChannelId() + "/" + Settings.instance().getMsgUserId());
        params[3] = new Param("ChannelDevice", "1");

        try {
            OkHttpClientManager.getHttpsDelegate().setCertificates(
                    getAssets().open("longhuapuxin.cer"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/im/binddevice", params,
                new OkHttpClientManager.ResultCallback<ResponseBindDevice>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        WaitDialog.instance().hideWaitNote();
//						Log.d("",
//								"-----------------bindDeviceNo"
//										+ e.getMessage());
                        Logger.info("-----------------bindDeviceNo"
                                + e.getMessage());
                    }

                    @Override
                    public void onResponse(ResponseBindDevice response) {
                        if (response.isSuccess()) {
                            WaitDialog.instance().hideWaitNote();
//							Log.d("", "-----------------bindDeviceOk");
                            Logger.info("-----------------bindDeviceOk");
                        } else {
                            WaitDialog.instance().hideWaitNote();
//							Log.d("",
//									"-----------bind"
//											+ response.getErrorMessage());
                            Logger.info("-----------bind"
                                    + response.getErrorMessage());
                        }
                    }

                });

    }

    @SuppressLint("NewApi")
    private void httpRequestGetAccount() {
        WaitDialog.instance().showWaitNote(this);
        Logger.info("GetAccount");
        Param[] params = new Param[3];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("TargetUserId", Settings.instance().getUserId());

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/auth/getaccount", params,
                new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("onError");
                        WaitDialog.instance().hideWaitNote();
                        getSlidingMenu().setBackground(
                                getResources().getDrawable(
                                        R.drawable.profile_bg));
                        // Toast.makeText(MainActivity.this, e.getMessage(),
                        // Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String u) {
                        Logger.info("onResponse");
                        try {

                            ResponseGetAccount responseGetAccount = ((U5Application) getApplication())
                                    .getGson().fromJson(u,
                                            ResponseGetAccount.class);
                            if (responseGetAccount.isSuccess()) {
                                Settings.instance().User = responseGetAccount
                                        .getUser();
                                Settings.instance().setManualyWithDraw(responseGetAccount.isManualyWithDraw());
                                initFragments(fragmentLeft, fragmentRight);
//								Log.d("", "------GetAccount success");
                                Logger.info("------GetAccount success");
                                WaitDialog.instance().hideWaitNote();
                                getSlidingMenu().setBackground(
                                        getResources().getDrawable(
                                                R.drawable.profile_bg));
                            } else {
//								Log.d("", "------GetAccount fail"
//										+ responseGetAccount.getErrorMessage());
//								Log.d("", "------GetAccount fail"
//										+ responseGetAccount.getErrorCode());
                                Logger.info("------GetAccount fail"
                                        + responseGetAccount.getErrorMessage());
                                Logger.info("------GetAccount fail"
                                        + responseGetAccount.getErrorCode());
                                WaitDialog.instance().hideWaitNote();
                                getSlidingMenu().setBackground(
                                        getResources().getDrawable(
                                                R.drawable.profile_bg));
                            }
                        } catch (Exception ex) {
                            WaitDialog.instance().hideWaitNote();
                            getSlidingMenu().setBackground(
                                    getResources().getDrawable(
                                            R.drawable.profile_bg));
                            Toast.makeText(MainActivity.this, ex.getMessage(),
                                    Toast.LENGTH_LONG).show();
//							Log.d("AA", ex.getMessage());
                            Logger.info(ex.getMessage());
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {
        closeMenu();
    }

    @Override
    public void optionClicked() {
        openMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isBind = false;
    }

    public Fragment getFragmentRight() {
        return fragmentRight;
    }

    public Fragment getFragmentLeft() {
        return fragmentLeft;
    }
}
