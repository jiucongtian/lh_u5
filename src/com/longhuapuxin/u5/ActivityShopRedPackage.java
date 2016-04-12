package com.longhuapuxin.u5;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.longhuapuxin.adapter.U5BaseAdapter;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.Utils;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseBonus;
import com.longhuapuxin.entity.ResponseBonus.Bonus;
import com.longhuapuxin.entity.ResponseDad;
import com.squareup.okhttp.Request;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZH on 2016/2/23.
 * Email zh@longhuapuxin.com
 */
public class ActivityShopRedPackage extends BaseActivity {

    @ViewInject(R.id.lvRedPackages)
    private ListView mBonusListView;

    @ViewInject(R.id.redBag)
    private View mRedBag;

    @ViewInject(R.id.red_money)
    private TextView mMoneyTv;

    @ViewInject(R.id.close)
    private View mClose;


    private List<Bonus> mBonusList;
    private BonusesAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_red_package);
        ViewUtils.inject(this);

        init();
        fetchBonus();
    }

    private void init() {
        initHeader(R.string.shop_red_package);
        mBonusList = new ArrayList<Bonus>();
        mAdapter = new BonusesAdapter(this, mBonusList);
        mBonusListView.setAdapter(mAdapter);
        mBonusListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bonus item = mBonusList.get(i);
                if (!item.isHasGot()) {
                    openBonus(i);
                }
            }
        });

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRedBag(false, 0);
            }
        });

    }

    private void fetchBonus() {
        WaitDialog.instance().showWaitNote(this);
        Settings setting = Settings.instance();
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[5];
        params[0] = new OkHttpClientManager.Param("Token", setting.getToken());
        params[1] = new OkHttpClientManager.Param("UserId", setting.getUserId());
        params[2] = new OkHttpClientManager.Param("PageSize", "20");
        params[3] = new OkHttpClientManager.Param("PageIndex", "1");
        params[4] = new OkHttpClientManager.Param("ShopCode", "");

        OkHttpClientManager
                .postAsyn(
                        setting.getApiUrl() + "/seller/getbounslist",
                        params,
                        new OkHttpClientManager.ResultCallback<ResponseBonus>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                WaitDialog.instance().hideWaitNote();
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(
                                    ResponseBonus response) {
                                if (response.isSuccess()) {
                                    mBonusList.addAll(response.getBounses());
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                }
                                WaitDialog.instance().hideWaitNote();
                            }
                        });
    }
    private void openBonus(final int index) {
        WaitDialog.instance().showWaitNote(this);
        Settings setting = Settings.instance();
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[3];
        params[0] = new OkHttpClientManager.Param("Token", setting.getToken());
        params[1] = new OkHttpClientManager.Param("UserId", setting.getUserId());
        params[2] = new OkHttpClientManager.Param("BounsId", mBonusList.get(index).getId());

        OkHttpClientManager
                .postAsyn(
                        setting.getApiUrl() + "/seller/openbouns",
                        params,
                        new OkHttpClientManager.ResultCallback<ResponseDad>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                WaitDialog.instance().hideWaitNote();
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(
                                    ResponseDad response) {
                                if (response.isSuccess()) {
                                    mBonusList.get(index).setHasGot(true);
                                    mAdapter.notifyDataSetChanged();
                                    showRedBag(true, index);

                                } else {
                                }
                                WaitDialog.instance().hideWaitNote();
                            }
                        });
    }

    private void showRedBag(Boolean isShow, int index) {
        if(isShow) {
            mRedBag.setVisibility(View.VISIBLE);
            mMoneyTv.setText(mBonusList.get(index).getAmount());
        } else {
            mRedBag.setVisibility(View.GONE);
        }
    }

    public class BonusesAdapter extends U5BaseAdapter<Bonus> {

        public BonusesAdapter(Context context, List<Bonus> list) {
            super(context, list);
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            Bonus item = mDatas.get(i);
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_red_package, viewGroup, false);
            }

            TextView shopNameTv = Utils.getAdapterView(convertView, R.id.tvShopName);
            shopNameTv.setText(item.getShopName());

            TextView dateTv = Utils.getAdapterView(convertView, R.id.tvDate);
            dateTv.setText(item.getCreateTime());

            TextView redPackageTv = Utils.getAdapterView(convertView, R.id.tvGetRedPackage);
            TextView redPackageDisableTv = Utils.getAdapterView(convertView, R.id.tvGetRedPackage_disable);
            ImageView imgIv = Utils.getAdapterView(convertView, R.id.ivImg);
            View moneyV = Utils.getAdapterView(convertView, R.id.containerGotPackage);
            if(item.isHasGot()) {
                redPackageTv.setVisibility(View.GONE);
                redPackageDisableTv.setVisibility(View.VISIBLE);
                moneyV.setVisibility(View.VISIBLE);
                TextView amountTv = Utils.getAdapterView(convertView, R.id.tvMoney);
                amountTv.setText(item.getAmount());
                imgIv.setImageResource(R.drawable.shop_money_pay_gray);
            } else {
                redPackageTv.setVisibility(View.VISIBLE);
                redPackageDisableTv.setVisibility(View.GONE);
                moneyV.setVisibility(View.GONE);
                imgIv.setImageResource(R.drawable.shop_money_pay);
            }

            return convertView;
        }
    }
}
