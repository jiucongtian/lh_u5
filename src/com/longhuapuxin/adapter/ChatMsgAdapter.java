package com.longhuapuxin.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.longhuapuxin.common.ImageUrlLoader;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.db.bean.ChatMsg;
import com.longhuapuxin.entity.ResponseViewMoneyEnvelope;
import com.longhuapuxin.u5.MyEnvelopeActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;

public class ChatMsgAdapter extends U5BaseAdapter<ChatMsg> implements
        OnClickListener {
    /**
     * 消息
     */
    public static final int MSG_MYSELF = 0;
    public static final int MSG_OTHERS = 1;
    /**
     * 文件接收
     */
    public static final int FILE_SEND = 2;
    public static final int FILE_RECIEVE = 3;
    /*
    * 红包接受
    */
    public static final int MONEY_RECIEVE = 300;
    public static final int MONEY_SEND = 301;
    public static final int TIME_INTERVAL = 5;
    private List<ChatMsg> mList;
    private LayoutInflater inflater;
    private Context context;
    private List<Integer> ImgFileId;
    private BroadcastReceiver receiver;
    private String recieverPortrait;
    private List<String> idList = new ArrayList<String>();
    private boolean isGroup = false;

    class TimeHolder {
        TextView dateTime;

        TimeHolder(View view) {
            dateTime = (TextView) view.findViewById(R.id.tvDateTime);
        }

        public void filterTime(int index, String timeString) {
            if(index % TIME_INTERVAL == 0) {
                dateTime.setText(timeString);
                dateTime.setVisibility(View.VISIBLE);
            } else {
                dateTime.setVisibility(View.GONE);
            }
        }
    }

    class HolderMoneySender extends TimeHolder {
        TextView moneyTv;
        ImageView msg_img;
        View moneyLayout;

        HolderMoneySender(View view) {
            super(view);
            moneyTv = (TextView) view.findViewById(R.id.money_tv);
            moneyLayout = view.findViewById(R.id.money_layout);
            msg_img = (ImageView) view.findViewById(R.id.msg_img);
            bindImageView(msg_img, Settings.instance().User.getPortrait());
        }

    }

    class HolderMoneyReciever extends TimeHolder {
        TextView moneyTv;
        ImageView msg_img;
        View moneyLayout;

        HolderMoneyReciever(View view) {
            super(view);
            moneyTv = (TextView) view.findViewById(R.id.money_tv);
            moneyLayout = view.findViewById(R.id.money_layout);
            msg_img = (ImageView) view.findViewById(R.id.msg_img);
        }

    }

    class HolderMSGMyself extends TimeHolder {
        TextView msg;
        ImageView msg_img;

        HolderMSGMyself(View view) {
            super(view);
            msg = (TextView) view.findViewById(R.id.msg_tv);
            msg_img = (ImageView) view.findViewById(R.id.msg_img);

            bindImageView(msg_img, Settings.instance().User.getPortrait());
            // String portrait = Settings.instance().User.getPortrait();
        }

    }

    class HolderMSGOthers extends TimeHolder {
        TextView msg;
        ImageView msg_img;

        HolderMSGOthers(View view) {
            super(view);
            msg = (TextView) view.findViewById(R.id.msg_tv);
            msg_img = (ImageView) view.findViewById(R.id.msg_img);
//            bindImageView(msg_img, recieverPortrait);
            // String portrait=recieverPortrait;
        }

    }

    class HolderFileSender extends TimeHolder {
        TextView acceptReally, refuseReally, loading, completeReally,
                defectReally;
        ImageView msg_img;

        HolderFileSender(View view) {
            super(view);
            loading = (TextView) view.findViewById(R.id.loading);
            acceptReally = (TextView) view.findViewById(R.id.accept_really);
            refuseReally = (TextView) view.findViewById(R.id.refuse_really);
            completeReally = (TextView) view.findViewById(R.id.complete_really);
            defectReally = (TextView) view.findViewById(R.id.defect_really);
            msg_img = (ImageView) view.findViewById(R.id.msg_img);
            bindImageView(msg_img, Settings.instance().User.getPortrait());
            // Settings.instance().getPortrait();
        }

    }

    class HolderFileReciever extends TimeHolder {
        CheckedTextView lookFileResquest, acceptFile, refuseFile, completeFile,
                defectFile;
        ImageView lookFile;
        ImageView msg_img;

        HolderFileReciever(View view) {
            super(view);
            lookFile = (ImageView) view.findViewById(R.id.look_file);
            lookFileResquest = (CheckedTextView) view
                    .findViewById(R.id.look_file_request);
            acceptFile = (CheckedTextView) view.findViewById(R.id.accept_file);
            refuseFile = (CheckedTextView) view.findViewById(R.id.refuse_file);
            completeFile = (CheckedTextView) view
                    .findViewById(R.id.complete_file);
            defectFile = (CheckedTextView) view.findViewById(R.id.defect_file);
            msg_img = (ImageView) view.findViewById(R.id.msg_img);
            bindImageView(msg_img, recieverPortrait);
        }

    }

    public ChatMsgAdapter(Context context, List<ChatMsg> mList,
                          String recieverPortrait, boolean isGroup) {
        super(context, mList);
        this.context = context;
        this.mList = mList;
        ImgFileId = new ArrayList<Integer>();
        inflater = LayoutInflater.from(context);
        this.recieverPortrait = recieverPortrait;
        this.isGroup = isGroup;
//        List<String> idList = new ArrayList<String>();
        idList.add(Settings.instance().User.getPortrait());
        idList.add(recieverPortrait);
        ImageUrlLoader.fetchImageUrl(this, idList);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mList.get(arg0);
    }

    // 返回 代表某一个样式 的 数值
    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    // 两个样式 返回2
    @Override
    public int getViewTypeCount() {
        return 500;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // for (ChatMsg x : mList) {
        // Log.d("", "-------getview" + x.toString());
        // }
        int type = getItemViewType(position);
        HolderMSGMyself holderMSGMyself = null;
        HolderMSGOthers holderMSGOthers = null;
        HolderFileSender holderFileSender = null;
        HolderFileReciever holderFileReciever = null;
        HolderMoneySender holderMoneySender = null;
        HolderMoneyReciever holderMoneyReciever = null;
        if (convertView == null) {
            // 选择某一个样式。。
            switch (type) {
                case MSG_MYSELF:
                    convertView = inflater.inflate(R.layout.chat_item_msg_myself,
                            null);
                    holderMSGMyself = new HolderMSGMyself(convertView);
                    convertView.setTag(holderMSGMyself);
                    break;
                case MSG_OTHERS:
                    convertView = inflater.inflate(R.layout.chat_item_msg_others,
                            null);
                    holderMSGOthers = new HolderMSGOthers(convertView);
                    convertView.setTag(holderMSGOthers);
                    break;
                case FILE_SEND:
                    convertView = inflater.inflate(R.layout.chat_item_file_sender,
                            null);
                    holderFileSender = new HolderFileSender(convertView);
                    convertView.setTag(holderFileSender);
                    break;
                case FILE_RECIEVE:
                    convertView = inflater.inflate(
                            R.layout.chat_item_file_reciever, null);
                    holderFileReciever = new HolderFileReciever(convertView);
                    convertView.setTag(holderFileReciever);
                    break;
                case MONEY_RECIEVE:
                    convertView = inflater.inflate(
                            R.layout.chat_item_money_reciever, null);
                    holderMoneyReciever = new HolderMoneyReciever(convertView);
                    convertView.setTag(holderMoneyReciever);
                    break;
                case MONEY_SEND:
                    convertView = inflater.inflate(
                            R.layout.chat_item_money_send, null);
                    holderMoneySender = new HolderMoneySender(convertView);
                    convertView.setTag(holderMoneySender);
                    break;
            }
        } else {
            switch (type) {
                case MSG_MYSELF:
                    holderMSGMyself = (HolderMSGMyself) convertView.getTag();
                    break;
                case MSG_OTHERS:
                    holderMSGOthers = (HolderMSGOthers) convertView.getTag();
                    break;
                case FILE_SEND:
                    holderFileSender = (HolderFileSender) convertView.getTag();
                    break;
                case FILE_RECIEVE:
                    holderFileReciever = (HolderFileReciever) convertView.getTag();
                    break;
                case MONEY_SEND:
                    holderMoneySender = (HolderMoneySender) convertView.getTag();
                    break;
                case MONEY_RECIEVE:
                    holderMoneyReciever = (HolderMoneyReciever) convertView.getTag();
                    break;
            }

        }
        switch (type) {
            case MSG_MYSELF:
                holderMSGMyself.msg.setText(mList.get(position).getText());
                holderMSGMyself.filterTime(position, mList.get(position).getTime());
                break;
            case MSG_OTHERS:
                if (!isGroup) {
                    bindImageView(holderMSGOthers.msg_img, recieverPortrait);
                } else {
                    String portrait = mList.get(position).getSenderPortrait();
                    if (!idList.contains(portrait)) {
                        idList.add(portrait);
                        ImageUrlLoader.fetchImageUrl(this, idList);
                    }
                    bindImageView(holderMSGOthers.msg_img, portrait);
                }
                holderMSGOthers.msg.setText(mList.get(position).getText());
                holderMSGOthers.filterTime(position, mList.get(position).getTime());
                break;
            case FILE_SEND:
                // if接受else拒绝
                if (mList.get(position).getSendStatus() != null) {
                    if (mList.get(position).getSendStatus().equals("1")) {
                        holderFileSender.loading.setVisibility(View.GONE);
                        holderFileSender.acceptReally.setVisibility(View.VISIBLE);
                        holderFileSender.refuseReally.setVisibility(View.GONE);
                        holderFileSender.completeReally.setVisibility(View.GONE);
                        holderFileSender.defectReally.setVisibility(View.GONE);
                    } else if (mList.get(position).getSendStatus().equals("2")) {
                        holderFileSender.loading.setVisibility(View.GONE);
                        holderFileSender.acceptReally.setVisibility(View.GONE);
                        holderFileSender.refuseReally.setVisibility(View.VISIBLE);
                        holderFileSender.completeReally.setVisibility(View.GONE);
                        holderFileSender.defectReally.setVisibility(View.GONE);
                    } else if (mList.get(position).getSendStatus().equals("0")) {
                        holderFileSender.loading.setVisibility(View.VISIBLE);
                        holderFileSender.acceptReally.setVisibility(View.GONE);
                        holderFileSender.refuseReally.setVisibility(View.GONE);
                        holderFileSender.completeReally.setVisibility(View.GONE);
                        holderFileSender.defectReally.setVisibility(View.GONE);
                    } else if (mList.get(position).getSendStatus().equals("3")) {
                        holderFileSender.loading.setVisibility(View.GONE);
                        holderFileSender.acceptReally.setVisibility(View.GONE);
                        holderFileSender.refuseReally.setVisibility(View.GONE);
                        holderFileSender.completeReally.setVisibility(View.VISIBLE);
                        holderFileSender.defectReally.setVisibility(View.GONE);
                    } else if (mList.get(position).getSendStatus().equals("4")) {
                        holderFileSender.loading.setVisibility(View.GONE);
                        holderFileSender.acceptReally.setVisibility(View.GONE);
                        holderFileSender.refuseReally.setVisibility(View.GONE);
                        holderFileSender.completeReally.setVisibility(View.GONE);
                        holderFileSender.defectReally.setVisibility(View.VISIBLE);
                    }
                    holderFileSender.filterTime(position, mList.get(position).getTime());
                }

                break;
            case FILE_RECIEVE:
                if (!ImgFileId.contains(position)) {
                    ImgFileId.add(position);
                }
                holderFileReciever.lookFile.setId(position);
                holderFileReciever.lookFile.setOnClickListener(this);
                // ChatMsg chatMsg = mList.get(position);
                if (mList.get(position).getReceiverStatus() != null) {
                    if (mList.get(position).getReceiverStatus().equals("1")) {
                        holderFileReciever.lookFile.setClickable(false);
                        holderFileReciever.lookFileResquest
                                .setVisibility(View.GONE);
                        holderFileReciever.completeFile.setVisibility(View.GONE);
                        holderFileReciever.defectFile.setVisibility(View.GONE);
                        holderFileReciever.acceptFile.setVisibility(View.VISIBLE);
                        holderFileReciever.refuseFile.setVisibility(View.GONE);
                    } else if (mList.get(position).getReceiverStatus().equals("2")) {
                        holderFileReciever.lookFile.setClickable(false);
                        holderFileReciever.lookFileResquest
                                .setVisibility(View.GONE);
                        holderFileReciever.acceptFile.setVisibility(View.GONE);
                        holderFileReciever.refuseFile.setVisibility(View.VISIBLE);
                        holderFileReciever.completeFile.setVisibility(View.GONE);
                        holderFileReciever.defectFile.setVisibility(View.GONE);
                    } else if (mList.get(position).getReceiverStatus().equals("0")) {
                        holderFileReciever.lookFileResquest
                                .setVisibility(View.VISIBLE);
                        holderFileReciever.acceptFile.setVisibility(View.GONE);
                        holderFileReciever.refuseFile.setVisibility(View.GONE);
                        holderFileReciever.completeFile.setVisibility(View.GONE);
                        holderFileReciever.defectFile.setVisibility(View.GONE);
                    } else if (mList.get(position).getReceiverStatus().equals("3")) {
                        holderFileReciever.lookFile.setClickable(false);
                        holderFileReciever.lookFileResquest
                                .setVisibility(View.GONE);
                        holderFileReciever.completeFile.setVisibility(View.VISIBLE);
                        holderFileReciever.defectFile.setVisibility(View.GONE);
                        holderFileReciever.acceptFile.setVisibility(View.GONE);
                        holderFileReciever.refuseFile.setVisibility(View.GONE);
                    } else if (mList.get(position).getReceiverStatus().equals("4")) {
                        holderFileReciever.lookFile.setClickable(false);
                        holderFileReciever.lookFileResquest
                                .setVisibility(View.GONE);
                        holderFileReciever.completeFile.setVisibility(View.GONE);
                        holderFileReciever.defectFile.setVisibility(View.VISIBLE);
                        holderFileReciever.acceptFile.setVisibility(View.GONE);
                        holderFileReciever.refuseFile.setVisibility(View.GONE);
                    }
                    holderFileReciever.filterTime(position, mList.get(position).getTime());
                }
                break;
            case MONEY_RECIEVE:
                if (!isGroup) {
                    bindImageView(holderMoneyReciever.msg_img, recieverPortrait);
                } else {
                    String portrait = mList.get(position).getSenderPortrait();
                    if (!idList.contains(portrait)) {
                        idList.add(portrait);
                        ImageUrlLoader.fetchImageUrl(this, idList);
                    }
                    bindImageView(holderMoneyReciever.msg_img, portrait);
                }
                holderMoneyReciever.moneyTv.setText(mList.get(position).getText());
                holderMoneyReciever.filterTime(position, mList.get(position).getTime());
                holderMoneyReciever.moneyLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        httpRequestViewMoneyEnvelope(mList.get(position).getOrderId(), position);
                    }
                });
                break;
            case MONEY_SEND:
                holderMoneySender.moneyTv.setText(mList.get(position).getText());
                holderMoneySender.filterTime(position, mList.get(position).getTime());
                holderMoneySender.moneyLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        httpRequestViewMoneyEnvelope(mList.get(position).getOrderId(), position);
                    }
                });
                break;
        }

        return convertView;
    }

    public void refreshData(List<ChatMsg> data) {
        this.mList = data;
        notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
        for (Integer position : ImgFileId) {
            if (position == v.getId()) {
                mOnItemClickListener.onItemClick(mList.get(position));

            }
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener OnItemClickListener) {
        mOnItemClickListener = OnItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(ChatMsg chatMsg);
    }

    private RedBagListener redBagListener;

    public interface RedBagListener {
        public void myRedBag(String portrait, String who, String money, String note, String orderId);
    }

    public void setRedBagListener(RedBagListener redBagListener) {
        this.redBagListener = redBagListener;
    }

    private void httpRequestViewMoneyEnvelope(String MoneyEnvelopeId, final int position) {
        WaitDialog.instance().showWaitNote(mContext);
        Settings setting = Settings.instance();
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[3];
        params[0] = new OkHttpClientManager.Param("Token", setting.getToken());
        params[1] = new OkHttpClientManager.Param("UserId", setting.getUserId());
        params[2] = new OkHttpClientManager.Param("MoneyEnvelopeId", MoneyEnvelopeId);

        OkHttpClientManager
                .postAsyn(
                        setting.getApiUrl() + "/im/viewMoneyEnvelope",
                        params,
                        new OkHttpClientManager.ResultCallback<ResponseViewMoneyEnvelope>() {
                            @Override
                            public void onError(Request request, Exception e) {
                                e.printStackTrace();
                                WaitDialog.instance().hideWaitNote();
                            }

                            @Override
                            public void onResponse(
                                    ResponseViewMoneyEnvelope response) {
                                if (response.isSuccess()) {
                                    Double total = Double.valueOf(response.getMoney());
                                    if (total == 0) {
                                        Intent intent = new Intent(mContext, MyEnvelopeActivity.class);
                                        intent.putExtra("portrait", response.getMoneyEnvelope().getPortrait());
                                        intent.putExtra("nickname", response.getMoneyEnvelope().getNickName());
                                        intent.putExtra("money", response.getMoneyEnvelope().getAmount());
                                        intent.putExtra("ownerid", response.getMoneyEnvelope().getUserId());
                                        intent.putExtra("note", response.getMoneyEnvelope().getNote());
                                        intent.putExtra("nicknames", response.getMoneyEnvelope().getNickNames());
                                        intent.putExtra("moneys", response.getMoneyEnvelope().getMoneyReceives());
                                        intent.putExtra("ids", response.getMoneyEnvelope().getUserIds());
                                        mContext.startActivity(intent);
                                    } else {
                                        if(total > 0) {
                                            Double balance = Double.valueOf(Settings.instance().User.getBalance());
                                            balance += total;
                                            Settings.instance().User.setBalance(balance.toString());
                                        }
                                        if (redBagListener != null) {
                                            ChatMsg chatMsg = mList.get(position);
                                            redBagListener.myRedBag(chatMsg.getSenderPortrait(), response.getMoneyEnvelope().getNickName(), response.getMoney(), response.getMoneyEnvelope().getNote(), response.getMoneyEnvelope().getId());
                                        }
                                    }
                                    WaitDialog.instance().hideWaitNote();
                                } else {
                                    WaitDialog.instance().hideWaitNote();
                                }
                            }
                        });

    }
}
