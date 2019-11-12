package com.cfang.exception;

public class MessageException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private int responseCode;
	private String errorMsg;

	public MessageException() {
		
	}
	
	public MessageException(String message) {
		super(message);
	}
	
	public MessageException(String message, Throwable throwable) {
		super(message, throwable);
		this.errorMsg = message;
		this.responseCode = -1;
	}
	
	public MessageException(String message,  int responseCode) {
		super(message);
		this.errorMsg = message;
		this.responseCode = -1;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
