package com.wflydevelopment.chapter3.exception;

public class SeatBookedException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public SeatBookedException() {}
	public SeatBookedException(String messageError) {
		super(messageError);
	}
}
