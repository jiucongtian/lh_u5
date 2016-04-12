package com.longhuapuxin.entity;

/**
 * Created by ZH on 2016/1/14.
 * Email zh@longhuapuxin.com
 */
public class ResponseVerify extends ResponseDad {
    private String Id;
    private Double Amount;
    private Integer Sequence;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Double getAmout() {
        return Amount;
    }

    public void setAmout(Double amout) {
        Amount = amout;
    }

    public Integer getSequence() {
        return Sequence;
    }

    public void setSequence(Integer sequence) {
        Sequence = sequence;
    }
}
