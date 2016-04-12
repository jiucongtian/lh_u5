package com.longhuapuxin.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseCheckBank;
import com.longhuapuxin.entity.ResponseDad;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.squareup.okhttp.Request;

/**
 * Created by ZH on 2016/3/9.
 * Email zh@longhuapuxin.com
 */
public class CommentsFragment extends Fragment implements View.OnClickListener {

    private View mView, mProfessionView;
    private TextView mNameTv, mContentTv;
    private EditText mNoteEt;
    private String mCommentsType, mOrderId;
    private RatingBar mServiceRb, mPriceRb, mProfessionRb;

    private SubmitCommentsListener mCallBack;

    @Override
    public void onClick(View view) {
        submitComments();
    }

    public interface SubmitCommentsListener {
        void callBack();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_comments, null);
        init();
        return mView;
    }

    private void init() {
        mNameTv = (TextView) mView.findViewById(R.id.nameTv);
        mContentTv = (TextView) mView.findViewById(R.id.contentTv);
        mProfessionView = mView.findViewById(R.id.professionContainer);
        mNoteEt = (EditText) mView.findViewById(R.id.noteEt);
        mServiceRb = (RatingBar) mView.findViewById(R.id.serviceRb);
        mPriceRb = (RatingBar) mView.findViewById(R.id.priceRb);
        mProfessionRb = (RatingBar) mView.findViewById(R.id.professionRb);


        mView.findViewById(R.id.submitCommentsBtn).setOnClickListener(this);
    }

    public void setupComments(SubmitCommentsListener callBack, String type, String name, String content, String orderId) {
        mCommentsType = type;
        mCallBack = callBack;
        mOrderId = orderId;

        if(mCommentsType.equals("1")) {
            mProfessionView.setVisibility(View.VISIBLE);
        } else {
            mProfessionView.setVisibility(View.GONE);
        }

        mNameTv.setText(name);
        mContentTv.setText(content);
    }

    private void submitComments() {
        WaitDialog.instance().showWaitNote(getActivity());
        Settings setting = Settings.instance();
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[8];
        params[0] = new OkHttpClientManager.Param("Token", setting.getToken());
        params[1] = new OkHttpClientManager.Param("UserId", setting.getUserId());
        params[2] = new OkHttpClientManager.Param("OrderType", mCommentsType);
        params[3] = new OkHttpClientManager.Param("OrderId", mOrderId);
        params[4] = new OkHttpClientManager.Param("Text", mNoteEt.getText().toString());
        params[5] = new OkHttpClientManager.Param("Estimate1", String.valueOf(mServiceRb.getRating()));
        params[6] = new OkHttpClientManager.Param("Estimate2", String.valueOf(mPriceRb.getRating()));
        params[7] = new OkHttpClientManager.Param("Estimate3", String.valueOf(mProfessionRb.getRating()));


        OkHttpClientManager
                .postAsyn(
                        setting.getApiUrl() + "/im/newfeedback",
                        params,
                        new OkHttpClientManager.ResultCallback<ResponseDad>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                e.printStackTrace();
                                WaitDialog.instance().hideWaitNote();
                            }

                            @Override
                            public void onResponse(
                                    ResponseDad response) {

                                if(response.isSuccess()) {
                                    mCallBack.callBack();
                                }
                                WaitDialog.instance().hideWaitNote();
                            }
                        });
    }
}
