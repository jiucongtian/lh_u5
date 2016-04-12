package com.longhuapuxin.entity;

public class ResponseGetChatMessages extends ResponseDad {
	private Messages Messages;

	public Messages getMessages() {
		return Messages;
	}

	public void setMessages(Messages messages) {
		Messages = messages;
	}

	class Messages {
		private String Userid;
		private String SendTime;

		public String getUserid() {
			return Userid;
		}

		public void setUserid(String userid) {
			Userid = userid;
		}

		public String getSendTime() {
			return SendTime;
		}

		public void setSendTime(String sendTime) {
			SendTime = sendTime;
		}

		public String getText() {
			return Text;
		}

		public void setText(String text) {
			Text = text;
		}

		public boolean isHasRead() {
			return HasRead;
		}

		public void setHasRead(boolean hasRead) {
			HasRead = hasRead;
		}

		private String Text;
		private boolean HasRead;
	}
}
