package com.longhuapuxin.db.bean;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

public class ChatSession implements Comparable<ChatSession> {

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false)
    private int Id;

    @DatabaseField(columnName = "session_id")
    private String SessionId;

    @DatabaseField(columnName = "sender_nickName")
    private String senderNickName;

    @DatabaseField(columnName = "sender_user_id")
    private String senderUserId;

    @DatabaseField(columnName = "sender_portrait")
    private String senderPortrait;

    @DatabaseField(columnName = "time")
    private Date time;

    @DatabaseField(columnName = "text")
    private String Text;

    @DatabaseField(columnName = "un_read_count")
    private int unReadCount;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(String sessionId) {
        SessionId = sessionId;
    }

    public String getSenderNickName() {
        return senderNickName;
    }

    public void setSenderNickName(String senderNickName) {
        this.senderNickName = senderNickName;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getSenderPortrait() {
        return senderPortrait;
    }

    public void setSenderPortrait(String senderPortrait) {
        this.senderPortrait = senderPortrait;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    @DatabaseField(columnName = "sender_gender")
    private String senderGender;

    public String getSenderGender() {
        return senderGender;
    }

    public void setSenderGender(String senderGender) {
        this.senderGender = senderGender;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    @DatabaseField(columnName = "is_group")
    private boolean isGroup;
    @DatabaseField(columnName = "group_owner_id")
    private String ownerId;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupNote() {
        return groupNote;
    }

    public void setGroupNote(String groupNote) {
        this.groupNote = groupNote;
    }

    @DatabaseField(columnName = "group_name")
    private String groupName;
    @DatabaseField(columnName = "group_note")
    private String groupNote;

    @Override
    public int compareTo(ChatSession another) {
        int i;
        if (this.getTime().after(another.getTime())) {
            i = -1;
        } else if (this.getTime().before(another.getTime())) {
            i = 1;
        } else {
            i = 0;
        }

        return i;
    }
}
