package com.longhuapuxin.entity;

import java.util.List;

public class ResponseGetOrderList extends ResponseDad {
    private int Total;
    private List<Order> Orders;

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }

    public List<Order> getOrders() {
        return Orders;
    }

    public void setOrders(List<Order> orders) {
        Orders = orders;
    }

    public class Order {
        private int Id;

        private int UserId1;

        private String Title;

        private String NickName1;

        private String OrderNote;

        private String OrderTime;

        public String getOrderTime() {
            return OrderTime;
        }

        public void setOrderTime(String orderTime) {
            OrderTime = orderTime;
        }

        public String getReceiveTime() {
            return ReceiveTime;
        }

        public void setReceiveTime(String receiveTime) {
            ReceiveTime = receiveTime;
        }

        private int Status;

        private int UserId2;

        private String NickName2;

        private String LabelCode;

        private String IndustryCode;

        private String ReceiveTime;

        private Double Amount;

        public Double getFee() {
            return Fee;
        }

        public void setFee(Double fee) {
            Fee = fee;
        }

        public Double getTotal() {
            return Total;
        }

        public void setTotal(Double total) {
            Total = total;
        }

        public String getPaymentStatus() {
            return PaymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            PaymentStatus = paymentStatus;
        }

        public String getTicket() {
            return Ticket;
        }

        public void setTicket(String ticket) {
            Ticket = ticket;
        }

        private Double Fee;
        private Double Total;
        private String PaymentStatus;
        private String Ticket;


        public int getId() {
            return Id;
        }

        public void setId(int id) {
            Id = id;
        }

        public int getUserId1() {
            return UserId1;
        }

        public void setUserId1(int userId1) {
            UserId1 = userId1;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String title) {
            Title = title;
        }

        public String getNickName1() {
            return NickName1;
        }

        public void setNickName1(String nickName1) {
            NickName1 = nickName1;
        }

        public String getOrderNote() {
            return OrderNote;
        }

        public void setOrderNote(String orderNote) {
            OrderNote = orderNote;
        }

        // public Date getOrderTime() {
        // return OrderTime;
        // }
        //
        // public void setOrderTime(Date orderTime) {
        // OrderTime = orderTime;
        // }

        public int getStatus() {
            return Status;
        }

        public void setStatus(int status) {
            Status = status;
        }

        public int getUserId2() {
            return UserId2;
        }

        public void setUserId2(int userId2) {
            UserId2 = userId2;
        }

        public String getNickName2() {
            return NickName2;
        }

        public void setNickName2(String nickName2) {
            NickName2 = nickName2;
        }

        public String getLabelCode() {
            return LabelCode;
        }

        public void setLabelCode(String labelCode) {
            LabelCode = labelCode;
        }

        public String getIndustryCode() {
            return IndustryCode;
        }

        public void setIndustryCode(String industryCode) {
            IndustryCode = industryCode;
        }

        // public Date getReceiveTime() {
        // return ReceiveTime;
        // }
        //
        // public void setReceiveTime(Date receiveTime) {
        // ReceiveTime = receiveTime;
        // }

        public Double getAmount() {
            return Amount;
        }

        public void setAmount(Double amount) {
            Amount = amount;
        }

    }
}
