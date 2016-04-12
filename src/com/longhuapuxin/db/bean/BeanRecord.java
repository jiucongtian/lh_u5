package com.longhuapuxin.db.bean;

import java.sql.Date;

public class BeanRecord {

    private int ID;

    private int UserId;

    private double LastBalance1;

    private double LastBalance2;

    private double BalancetChange1;

    private double BalanceChange2;

    private double Balance1;

    private double Balance2;

    private String RecordTime;

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public double getLastBalance1() {
        return LastBalance1;
    }

    public void setLastBalance1(double lastBalance1) {
        LastBalance1 = lastBalance1;
    }

    public double getLastBalance2() {
        return LastBalance2;
    }

    public void setLastBalance2(double lastBalance2) {
        LastBalance2 = lastBalance2;
    }

    public double getBalancetChange1() {
        return BalancetChange1;
    }

    public void setBalancetChange1(double balancetChange1) {
        BalancetChange1 = balancetChange1;
    }

    public double getBalanceChange2() {
        return BalanceChange2;
    }

    public void setBalanceChange2(double balanceChange2) {
        BalanceChange2 = balanceChange2;
    }

    public double getBalance1() {
        return Balance1;
    }

    public void setBalance1(double balance1) {
        Balance1 = balance1;
    }

    public double getBalance2() {
        return Balance2;
    }

    public void setBalance2(double balance2) {
        Balance2 = balance2;
    }

    public String getRecordTime() {
        return RecordTime;
    }

    public void setRecordTime(String recordTime) {
        RecordTime = recordTime;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public String getPaymentCode() {
        return PaymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        PaymentCode = paymentCode;
    }

    public String getRecordNote() {
        return RecordNote;
    }

    public void setRecordNote(String recordNote) {
        RecordNote = recordNote;
    }

    private String Reason;

    private String PaymentCode;

    private String RecordNote;

    public String getRecordTikert() {
        return RecordTikert;
    }

    public void setRecordTikert(String recordTikert) {
        RecordTikert = recordTikert;
    }

    private String RecordTikert;
}
