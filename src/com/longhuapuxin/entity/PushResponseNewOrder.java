package com.longhuapuxin.entity;

public class PushResponseNewOrder extends PushResponseDad {
	private String Id;
	private String UserId1;
	private String UserId2;
	private String SessionId;

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

	public String getUserId2() {
		return UserId2;
	}

	public void setUserId2(String userId2) {
		UserId2 = userId2;
	}

	public String getSessionId() {
		return SessionId;
	}

	public void setSessionId(String sessionId) {
		SessionId = sessionId;
	}
}
