package com.longhuapuxin.entity;

import java.util.List;

public class ResponseShopList extends ResponseDad {

	int Total;

	List<Shop> Shops;

	public int getTotal() {
		return Total;
	}

	public void setTotal(int total) {
		Total = total;
	}

	public List<Shop> getShops() {
		return Shops;
	}

	public void setShops(List<Shop> shops) {
		Shops = shops;
	}

	public class Shop {
		String Code;
		String Name;
		String Address;
		String Zip;
		String Phone;
		String City;
		Double Longitude;
		Double Latitude;
		String Logo;
		String Description;
		String WorkTime;
		boolean HasWIFI;
		boolean HasParking;
		String Photos;
		Long distance;
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
		public String getZip() {
			return Zip;
		}
		public void setZip(String zip) {
			Zip = zip;
		}
		public String getPhone() {
			return Phone;
		}
		public void setPhone(String phone) {
			Phone = phone;
		}
		public String getCity() {
			return City;
		}
		public void setCity(String city) {
			City = city;
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
		public String getWorkTime() {
			return WorkTime;
		}
		public void setWorkTime(String workTime) {
			WorkTime = workTime;
		}
		public boolean getHasWIFI() {
			return HasWIFI;
		}
		public void setHasWIFI(boolean hasWIFI) {
			HasWIFI = hasWIFI;
		}
		public boolean getHasParking() {
			return HasParking;
		}
		public void setHasParking(boolean hasParking) {
			HasParking = hasParking;
		}
		public String getPhotos() {
			return Photos;
		}
		public void setPhotos(String photos) {
			Photos = photos;
		}
		public Long getDistance() {
			return distance;
		}
		public void setDistance(Long distance) {
			this.distance = distance;
		}
	}
}
