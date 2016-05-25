package com.ticketservice.interaction.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.ticketservice.TicketServiceApplication;
import com.ticketservice.ws.dao.ReservationDetailsRepository;
import com.ticketservice.ws.dto.ConfirmReservationDTO;
import com.ticketservice.ws.dto.ReservationDTO;
import com.ticketservice.ws.dto.SeatsAvailabilityDTO;
import com.ticketservice.ws.interaction.TicketServiceInteraction;
import com.ticketservice.ws.model.ReservationDetails;
import com.ticketservice.ws.model.SeatsAvailability;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TicketServiceApplication.class)
@WebAppConfiguration
@Transactional
public class TicketServiceInteractionTest{
	
	@Autowired
	TicketServiceInteraction ticketServiceInteraction;
	ReservationDTO reservationDto;
	SeatsAvailabilityDTO seatsAvailabilityDto;
	ReservationDetails reservationDetails;
	ReservationDetailsRepository reservationDetailsRepository;
	ConfirmReservationDTO confirmReservationDto;
	
	@Before
	public void initialize(){
		reservationDto=new ReservationDTO();
		reservationDetails=new ReservationDetails();
	}
	
	@Test
	public void testfindAvailableSeats(){
		SeatsAvailability seatsAvailability;
		seatsAvailabilityDto=ticketServiceInteraction.findAvailableSeats(1);
		assertNotNull(seatsAvailabilityDto);
		assertEquals(1250,seatsAvailabilityDto.getAvailableSeats());
		assertNotEquals(100,seatsAvailabilityDto.getAvailableSeats());
	}
	
	/*
	 * It will test (maxlevel<minlevel) scenario
	 * In this scenario maxlevel will be checked and given priority 
	 * for holding seats if not available it goes to minlevel by incrementing
	 * one by one and checking the best available seats between maxlevel and minlevel
	 * */
	@Test
	public void testFindAndHoldSeats_scenario1(){
		//case-1
		reservationDto.setCustomerEmail("chandraravi2@gmail.com");
		reservationDto.setMaxLevel(1);
		reservationDto.setMinLevel(2);
		reservationDto.setNoOfSeats(120);
		ReservationDetails reservationDetails=ticketServiceInteraction.findAndHoldSeats(reservationDto);
		assertNotNull(reservationDetails);
		assertEquals(1130,ticketServiceInteraction.findAvailableSeats(reservationDetails.getLevelid()).getAvailableSeats());
		assertNotEquals(1131,ticketServiceInteraction.findAvailableSeats(reservationDetails.getLevelid()).getAvailableSeats());
		
		//case-2
		reservationDto.setCustomerEmail("chandraravi2@gmail.com");
		reservationDto.setMaxLevel(1);
		reservationDto.setMinLevel(3);
		reservationDto.setNoOfSeats(1131);
		ReservationDetails reservationDetails2=ticketServiceInteraction.findAndHoldSeats(reservationDto);
		assertNotNull(reservationDetails);
		assertEquals(869,ticketServiceInteraction.findAvailableSeats(reservationDetails2.getLevelid()).getAvailableSeats());
		assertNotEquals(870,ticketServiceInteraction.findAvailableSeats(reservationDetails2.getLevelid()).getAvailableSeats());
		assertEquals(2,reservationDetails2.getLevelid());
		
	}
	
	/*
	 * It will test (maxlevel==minlevel) and (maxlevel>minlevel) scenario
	 * if (maxlevel==minlevel) then it will check one level of seats to hold if it is not 
	 * available send response as canthold
	 * 
	 * if(maxlevel>minlevel) this is a invalid scenario but it will check for minlevel seats availability 
	 * with needed seats if it is not available send response as canthold
	 * */
	@Test
	public void testFindAndHoldSeats_scenario2(){
		
		//case-1
		reservationDto.setCustomerEmail("chandraravi2@gmail.com");
		reservationDto.setMaxLevel(3);
		reservationDto.setMinLevel(3);
		reservationDto.setNoOfSeats(1499);
		ReservationDetails reservationDetails=ticketServiceInteraction.findAndHoldSeats(reservationDto);
		assertNotNull(reservationDetails);
		assertEquals(1,ticketServiceInteraction.findAvailableSeats(reservationDetails.getLevelid()).getAvailableSeats());
		assertNotEquals(5,ticketServiceInteraction.findAvailableSeats(reservationDetails.getLevelid()).getAvailableSeats());
		assertEquals("hold",reservationDetails.getStatus());
		
		//case-2
		reservationDto.setCustomerEmail("chandraravi2@gmail.com");
		reservationDto.setMaxLevel(4);
		reservationDto.setMinLevel(4);
		reservationDto.setNoOfSeats(1501);
		ReservationDetails reservationDetails1=ticketServiceInteraction.findAndHoldSeats(reservationDto);
		assertNotNull(reservationDetails1);
		assertEquals("canthold",reservationDetails1.getStatus());
		assertNotEquals("hold",reservationDetails1.getStatus());
		
	}
	
	/*
	 * It will test the scenario when user does not provide 
	 * either maxlevel or minlevel
	 * In this scenario it will select all the available seats 
	 * starting from level 4 to level 1 which ever is available it will
	 * hold for the customer
	 */
	@Test
	public void testFindAndHoldSeats_scenario3(){
		//case1
		reservationDto.setCustomerEmail("test@gmail.com");
		reservationDto.setNoOfSeats(1000);
		ticketServiceInteraction.findAndHoldSeats(reservationDto);
		assertEquals(500,ticketServiceInteraction.findAvailableSeats(4).getAvailableSeats());
		assertNotEquals(25,ticketServiceInteraction.findAvailableSeats(4).getAvailableSeats());
		
		//case2
		reservationDto.setCustomerEmail("test@gamil.com");
		reservationDto.setNoOfSeats(2000);
		ticketServiceInteraction.findAndHoldSeats(reservationDto);
		assertEquals(0,ticketServiceInteraction.findAvailableSeats(2).getAvailableSeats());
	}
	
	/*
	 * It will test the scenario when user wants to confirm the hold seats 
	 * In case of error it returns value 0 (possibility can be user cant hold seats
	 * at the time of hold , user input holdid and email might be wrong)
	 * other than that it return holdid
	 */
	@Test
	public void testConfirmTheHoldSeats_Scenario1(){
		reservationDto.setCustomerEmail("chandraravi2@gmail.com");
		reservationDto.setMaxLevel(4);
		reservationDto.setMinLevel(4);
		reservationDto.setNoOfSeats(1500);
		ReservationDetails reservationDetails1=ticketServiceInteraction.findAndHoldSeats(reservationDto);
		confirmReservationDto=ticketServiceInteraction.confirmTheHoldSeats(reservationDetails1.getId(), "chandraravi2@gmail.com");
		assertNotEquals(0,confirmReservationDto.getReservationID());
	}
	
	
	/*
	 *It will test the scenario when user wants to confirm the hold seats 
	 * In case of error it returns value 0 (possibility can be user cant hold seats
	 * at the time of hold , user input holdid and email might be wrong)
	 * other than that it return holdid
	 */
	@Test
	public void testConfirmTheHoldSeats_Scenario2(){
		reservationDto.setCustomerEmail("chandraravi2@gmail.com");
		reservationDto.setMaxLevel(4);
		reservationDto.setMinLevel(4);
		reservationDto.setNoOfSeats(1501);
		ReservationDetails reservationDetails1=ticketServiceInteraction.findAndHoldSeats(reservationDto);
		confirmReservationDto=ticketServiceInteraction.confirmTheHoldSeats(reservationDetails1.getId(), "chandraravi2@gmail.com");
		assertEquals(0,confirmReservationDto.getReservationID());
	}
	
}
