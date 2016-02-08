package com.wflydevelopment.chapter3.model;

public class Seat {
	private int id;
	private String name;
	private int price;
	private boolean booked;
	
	public Seat(int id, String name, int price) {
		this(id, name, price, false);
	}
	
	private Seat(int id, String name, int price, boolean booked) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.booked = booked;
	}
	
	public Seat getBookedSeat() {
		return new Seat(getId(), getName(), getPrice(), true);
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return price;
	}

	public boolean isBooked() {
		return booked;
	}
	
	public String toString() { 
		return "Id: " + id + " | Name: " + name + " | Price: " + price + " | Is Booked? " + booked;
	}
}
