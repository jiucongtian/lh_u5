package com.longhuapuxin.db.bean;

import java.io.Serializable;

/**
 * Created by asus on 2016/1/20.
 */
public class GroupMsg implements Serializable {
    private String CircleId;
    private String Time;
    private String circleName;

    public String getPortrait() {
        return Portrait;
    }

    public void setPortrait(String portrait) {
        Portrait = portrait;
    }

    public String getCircleId() {
        return CircleId;
    }

    public void setCircleId(String circleId) {
        CircleId = circleId;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    private String SenderId;
    private String Text;
    private String NickName;
    private String Portrait;

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String name) {
        circleName = name;
    }
}
