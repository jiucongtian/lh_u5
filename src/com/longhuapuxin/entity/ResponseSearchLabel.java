package com.longhuapuxin.entity;

import java.util.List;

public class ResponseSearchLabel extends ResponseDad {

    private List<User> Users;

    public List<ResponsePhoto.Photo> getFiles() {
        return Files;
    }

    public void setFiles(List<ResponsePhoto.Photo> files) {
        Files = files;
    }

    private List<ResponsePhoto.Photo> Files;
    public List<User> getUsers() {
        return Users;
    }

    public void setUsers(List<User> users) {
        Users = users;
    }

    public class User {
        private String ID;
        private String LabelCode;
        private String UserId;
        private String IndustryCode;
        private String Experience;
        private String LabelName;
        private String NickName;
        private String Distance;
        private Double Latitude;
        private Double Longitude;
        private String CityCode;
        private String Note;
        private String Portrait;
        private String Gender;
        private String GuidePrice;
        private String Birthday;
        private String Address;

        public String getBankAccount() {
            return BankAccount;
        }

        public void setBankAccount(String bankAccount) {
            BankAccount = bankAccount;
        }

        public String getIdNo() {
            return IdNo;
        }

        public void setIdNo(String idNo) {
            IdNo = idNo;
        }

        private String BankAccount;
        private String IdNo;
        public String getPhotos() {
            return Photos;
        }

        public void setPhotos(String photos) {
            Photos = photos;
        }

        private String Photos;

        public User() {
        }

        public User(ResponseGetAccount.User convert) {
            UserId = convert.getId();
            Gender = convert.getGender();
            Birthday = convert.getBirthday();
            NickName = convert.getNickName();
            Portrait = convert.getPortrait();
            Status=convert.getStatus();
            IdNo=convert.getIdNo();
            BankAccount=convert.getBankAccount();
        }

        public String getStatus() {
            return Status;
        }

        public void setStatus(String status) {
            Status = status;
        }

        private String Status;

        public String getGender() {
            return Gender;
        }

        public void setGender(String gender) {
            Gender = gender;
        }

        public String getPortrait() {
            return Portrait;
        }

        public void setPortrait(String portrait) {
            Portrait = portrait;
        }

        public String getID() {
            return ID;
        }

        public void setID(String iD) {
            ID = iD;
        }

        public String getLabelCode() {
            return LabelCode;
        }

        public void setLabelCode(String labelCode) {
            LabelCode = labelCode;
        }

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String userId) {
            UserId = userId;
        }

        public String getLabelName() {
            return LabelName;
        }

        public void setLabelName(String labelName) {
            LabelName = labelName;
        }

        public String getDistance() {
            return Distance;
        }

        public void setDistance(String distance) {
            Distance = distance;
        }

        public String getIndustryCode() {
            return IndustryCode;
        }

        public void setIndustryCode(String industryCode) {
            IndustryCode = industryCode;
        }

        public String getExperience() {
            return Experience;
        }

        public void setExperience(String experience) {
            Experience = experience;
        }

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String nickName) {
            NickName = nickName;
        }

        public Double getLatitude() {
            return Latitude;
        }

        public void setLatitude(Double latitude) {
            Latitude = latitude;
        }

        public Double getLongitude() {
            return Longitude;
        }

        public void setLongitude(Double longitude) {
            Longitude = longitude;
        }

        public String getCityCode() {
            return CityCode;
        }

        public void setCityCode(String cityCode) {
            CityCode = cityCode;
        }

        public String getNote() {
            return Note;
        }

        public void setNote(String note) {
            Note = note;
        }

        public String getGuidePrice() {
            return GuidePrice;
        }

        public void setGuidePrice(String guidePrice) {
            GuidePrice = guidePrice;
        }

        public String getBirthday() {
            return Birthday;
        }

        public void setBirthday(String birthday) {
            Birthday = birthday;
        }

        public String getAddress() {
            return Address;
        }

        public void setAddress(String address) {
            Address = address;
        }
    }
}
