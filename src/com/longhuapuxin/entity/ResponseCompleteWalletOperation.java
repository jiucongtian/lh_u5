package com.longhuapuxin.entity;

/**
 * Created by asus on 2016/1/27.
 */
public class ResponseCompleteWalletOperation extends ResponseDad {

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    private String Time;

    public String getBounsId() {
        return BounsId;
    }

    public void setBounsId(String bounsId) {
        BounsId = bounsId;
    }

    private String BounsId;
}
