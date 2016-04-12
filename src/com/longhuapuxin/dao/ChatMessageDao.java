package com.longhuapuxin.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.longhuapuxin.db.bean.ChatMessage;
import com.longhuapuxin.db.bean.User;

public class ChatMessageDao {

    private Dao<ChatMessage, Integer> messageDaoOpe;
    private DatabaseHelper helper;

    @SuppressWarnings("unchecked")
    public ChatMessageDao(Context context) {
        try {
            helper = DatabaseHelper.getHelper(context);
            messageDaoOpe = helper.getDao(ChatMessage.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void add(ChatMessage chatMessage) {
        try {
            messageDaoOpe.create(chatMessage);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ChatMessage> getMessageBySession(String sessionId, Date fromDate, boolean isGroup) {
        List<ChatMessage> list = new ArrayList<ChatMessage>();
        QueryBuilder<ChatMessage, Integer> builder = messageDaoOpe
                .queryBuilder();
        fromDate.setTime(fromDate.getTime() + 1);
        try {
            // 获取明天的时间
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            date = cal.getTime();
            list = builder.orderBy("time", true)
                    ./* limit((long) count). */where()
                    .eq("session_id", sessionId).and().eq("is_group", isGroup).and()
                    .between("time", fromDate, date).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<ChatMessage> getMessageBySession(String sessionId, boolean isGroup) {
        List<ChatMessage> list = new ArrayList<ChatMessage>();
        try {
            list = messageDaoOpe.queryForEq("session_id", sessionId);
            if (list.size() > 0) {
                list = messageDaoOpe.queryForEq("is_group", isGroup);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ChatMessage> getMessageByOrderId(String orderId) {
        List<ChatMessage> list = new ArrayList<ChatMessage>();
        try {
            list = messageDaoOpe.queryForEq("order_id", orderId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateMessageByOrderId(ChatMessage chatMessage) {
        try {
            messageDaoOpe.update(chatMessage);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public List<ChatMessage> fetchAll() {
        List<ChatMessage> list = new ArrayList<ChatMessage>();
        try {
            list = messageDaoOpe.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void deleteChatMessage(ChatMessage chatMessage) {
        try {
            messageDaoOpe.delete(chatMessage);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public List<String> getSessionList() {
        List<String> sessions = new ArrayList<String>();
        try {
            List<ChatMessage> queryForAll = messageDaoOpe.queryForAll();
            for (ChatMessage chatMessage : queryForAll) {
                if (!sessions.contains(chatMessage.getSessionId())) {
                    sessions.add(chatMessage.getSessionId());
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sessions;
    }

}
