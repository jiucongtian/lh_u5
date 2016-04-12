package com.longhuapuxin.entity;

import java.util.List;

public class ResponseGetCompletedOrder extends ResponseDad {
	private List<Order> Orders;

	public List<Order> getOrders() {
		return Orders;
	}

	public void setOrders(List<Order> orders) {
		Orders = orders;
	}

	private int Total;

	public int getTotal() {
		return Total;
	}

	public void setTotal(int total) {
		Total = total;
	}

	public class Order {
		private String ID;
		private String UserId1;
		private String NickName1;
		private String OrderNote;
		private String OrderTime;
		private String Status;
		private String UserId2;
		private String NickName2;
		private String LabelCode;
		private String FeedBackId;

		public String getFeedBackId() {
			return FeedBackId;
		}

		public void setFeedBackId(String feedBackId) {
			FeedBackId = feedBackId;
		}

		public String getId() {
			return ID;
		}

		public void setId(String id) {
			ID = id;
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

		public String getFinishTime() {
			return FinishTime;
		}

		public void setFinishTime(String finishTime) {
			FinishTime = finishTime;
		}

		public String getEstimate() {
			return Estimate;
		}

		public void setEstimate(String estimate) {
			Estimate = estimate;
		}

		public String getComment() {
			return Comment;
		}

		public void setComment(String comment) {
			Comment = comment;
		}

		public String getExperience() {
			return Experience;
		}

		public void setExperience(String experience) {
			Experience = experience;
		}

		public String getAmount() {
			return Amount;
		}

		public void setAmount(String amount) {
			Amount = amount;
		}

		private String IndustryCode;
		private String ReceiveTime;
		private String FinishTime;
		private String Estimate;
		private String Comment;
		private String Experience;
		private String Amount;
	}
}
