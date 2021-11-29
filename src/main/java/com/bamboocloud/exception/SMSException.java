package com.bamboocloud.exception;

public class SMSException extends Exception {
	public SMSException(String message) {
		super(message);
	}

	public SMSException(String message, Throwable cause) {
		super(message, cause);
	}
}
