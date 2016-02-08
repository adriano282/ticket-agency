package com.wflydevelopment.chapter3.control;

import static javax.ejb.LockType.READ;
import static javax.ejb.LockType.WRITE;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.AccessTimeout;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.jboss.logging.Logger;

import com.wflydevelopment.chapter3.exception.NoSuchSeatException;
import com.wflydevelopment.chapter3.exception.SeatBookedException;
import com.wflydevelopment.chapter3.model.Seat;

@Singleton
@Startup
@AccessTimeout(value = 5, unit = TimeUnit.MINUTES)
public class TheatreBox {
	
	@Resource 
	private TimerService timerService;
	
	private static final long DURATION = TimeUnit.SECONDS.toMillis(60);
	
	private static final Logger logger = 
			Logger.getLogger(TheatreBox.class);
	
	private Map<Integer, Seat> seats;
	
	@PostConstruct
	public void setupTheatre() {
		seats = new HashMap<>();
		int id = 0;
		for (int i = 0; i< 5; i++) {
			addSeat(new Seat(++id, "Stalls", 40));
			addSeat(new Seat(++id, "Circle", 20));
			addSeat(new Seat(++id, "Balcony", 20));
		}
		logger.info("Seat Map constructed.");
	}
	
	private void addSeat(Seat seat) {
		seats.put(seat.getId(), seat);
	}

	@Lock(READ)
	public Collection<Seat> getSeats() {
		return Collections.unmodifiableCollection(seats.values());
	}
	
	@Lock(READ)
	public int getSeatPrice(int seatId) throws NoSuchSeatException {
		return getSeat(seatId).getPrice();
	}
	
	@Lock(WRITE)
	public void buyTicket(int seatId) throws SeatBookedException, NoSuchSeatException {
		final Seat seat = getSeat(seatId);
		if (seat.isBooked())
			throw new SeatBookedException("Seat " + seatId + " already booked!");
		
		addSeat(seat.getBookedSeat());
	}
	
	@Lock(READ)
	private Seat getSeat(int seatId) throws NoSuchSeatException {
		final Seat seat = seats.get(seatId);
		if (seat == null)
			throw new NoSuchSeatException("Seat " + seatId + "does not exist!");
		
		return seat;
	}
	
	public void createTimer() {
		timerService.createSingleActionTimer(DURATION, new TimerConfig());
	}
	
	@Timeout
	public void timeout(Timer timer) {
		logger.info("Re-building Theatre Map.");
		setupTheatre();
	}
}
