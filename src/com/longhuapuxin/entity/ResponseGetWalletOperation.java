package com.longhuapuxin.entity;

/**
 * Created by asus on 2016/1/12.
 */
public class ResponseGetWalletOperation extends ResponseDad {
    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    private String Status;
}
