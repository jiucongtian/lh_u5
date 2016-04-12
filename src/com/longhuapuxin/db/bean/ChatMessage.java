package com.longhuapuxin.db.bean;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author zh
 * @date 2015-8-28
 */
@DatabaseTable(tableName = "chat_message")
public class ChatMessage implements Comparable<ChatMessage> {
    @Override
    public String toString() {
        return "ChatMessage{" +
                "messageId=" + messageId +
                ", SessionId='" + SessionId + '\'' +
                ", Text='" + Text + '\'' +
                ", OrderId='" + OrderId + '\'' +
                ", Time=" + Time +
                ", receiverStatus='" + receiverStatus + '\'' +
                ", sendStatus='" + sendStatus + '\'' +
                ", type=" + type +
                ", isGroup=" + isGroup +
                ", senderPortrait='" + senderPortrait + '\'' +
                '}';
    }

    @DatabaseField(columnName = "message_id", generatedId = true, canBeNull = false)
    private int messageId;
    @DatabaseField(columnName = "session_id")
    private String SessionId;
    @DatabaseField(columnName = "text")
    private String Text;
    @DatabaseField(columnName = "order_id")
    private String OrderId;
    @DatabaseField(columnName = "time")
    private Date Time;
    @DatabaseField(columnName = "receiver_status")
    private String receiverStatus;
    @DatabaseField(columnName = "send_status")
    private String sendStatus;
    @DatabaseField(columnName = "type")
    private int type;
    @DatabaseField(columnName = "is_group")
    private boolean isGroup;


    public String getSenderPortrait() {
        return senderPortrait;
    }

    public void setSenderPortrait(String senderPortrait) {
        this.senderPortrait = senderPortrait;
    }

    @DatabaseField(columnName = "send_portrait")
    private String senderPortrait;

    public boolean isGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(String sessionId) {
        this.SessionId = sessionId;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        this.Text = text;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        this.OrderId = orderId;
    }

    public Date getTime() {
        return Time;
    }

    public void setTime(Date time) {
        this.Time = time;
    }

    public String getReceiverStatus() {
        return receiverStatus;
    }

    public void setReceiverStatus(String receiverStatus) {
        this.receiverStatus = receiverStatus;
    }

    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int compareTo(ChatMessage another) {
        int i;
        if (this.getTime().after(another.getTime())) {
            i = 1;
        } else if (this.getTime().before(another.getTime())) {
            i = -1;
        } else {
            i = 0;
        }

        return i;
    }

}
