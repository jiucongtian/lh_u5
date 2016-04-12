package com.longhuapuxin.db.bean;

public class DiscountCoupon {
	private String Code;
	private String Description;
	private String ShopLogo;
	private String CouponName;
	private String CouponNote;
	private String Discount;
	private String ActiveDate;
	private String DueTo;
	private String ShopName;
	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getShopLogo() {
		return ShopLogo;
	}

	public void setShopLogo(String shopLogo) {
		ShopLogo = shopLogo;
	}

	public String getCouponNote() {
		return CouponNote;
	}

	public void setCouponNote(String couponNote) {
		CouponNote = couponNote;
	}

	public String getCouponName() {
		return CouponName;
	}

	public void setCouponName(String couponName) {
		CouponName = couponName;
	}

	public String getDiscount() {
		return Discount;
	}

	public void setDiscount(String discount) {
		Discount = discount;
	}

	public String getActiveDate() {
		return ActiveDate;
	}

	public void setActiveDate(String activeDate) {
		ActiveDate = activeDate;
	}

	public String getDueTo() {
		return DueTo;
	}

	public void setDueTo(String dueTo) {
		DueTo = dueTo;
	}
	public boolean isHasDueTo() {
		return HasDueTo;
	}

	public void setHasDueTo(boolean hasDueTo) {
		HasDueTo = hasDueTo;
	}

	public String getShopName() {
		return ShopName;
	}

	public void setShopName(String shopName) {
		ShopName = shopName;
	}

	private boolean HasDueTo;
}

