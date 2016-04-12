package com.longhuapuxin.db.bean;

/**
 * Created by lsy on 2016/3/10.
 */
public class Estimation {
    private int Id,OrderType,ReplyId,OwnerUserId,UserId;
    private String OrderId,OwnerShopCode,NickName,ShopName,Text,Photos,FeedBackTime;
    private float Estimate1;
    private float Estimate2;
    private float Estimate3;
    private float Estimate4;

    public float getEstimate5() {
        return Estimate5;
    }

    public void setEstimate5(float estimate5) {
        Estimate5 = estimate5;
    }

    private float Estimate5;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getOrderType() {
        return OrderType;
    }

    public void setOrderType(int orderType) {
        OrderType = orderType;
    }

    public int getReplyId() {
        return ReplyId;
    }

    public void setReplyId(int replyId) {
        ReplyId = replyId;
    }

    public int getOwnerUserId() {
        return OwnerUserId;
    }

    public void setOwnerUserId(int ownerUserId) {
        OwnerUserId = ownerUserId;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getOwnerShopCode() {
        return OwnerShopCode;
    }

    public void setOwnerShopCode(String ownerShopCode) {
        OwnerShopCode = ownerShopCode;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getPhotos() {
        return Photos;
    }

    public void setPhotos(String photos) {
        Photos = photos;
    }

    public String getFeedBackTime() {
        return FeedBackTime;
    }

    public void setFeedBackTime(String feedBackTime) {
        FeedBackTime = feedBackTime;
    }

    public float getEstimate1() {
        return Estimate1;
    }

    public void setEstimate1(float estimate1) {
        Estimate1 = estimate1;
    }

    public float getEstimate2() {
        return Estimate2;
    }

    public void setEstimate2(float estimate2) {
        Estimate2 = estimate2;
    }

    public float getEstimate3() {
        return Estimate3;
    }

    public void setEstimate3(float estimate3) {
        Estimate3 = estimate3;
    }

    public float getEstimate4() {
        return Estimate4;
    }

    public void setEstimate4(float estimate4) {
        Estimate4 = estimate4;
    }
}
