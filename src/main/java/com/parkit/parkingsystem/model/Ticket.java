package com.parkit.parkingsystem.model;

import java.util.Calendar;
import java.util.Date;

public class Ticket {
	private int id;
	private ParkingSpot parkingSpot;
	private String vehicleRegNumber;
	private double price;
	private Calendar inTime = null;
	private Calendar outTime = null;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ParkingSpot getParkingSpot() {
		return parkingSpot;
	}

	public void setParkingSpot(ParkingSpot parkingSpot) {
		this.parkingSpot = parkingSpot;
	}

	public String getVehicleRegNumber() {
		return vehicleRegNumber;
	}

	public void setVehicleRegNumber(String vehicleRegNumber) {
		this.vehicleRegNumber = vehicleRegNumber;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Calendar getInTime() {
		return inTime;
	}

	public void setInTime(Date inTime) {
		if (inTime != null) {
			this.inTime = Calendar.getInstance();
			this.inTime.setTime(inTime);
		}

	}

	public Calendar getOutTime() {
		return outTime;
	}

	public void setOutTime(Date outTime) {
		if (outTime != null) {
			this.outTime = Calendar.getInstance();
			this.outTime.setTime(outTime);
		}
	}
}