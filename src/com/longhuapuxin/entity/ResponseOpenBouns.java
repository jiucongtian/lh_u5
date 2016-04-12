package com.longhuapuxin.entity;

/**
 * Created by asus on 2016/2/24.
 */
public class ResponseOpenBouns extends ResponseDad {
    private String Amount;

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    private String Note;
}
