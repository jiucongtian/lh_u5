package com.longhuapuxin.entity;

/**
 * Created by asus on 2016/1/6.
 */
public class PushResponseShopAppointment extends PushResponseDad {
    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    private String Text;
    private String Time;
}
