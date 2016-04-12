package com.longhuapuxin.entity;

public class ResponseDad {
	private boolean Success;

	public boolean isSuccess() {
		return Success;
	}

	public void setSuccess(boolean success) {
		Success = success;
	}

	private String ErrorMessage;

	private String ErrorCode;

	public String getErrorMessage() {
		return ErrorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		ErrorMessage = errorMessage;
	}

	public String getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(String errorCode) {
		ErrorCode = errorCode;
	}

	@Override
	public String toString() {
		return "ResponseDad [Success=" + Success + ", ErrorMessage="
				+ ErrorMessage + ", ErrorCode=" + ErrorCode + "]";
	}
}
