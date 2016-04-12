package com.longhuapuxin.u5;

import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.longhuapuxin.service.IMService;
import com.squareup.okhttp.Request;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BlankActivity extends Activity {

    private String userId;
    private String token;
    private boolean isFirstUsed;

    private ViewPager mGuidVp;
    private List<View> mViewList = new ArrayList<View>();
    private GuidPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.info("onCreate");
        loadSettings();

        setContentView(R.layout.activity_blank);
        U5Application app = (U5Application) getApplication();
        app.addActivity(this);
        app.registNetWorkReceiver();

        Intent intent = new Intent(BlankActivity.this, IMService.class);
        startService(intent);

        initView();

    }

    private void initView() {

        if(isFirstUsed) {
            View tmpView = getLayoutInflater().inflate(R.layout.pager_img, null);
            ((ImageView)tmpView.findViewById(R.id.guidIv)).setImageResource(R.drawable.guide_1);
            mViewList.add(tmpView);
            tmpView = getLayoutInflater().inflate(R.layout.pager_img, null);
            ((ImageView)tmpView.findViewById(R.id.guidIv)).setImageResource(R.drawable.guide_2);
            mViewList.add(tmpView);
            tmpView = getLayoutInflater().inflate(R.layout.pager_img, null);
            ImageView lastImageView = (ImageView) tmpView.findViewById(R.id.guidIv);
            lastImageView.setImageResource(R.drawable.guide_3);
            lastImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Settings.instance().setIsFirstUsed(false);
//                    Settings.instance().save(BlankActivity.this);
                    Intent intent = new Intent(BlankActivity.this,
                            CopyOfWelcomeActivity.class);
                    startActivity(intent);
                }
            });
            mViewList.add(tmpView);
        } else {
            View tmpView = getLayoutInflater().inflate(R.layout.pager_img, null);
            ((ImageView)tmpView.findViewById(R.id.guidIv)).setImageResource(R.drawable.preview);
            mViewList.add(tmpView);
            new Handler(new Handler.Callback() {

                @Override
                public boolean handleMessage(Message msg) {
                    CheckStoredAccount();
                    return false;
                }
            }).sendEmptyMessageDelayed(0, 500);
        }


        mAdapter = new GuidPagerAdapter(mViewList);
        mGuidVp = (ViewPager) findViewById(R.id.guidVp);
        mGuidVp.setAdapter(mAdapter);
    }

    private void loadSettings() {
        Settings.instance().load(BlankActivity.this);
        userId = Settings.instance().getUserId();
        token = Settings.instance().getToken();
        isFirstUsed = Settings.instance().getIsFirstUsed();
    }

    private void CheckStoredAccount() {
        Logger.info("CheckStoredAccount");
//        Settings.instance().load(BlankActivity.this);
//
//        String userId = Settings.instance().getUserId();
//        String token = Settings.instance().getToken();
        if (userId.length() > 0 && token.length() > 0) {
            httpRequestGetAccount(userId, token);
        } else {
            Intent intent = new Intent(BlankActivity.this,
                    CopyOfWelcomeActivity.class);
            startActivity(intent);
        }
    }

    private void httpRequestGetAccount(final String userId, final String token) {
        Logger.info("GetAccount");
        Param[] params = new Param[3];
        params[0] = new Param("UserId", userId);
        params[1] = new Param("Token", token);
        params[2] = new Param("TargetUserId", userId);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/auth/getaccount", params,
                new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("onError");
                        Toast.makeText(BlankActivity.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        Intent intent = new Intent(BlankActivity.this,
                                CopyOfWelcomeActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onResponse(String u) {
                        Logger.info("onResponse");
                        try {

                            ResponseGetAccount responseGetAccount = ((U5Application) getApplication())
                                    .getGson().fromJson(u,
                                            ResponseGetAccount.class);
                            // Log.d("", "--------" + responseLoginByPhone);
                            if (responseGetAccount.isSuccess()) {
                                Settings.instance().setMyName(
                                        responseGetAccount.getUser()
                                                .getNickName());
                                Settings.instance().User = responseGetAccount
                                        .getUser();
                                Settings.instance().setUserId(userId);
                                Settings.instance().setToken(token);
                                Settings.instance().save(BlankActivity.this);
                                Settings.instance().setManualyWithDraw(responseGetAccount.isManualyWithDraw());
                                Logger.info("------GetAccount success");
//								Log.d("", "------GetAccount success");
                                Intent intent = new Intent(BlankActivity.this,
                                        MainActivity.class);
                                startActivity(intent);

                            } else {

                                Settings.instance().User = null;
                                Settings.instance().setUserId("");
                                Settings.instance().setToken("");
                                Settings.instance().save(BlankActivity.this);
//                                Log.d("", "------GetAccount error"
//                                        + responseGetAccount.getErrorMessage());
//                                Log.d("", "------GetAccount error"
//                                        + responseGetAccount.getErrorCode());
                                Logger.info("------GetAccount error"
                                        + responseGetAccount.getErrorMessage());
                                Logger.info("------GetAccount error"
                                        + responseGetAccount.getErrorCode());
                                Intent intent = new Intent(BlankActivity.this,
                                        CopyOfWelcomeActivity.class);
                                startActivity(intent);
                            }
                        } catch (Exception ex) {
                            Toast.makeText(BlankActivity.this, ex.getMessage(),
                                    Toast.LENGTH_LONG).show();
//                            Log.d("AA", ex.getMessage());
                            Logger.info(ex.getMessage());
                            Intent intent = new Intent(BlankActivity.this,
                                    CopyOfWelcomeActivity.class);
                            startActivity(intent);
                        }
                    }
                });

    }

    public class GuidPagerAdapter extends PagerAdapter {

        List<View> viewList;

        public GuidPagerAdapter(List<View> viewList) {
            this.viewList = viewList;
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position), 0);
            return viewList.get(position);
        }
    }

}
