package com.longhuapuxin.entity;

import java.util.List;

/**
 * Created by asus on 2016/1/5.
 */
public class ResponseGetAppoints extends ResponseDad {
    private int Total;

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }

    public List<Appoint> getAppoints() {
        return Appoints;
    }

    public void setAppoints(List<Appoint> appoints) {
        Appoints = appoints;
    }

    private List<Appoint> Appoints;

    public class Appoint {
        private String UserId;
        private String NickName;

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String userId) {
            UserId = userId;
        }

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String nickName) {
            NickName = nickName;
        }

        public String getUserPhone() {
            return UserPhone;
        }

        public void setUserPhone(String userPhone) {
            UserPhone = userPhone;
        }

        public String getShopCode() {
            return ShopCode;
        }

        public void setShopCode(String shopCode) {
            ShopCode = shopCode;
        }

        public String getShopPhone() {
            return ShopPhone;
        }

        public void setShopPhone(String shopPhone) {
            ShopPhone = shopPhone;
        }

        public String getShopName() {
            return ShopName;
        }

        public void setShopName(String shopName) {
            ShopName = shopName;
        }

        public String getTime1() {
            return Time1;
        }

        public void setTime1(String time1) {
            Time1 = time1;
        }

        public String getTime2() {
            return Time2;
        }

        public void setTime2(String time2) {
            Time2 = time2;
        }

        public String getNote() {
            return Note;
        }

        public void setNote(String note) {
            Note = note;
        }

        public String getAddress() {
            return Address;
        }

        public void setAddress(String address) {
            Address = address;
        }

        public String getState() {
            return State;
        }

        public void setState(String state) {
            State = state;
        }

        private String UserPhone;
        private String ShopCode;
        private String ShopPhone;
        private String ShopName;
        private String Time1;
        private String Time2;
        private String Note;
        private String Address;
        private String State;
        private String PublishDate;
        private String ResponseDate;
        private String MeetDate;



        public String getId() {
            return Id;
        }

        public void setId(String id) {
            Id = id;
        }

        private String Id;

        public String getProduct() {
            return Product;
        }

        public void setProduct(String product) {
            Product = product;
        }

        public String getPublishDate() {
            return PublishDate;
        }

        public void setPublishDate(String publishDate) {
            PublishDate = publishDate;
        }

        public String getResponseDate() {
            return ResponseDate;
        }

        public void setResponseDate(String responseDate) {
            ResponseDate = responseDate;
        }

        public String getMeetDate() {
            return MeetDate;
        }

        public void setMeetDate(String meetDate) {
            MeetDate = meetDate;
        }

        public String getPayDate() {
            return PayDate;
        }

        public void setPayDate(String payDate) {
            PayDate = payDate;
        }

        private String PayDate;
        private String Product;

        private String FeedbackId;

        public String getFeeBackId() {
            return FeedbackId;
        }

        public void setFeeBackId(String feeBackId) {
            FeedbackId = feeBackId;
        }
    }
}
