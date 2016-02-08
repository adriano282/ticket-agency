package com.wflydevelopment.chapter3.exception;

public class NotEnoughMoneyException extends Exception {
	private static final long serialVersionUID = 1L;

	public NotEnoughMoneyException(String error) {
		super(error);
	}
	
	public NotEnoughMoneyException() {}
}
