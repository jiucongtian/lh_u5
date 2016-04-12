package com.longhuapuxin.entity;

/**
 * Created by ZH on 2016/1/13.
 * Email zh@longhuapuxin.com
 */
public class BindBankResponse extends ResponseDad {
    private String Id;
    private String Amount;
    private String Sequence;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getSequence() {
        return Sequence;
    }

    public void setSequence(String sequence) {
        Sequence = sequence;
    }
}
