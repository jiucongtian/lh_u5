package com.longhuapuxin.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.longhuapuxin.common.DateFormate;
import com.longhuapuxin.common.ImageUrlLoader;
import com.longhuapuxin.dao.SessionDao;
import com.longhuapuxin.db.bean.ChatSession;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.view.SlideView;

import java.util.ArrayList;
import java.util.List;

public class MessageCanDeleteAdapter extends U5BaseAdapter<ChatSession>
        implements OnClickListener {
    private List<ChatSession> list;
    private LayoutInflater mInflater;
    private Context context;
    private List<Integer> deleteIds;
    private List<String> idList = new ArrayList<String>();
    private List<SlideView> slideViewList;
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                ChatSession chatSession = (ChatSession) msg.obj;
                list.remove(chatSession);
                deleteChatSessionDB(chatSession);
                notifyDataSetChanged();
            }
            return false;
        }
    });

    public MessageCanDeleteAdapter(Context context, List<ChatSession> list) {
        super(context, list);
        this.list = list;
        mInflater = LayoutInflater.from(context);
        this.context = context;
        deleteIds = new ArrayList<Integer>();
        slideViewList = new ArrayList<SlideView>();
        setIdList();
    }

    private void setIdList() {
        for (ChatSession chatSession : list) {
            if (!chatSession.isGroup()) {
                idList.add(chatSession.getSenderPortrait());
            }
        }
        ImageUrlLoader.fetchImageUrl(this, idList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        SlideView slideView = (SlideView) convertView;
        if (slideView == null) {
            View itemView = mInflater.inflate(R.layout.item_message_can_delete,
                    null);

            slideView = new SlideView(context);
            slideViewList.add(slideView);
            slideView.setContentView(itemView);

            holder = new ViewHolder(slideView);
            slideView.setTag(holder);
        } else {
            holder = (ViewHolder) slideView.getTag();
        }
        ChatSession item = list.get(position);
        // if (Settings.instance().getUserId() == item.getUserId1()) {
        // bindImageView(holder.icon, item.getPortrait2());
        // holder.title.setText(item.getNickName1());
        // } else {
        // bindImageView(holder.icon, item.getPortrait1());
        // holder.title.setText(item.getNickName2());
        // }
        if (!item.isGroup()) {
            bindImageView(holder.icon, item.getSenderPortrait());
            holder.title.setText(item.getSenderNickName());
            holder.msg.setText(item.getText());
            if (item.getTime() != null) {
                holder.time.setText(DateFormate.Date2String(item.getTime()));
            } else {
                holder.time.setText("test");
            }
        } else {
            holder.icon.setImageResource(R.drawable.attention_picture);
            holder.title.setText(item.getGroupName());
            holder.msg.setText(item.getText());
            holder.time.setText(DateFormate.Date2String(item.getTime()));
        }
        if (item.getUnReadCount() > 0) {
            holder.unReadIcon.setVisibility(View.VISIBLE);
        } else {
            holder.unReadIcon.setVisibility(View.INVISIBLE);
        }
        holder.deleteHolder.setId(position);
        if (!deleteIds.contains(position)) {
            deleteIds.add(position);
        }
        holder.deleteHolder.setOnClickListener(this);

        return slideView;
    }

    public class ViewHolder {
        public ImageView icon;
        public TextView title;
        public TextView msg;
        public TextView time;
        public ImageView unReadIcon;
        public ViewGroup deleteHolder;

        ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.icon);
            title = (TextView) view.findViewById(R.id.title);
            msg = (TextView) view.findViewById(R.id.msg);
            time = (TextView) view.findViewById(R.id.time);
            unReadIcon = (ImageView) view.findViewById(R.id.unReadIcon);
            deleteHolder = (ViewGroup) view.findViewById(R.id.holder);
        }
    }

    @Override
    public void onClick(View v) {
        for (Integer position : deleteIds) {
            if (v.getId() == position) {
                ChatSession chatSession = list.get(position);
                list.remove(chatSession);
                deleteChatSessionDB(chatSession);
                closeSlideView(chatSession, position);
                notifyDataSetChanged();

            }
        }
    }

    private void closeSlideView(ChatSession chatSession, int position) {
        SlideView slideView = slideViewList.get(position);
        slideView.shrink();
    }

    public void closeAllSlideView() {
        for (SlideView slideView : slideViewList) {
            if (slideView.getScrollX() > 0) {
                slideView.shrink();
            }
        }
    }

    public void refresh() {
        setIdList();
        notifyDataSetChanged();
    }

    private void deleteChatSessionDB(ChatSession chatSession) {
        SessionDao sessionHelper = new SessionDao(context);
        sessionHelper.deleteMessage(chatSession);
    }
}
