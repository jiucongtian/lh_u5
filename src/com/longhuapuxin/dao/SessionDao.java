package com.longhuapuxin.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.longhuapuxin.db.bean.ChatMessage;
import com.longhuapuxin.db.bean.ChatSession;

public class SessionDao {
    private Dao<ChatSession, Integer> sessionDaoOpe;
    private DatabaseHelper helper;

    @SuppressWarnings("unchecked")
    public SessionDao(Context context) {
        try {
            helper = DatabaseHelper.getHelper(context);
            sessionDaoOpe = helper.getDao(ChatSession.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void add(ChatSession session) {
        try {
            sessionDaoOpe.create(session);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasUnReadMsg() {
        QueryBuilder<ChatSession, Integer> builder = sessionDaoOpe
                .queryBuilder();
        boolean result = false;
        try {
            List<ChatSession> unReadList = builder.where().not()
                    .eq("un_read_count", 0).query();
            if (unReadList.size() > 0) {
                result = true;
            } else {
                result = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<ChatSession> fetchAll() {
        List<ChatSession> list = new ArrayList<ChatSession>();
        try {
            list = sessionDaoOpe.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getSessionList() {
        List<String> sessions = new ArrayList<String>();
        try {
            List<ChatSession> queryForAll = sessionDaoOpe.queryForAll();
            for (ChatSession chatSession : queryForAll) {
                if (!sessions.contains(chatSession.getSessionId()) && !sessions.contains(chatSession.isGroup())) {
                    sessions.add(chatSession.getSessionId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    public void update(ChatSession chatSession) {
        try {
            sessionDaoOpe.update(chatSession);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ChatSession> getMessageBySession(String sessionId, boolean isGroup) {
        List<ChatSession> list = new ArrayList<ChatSession>();
        try {
            QueryBuilder qb = sessionDaoOpe.queryBuilder();
            qb.where().eq("session_id", sessionId).and().eq("is_group", isGroup);
            list = qb.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ChatSession> getMessageBySession(String sessionId) {
        List<ChatSession> list = new ArrayList<ChatSession>();
        try {
            list = sessionDaoOpe.queryForEq("session_id", sessionId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteMessage(ChatSession chatSession) {
        try {
            sessionDaoOpe.delete(chatSession);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
