package com.longhuapuxin.entity;

import java.util.List;

public class ResponseShopCategory extends ResponseDad {
	
	List<Category> Categories;

	public List<Category> getCategories() {
		return Categories;
	}

	public void setCategories(List<Category> categories) {
		Categories = categories;
	}

	public class Category {
		String Code;
		String Name;
		String Image;
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
		public String getImage() {
			return Image;
		}
		public void setImage(String image) {
			Image = image;
		}
	}

}
