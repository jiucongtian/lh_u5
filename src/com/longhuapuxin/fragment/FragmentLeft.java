package com.longhuapuxin.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.entity.ResponseDad;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.longhuapuxin.entity.ResponseSearchLabel;
import com.longhuapuxin.entity.ResponseSearchLabel.User;
import com.longhuapuxin.u5.AboutU5Activity;
import com.longhuapuxin.u5.AboutUsActivity;
import com.longhuapuxin.u5.ActivityShopRedPackage;
import com.longhuapuxin.u5.AgreementActivity;
import com.longhuapuxin.u5.AppointmentActivity;
import com.longhuapuxin.u5.BillActivity;
import com.longhuapuxin.u5.DiscountCouponActivity;
import com.longhuapuxin.u5.LabelDetailActivity;
import com.longhuapuxin.u5.MyAddressActivity;
import com.longhuapuxin.u5.MyProfileActiviy;
import com.longhuapuxin.u5.MyWalletActivity;
import com.longhuapuxin.u5.OrderActivity;
import com.longhuapuxin.u5.PaymentActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.SecurityActivity;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.u5.SuggestionActivity;
import com.longhuapuxin.u5.U5Application;
import com.longhuapuxin.view.IsOnlineView;
import com.longhuapuxin.view.RoundCornerImageView;
import com.squareup.okhttp.Request;

@SuppressLint("NewApi")
public class FragmentLeft extends Fragment implements OnClickListener {
    LinearLayout imgPortrait;
    RoundCornerImageView myPortrait;
    TextView myName;
    CheckedTextView myLabel, myTransaction, myConsume, myDiscount, myShopRedBag,
            myULock, exit, myAppoint, myWallet, aboutU5, myAddress;
    private U5Application app;

    private ImageView onlineRound, busyRound;
    private IsOnlineView isOnlineView;
    private int onlineViewwidth;
    private int onlineRoundWidth;
    private TextView onlineText, busyText;
    private boolean isOnline = true;
    private boolean isOnlineViewwidthMeasured = false;
    private boolean isOnlineRoundWidthMeasured = false;
    private List<View> setClickbleViews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main_fragment_left, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        measureWidth();
    }

    private void setOnLine() {
        if (Settings.instance().User.getStatus().equals("1")) {
            isOnline = true;
        } else {
            isOnline = false;
        }
        if (isOnline) {
            busyText.setVisibility(View.GONE);
            busyRound.setVisibility(View.GONE);
            onlineRound.setVisibility(View.VISIBLE);
            isOnlineView.smoothToOnline(onlineRound, busyRound, onlineText,
                    busyText, isOnline);
        } else {
            onlineText.setVisibility(View.GONE);
            onlineRound.setVisibility(View.GONE);
            busyRound.setVisibility(View.VISIBLE);
            if (onlineViewwidth != 0 && onlineRoundWidth != 0) {
                isOnlineView.smoothToBusy(onlineViewwidth, onlineRoundWidth,
                        onlineRound, busyRound, onlineText, busyText, isOnline);
            }
        }
    }

    private void measureWidth() {
        ViewTreeObserver isOnlineViewObserver = isOnlineView
                .getViewTreeObserver();
        isOnlineViewObserver
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        if (!isOnlineViewwidthMeasured) {
                            onlineViewwidth = isOnlineView.getMeasuredWidth();
                            isOnlineViewwidthMeasured = true;
                        }
                        return true;
                    }
                });
        ViewTreeObserver onlineRoundObserver = onlineRound
                .getViewTreeObserver();
        onlineRoundObserver
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        if (!isOnlineRoundWidthMeasured) {
                            onlineRoundWidth = onlineRound.getMeasuredWidth();
                            isOnlineRoundWidthMeasured = true;
                            setOnLine();
                        }
                        return true;
                    }
                });
    }

    private void initViews() {
        setClickbleViews = new ArrayList<View>();
        app = (U5Application) getActivity().getApplication();
        imgPortrait = (LinearLayout) getView().findViewById(R.id.img_portrait);
        myName = (TextView) getView().findViewById(R.id.my_name);
        myPortrait = (RoundCornerImageView) getView().findViewById(
                R.id.my_portrait);
        myPortrait.setOnClickListener(this);
        myPortrait.setEnabled(false);
        myLabel = (CheckedTextView) getView().findViewById(R.id.my_label);
        myLabel.setOnClickListener(this);
        aboutU5 = (CheckedTextView) getView().findViewById(R.id.my_about_u5);
        aboutU5.setOnClickListener(this);
        myAppoint = (CheckedTextView) getView().findViewById(R.id.my_appointment);
        myAppoint.setOnClickListener(this);
        myAddress = (CheckedTextView) getView().findViewById(R.id.my_address);
        myAddress.setOnClickListener(this);
        myWallet = (CheckedTextView) getView().findViewById(R.id.my_wallet);
        myWallet.setOnClickListener(this);
        myTransaction = (CheckedTextView) getView().findViewById(
                R.id.my_transaction);
        myTransaction.setOnClickListener(this);
        myConsume = (CheckedTextView) getView().findViewById(R.id.my_consume);
        myConsume.setOnClickListener(this);
        myDiscount = (CheckedTextView) getView().findViewById(R.id.my_discount);
        myDiscount.setOnClickListener(this);
        myShopRedBag = (CheckedTextView) getView().findViewById(R.id.my_shop_red_bag);
        myShopRedBag.setOnClickListener(this);
        myULock = (CheckedTextView) getView().findViewById(R.id.my_u_lock);
        myULock.setOnClickListener(this);
        exit = (CheckedTextView) getView().findViewById(R.id.exit);
        exit.setOnClickListener(this);
        onlineText = (TextView) getView().findViewById(R.id.online_text);
        busyText = (TextView) getView().findViewById(R.id.busy_text);
        onlineRound = (ImageView) getView().findViewById(R.id.online_round);
        busyRound = (ImageView) getView().findViewById(R.id.busy_round);
        isOnlineView = (IsOnlineView) getView().findViewById(
                R.id.is_online_view);
        isOnlineView.setOnClickListener(this);
        setClickbleViews.add(myPortrait);
        setClickbleViews.add(isOnlineView);
        setClickbleViews.add(myLabel);
        setClickbleViews.add(myTransaction);
        setClickbleViews.add(myConsume);
        setClickbleViews.add(myDiscount);
        setClickbleViews.add(myULock);
        setClickbleViews.add(exit);
    }

    @Override
    public void onResume() {
        super.onResume();
        app.ObserveNickName(myName);
        app.ObservePortait(myPortrait);
    }

    @Override
    public void onPause() {
        super.onPause();
        app.StopObserveNickName(myName);
        app.StopObservePortait(myPortrait);
    }

    @Override
    public void onClick(View v) {
        if (v == myPortrait) {
            Intent intent = new Intent(getActivity(), MyProfileActiviy.class);
            startActivity(intent);
        } else if (v == myLabel) {
            ResponseGetAccount.User accountUser = Settings.instance().User;
            ResponseSearchLabel newer = new ResponseSearchLabel();
            User obj = newer.new User(accountUser);
            ((U5Application) getActivity().getApplication()).putParam(
                    U5Application.USER_DETAIL, obj);
            Intent intent = new Intent(getActivity(), LabelDetailActivity.class);
            startActivity(intent);
        } else if (v == myTransaction) {
            Intent intent = new Intent(getActivity(), OrderActivity.class);
            startActivity(intent);
        } else if (v == myConsume) {
            Intent intent = new Intent(getActivity(), PaymentActivity.class);
            startActivity(intent);
        } else if (v == myDiscount) {
            Intent intent = new Intent(getActivity(),
                    DiscountCouponActivity.class);
            startActivity(intent);
        } else if (v == myShopRedBag) {
            Intent intent = new Intent(getActivity(),
                    ActivityShopRedPackage.class);
            startActivity(intent);
        } else if (v == myULock) {
            Intent intent = new Intent(getActivity(), SecurityActivity.class);
            startActivity(intent);
        } else if (v == aboutU5) {
            Intent intent = new Intent(getActivity(), AboutU5Activity.class);
            startActivity(intent);
        } else if (v == myAppoint) {
            Intent intent = new Intent(getActivity(), AppointmentActivity.class);
            startActivity(intent);
        } else if (v == myWallet) {
            Intent intent = new Intent(getActivity(), MyWalletActivity.class);
            startActivity(intent);
        } else if (v == myAddress) {
            Intent intent = new Intent(getActivity(), MyAddressActivity.class);
            startActivity(intent);
        } else if (v == exit) {
            ((U5Application) getActivity().getApplication()).exit();
        } else if (v == isOnlineView) {
            if (isOnline) {
                isOnline = false;
                // 1 在线 2 离线 3 忙碌
                httpRequestSetStatus("3");
                onlineText.setVisibility(View.GONE);
                onlineRound.setVisibility(View.GONE);
                busyRound.setVisibility(View.VISIBLE);
                isOnlineView.smoothToBusy(onlineViewwidth, onlineRoundWidth,
                        onlineRound, busyRound, onlineText, busyText, isOnline);
            } else {
                isOnline = true;
                httpRequestSetStatus("1");
                busyText.setVisibility(View.GONE);
                busyRound.setVisibility(View.GONE);
                onlineRound.setVisibility(View.VISIBLE);
                isOnlineView.smoothToOnline(onlineRound, busyRound, onlineText,
                        busyText, isOnline);
            }
        }
    }

    private void httpRequestSetStatus(final String status) {
        Param[] params = new Param[3];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("Status", status);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/auth/setstatus", params,
                new OkHttpClientManager.ResultCallback<ResponseDad>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        if (status.equals("1")) {
                            isOnline = false;
                            onlineText.setVisibility(View.GONE);
                            onlineRound.setVisibility(View.GONE);
                            busyRound.setVisibility(View.VISIBLE);
                            isOnlineView.smoothToBusy(onlineViewwidth,
                                    onlineRoundWidth, onlineRound, busyRound,
                                    onlineText, busyText, isOnline);
//                            Settings.instance().User.setIsOnline(false);
                        } else if (status.equals("3")) {
                            isOnline = true;
                            busyText.setVisibility(View.GONE);
                            busyRound.setVisibility(View.GONE);
                            onlineRound.setVisibility(View.VISIBLE);
                            isOnlineView.smoothToOnline(onlineRound, busyRound,
                                    onlineText, busyText, isOnline);
//                            Settings.instance().User.setIsOnline(true);
                        }
                    }

                    @Override
                    public void onResponse(ResponseDad response) {
                        if (response.isSuccess()) {
//                            Log.d("", "-----httpRequestSetStatus success");
                            Logger.info("-----httpRequestSetStatus success");
                        } else {
                            if (status.equals("1")) {
                                isOnline = false;
                                onlineText.setVisibility(View.GONE);
                                onlineRound.setVisibility(View.GONE);
                                busyRound.setVisibility(View.VISIBLE);
                                isOnlineView.smoothToBusy(onlineViewwidth,
                                        onlineRoundWidth, onlineRound,
                                        busyRound, onlineText, busyText,
                                        isOnline);
                            } else if (status.equals("3")) {
                                isOnline = true;
                                busyText.setVisibility(View.GONE);
                                busyRound.setVisibility(View.GONE);
                                onlineRound.setVisibility(View.VISIBLE);
                                isOnlineView.smoothToOnline(onlineRound,
                                        busyRound, onlineText, busyText,
                                        isOnline);
                            }
                        }
                    }

                });
    }

    public void getSetClickbleViewsFalse() {
        for (View view : setClickbleViews) {
            view.setEnabled(false);
        }
    }

    public void getSetClickbleViewsTrue() {
        for (View view : setClickbleViews) {
            view.setEnabled(true);
        }

    }
}
