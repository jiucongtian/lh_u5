package com.longhuapuxin.db.bean;
public class DiscountTicket {
	private String Code;
	private String ShopCode;
	private String ShopName;
	private String CouponName;
	private String CouponNote;
	private String Discount;
	private String PublishDate;
	private String ActiveDate;
	private boolean IsActive;
	private String DueTo;
	private boolean HasReceive;
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
	public String getCouponName() {
		return CouponName;
	}
	public void setCouponName(String couponName) {
		CouponName = couponName;
	}
	public String getCouponNote() {
		return CouponNote;
	}
	public void setCouponNote(String couponNote) {
		CouponNote = couponNote;
	}
	public String getDiscount() {
		return Discount;
	}
	public void setDiscount(String discount) {
		Discount = discount;
	}
	public boolean isIsActive() {
		return IsActive;
	}
	public void setIsActive(boolean isActive) {
		IsActive = isActive;
	}
	public boolean isHasReceive() {
		return HasReceive;
	}
	public void setHasReceive(boolean hasReceive) {
		HasReceive = hasReceive;
	}
	public String getPublishDate() {
		return PublishDate;
	}
	public void setPublishDate(String publishDate) {
		PublishDate = publishDate;
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
	
}
