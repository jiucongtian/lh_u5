package com.longhuapuxin.entity;


public class ResponseGetOrder extends ResponseDad {
	private Order Order;

	public Order getOrder() {
		return Order;
	}

	public void setOrder(Order order) {
		Order = order;
	}

	public class Order {
		private String Id;
		private String UserId1;
		private String NickName1;
		private String OrderNote;
		private String OrderTime;
		private String Status;
		private String UserId2;

		public String getId() {
			return Id;
		}

		public void setId(String id) {
			Id = id;
		}

		public String getUserId1() {
			return UserId1;
		}

		public void setUserId1(String userId1) {
			UserId1 = userId1;
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

		public String getOrderTime() {
			return OrderTime;
		}

		public void setOrderTime(String orderTime) {
			OrderTime = orderTime;
		}

		public String getStatus() {
			return Status;
		}

		public void setStatus(String status) {
			Status = status;
		}

		public String getUserId2() {
			return UserId2;
		}

		public void setUserId2(String userId2) {
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

		public String getReceiveTime() {
			return ReceiveTime;
		}

		public void setReceiveTime(String receiveTime) {
			ReceiveTime = receiveTime;
		}

		public String getAmount() {
			return Amount;
		}

		public void setAmount(String amount) {
			Amount = amount;
		}

		private String NickName2;
		private String LabelCode;
		private String IndustryCode;
		private String ReceiveTime;
		private String Amount;

	}
}
