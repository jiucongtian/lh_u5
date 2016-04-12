package com.longhuapuxin.entity;

public class PushResponseTalkTo extends PushResponseDad {
	private String SessionId;

	public String getSessionId() {
		return SessionId;
	}

	public void setSessionId(String sessionId) {
		SessionId = sessionId;
	}

	private String MessageText;
	private String MessageSendTime;
	private String MessageUserId;

	public String getMessageText() {
		return MessageText;
	}

	public void setMessageText(String messageText) {
		MessageText = messageText;
	}

	public String getMessageSendTime() {
		return MessageSendTime;
	}

	public void setMessageSendTime(String messageSendTime) {
		MessageSendTime = messageSendTime;
	}

	public String getMessageUserId() {
		return MessageUserId;
	}

	public void setMessageUserId(String messageUserId) {
		MessageUserId = messageUserId;
	}
}
