package com.parkit.parkingsystem.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		LocalDateTime inTime = LocalDateTime.of(ticket.getInTime().getYear() + 1900, ticket.getInTime().getMonth() + 1,
				ticket.getInTime().getDay() + 1, ticket.getInTime().getHours(), ticket.getInTime().getMinutes(),
				ticket.getInTime().getSeconds());
		LocalDateTime outTime = LocalDateTime.of(ticket.getOutTime().getYear() + 1900,
				ticket.getOutTime().getMonth() + 1,
				ticket.getOutTime().getDay() + 1, ticket.getOutTime().getHours(), ticket.getOutTime().getMinutes(),
				ticket.getOutTime().getSeconds());

		float duration = (float) (ChronoUnit.MINUTES.between(inTime, outTime) / 60.0);

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
			break;
		}
		case BIKE: {
			ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
	}
}