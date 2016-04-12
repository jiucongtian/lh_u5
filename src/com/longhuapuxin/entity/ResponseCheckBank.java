package com.longhuapuxin.entity;

/**
 * Created by lsy on 2016/2/19.
 */
public class ResponseCheckBank  extends ResponseDad {

    private String RealName,BankName,BankNo,BankLocation,StatusText;

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getBankNo() {
        return BankNo;
    }

    public void setBankNo(String bankNo) {
        BankNo = bankNo;
    }

    public String getBankLocation() {
        return BankLocation;
    }

    public void setBankLocation(String bankLocation) {
        BankLocation = bankLocation;
    }

    public String getStatusText() {
        return StatusText;
    }

    public void setStatusText(String statusText) {
        StatusText = statusText;
    }


}
