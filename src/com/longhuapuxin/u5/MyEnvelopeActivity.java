package com.longhuapuxin.u5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.longhuapuxin.adapter.U5BaseAdapter;
import com.longhuapuxin.common.ImageUrlLoader;
import com.longhuapuxin.common.Utils;
import com.longhuapuxin.view.RoundCornerImageView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZH on 2016/1/28.
 * Email zh@longhuapuxin.com
 */
public class MyEnvelopeActivity extends BaseActivity {

    private String mOwnerNick, mOwnerPortrait, mNote, mMoney, mAmount, mOwnerId;
    private List<ReceivedNode> mReceiveHistory;
    private Double mTotalMoney = 0d;
    private boolean mIsGotMoney;
    private ReceivedHistoryAdapter mAdapter;

    @ViewInject(R.id.rivOwnerPortrait)
    private RoundCornerImageView mOwnerPotraitIv;

    @ViewInject(R.id.tvName)
    private TextView mOwnerTv;

    @ViewInject(R.id.tvNote)
    private TextView mNoteTv;

    @ViewInject(R.id.lateTv)
    private TextView mLateTv;

    @ViewInject(R.id.moneyContainer)
    private LinearLayout mMoneyContainer;

    @ViewInject(R.id.tvMoney)
    private TextView mMoneyTv;

    @ViewInject(R.id.tvTotalLuckMoney)
    private TextView mAllLuckyMoneyTv;

    @ViewInject(R.id.lvHistory)
    private ListView mHistoryLv;

    @ViewInject(R.id.tvPrompt)
    private TextView mPromptTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_envelope);
        init();
        initView();
    }

    private void init() {
        mIsGotMoney = false;
        mReceiveHistory = new ArrayList<ReceivedNode>();


        Intent intent = getIntent();
        mOwnerNick = intent.getStringExtra("nickname");
        mOwnerPortrait = intent.getStringExtra("portrait");
        mNote = intent.getStringExtra("note");
        mAmount = intent.getStringExtra("money");
        mOwnerId = intent.getStringExtra("ownerid");
        String tmpNameList = intent.getStringExtra("nicknames");
        String tmpMoneyList = intent.getStringExtra("moneys");
        String tmpIdList = intent.getStringExtra("ids");


        String[] nameArray = (tmpNameList == null) ? new String[]{} : tmpNameList.split(",");
        String[] moneyArray = (tmpMoneyList == null) ? new String[]{} : tmpMoneyList.split(",");
        String[] idArray = (tmpIdList == null) ? new String[]{} : tmpIdList.split(",");




        for (int i = 0; i < nameArray.length; i++) {
            ReceivedNode newNode = new ReceivedNode();
            newNode.nickName = nameArray[i];
            newNode.money = moneyArray[i];
            newNode.id = idArray[i];
            mReceiveHistory.add(newNode);
            if (newNode.id.equals(Settings.instance().User.getId())) {
                mIsGotMoney = true;
                mMoney = newNode.money;
            }
            mTotalMoney += Double.valueOf(newNode.money);
        }

        mAdapter = new ReceivedHistoryAdapter(this, mReceiveHistory);

    }

    private void initView() {
        ViewUtils.inject(this);
        initHeader(R.string.myLuckMondy);

        ImageUrlLoader.fetchImageUrl(mOwnerPortrait, mOwnerPotraitIv, this);
        mOwnerTv.setText(getString(R.string.ownerLuckMondy, mOwnerNick));
        mNoteTv.setText(mNote);

        DecimalFormat df = new DecimalFormat("####0.00");

        if(mOwnerId.equals(Settings.instance().User.getId())) {
            mMoneyContainer.setVisibility(View.VISIBLE);
            mPromptTv.setVisibility(View.GONE);
            mLateTv.setVisibility(View.GONE);
            mMoneyTv.setText(df.format(Double.valueOf(mAmount)));
        } else if (mIsGotMoney) {
            mMoneyContainer.setVisibility(View.VISIBLE);
            mPromptTv.setVisibility(View.VISIBLE);
            mLateTv.setVisibility(View.GONE);
            Double dMoney = Double.valueOf(mMoney);
            mMoneyTv.setText(df.format(dMoney));
        } else {
            mMoneyContainer.setVisibility(View.GONE);
            mLateTv.setVisibility(View.VISIBLE);
        }
        mAllLuckyMoneyTv.setText(getString(R.string.allLuckMoney, mReceiveHistory.size(), df.format(mTotalMoney)));
        mHistoryLv.setAdapter(mAdapter);
    }

    public class ReceivedNode {
        public String nickName;
        public String money;
        public String id;
    }

    public class ReceivedHistoryAdapter extends U5BaseAdapter<ReceivedNode> {

        public ReceivedHistoryAdapter(Context context, List<ReceivedNode> list) {
            super(context, list);
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_get_money_history, viewGroup, false);
            }

            TextView nameTv = Utils.getAdapterView(convertView, R.id.tvNickName);
            TextView getMoneyTv = Utils.getAdapterView(convertView, R.id.tvMoney);
            nameTv.setText(mDatas.get(i).nickName);
            String moneyString = getString(R.string.fetchMoney, mDatas.get(i).money);
            getMoneyTv.setText(moneyString);
            return convertView;
        }
    }
}
