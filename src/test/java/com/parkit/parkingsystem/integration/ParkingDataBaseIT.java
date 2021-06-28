package com.parkit.parkingsystem.integration;

import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;
	public static final long HOUR = 3600 * 1000; // in milli-seconds.

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@Mock
	private static Ticket ticket;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	// check that a ticket is actualy saved in DB and Parking table is updated
	// with availability

	@Test
	public void testParkingACar() {
		// GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processIncomingVehicle();
		// THEN
		Ticket ticketInDB = ticketDAO.getTicket("ABCDEF");
		Assert.assertNotNull(ticketInDB);
		Assert.assertEquals(ticketInDB.getVehicleRegNumber(), "ABCDEF");
		Assert.assertFalse(ticketInDB.getParkingSpot().isAvailable());

	}

	// check that the fare generated and out time are populated correctly in
	// the database
	@Test
	public void testParkingLotExit() {
		// GIVEN
		testParkingACar();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		Date outTime = new Date(new Date().getTime() + 2 * HOUR);
		parkingService.processExitingVehicle(outTime);

		// THEN
		Ticket ticketInDB = ticketDAO.getTicket("ABCDEF");

		Assert.assertTrue(ticketInDB.getPrice() > 0.0);
		Assert.assertNotNull(ticketInDB.getOutTime());
	}

}
