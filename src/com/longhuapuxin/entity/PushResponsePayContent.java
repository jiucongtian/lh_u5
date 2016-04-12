package com.longhuapuxin.entity;

import java.io.Serializable;

public class PushResponsePayContent extends PushResponseDad implements
		Serializable {

	private String Code;

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}

	public String getDiscount() {
		return Discount;
	}

	public void setDiscount(String discount) {
		Discount = discount;
	}

	public String getTotal() {
		return Total;
	}

	public void setTotal(String total) {
		Total = total;
	}

	private String Amount;
	private String Discount;
	private String Total;

	// private String Code;
	// private String ShopCode;
	//
	// public String getCode() {
	// return Code;
	// }
	//
	// public void setCode(String code) {
	// Code = code;
	// }
	//
	// public String getShopCode() {
	// return ShopCode;
	// }
	//
	// public void setShopCode(String shopCode) {
	// ShopCode = shopCode;
	// }
	//
	public String getShopName() {
		return ShopName;
	}

	public void setShopName(String shopName) {
		ShopName = shopName;
	}

	//
	// public String getHasCoupon() {
	// return HasCoupon;
	// }
	//
	// public void setHasCoupon(String hasCoupon) {
	// HasCoupon = hasCoupon;
	// }
	//
	// public String getTotal() {
	// return Total;
	// }
	//
	// public void setTotal(String total) {
	// Total = total;
	// }
	//
	// public String getDiscount() {
	// return Discount;
	// }
	//
	// public void setDiscount(String discount) {
	// Discount = discount;
	// }
	//
	// public String getAmount() {
	// return Amount;
	// }
	//
	// public void setAmount(String amount) {
	// Amount = amount;
	// }
	//
	// public String getInsertDate() {
	// return InsertDate;
	// }
	//
	// public void setInsertDate(String insertDate) {
	// InsertDate = insertDate;
	// }
	//
	// public String getUserId() {
	// return UserId;
	// }
	//
	// public void setUserId(String userId) {
	// UserId = userId;
	// }
	//
	// public String getNickName() {
	// return NickName;
	// }
	//
	// public void setNickName(String nickName) {
	// NickName = nickName;
	// }
	//
	// public String getPaymentNote() {
	// return PaymentNote;
	// }
	//
	// public void setPaymentNote(String paymentNote) {
	// PaymentNote = paymentNote;
	// }
	//
	// public String getPaymentWay() {
	// return PaymentWay;
	// }
	//
	// public void setPaymentWay(String paymentWay) {
	// PaymentWay = paymentWay;
	// }
	//
	// public String getPaymentTime() {
	// return PaymentTime;
	// }
	//
	// public void setPaymentTime(String paymentTime) {
	// PaymentTime = paymentTime;
	// }
	//
	// public String getTicket() {
	// return Ticket;
	// }
	//
	// public void setTicket(String ticket) {
	// Ticket = ticket;
	// }
	//
	// public boolean isSuccess() {
	// return Success;
	// }
	//
	// public void setSuccess(boolean success) {
	// Success = success;
	// }
	//
	// public String getShopUserId() {
	// return ShopUserId;
	// }
	//
	// public void setShopUserId(String shopUserId) {
	// ShopUserId = shopUserId;
	// }
	//
	// public String getCouponCode() {
	// return CouponCode;
	// }
	//
	// public void setCouponCode(String couponCode) {
	// CouponCode = couponCode;
	// }
	//
	// public String getCouponName() {
	// return CouponName;
	// }
	//
	// public void setCouponName(String couponName) {
	// CouponName = couponName;
	// }
	//
	private String ShopName;
	// private String HasCoupon;
	// // / <summary>
	// // / 消费金额
	// // / </summary>
	// private String Total;
	// private String Discount;
	// // / <summary>
	// // / 应付金额
	// // / </summary>
	// private String Amount;
	// private String InsertDate;
	// private String UserId;
	// private String NickName;
	// private String PaymentNote;
	// private String PaymentWay;
	// private String PaymentTime;
	// private String Ticket;
	// private boolean Success;
	// private String ShopUserId;
	// private String CouponCode;
	// private String CouponName;

}
