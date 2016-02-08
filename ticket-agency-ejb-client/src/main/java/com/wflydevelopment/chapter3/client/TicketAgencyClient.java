package com.wflydevelopment.chapter3.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.wflydevelopment.chapter3.boundary.remote.TheatreBookerRemote;
import com.wflydevelopment.chapter3.boundary.remote.TheatreInfoRemote;
import com.wflydevelopment.chapter3.exception.NoSuchSeatException;
import com.wflydevelopment.chapter3.exception.NotEnoughMoneyException;
import com.wflydevelopment.chapter3.exception.SeatBookedException;
import com.wflydevelopment.chapter3.util.IOUtils;

public class TicketAgencyClient {
	private static final Logger logger = 
			Logger.getLogger(TicketAgencyClient.class.getName());
	
	private final List<Future<String>> lastBookings = new ArrayList<>();
	
	public static void main(String...args) throws Exception {
		Logger.getLogger("org.jboss").setLevel(Level.SEVERE);
		Logger.getLogger("org.xnio").setLevel(Level.SEVERE);
		
		new TicketAgencyClient().run();
	}
	
	private final Context context;
	private TheatreInfoRemote theatreInfo;
	private TheatreBookerRemote theatreBooker;
	
	public TicketAgencyClient() throws NamingException {
		final Properties jndiProperties = new Properties();
		jndiProperties.setProperty(Context.URL_PKG_PREFIXES,
				"org.jboss.ejb.client.naming");
		this.context = new InitialContext(jndiProperties);
	}
	
	private enum Command {
		BOOK, LIST, MONEY, QUIT, INVALID, BOOKASYNC, MAIL;
		
		public static Command parseCommand(String stringCommand) {
			try {
				return valueOf(stringCommand.trim().toUpperCase());
			} catch (IllegalArgumentException iae) {
				return INVALID;
			}
		}
	}
	
	private void run() throws NamingException {
		this.theatreInfo = lookupTheatreInfoEJB();
		this.theatreBooker = lookupTheatreBookerEJB();
		showWelcomeMessage();
		
		while (true) {			
			final String stringCommand = IOUtils.readLine("> ");
			final Command command = Command.parseCommand(stringCommand);
			
			switch (command) {
				case BOOK:
					handleBook();
					break;
					
				case LIST:
					handleList();
					break;
					
				case MONEY:
					handleMoney();
					break;
					
				case QUIT:
					handleQuit();
					break;
				
				case BOOKASYNC:
					handleBookAsync();
					break;
					
				case MAIL:
					handleMail();
					break;
					
				default: 
					logger.warning("Unknow command " + stringCommand);
					System.out.println(">");
			}
		}
	}
	
	private void handleBookAsync() {
		int seatId = 0;
		try {
			seatId = IOUtils.readInt("Enter SeatId: ");
		} catch (NumberFormatException e) {
			logger.warning("Wrong seat Id.");
		}
		lastBookings.add(theatreBooker.bookSeatAsync(seatId));
		logger.info("Booking issued. Verify your mail!");
	}
	
	private void handleMail() {
		boolean displayed = false;
		final List<Future<String>> notFinished = new ArrayList<>();
		
		for (Future<String> booking : lastBookings) {
			if (booking.isDone()) {
				try {
					final String result = booking.get();
					logger.info("Mail received: " + result);
					displayed = true;
				} catch (InterruptedException | ExecutionException e) {
					logger.warning(e.getMessage());
				}
			} else {
				notFinished.add(booking);
			}
		}
		
		lastBookings.retainAll(notFinished);
		if (!displayed) {
			logger.info("No mail received!");
		}
	}
	
	private void handleBook() {
		int seatId;
		
		try {
			seatId = IOUtils.readInt("Enter SeatId: ");
		} catch (NumberFormatException e1) {
			logger.warning("Wrong SeatId format!");
			return;
		}
		
		try {
			final String retVal = theatreBooker.bookSeat(seatId);
			System.out.println(retVal);
		} catch (SeatBookedException | NotEnoughMoneyException
				| NoSuchSeatException e) {
			logger.warning(e.getMessage());
			return;
		}
	}
	
	private void handleList() {
		logger.info(theatreInfo.printSeatList());
	}
	
	private void handleMoney() {
		final int accountBalance = theatreBooker.getAccountBalance();
		logger.info("You have: " + accountBalance + " money left.");
	}
	
	private void handleQuit() {
		logger.info("Bye");
		System.exit(0);
	}
	
	private TheatreInfoRemote lookupTheatreInfoEJB() throws NamingException {
		return (TheatreInfoRemote) context.lookup("ejb:/ticket-agency-ejb//TheatreInfo!com.wflydevelopment.chapter3.boundary.remote.TheatreInfoRemote");
	}
	
	private TheatreBookerRemote lookupTheatreBookerEJB() throws NamingException {
		return (TheatreBookerRemote) context.lookup("ejb:/ticket-agency-ejb//TheatreBooker!com.wflydevelopment.chapter3.boundary.remote.TheatreBookerRemote?stateful");
	}
	
	private void showWelcomeMessage() {
		System.out.println("Theatre booking system");
		System.out.println("===================================");
		System.out.println("Commands: book, list, money, quit, bookasync, mail");
	}
}
