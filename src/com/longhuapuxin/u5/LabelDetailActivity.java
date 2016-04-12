package com.longhuapuxin.u5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.longhuapuxin.adapter.CommentsAdapter;
import com.longhuapuxin.adapter.U5BaseAdapter;
import com.longhuapuxin.common.ImageUrlLoader;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.common.Synchronizer;
import com.longhuapuxin.common.Synchronizer.OnSyncCallBack;
import com.longhuapuxin.common.Utils;
import com.longhuapuxin.entity.ResponseCare;
import com.longhuapuxin.entity.ResponseGetAccount.User.CareWho;
import com.longhuapuxin.entity.ResponseGetAccount.User.Label;
import com.longhuapuxin.entity.ResponseMarkedLabels;
import com.longhuapuxin.entity.ResponseSearchLabel.User;
import com.longhuapuxin.entity.ResponseViewFeedBack;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class LabelDetailActivity extends BaseActivity implements OnClickListener {
    private boolean cared=false;
    private static final int NOT_SURE = 0;
    private static final int MALE = 1;
    private static final int FE_MALE = 2;

    String mUserId, mUserName, mUserBirthday;
    User mUser;
    private com.longhuapuxin.view.RoundCornerImageView portrait;
    private ImageView iv_gender;
    private TextView txtIdStatus,txtBankStatus,tv_age,tv_online,txtLabelName,txtPrice,txtDescription,txtEstCount,txtViewEstimation,txtCare,txtChat;
    private ListView lstImages,lstEstimations;
    private List<String> photoList;
    private PhotoAdapter mAdapter;
    private View mContactBtn, mHeader, mFooter;
    private int mScreenWidth;
    private final  int MSG_GetEstmation=2;

    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GetEstmation:
                    GetEstimation();
                    break;
            }

        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_detail);


        U5Application u5app = (U5Application) getApplication();
        mUser = (User) u5app.getParam(U5Application.USER_DETAIL);

        mUserBirthday = mUser.getBirthday();
        mUserId = mUser.getUserId();
        mUserName = mUser.getNickName();

        initHead();
        initFooter();

        txtCare=(TextView)findViewById(R.id.txtCare);
        txtChat=(TextView)findViewById(R.id.txtChat);
        mContactBtn = findViewById(R.id.contactLayout);


        txtChat.setOnClickListener(this);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        lstImages=(ListView)findViewById(R.id.lstImages);
        lstImages.addHeaderView(mHeader);
        lstImages.addFooterView(mFooter);
        photoList = new ArrayList<String>();
        mAdapter = new PhotoAdapter(LabelDetailActivity.this, photoList);
        lstImages.setAdapter(mAdapter);


        mScreenWidth = displaymetrics.widthPixels-20;

        init(mUserName);
    }

    private void initHead() {
        mHeader = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.header_label_detail, null);
        //head
        txtIdStatus=(TextView)mHeader.findViewById(R.id.txtIdStatus);
        txtBankStatus=(TextView)mHeader.findViewById(R.id.txtBankStatus);
        tv_age=(TextView)mHeader.findViewById(R.id.tv_age);
        tv_online=(TextView)mHeader.findViewById(R.id.tv_online);
        txtLabelName=(TextView)mHeader.findViewById(R.id.txtLabelName);
        txtPrice=(TextView)mHeader.findViewById(R.id.txtPrice);
        txtDescription=(TextView)mHeader.findViewById(R.id.txtDescription);
        portrait=(com.longhuapuxin.view.RoundCornerImageView)mHeader.findViewById(R.id.userIcon);
        iv_gender=(ImageView)mHeader.findViewById(R.id.iv_gender);
    }

    private void initFooter() {
        mFooter = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.footer_label_detail, null);

        //foot
        txtEstCount=(TextView)mFooter.findViewById(R.id.txtEstCount);
        txtViewEstimation=(TextView)mFooter.findViewById(R.id.txtViewEstimation);
        lstEstimations=(com.longhuapuxin.view.MyListView)mFooter.findViewById(R.id.lstEstimations);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void init(final String nickName) {

        initHeader(nickName);
        txtCare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if(cared){
                    leavePerson(mUserId);
                }
                else{
                    carePerson(mUserId, mUserName);
                }

            }
        });
        if(mUserId.equals(Settings.instance().User.getId()))
        {
            mContactBtn.setVisibility(View.GONE);

        }

        if(mUser.getStatus() != null) {
            if (mUser.getStatus().equals("1")) {
                tv_online.setText("在线");
            } else {
                tv_online.setText("忙碌");
            }
        } else {
            tv_online.setText("忙碌");
        }

        int age = Utils.getAgeByBirthday(mUserBirthday);
        tv_age.setText(String.valueOf(age) + "岁");
        ImageUrlLoader.fetchImageUrl(mUser.getPortrait(), portrait, this);

        int gender;
        if (TextUtils.isEmpty(mUser.getGender())) {
            gender = NOT_SURE;
        } else {
            gender = Integer.valueOf(mUser.getGender());
        }

        int genderIcon;

        switch (gender) {
            case MALE:
                genderIcon = R.drawable.label_sex_man_round;
                iv_gender.setImageResource(genderIcon);
                break;
            case FE_MALE:
                genderIcon = R.drawable.label_sex_woman_round;
                iv_gender.setImageResource(genderIcon);
                break;
            case NOT_SURE:
            default:

                break;
        }



        if(mUser.getIdNo()==null || mUser.getIdNo().equals("")){
            txtIdStatus.setText("身份未验证");
        }
        else{
            txtIdStatus.setText("身份已验证");
        }
        if(mUser.getBankAccount()==null || mUser.getBankAccount().equals("")){
            txtBankStatus.setText("银行卡未验证");
        }
        else{
            txtBankStatus.setText("银行卡已验证");
        }

        updateCareStatus();
        fetchLabelByUserId(mUserId);
    }

    private void updateCareStatus() {
        List<CareWho> careList = Settings.instance().User.getCareWho();
        cared=false;
        for (CareWho who : careList) {
            if (who.getId().equals(mUserId)) {
                cared = true;
                break;
            }
        }

        if (cared) {
            txtCare.setText("已关注");

        } else {
            txtCare.setText("关注");

        }
    }
    private void fetchLabelByUserId(String userId) {
        Param[] params = new Param[3];
        params[0] = new Param("TargetUserId", userId);
        params[1] = new Param("UserId", Settings.instance().getUserId());
        params[2] = new Param("Token", Settings.instance().getToken());
        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/label/getmarkedlabels", params,
                new OkHttpClientManager.ResultCallback<ResponseMarkedLabels>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("ShopFragment.fetchRecommendShops.onError"
                                + e.toString());

                    }

                    @Override
                    public void onResponse(ResponseMarkedLabels response) {
                        Logger.info("ShopFragment.fetchRecommendShops.onResponse"
                                + response);
                        if (response.isSuccess()) {
                           if(response.getLabels()!=null && response.getLabels().size()>0){
                               Label label=response.getLabels().get(0);
                               txtDescription.setText(label.getNote());
                               txtLabelName.setText(label.getLabelName());
                               txtPrice.setText(label.getGuidePrice());
                               if(label.getPhotos()!=null &! label.getPhotos().equals("")) {

                                   photoList.clear();
                                   List photos = Arrays.asList(label.getPhotos().split(","));
                                   if(photos.size() > 0 ) {
                                       photoList.addAll(photos);
                                   }
                                   mAdapter.notifyDataSetChangedWithImages();
                               }
                           }
                        }
                        mHandler.sendEmptyMessage(MSG_GetEstmation);
                    }

                });
    }


    private void carePerson(String userId, String nickName) {
        Param[] params = new Param[6];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("TargetUserId", userId);
        params[3] = new Param("TargetName", nickName);
        params[4] = new Param("LabelCode", "");
        params[5] = new Param("LabelName", "");

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/label/careperson", params,
                new OkHttpClientManager.ResultCallback<ResponseCare>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("ShopFragment.fetchRecommendShops.onError"
                                + e.toString());

//						WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseCare response) {
                        Logger.info("ShopFragment.fetchRecommendShops.onResponse"
                                + response);

                        if (response.isSuccess()) {
                            List<CareWho> who = response.getCareWho();

                            List<CareWho> careWho = Settings.instance().User.getCareWho();
                            careWho.clear();
                            careWho.addAll(who);
                            updateCareStatus();
                        }

                    }

                });

    }
    private void leavePerson(String userId ) {
        Param[] params = new Param[3];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("TargetUserId", userId);


        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/label/leaveperson", params,
                new OkHttpClientManager.ResultCallback<ResponseCare>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("ShopFragment.fetchRecommendShops.onError"
                                + e.toString());

//						WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseCare response) {
                        Logger.info("ShopFragment.fetchRecommendShops.onResponse"
                                + response);

                        if (response.isSuccess()) {


                            List<CareWho> careList = Settings.instance().User.getCareWho();
                            for (CareWho who : careList) {
                                if (who.getId().equals(mUserId)) {
                                    careList.remove(who);
                                    break;
                                }
                            }
                            updateCareStatus();
                        }

                    }

                });

    }
    public class PhotoAdapter extends U5BaseAdapter<String> {


        public PhotoAdapter(Context context, List<String> list) {
            super(context, list);
        }

        @Override
        public String getImageId(String item) {
            return item;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(i>=mDatas.size()) {
                return  null;
            }

            if(view==null){
                view=  mInflater.inflate(R.layout.item_labeldetail_image,
                        viewGroup, false);
            }
            ImageView  img=(ImageView)view.findViewById(R.id.icon);
            ViewGroup.LayoutParams lp = img.getLayoutParams();
            lp.width = mScreenWidth;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            img.setLayoutParams(lp);
            img.setMaxWidth(mScreenWidth);
            img.setMaxHeight(mScreenWidth * 5);
            bindImageView(img, mDatas.get(i), true);

            return view;
        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtChat:
                Synchronizer.httpRequestNewSession(mUser.getUserId(), new OnSyncCallBack() {

                    @Override
                    public void syncSuccess(Object object) {
                        String sessionId = (String) object;
                        LabelDetailActivity.this.talkTo(sessionId);
                    }

                    @Override
                    public void syncError() {
                    }
                });
                break;
        }
    }

    private void talkTo(String sessionId) {
        Intent intent = new Intent(LabelDetailActivity.this, ChatActivity.class);
        intent.putExtra("NickName", Settings.instance().getMyName());
        intent.putExtra("NickName2", mUser.getNickName());
        intent.putExtra("UserId", Settings.instance().getUserId());
        intent.putExtra("UserId2", mUser.getUserId());
        intent.putExtra("SessionId", sessionId);
        intent.putExtra("Portrait2", mUser.getPortrait());

        startActivity(intent);
    }

    private  void GetEstimation() {
        Param[] params = new Param[5];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("TargetUserId", mUserId);
        params[3] = new Param("PageSize", "10");//only need one piece
        params[4] = new Param("PageIndex", "1");
        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/im/ViewFeedBack", params,
                new OkHttpClientManager.ResultCallback<ResponseViewFeedBack>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("ShopFragment.fetchRecommendShops.onError"
                                + e.toString());

                    }

                    @Override
                    public void onResponse(ResponseViewFeedBack response) {
                        Logger.info("ShopFragment.fetchRecommendShops.onResponse"
                                + response);

                        if (response.isSuccess()) {
                            if (response.getCount() > 0) {


                                txtEstCount.setText("(" + response.getCount() + "条）");
                                CommentsAdapter adapter = new CommentsAdapter(LabelDetailActivity.this, response.getFeedBacks());
                                adapter.setmFeedBackType("1");
                                lstEstimations.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                txtViewEstimation.setVisibility(View.VISIBLE);
                                txtViewEstimation.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent itent=new Intent(LabelDetailActivity.this,ShowCommentsActivity.class);
                                        itent.putExtra("OrderType","1");
                                        itent.putExtra("TargetUserId",String.valueOf(mUserId));
                                        startActivity(itent);
                                    }
                                });
                            } else {
                                txtEstCount.setText("还没有评价");
                                txtViewEstimation.setVisibility(View.GONE);
                            }
                        }


                    }

                });
    }
}
