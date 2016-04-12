package com.longhuapuxin.entity;

import java.util.Date;
import java.util.List;

public class ResponseGetConsumeList extends ResponseDad {
	public class Payment {
		private String Code;
		private String ShopCode;
		private String ShopName;
		private Double Total;
		private Double Discount;

		public String getCode() {
			return Code;
		}

		public void setCode(String code) {
			Code = code;
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

		public Double getTotal() {
			return Total;
		}

		public void setTotal(Double total) {
			Total = total;
		}

		public Double getDiscount() {
			return Discount;
		}

		public void setDiscount(Double discount) {
			Discount = discount;
		}

		public Double getAmount() {
			return Amount;
		}

		public void setAmount(Double amount) {
			Amount = amount;
		}

		public String getInsertDate() {
			return InsertDate;
		}

		public void setInsertDate(String insertDate) {
			InsertDate = insertDate;
		}

		public Boolean getSuccess() {
			return Success;
		}

		public void setSuccess(Boolean success) {
			Success = success;
		}

		public int getShopUserId() {
			return ShopUserId;
		}

		public void setShopUserId(int shopUserId) {
			ShopUserId = shopUserId;
		}

		public String getCouponCode() {
			return CouponCode;
		}

		public void setCouponCode(String couponCode) {
			CouponCode = couponCode;
		}

		public String getCouponName() {
			return CouponName;
		}

		public void setCouponName(String couponName) {
			CouponName = couponName;
		}

		private Double Amount;
		private String InsertDate;
		private Boolean Success;
		private int ShopUserId;
		private String CouponCode;
		private String CouponName;
		private String FeeBackId;

		public String getFeeBackId() {
			return FeeBackId;
		}

		public void setFeeBackId(String feeBackId) {
			FeeBackId = feeBackId;
		}
	}

	private int Total;
	private List<Payment> Payments;

	public int getTotal() {
		return Total;
	}

	public void setTotal(int total) {
		Total = total;
	}

	public List<Payment> getPayments() {
		return Payments;
	}

	public void setPayments(List<Payment> payments) {
		Payments = payments;
	}
}
