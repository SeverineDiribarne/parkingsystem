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
		if (duration <= 0.5) {
			ticket.setPrice(0);
		} else {
			boolean applyDiscount = this.isDiscount(ticketDAO, ticket.getVehicleRegNumber());

			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				double total = calculatePriceWithOrWithoutDiscount(duration, applyDiscount, Fare.CAR_RATE_PER_HOUR);
				ticket.setPrice(total);
				break;
			}
			case BIKE: {

				double total = calculatePriceWithOrWithoutDiscount(duration, applyDiscount, Fare.BIKE_RATE_PER_HOUR);
				ticket.setPrice(total);
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		}
	}

	// return the license plate of the vehicle found in the database in order to
	// perform the reduction
	public boolean isDiscount(TicketDAO ticketDAO, String vehicleRegNumber) {
		return ticketDAO.findVehicleRegNumber(vehicleRegNumber);
	}

	// returns the total according to the application of the discount
	public double calculatePriceWithOrWithoutDiscount(double duration, boolean applyDiscount, double type) {
		double total = (duration * type);

		// application of the reduction if the plate exists in the database
		if (applyDiscount) {
			total -= (total * 0.05);
		}
		return total;
	}
}