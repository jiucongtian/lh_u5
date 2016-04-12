package com.longhuapuxin.entity;

import java.util.List;

/**
 * Created by ZH on 2016/1/6.
 * Email zh@longhuapuxin.com
 */
public class ResponseGetProducts extends ResponseDad {

    private String Total;
    private List<Product> Products;

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public List<Product> getProducts() {
        return Products;
    }

    public void setProducts(List<Product> products) {
        Products = products;
    }

    public class Product {
        private String Code;
        private String Name;
        private String Description;
        private String Unit;
        private String Price;
        private String Photos;

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

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public String getUnit() {
            return Unit;
        }

        public void setUnit(String unit) {
            Unit = unit;
        }

        public String getPrice() {
            return Price;
        }

        public void setPrice(String price) {
            Price = price;
        }

        public String getPhotos() {
            return Photos;
        }

        public void setPhotos(String photos) {
            Photos = photos;
        }
    }
}
