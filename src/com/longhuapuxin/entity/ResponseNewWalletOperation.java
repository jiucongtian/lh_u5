package com.longhuapuxin.entity;

import java.util.List;

/**
 * Created by asus on 2016/1/12.
 */
public class ResponseNewWalletOperation extends ResponseDad {
    private String Id;

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    private String NickName;
    private String ShopName;
    private String Phone;
    private String Discount;

    public List<BounsSetting> getBounsSettings() {
        return BounsSettings;
    }

    public void setBounsSettings(List<BounsSetting> bounsSettings) {
        BounsSettings = bounsSettings;
    }


    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    private List<BounsSetting> BounsSettings;

    public String getId() {
        return Id;
    }

    public void setID(String Id) {
        this.Id = Id;
    }


    public class BounsSetting {
        public String getMinVal() {
            return MinVal;
        }

        public void setMinVal(String minVal) {
            MinVal = minVal;
        }

        public String getMaxVal() {
            return MaxVal;
        }

        public void setMaxVal(String maxVal) {
            MaxVal = maxVal;
        }

        public String getAmountFrom() {
            return AmountFrom;
        }

        public void setAmountFrom(String amountFrom) {
            AmountFrom = amountFrom;
        }

        private String MinVal;
        private String MaxVal;
        private String AmountFrom;
    }
}
