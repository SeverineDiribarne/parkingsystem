package com.parkit.parkingsystem;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import junit.framework.Assert;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	// private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	// check that the parking table is updated and that the ticket is generated
	// after exiting a vehicle
	@Test
	public void processExitingVehicleTest() {
		ParkingService parkingService = null;
		try {
			// GIVEN
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

			Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");
			when(ticketDAO.getTicket(any(String.class))).thenReturn(ticket);
			when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
		// WHEN
		parkingService.processExitingVehicle(new Date());
		// THEN
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, Mockito.times(1)).getTicket("ABCDEF");
	}

	// check that the price is zero in the saved ticket and check that the license
	// plate of the car is the one recorded
	// in the ticket when the car enters the parking lot
	@Test
	public void priceIsEqualToZeroWhenTheTicketIsStarted() {
		ParkingService parkingService = null;
		try {
			// GIVEN
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
			when(inputReaderUtil.readSelection()).thenReturn(1);
			when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

			Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis()));
			ticket.setParkingSpot(parkingSpot);
			ticket.setPrice(0);
			ticket.setVehicleRegNumber("ABCDEF");
			when(ticketDAO.findVehicleRegNumber(any(String.class))).thenReturn(false);
			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			// WHEN
			parkingService.processIncomingVehicle();
			// THEN
			ArgumentCaptor<Ticket> ticketArgumentCaptor = ArgumentCaptor.forClass(Ticket.class);
			verify(ticketDAO, Mockito.times(1)).saveTicket(ticketArgumentCaptor.capture());
			Ticket savedTicket = ticketArgumentCaptor.getValue();
			assertTrue(savedTicket.getPrice() == 0.0);
			Assert.assertEquals(savedTicket.getVehicleRegNumber(), "ABCDEF");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// check that the type of vehicle returned is the bike
	@Test
	public void bikeTypeIsTheReturnedType() {
		ParkingService parkingService = null;
		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

		// THEN
		assertTrue(parkingSpot.getParkingType() == ParkingType.BIKE);
	}
}