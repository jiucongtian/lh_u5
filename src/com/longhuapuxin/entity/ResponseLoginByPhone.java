package com.longhuapuxin.entity;

public class ResponseLoginByPhone extends ResponseDad {
	private String UserId;

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public String getToken() {
		return Token;
	}

	public void setToken(String token) {
		Token = token;
	}

	public boolean isIsNew() {
		return IsNew;
	}

	public void setIsNew(boolean isNew) {
		IsNew = isNew;
	}

	private String Token;
	private boolean IsNew;
}
