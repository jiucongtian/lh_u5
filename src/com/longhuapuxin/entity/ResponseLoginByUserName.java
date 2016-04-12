package com.longhuapuxin.entity;

public class ResponseLoginByUserName extends ResponseDad {
	private String UserId;
	private String Token;

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
}
