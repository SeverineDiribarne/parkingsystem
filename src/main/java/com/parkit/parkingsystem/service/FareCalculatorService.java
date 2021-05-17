package com.parkit.parkingsystem.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(TicketDAO ticketDAO, Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		LocalDateTime inTime = LocalDateTime.of(
				ticket.getInTime().get(Calendar.YEAR),
				ticket.getInTime().get(Calendar.MONTH),
				ticket.getInTime().get(Calendar.DAY_OF_MONTH),
				ticket.getInTime().get(Calendar.HOUR),
				ticket.getInTime().get(Calendar.MINUTE),
				ticket.getInTime().get(Calendar.SECOND));
		LocalDateTime outTime = LocalDateTime.of(
				ticket.getOutTime().get(Calendar.YEAR),
				ticket.getOutTime().get(Calendar.MONTH),
				ticket.getOutTime().get(Calendar.DAY_OF_MONTH),
				ticket.getOutTime().get(Calendar.HOUR),
				ticket.getOutTime().get(Calendar.MINUTE),
				ticket.getOutTime().get(Calendar.SECOND));

		float duration = (float) (ChronoUnit.MINUTES.between(inTime, outTime) / 60.0);
		boolean applyDiscount = this.isDiscount(ticketDAO, ticket.getVehicleRegNumber());
		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {

			double total = duration * Fare.CAR_RATE_PER_HOUR;
			if (applyDiscount) {
				total -= (total * 0.05);
			}
			ticket.setPrice(total);
			break;
		}
		case BIKE: {
			double total = (duration * Fare.BIKE_RATE_PER_HOUR);
			if (applyDiscount) {
				total -= (total * 0.05);
			}
			ticket.setPrice(total);
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
	}

	public boolean isDiscount(TicketDAO ticketDAO, String vehicleRegNumber) {

		return ticketDAO.findVehicleRegNumber(vehicleRegNumber);
	}
}