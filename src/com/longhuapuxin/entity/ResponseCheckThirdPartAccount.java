package com.longhuapuxin.entity;

public class ResponseCheckThirdPartAccount extends ResponseDad {
	private String UserId;
	private String Token;

	public String getToken() {
		return Token;
	}

	public void setToken(String token) {
		Token = token;
	}

	public boolean isHasPhone() {
		return HasPhone;
	}

	public void setHasPhone(boolean hasPhone) {
		HasPhone = hasPhone;
	}

	private boolean HasPhone;

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}
}
