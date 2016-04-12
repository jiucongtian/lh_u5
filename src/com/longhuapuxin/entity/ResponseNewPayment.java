package com.longhuapuxin.entity;

/**
 * Created by asus on 2016/2/24.
 */
public class ResponseNewPayment extends ResponseDad {
    public String getPaymentCode() {
        return PaymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        PaymentCode = paymentCode;
    }

    private String PaymentCode;
}
