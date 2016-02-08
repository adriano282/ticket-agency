package com.wflydevelopment.chapter3.boundary.remote;

import java.util.concurrent.Future;

import com.wflydevelopment.chapter3.exception.*;

public interface TheatreBookerRemote {

	String bookSeat(int seatId) throws SeatBookedException,
			NotEnoughMoneyException, NoSuchSeatException;

	int getAccountBalance();

	Future<String> bookSeatAsync(int seatId);

}
