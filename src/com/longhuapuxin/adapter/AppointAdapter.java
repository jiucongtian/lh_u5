package com.longhuapuxin.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseDad;
import com.longhuapuxin.entity.ResponseGetAppoints;
import com.longhuapuxin.u5.CommentsActivity;
import com.longhuapuxin.u5.MySubscriptionActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.u5.ShowCommentsActivity;
import com.longhuapuxin.uppay.UpPayBaseActivity;
import com.squareup.okhttp.Request;

import java.util.List;

/**
 * Created by asus on 2016/1/5.
 */
public class AppointAdapter extends BaseAdapter {
    private List<ResponseGetAppoints.Appoint> items;
    private LayoutInflater mInflater;
    private Context mContext;

    public AppointAdapter(Context context, List<ResponseGetAppoints.Appoint> items) {
        this.items = items;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ResponseGetAppoints.Appoint item = items.get(i);
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_my_subscribe, null);
            holder.date = (TextView) view.findViewById(R.id.tv_date);
            holder.duration = (TextView) view.findViewById(R.id.tv_duration);
            holder.shopPhone = (TextView) view.findViewById(R.id.tv_number);
            holder.shopName = (TextView) view.findViewById(R.id.tv_shop_name);
            holder.product = (TextView) view.findViewById(R.id.tv_product_content);
            holder.content = (TextView) view.findViewById(R.id.tv_content);
            holder.cancel = (Button) view.findViewById(R.id.btn_cancel);
            holder.pay = (Button) view.findViewById(R.id.btn_pay);
            holder.service = (Button) view.findViewById(R.id.btn_service);
            holder.statusConsume = view.findViewById(R.id.status_consume);
            holder.statusCancel = view.findViewById(R.id.status_cancel);
            holder.statusRefuse = view.findViewById(R.id.status_refuse);
            holder.statusMeet = view.findViewById(R.id.status_meet);
            holder.statusPay = view.findViewById(R.id.status_pay);
            holder.statusWait = view.findViewById(R.id.status_wait);
            holder.statusComments = (Button) view.findViewById(R.id.status_comment);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
//        判断状态
        holder.statusConsume.setVisibility(View.GONE);
        holder.statusCancel.setVisibility(View.GONE);
        holder.statusRefuse.setVisibility(View.GONE);
        holder.statusMeet.setVisibility(View.GONE);
        holder.statusPay.setVisibility(View.GONE);
        holder.statusWait.setVisibility(View.GONE);
        holder.statusComments.setVisibility(View.GONE);
//        等待商家回复
        if (item.getState().equals("1")) {
            holder.statusWait.setVisibility(View.VISIBLE);
        }
//        商家已答应
        else if (item.getState().equals("2")) {
            holder.statusConsume.setVisibility(View.VISIBLE);
        }
//        商家已拒绝
        else if (item.getState().equals("3")) {
            holder.statusRefuse.setVisibility(View.VISIBLE);
        }
//        已上门体验
        else if (item.getState().equals("4")) {
            holder.statusMeet.setVisibility(View.VISIBLE);
        }
//        已经付款
        else if (item.getState().equals("5")) {
            holder.statusPay.setVisibility(View.VISIBLE);
            holder.statusComments.setVisibility(View.VISIBLE);
        }
//        已经取消
        else if (item.getState().equals("6")) {
            holder.statusCancel.setVisibility(View.VISIBLE);
        }
        holder.date.setText(item.getPublishDate());
        holder.duration.setText(item.getTime1() + "-" + item.getTime2());
        holder.shopName.setText(item.getShopName());
        holder.shopPhone.setText(item.getShopPhone());
        holder.product.setText(getProduct(item.getProduct()));
        holder.content.setText(item.getNote());
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCancelAppoint(item);
            }
        });
        holder.pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MySubscriptionActivity.class);
                intent.putExtra("product", "" + item.getProduct());
                intent.putExtra("orderId", "" + item.getId());
                ( (Activity)  mContext).startActivityForResult(intent, 3);
            }
        });
        holder.service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestReceiveAppoint(item);
            }
        });



        if(TextUtils.isEmpty(item.getFeeBackId())) {
            holder.statusComments.setText("去评价");
        } else {
            holder.statusComments.setText("查看评价");
        }


        holder.statusComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(TextUtils.isEmpty(item.getFeeBackId())) {
                    Intent intent = new Intent(mContext, CommentsActivity.class);
                    intent.putExtra("OrderType", "3");
                    intent.putExtra("OrderId", String.valueOf(item.getId()));
                    intent.putExtra("UserName", item.getNickName());

                    intent.putExtra("OrderNote", item.getNote());
                    mContext.startActivity(intent);
                } else {
//					Toast.makeText(context, "show comments", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(mContext, ShowCommentsActivity.class);
                    intent.putExtra("OrderType", "3");
                    intent.putExtra("FeedBackId", item.getFeeBackId());
                    mContext.startActivity(intent);
                }





            }
        });





        return view;
    }

    public class ViewHolder {
        public TextView date, duration, product, shopPhone, shopName, content;
        public Button cancel, pay, service, statusComments;
        public View statusConsume, statusCancel, statusRefuse, statusMeet, statusPay, statusWait;
    }

    private void requestCancelAppoint(final ResponseGetAppoints.Appoint item) {
        WaitDialog.instance().showWaitNote(mContext);
        Settings setting = Settings.instance();
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[4];
        params[0] = new OkHttpClientManager.Param("Token", setting.getToken());
        params[1] = new OkHttpClientManager.Param("UserId", setting.getUserId());
        params[2] = new OkHttpClientManager.Param("AppointId", item.getId());
        params[3] = new OkHttpClientManager.Param("Note", "");


        OkHttpClientManager
                .postAsyn(
                        setting.getApiUrl() + "/shop/cancelappoint",
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
                                    WaitDialog.instance().hideWaitNote();
                                    item.setState("6");
                                    refresh();
                                } else {
                                    WaitDialog.instance().hideWaitNote();
                                }
                            }
                        });
    }

    private void requestReceiveAppoint(final ResponseGetAppoints.Appoint item) {
        WaitDialog.instance().showWaitNote(mContext);
        Settings setting = Settings.instance();
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[4];
        params[0] = new OkHttpClientManager.Param("Token", setting.getToken());
        params[1] = new OkHttpClientManager.Param("UserId", setting.getUserId());
        params[2] = new OkHttpClientManager.Param("AppointId", item.getId());
        params[3] = new OkHttpClientManager.Param("Note", "");


        OkHttpClientManager
                .postAsyn(
                        setting.getApiUrl() + "/shop/receiveappoint",
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
                                    WaitDialog.instance().hideWaitNote();
                                    item.setState("4");
                                    refresh();
                                } else {
                                    WaitDialog.instance().hideWaitNote();
                                }
                            }
                        });
    }

    private void refresh() {
        notifyDataSetChanged();
    }

    private StringBuffer mProduct = null;

    private String getProduct(String appointProduct) {
        if (appointProduct == null) {
            return "";
        }
        mProduct = new StringBuffer();
        String[] products = appointProduct.split(";");
        for (String product :
                products) {
            String[] pp = product.split(",");
            if (!product.equals(products[products.length - 1])) {
                mProduct.append(pp[3]);
                mProduct.append("x");
                mProduct.append(pp[1]);
                mProduct.append(pp[2]);
                mProduct.append(",");
            } else {
                mProduct.append(pp[3]);
                mProduct.append("x");
                mProduct.append(pp[1]);
                mProduct.append(pp[2]);
                mProduct.append("。");
            }
        }
        return mProduct.toString();
    }

}
