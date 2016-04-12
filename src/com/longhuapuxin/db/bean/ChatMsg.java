package com.longhuapuxin.db.bean;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;

public class ChatMsg implements Serializable {
    @Override
    public String toString() {
        return "ChatMsg{" +
                "SessionId='" + SessionId + '\'' +
                ", Text='" + Text + '\'' +
                ", OrderId='" + OrderId + '\'' +
                ", Time='" + Time + '\'' +
                ", receiverStatus='" + receiverStatus + '\'' +
                ", sendStatus='" + sendStatus + '\'' +
                ", type=" + type +
                ", isGroup=" + isGroup +
                ", senderPortrait='" + senderPortrait + '\'' +
                '}';
    }

    private String SessionId;
    private String Text;

    private String OrderId;
    private String Time;
    // 判断的是接受方接受或者拒绝
    private String receiverStatus;
    // 判断的是发送方接受或者拒绝
    private String sendStatus;
    private int type;

    public boolean isGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    private boolean isGroup;

    public String getReceiverStatus() {
        return receiverStatus;
    }

    public void setReceiverStatus(String receiverStatus) {
        this.receiverStatus = receiverStatus;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        this.Time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        this.OrderId = orderId;
    }

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(String sessionId) {
        SessionId = sessionId;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getSenderPortrait() {
        return senderPortrait;
    }

    public void setSenderPortrait(String senderPortrait) {
        this.senderPortrait = senderPortrait;
    }

    private String senderPortrait;


}
