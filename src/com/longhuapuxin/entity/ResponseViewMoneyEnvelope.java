package com.longhuapuxin.entity;

/**
 * Created by ZH on 2016/1/28.
 * Email zh@longhuapuxin.com
 */
public class ResponseViewMoneyEnvelope extends ResponseDad {
    private String Money;

    public ResponseViewMoneyEnvelope.MoneyEnvelope getMoneyEnvelope() {
        return MoneyEnvelope;
    }

    public void setMoneyEnvelope(ResponseViewMoneyEnvelope.MoneyEnvelope moneyEnvelope) {
        MoneyEnvelope = moneyEnvelope;
    }

    public String getMoney() {
        return Money;
    }

    public void setMoney(String money) {
        Money = money;
    }

    private MoneyEnvelope MoneyEnvelope;

    public class MoneyEnvelope {
        private String Id;
        private String NickName;
        private String UserId;
        private String CircleId;
        private String SessionId;
        private String Note;
        private String Amount;
        private String PersonLimit;
        private String CreateTIme;

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

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String userId) {
            UserId = userId;
        }

        public String getCircleId() {
            return CircleId;
        }

        public void setCircleId(String circleId) {
            CircleId = circleId;
        }

        public String getSessionId() {
            return SessionId;
        }

        public void setSessionId(String sessionId) {
            SessionId = sessionId;
        }

        public String getNote() {
            return Note;
        }

        public void setNote(String note) {
            Note = note;
        }

        public String getAmount() {
            return Amount;
        }

        public void setAmount(String amount) {
            Amount = amount;
        }

        public String getPersonLimit() {
            return PersonLimit;
        }

        public void setPersonLimit(String personLimit) {
            PersonLimit = personLimit;
        }

        public String getCreateTIme() {
            return CreateTIme;
        }

        public void setCreateTIme(String createTIme) {
            CreateTIme = createTIme;
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

        public String getStatus() {
            return Status;
        }

        public void setStatus(String status) {
            Status = status;
        }

        public String getRemain() {
            return Remain;
        }

        public void setRemain(String remain) {
            Remain = remain;
        }

        public String getPersonCount() {
            return PersonCount;
        }

        public void setPersonCount(String personCount) {
            PersonCount = personCount;
        }

        public String getMoneyReceives() {
            return MoneyReceives;
        }

        public void setMoneyReceives(String moneyReceives) {
            MoneyReceives = moneyReceives;
        }

        public String getNickNames() {
            return NickNames;
        }

        public void setNickNames(String nickNames) {
            NickNames = nickNames;
        }

        public String getUserIds() {
            return UserIds;
        }

        public void setUserIds(String userIds) {
            UserIds = userIds;
        }

        private String PaymentStatus;
        private String Ticket;
        private String Status;
        private String Remain;
        private String PersonCount;
        private String MoneyReceives;
        private String NickNames;
        private String UserIds;

        public String getPortrait() {
            return Portrait;
        }

        public void setPortrait(String portrait) {
            Portrait = portrait;
        }

        private String Portrait;
    }
}
