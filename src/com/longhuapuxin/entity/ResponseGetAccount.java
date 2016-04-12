package com.longhuapuxin.entity;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import com.longhuapuxin.db.bean.DiscountCoupon;
import com.longhuapuxin.entity.ResponseShop.Shop;

public class ResponseGetAccount extends ResponseDad {
    private User User;

    public boolean isManualyWithDraw() {
        return ManualyWithDraw;
    }

    public void setManualyWithDraw(boolean manualyWithDraw) {
        ManualyWithDraw = manualyWithDraw;
    }

    private boolean ManualyWithDraw;

    public User getUser() {
        return User;
    }

    public void setUser(User User) {
        this.User = User;
    }

    public class User {
        private String UserName;
        private String Password;
        private String Id;
        private String NickName;
        private String PayPassword;
        private String Phone;
        private String Email;
        private String Gender;
        private String Portrait;
        private String Balance;
        private List<Label> Labels;
        private List<DiscountCoupon> DiscountCoupons;
        private List<CareWho> CareWho;
        private boolean IsOnline;
        private String LoginDevice;
        private String PushChannel;
        private String Token;
        private String Birthday;
        private List<CareWhichShop> CareWhichShops;
        private String BankAccount;
        private String BankName;
        private String IdNo;
        private int BindCount;
        private String LastName;
        private String FirstName;
        private String Address;

        public String getAddress() {
            return Address;
        }

        public void setAddress(String address) {
            Address = address;
        }

        public String getStatus() {
            return Status;
        }

        public void setStatus(String status) {
            Status = status;
        }

        private String Status;

        public String getFirstName() {
            return FirstName;
        }

        public void setFirstName(String firstName) {
            FirstName = firstName;
        }

        public String getLastName() {
            return LastName;
        }

        public void setLastName(String lastName) {
            LastName = lastName;
        }

        public int getBindCount() {
            return BindCount;
        }

        public void setBindCount(int bindCount) {
            BindCount = bindCount;
        }

        public String getIdNo() {
            return IdNo;
        }

        public void setIdNo(String idNo) {
            IdNo = idNo;
        }

        public String getBankName() {
            return BankName;
        }

        public void setBankName(String bankName) {
            BankName = bankName;
        }

        public String getBankAccount() {
            return BankAccount;
        }

        public void setBankAccount(String bankAccount) {
            BankAccount = bankAccount;
        }

        public String getPassword() {
            return Password;
        }

        public void setPassword(String password) {
            Password = password;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String userName) {
            UserName = userName;
        }


        public String getId() {
            return Id;
        }

        public void setId(String id) {
            Id = id;
        }

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String nickName) {
            NickName = nickName;
        }

        public String getPhone() {
            return Phone;
        }

        public void setPhone(String phone) {
            Phone = phone;
        }

        public String getEmail() {
            return Email;
        }

        public void setEmail(String email) {
            Email = email;
        }

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

        public String getBalance() {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            if (Balance != null && Balance.length() > 0) {
                return decimalFormat.format(Double.parseDouble(Balance));
            } else {
                return "0";
            }

        }

        public void setBalance(String balance) {
            Balance = balance;
        }

        public List<Label> getLabels() {
            return Labels;
        }

        public void setLabels(List<Label> labels) {
            Labels = labels;
        }

        public void updateLabels(List<Label> labels) {
            Labels.clear();
            Labels.addAll(labels);
        }

        public List<DiscountCoupon> getDiscountCoupons() {
            return DiscountCoupons;
        }

        public void setDiscountCoupons(List<DiscountCoupon> discountCoupons) {
            DiscountCoupons = discountCoupons;
        }

        public List<CareWho> getCareWho() {
            return CareWho;
        }

        public void setCareWho(List<CareWho> careWho) {
            CareWho = careWho;
        }

        public List<CareWhichShop> getCareWhichShops() {
            return CareWhichShops;
        }

        public void setCareWhichShops(List<CareWhichShop> careWhichShops) {
            CareWhichShops = careWhichShops;
        }

        public boolean isIsOnline() {
            return IsOnline;
        }

        public void setIsOnline(boolean isOnline) {
            IsOnline = isOnline;
        }

        public String getLoginDevice() {
            return LoginDevice;
        }

        public void setLoginDevice(String loginDevice) {
            LoginDevice = loginDevice;
        }

        public String getPushChannel() {
            return PushChannel;
        }

        public void setPushChannel(String pushChannel) {
            PushChannel = pushChannel;
        }

        public String getToken() {
            return Token;
        }

        public void setToken(String token) {
            Token = token;
        }

        public String getPayPassword() {
            return PayPassword;
        }

        public void setPayPassword(String payPassword) {
            PayPassword = payPassword;
        }

        public String getBirthday() {
            if (Birthday == null) {
                return "";
            } else {
                return Birthday;
            }
        }

        public void setBirthday(String birthday) {
            Birthday = birthday;
        }

        public class Label {
            private String LabelCode;
            private String LabelName;
            private String Experience;
            private String CityCode;
            private String Index;
            private String Photos;
            private String Note;
            //			private String IndustryCode;
//			private String CategoryCode;
            private String GuidePrice;

            public String getLabelCode() {
                return LabelCode;
            }

            public void setLabelCode(String labelCode) {
                LabelCode = labelCode;
            }

            public String getLabelName() {
                return LabelName;
            }

            public void setLabelName(String labelName) {
                LabelName = labelName;
            }

            public String getExperience() {
                return Experience;
            }

            public void setExperience(String experience) {
                Experience = experience;
            }

            public String getCityCode() {
                return CityCode;
            }

            public void setCityCode(String cityCode) {
                CityCode = cityCode;
            }

            public String getIndex() {
                return Index;
            }

            public void setIndex(String index) {
                Index = index;
            }

            public String getPhotos() {
                return Photos;
            }

            public void setPhotos(String photos) {
                Photos = photos;
            }

            public String getNote() {
                return Note;
            }

            public void setNote(String note) {
                Note = note;
            }

//			public String getIndustryCode() {
//				return IndustryCode;
//			}
//
//			public void setIndustryCode(String industryCode) {
//				IndustryCode = industryCode;
//			}

            public String getGuidePrice() {
                return GuidePrice;
            }

            public void setGuidePrice(String guidePrice) {
                GuidePrice = guidePrice;
            }

//			public String getCategoryCode() {
//				return CategoryCode;
//			}
//
//			public void setCategoryCode(String categoryCode) {
//				CategoryCode = categoryCode;
//			}
        }

        public class CareWho implements Comparable {

            private String FirstChar;

            public String getFirstChar() {

                return FirstChar;
            }

            public void setFirstChar(String firstChar) {
                FirstChar = firstChar;
            }

            private String Id;

            public String getId() {
                return Id;
            }

            public void setId(String id) {
                Id = id;
            }

            public String getNickName() {
                return NickName;
            }

            public void setNickName(String nickName) {
                NickName = nickName;
            }

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

            public boolean isIsFriend() {
                return IsFriend;
            }

            public void setIsFriend(boolean isFriend) {
                IsFriend = isFriend;
            }

            private String NickName;
            private String Gender;
            private String Portrait;
            private boolean IsFriend;

            public int compareTo(Object another) {
                CareWho other = (CareWho) another;
                int i = this.FirstChar.compareTo(other.getFirstChar());
                if (i == 0) {
                    i = this.NickName.compareTo(other.getNickName());
                }
                return i;
            }
        }

        public class CareWhichShop implements Comparable {
            private String Code;
            private String Name;
            private String Address;
            private String Phone;
            private String ShopLogo;
            private String FirstChar;
            private boolean HasParking;
            private Double Longitude;
            private boolean HasWIFI;
            private String WorkTime;
            private Double Latitude;

            public CareWhichShop(Shop shop) {
                Code = shop.getCode();
                Name = shop.getName();
                Address = shop.getAddress();
                Phone = shop.getPhone();
                ShopLogo = shop.getLogo();
//				FirstChar 
                HasParking = shop.isHasParking();
                Longitude = shop.getLongitude();
                HasWIFI = shop.isHasWifi();
                WorkTime = shop.getWorkTime();
                Latitude = shop.getLatitude();
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


            public boolean isHasParking() {
                return HasParking;
            }

            public void setHasParking(boolean hasParking) {
                HasParking = hasParking;
            }

            public boolean isHasWIFI() {
                return HasWIFI;
            }

            public void setHasWIFI(boolean hasWIFI) {
                HasWIFI = hasWIFI;
            }

            public String getWorkTime() {
                return WorkTime;
            }

            public void setWorkTime(String workTime) {
                WorkTime = workTime;
            }


            public String getFirstChar() {
                return FirstChar;
            }

            public void setFirstChar(String firstChar) {
                FirstChar = firstChar;
            }

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

            public String getShopLogo() {
                return ShopLogo;
            }

            public void setShopLogo(String shopLogo) {
                ShopLogo = shopLogo;
            }

            public int compareTo(Object another) {
                CareWhichShop other = (CareWhichShop) another;
                int i = this.FirstChar.compareTo(other.getFirstChar());
                if (i == 0) {
                    i = this.Name.compareTo(other.getName());
                }
                return i;
            }
        }

    }
}
