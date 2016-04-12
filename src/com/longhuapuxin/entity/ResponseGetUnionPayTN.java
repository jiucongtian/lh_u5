package com.longhuapuxin.entity;

/**
 * Created by asus on 2016/3/3.
 */
public class ResponseGetUnionPayTN extends ResponseDad {
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTN() {
        return TN;
    }

    public void setTN(String TN) {
        this.TN = TN;
    }

    private String Id;
    private String TN;
}
