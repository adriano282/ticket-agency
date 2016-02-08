package com.wflydevelopment.chapter3.exception;

public class NoSuchSeatException extends Exception {
	private static final long serialVersionUID = 1L;
	public NoSuchSeatException() {}
	public NoSuchSeatException(String messageError) {super(messageError);}
}
