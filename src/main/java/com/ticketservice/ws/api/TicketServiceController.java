package com.ticketservice.ws.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ticketservice.ws.dto.ConfirmReservationDTO;
import com.ticketservice.ws.dto.ReservationDTO;
import com.ticketservice.ws.dto.SeatsAvailabilityDTO;
import com.ticketservice.ws.interaction.TicketServiceInteraction;
import com.ticketservice.ws.model.ReservationDetails;


@RestController
public class TicketServiceController {
	
	@Autowired
	private TicketServiceInteraction ticketServiceInteraction;
	

	/*
	 * Restful Service GET (Endpoint) provides number of available
	 * seats in a particular level (1,4)
	 * @PathParam is id (levelid)
	 */
	@RequestMapping(value= ApiEndPoints._NUM_SEATS_AVAILABL,
			method= RequestMethod.GET,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public SeatsAvailabilityDTO getNumSeatsAvailable(@PathVariable int id){
		return ticketServiceInteraction.findAvailableSeats(id);
	}
	
	
	/*
	 * Restful Service POST (Endpoint) finds available seats and
	 * holds them 
	 */
	@RequestMapping(value = ApiEndPoints._HOLD_SEATS, method = RequestMethod.POST, produces={MediaType.APPLICATION_JSON_VALUE}, consumes=MediaType.APPLICATION_JSON_VALUE)
	public ReservationDetails findAndHoldSeats(@RequestBody ReservationDTO reservationDto){
		return ticketServiceInteraction.findAndHoldSeats(reservationDto);
		
	}
	
	/*
	 * Restful Service GET (Endpoint) reserves the held seats
	 * @QueryParam is seatHoldId,customerEmailaddress
	 */
	@RequestMapping(value = ApiEndPoints._CONFIRM_HOLD_SEATS, method = RequestMethod.GET, produces={MediaType.APPLICATION_JSON_VALUE})
	public ConfirmReservationDTO confirmTheHoldSeats(@RequestParam("seatHoldId") Long seatHoldId,@RequestParam("customerEmail") String customerEmail ){
		return ticketServiceInteraction.confirmTheHoldSeats(seatHoldId, customerEmail);
	}
	
	
}
