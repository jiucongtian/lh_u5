package com.longhuapuxin.u5;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.longhuapuxin.adapter.U5BaseAdapter;
import com.longhuapuxin.common.ImageUrlLoader;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.Utils;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.db.bean.BounsSetting;
import com.longhuapuxin.db.bean.Estimation;
import com.longhuapuxin.entity.ResponseCare;
import com.longhuapuxin.entity.ResponseDad;
import com.longhuapuxin.entity.ResponseFilePath;
import com.longhuapuxin.entity.ResponseFilePath.FileObject;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.longhuapuxin.entity.ResponseGetAccount.User.CareWhichShop;
import com.longhuapuxin.entity.ResponseGetProducts;
import com.longhuapuxin.entity.ResponseShop;
import com.longhuapuxin.entity.ResponseGetAccount.User.CareWho;
import com.longhuapuxin.entity.ResponseShop.Shop;
import com.longhuapuxin.db.bean.DiscountCoupon;
import com.longhuapuxin.db.bean.DiscountTicket;
import com.longhuapuxin.entity.ResponseViewFeedBack;
import com.squareup.okhttp.Request;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.longhuapuxin.entity.ResponseGetAccount.User.CareWhichShop;

import org.w3c.dom.Text;

public class ShopDetailActivity extends BaseActivity implements OnClickListener {

    private TextView mAddress, mDistance, mWorkingTime, mPhotosCount, /*mDiscountNumber,*/ txtDescription, mCouponDesc,mCouponDiscount,txtBouns;
    private ImageView mShopImage, mHasParking, mHasWifi, mGoodsPhotos, shopEnter,mProduct1,mProduct2,mProduct3, addressIv;
    private RatingBar star1,star2;
    private TextView txtEstCount, btn_pay;
    private int State=0;//0 has got basic shop info, 1 has got products, 2 has got promotion 3 has get estimations

    private List<DiscountTicket> mDiscountList;
    private static final int MSG_ShowPhone = 1;
    private static final int MSG_GetNextInfo=2;
    private Shop mShop;
    private String mShopCode, mShopName;
    private static final int PHOTO_COUNT = 6;
    private String[] mPhotoUrls = null;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ShowPhone:
                    ShowCallButton();
                    break;
                case MSG_GetNextInfo:
                    if(State==0){
                        GetProducts(mShopCode);

                    }
                    else if(State==1){
                        GetPromotion();
                    }
                    else if(State==2){
                        GetEstimation();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        mDiscountList = new ArrayList<DiscountTicket>();
        init();
    }

    private void init() {
        final Intent intent = getIntent();
        mShopCode = intent.getStringExtra("ShopCode");
        mShopName = intent.getStringExtra("ShopName");
        initHeader(mShopName);

        updateCareShopStatus();

        shopEnter = (ImageView) findViewById(R.id.shop_enter);
        shopEnter.setOnClickListener(this);
        findViewById(R.id.imgProduct1).setOnClickListener(this);
        findViewById(R.id.imgProduct2).setOnClickListener(this);
        findViewById(R.id.imgProduct3).setOnClickListener(this);
        mAddress = (TextView) findViewById(R.id.shop_address);
        mDistance = (TextView) findViewById(R.id.shop_distance_txt);
        mHasParking = (ImageView) findViewById(R.id.img_has_parking);
        mHasWifi = (ImageView) findViewById(R.id.img_has_wifi);
        mWorkingTime = (TextView) findViewById(R.id.shop_duration);
        mShopImage = (ImageView) findViewById(R.id.shop_image);
        mShopImage.setOnClickListener(this);
        mGoodsPhotos = (ImageView) findViewById(R.id.goods_photos);
        mProduct1=(ImageView)findViewById(R.id.imgProduct1);
        mProduct2=(ImageView)findViewById(R.id.imgProduct2);
        mProduct3=(ImageView)findViewById(R.id.imgProduct3);
       mCouponDesc=(TextView)findViewById(R.id.couponDesc);
        mCouponDiscount=(TextView)findViewById(R.id.couponDiscount);
        mPhotosCount = (TextView) findViewById(R.id.txtPhotoCount);
        star1=(RatingBar)findViewById(R.id.start1);
        star2=(RatingBar)findViewById(R.id.start2);
        txtEstCount=(TextView)findViewById(R.id.estCount);
        btn_pay= (TextView) findViewById(R.id.btn_pay);

        btn_pay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itent=new Intent(ShopDetailActivity.this,ConfirmMoneyActivity.class);
                itent.putExtra("shopCode",mShopCode);
                startActivity(itent);
            }
        });
        addressIv=(ImageView)findViewById(R.id.addressIv);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtBouns=(TextView)findViewById(R.id.txtBouns);
        addressIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.putExtra("ShopName",mShop.getName());
                intent.putExtra("Longitude",mShop.getLongitude());
                intent.putExtra("Latitude",mShop.getLatitude());
                intent.putExtra("Address",mShop.getAddress());
                intent.setClass(ShopDetailActivity.this,MapActivity.class);
                startActivity(intent);
            }
        });
        fetchShopDetail(mShopCode);
    }

    private void updateCareShopStatus() {
        List<CareWhichShop> careList = Settings.instance().User.getCareWhichShops();
        boolean cared = false;
        for (CareWhichShop who : careList) {
            if (who.getCode().equals(mShopCode)) {
                cared = true;
                break;
            }
        }

        if (cared) {
            enableRightTextBtn(R.string.label_detail_header_btn_cared, true, new OnClickListener() {
                @Override
                public void onClick(View view) {
                    disCareShop(mShopCode);
                }
            });
        } else {
            enableRightTextBtn(R.string.label_detail_header_btn, true, new OnClickListener() {

                @Override
                public void onClick(View v) {
                    //关注用户。
                    careShop(mShopCode, mShopName);
                }
            });
        }


    }


    private void careShop(String shopCode, String shopName) {
        Param[] params = new Param[4];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("ShopCode", shopCode);
        params[3] = new Param("ShopName", shopName);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/shop/careshop", params,
                new OkHttpClientManager.ResultCallback<ResponseDad>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("ShopFragment.fetchRecommendShops.onError"
                                + e.toString());

//						WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseDad response) {
                        Logger.info("ShopFragment.fetchRecommendShops.onResponse"
                                + response);

                        if (response.isSuccess()) {
                            CareWhichShop newCaredShop = Settings.instance().User.new CareWhichShop(mShop);
                            Settings.instance().User.getCareWhichShops().add(newCaredShop);
                            updateCareShopStatus();
                        }
                    }

                });

    }


    private void disCareShop(String shopCode) {
        Param[] params = new Param[3];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("ShopCode", shopCode);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/shop/leaveshop", params,
                new OkHttpClientManager.ResultCallback<ResponseDad>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("disCareShop.onError"
                                + e.toString());

//						WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseDad response) {
                        Logger.info("disCareShop.onResponse"
                                + response);

                        if (response.isSuccess()) {
                            List<CareWhichShop> caredList = Settings.instance().User.getCareWhichShops();
                            for(CareWhichShop item : caredList) {
                                if(item.getCode().equals(mShopCode)) {
                                    caredList.remove(item);
                                    break;
                                }
                            }
                            updateCareShopStatus();
                        }
                    }

                });

    }


    @SuppressLint("NewApi")
    private void ShowCallButton() {
        if (mShop != null && mShop.getPhone() != null && mShop.getPhone().length() > 0) {
            ImageView btn = (ImageView) findViewById(R.id.shop_phone);
//            btn.setBackground(btn.getResources().getDrawable(R.drawable.shop_content_phone_sel));


            btn.setImageResource(R.drawable.shop_content_phone_sel);

            btn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mShop.getPhone()));
                    //通知activtity处理传入的call服务
                    ShopDetailActivity.this.startActivity(intent);

                }
            });
        }
        handler.sendEmptyMessage(MSG_GetNextInfo);
    }

    private void fetchShopDetail(String shopCode) {
        WaitDialog.instance().showWaitNote(this);
        Param[] params = new Param[3];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("ShopCode", shopCode);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/shop/shop", params,
                new OkHttpClientManager.ResultCallback<String>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("onError"
                                + e.toString());
                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(String responseText) {
                        U5Application app = (U5Application) getApplicationContext();
                        Gson gson = app.getGson();
                        ResponseShop response = gson.fromJson(responseText, ResponseShop.class);
                        Logger.info("onResponse"
                                + response);

                        if (response.isSuccess()) {
                            mShop = response.getShop();
                            mDiscountList.clear();
                            mDiscountList.addAll(mShop.getDiscountCoupon());
                            mAddress.setText(mShop.getAddress());
                            mWorkingTime.setText(mShop.getWorkTime());
                            txtDescription.setText(mShop.getDescription());
                            Double shopLatitude = mShop.getLatitude();
                            Double shopLongitude = mShop.getLongitude();
                            Float myFloatLatitude = Settings.instance().getLatitude();
                            Float myFlostLongitude = Settings.instance().getLontitude();

                            if (shopLatitude != null && shopLongitude != null &&
                                    myFloatLatitude != null && myFlostLongitude != null) {
                                Double myDoubleLatitude = Double.parseDouble(Float.toString(myFloatLatitude));
                                Double myDoubleLongitude = Double.parseDouble(Float.toString(myFlostLongitude));
                                String distance = Utils.getStrDistance(myDoubleLongitude, myDoubleLatitude, shopLongitude, shopLatitude);
                                mDistance.setText(String.format(getString(R.string.shop_list_diatance), distance));
                            } else {
                                mDistance.setText(R.string.notSure);
                            }


                            if (mShop.isHasWifi()) {
                                mHasWifi.setImageResource(R.drawable.shop_content_kou);
                            } else {
                                mHasWifi.setImageResource(R.drawable.shop_content_bad);
                            }
                            if (mShop.isHasParking()) {
                                mHasParking.setImageResource(R.drawable.shop_content_kou);
                            } else {
                                mHasParking.setImageResource(R.drawable.shop_content_bad);
                            }

                            String photots = mShop.getPhotos();
                            int photoCount = 0;
                            if (!TextUtils.isEmpty(photots)) {
                                fetchPhotoUrls(photots);
                                String[] photoList = photots.split(",");
                                photoCount = photoList.length;
                            }

                            mPhotosCount.setText(String.valueOf(photoCount));

                            String photo = Utils.getFirstId(photots);
                            ImageUrlLoader.fetchImageUrl(photo, mShopImage, ShopDetailActivity.this, true);
                            ImageUrlLoader.fetchImageUrl(mShop.getLogo(), mGoodsPhotos, ShopDetailActivity.this);
                            handler.sendEmptyMessage(MSG_ShowPhone);

                        }

                        WaitDialog.instance().hideWaitNote();
                    }

                });
    }

    private void fetchPhotoUrls(String ids) {
        if (!TextUtils.isEmpty(ids)) {
            Param[] params = new Param[3];
            params[0] = new Param("UserId", Settings.instance().getUserId());
            params[1] = new Param("Token", Settings.instance().getToken());
            params[2] = new Param("FileId", ids);

            OkHttpClientManager.postAsyn(Settings.instance().getApiUrl() + "/basic/getphoto", params, new OkHttpClientManager.ResultCallback<ResponseFilePath>() {

                @Override
                public void onError(Request request, Exception e) {
                    Logger.info("load image onError: " + e);
                }

                @Override
                public void onResponse(ResponseFilePath response) {
                    Logger.info("load image error code is: " + response.getErrorCode());
                    Logger.info("load image error msg is: " + response.getErrorMessage());
                    if (response.isSuccess()) {
                        if (response.getFiles().size() > 0) {
                            List<String> tmpUrls = new ArrayList<String>();
                            for (FileObject object : response.getFiles()) {
                                tmpUrls.add(object.getOriginal());
                            }
                            int size = tmpUrls.size();
                            mPhotoUrls = (String[]) tmpUrls.toArray(new String[size]);
                        }
                    }
                }

            });
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shop_image:
                if (mPhotoUrls != null) {
                    ShowGalleryActivity(mPhotoUrls, 0);
                }
                break;
            case R.id.shop_enter:
            case R.id.imgProduct1:
            case R.id.imgProduct2:
            case R.id.imgProduct3:
                go2ProductlistActivity();
                break;
        }
    }

    private void go2ProductlistActivity() {
        Intent intent = new Intent(this, ProductListActivity.class);
        intent.putExtra("shopCode", mShopCode);
        intent.putExtra("shopName", mShopName);
        startActivity(intent);
    }
    private void GetProducts(String shopCode) {
        Param[] params = new Param[5];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("ShopCode", shopCode);
        params[3] = new Param("PageSize", "3");
        params[4] = new Param("PageIndex", "1");
        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/shop/getproducts", params,
                new OkHttpClientManager.ResultCallback<ResponseGetProducts>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("ShopFragment.fetchRecommendShops.onError"
                                + e.toString());

                    }

                    @Override
                    public void onResponse(ResponseGetProducts response) {
                        Logger.info("ShopFragment.fetchRecommendShops.onResponse"
                                + response);

                        if (response.isSuccess()) {
                            if (response.getProducts() != null) {
                                ResponseGetProducts.Product product = null;
                                String photoId = null;
                                for (int i = 0; i < response.getProducts().size(); i++) {
                                    product = response.getProducts().get(i);
                                    photoId = product.getPhotos();
                                    if (photoId.indexOf(",") > -1) {
                                        photoId = photoId.substring(0, photoId.indexOf(","));
                                    }
                                    if (photoId != null) {
                                        if (i == 0) {
                                            ImageUrlLoader.fetchImageUrl(photoId, mProduct1, ShopDetailActivity.this);
                                        } else if (i == 1) {
                                            ImageUrlLoader.fetchImageUrl(photoId, mProduct2, ShopDetailActivity.this);
                                        } else if (i == 2) {
                                            ImageUrlLoader.fetchImageUrl(photoId, mProduct3, ShopDetailActivity.this);
                                        }

                                    }
                                }

                            }
                        }
                        State = 1;
                        handler.sendEmptyMessage(MSG_GetNextInfo);
                    }

                });

    }

    private void GetPromotion(){
        if (mShop.getDiscountCoupon() != null && mShop.getDiscountCoupon().size() > 0) {
            DiscountTicket discount = mShop.getDiscountCoupon().get(0);
            mCouponDesc.setText("全场通用");
            mCouponDiscount.setText(
                    String.valueOf((100 - Double.valueOf(discount.getDiscount())) / 10)
                            + "折"
            );
        } else {
            mCouponDesc.setText("无");
            mCouponDiscount.setText("");
        }
        if(mShop.getBounsSettings()==null || mShop.getBounsSettings().size()==0){
            txtBouns.setTextColor(getResources().getColor(R.color.deep_gray) );
            txtBouns.setText("没有设置随机红包");

        }
        else{
            StringBuilder sBounts=new StringBuilder();
            for(BounsSetting b:mShop.getBounsSettings()){
                sBounts.append("满"+b.getAmountFrom()+"送"+b.getMinVal()+"-"+b.getMaxVal()+"元红包\n");
            }
            txtBouns.setText(sBounts.toString());
        }
        State=2;
        handler.sendEmptyMessage(MSG_GetNextInfo);
    }
    private  void GetEstimation(){
        Param[] params = new Param[5];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("TargetShopCode", mShopCode);
        params[3] = new Param("PageSize", "1");//only need one piece
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
                            if(response.getCount()>0) {
                                Estimation summary = response.getFeedbackSummary();
                                star1.setRating(summary.getEstimate1());
                                star2.setRating(summary.getEstimate2());
                                txtEstCount.setText("查看所有评价（共" + response.getCount() + "条）");
                                txtEstCount.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent itent=new Intent(ShopDetailActivity.this,ShowCommentsActivity.class);
                                        itent.putExtra("OrderType","2");
                                        itent.putExtra("TargetShopCode",mShopCode);
                                        startActivity(itent);
                                    }
                                });
                            }
                            else{
                                txtEstCount.setText("还没有评价");
                            }
                        }
                        State = 3;


                    }

                });
    }
}
