package com.longhuapuxin.db.bean;

/**
 * Created by lsy on 2016/3/10.
 */
public class BounsSetting {
    private int Id;
    private double AmountFrom,MaxVal,MinVal;
    private String Note;

    public double getMinVal() {
        return MinVal;
    }

    public void setMinVal(double minVal) {
        MinVal = minVal;
    }

    public double getMaxVal() {
        return MaxVal;
    }

    public void setMaxVal(double maxVal) {
        MaxVal = maxVal;
    }

    public double getAmountFrom() {
        return AmountFrom;
    }

    public void setAmountFrom(double amountFrom) {
        AmountFrom = amountFrom;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
}
