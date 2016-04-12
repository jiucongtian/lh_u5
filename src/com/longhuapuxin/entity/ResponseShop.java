package com.longhuapuxin.entity;

import java.util.List;

import com.longhuapuxin.db.bean.BounsSetting;
import com.longhuapuxin.db.bean.DiscountTicket;

public class ResponseShop extends ResponseDad {
	private Shop Shop;

	public Shop getShop() {
		return Shop;
	}

	public void setShop(Shop shop) {
		this.Shop = shop;
	}

	public class Shop {
		private String Code;
		private String Name;
		private String Address;
		private String Phone;
		private Double Longitude;
		private Double Latitude;
		private String Logo;
		private String Description;
		private String ReceiptCount;
		private String HasCoupon;
		private String Balance;
		private String Photos;
		private boolean HasWIFI;
		private boolean HasParking;
		private String WorkTime;
		private List<DiscountTicket> DiscountCoupon;
		private List<BounsSetting> BounsSettings;
		public String getCode() {
			return Code;
		}

		public void setCode(String code) {
			Code = code;
		}

		public String getName() {
			return Name;
		}

		public void setName(String name) {
			Name = name;
		}

		public String getAddress() {
			return Address;
		}

		public void setAddress(String address) {
			Address = address;
		}

		public String getPhone() {
			return Phone;
		}

		public void setPhone(String phone) {
			Phone = phone;
		}

		public Double getLongitude() {
			return Longitude;
		}

		public void setLongitude(Double longitude) {
			Longitude = longitude;
		}

		public Double getLatitude() {
			return Latitude;
		}

		public void setLatitude(Double latitude) {
			Latitude = latitude;
		}

		public String getLogo() {
			return Logo;
		}

		public void setLogo(String logo) {
			Logo = logo;
		}

		public String getDescription() {
			return Description;
		}

		public void setDescription(String description) {
			Description = description;
		}

		public String getReceiptCount() {
			return ReceiptCount;
		}

		public void setReceiptCount(String receiptCount) {
			ReceiptCount = receiptCount;
		}

		public String getHasCoupon() {
			return HasCoupon;
		}

		public void setHasCoupon(String hasCoupon) {
			HasCoupon = hasCoupon;
		}

		public String getBalance() {
			return Balance;
		}

		public void setBalance(String balance) {
			Balance = balance;
		}

		public String getPhotos() {
			return Photos;
		}

		public void setPhotos(String photos) {
			Photos = photos;
		}

		public String getWorkTime() {
			return WorkTime;
		}

		public void setWorkTime(String workTime) {
			WorkTime = workTime;
		}

		public boolean isHasWifi() {
			return HasWIFI;
		}

		public void setHasWifi(boolean hasWifi) {
			HasWIFI = hasWifi;
		}

		public boolean isHasParking() {
			return HasParking;
		}

		public void setHasParking(boolean hasParking) {
			HasParking = hasParking;
		}

		public List<DiscountTicket> getDiscountCoupon() {
			return DiscountCoupon;
		}

		public void setDiscountCoupon(List<DiscountTicket> discountCoupon) {
			DiscountCoupon = discountCoupon;
		}

		public List<BounsSetting> getBounsSettings() {
			return BounsSettings;
		}

		public void setBounsSettings(List<BounsSetting> bounsSettings) {
			BounsSettings = bounsSettings;
		}
	}
}
