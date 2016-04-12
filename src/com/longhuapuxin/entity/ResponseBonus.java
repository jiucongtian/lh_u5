package com.longhuapuxin.entity;

import java.util.List;

/**
 * Created by ZH on 2016/2/23.
 * Email zh@longhuapuxin.com
 */
public class ResponseBonus extends ResponseDad {


    private List<Bonus> Bounses;

    public List<Bonus> getBounses() {
        return Bounses;
    }

    public void setBounses(List<Bonus> bounses) {
        Bounses = bounses;
    }

    public class Bonus {
        private String Id;
        private String ShopCode;
        private String ShopName;
        private String UserId;
        private String NickName;
        private String Amount;
        private boolean HasGot;
        private String CreateTime;
        private String Note;

        public String getId() {
            return Id;
        }

        public void setId(String id) {
            Id = id;
        }

        public String getShopCode() {
            return ShopCode;
        }

        public void setShopCode(String shopCode) {
            ShopCode = shopCode;
        }

        public String getShopName() {
            return ShopName;
        }

        public void setShopName(String shopName) {
            ShopName = shopName;
        }

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

        public String getAmount() {
            return Amount;
        }

        public void setAmount(String amount) {
            Amount = amount;
        }

        public boolean isHasGot() {
            return HasGot;
        }

        public void setHasGot(boolean hasGot) {
            HasGot = hasGot;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String createTime) {
            CreateTime = createTime;
        }

        public String getNote() {
            return Note;
        }

        public void setNote(String note) {
            Note = note;
        }
    }
}
