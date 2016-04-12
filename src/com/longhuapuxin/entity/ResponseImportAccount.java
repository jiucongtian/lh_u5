package com.longhuapuxin.entity;

public class ResponseImportAccount extends ResponseDad {
	private String Token;

	public String getToken() {
		return Token;
	}

	public void setToken(String token) {
		Token = token;
	}

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	private String UserId;

	private boolean IsNew;

	public boolean isIsNew() {
		return IsNew;
	}

	public void setIsNew(boolean isNew) {
		IsNew = isNew;
	}
}
