package com.ticketservice.ws.interaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ticketservice.ws.dao.ReservationDetailsRepository;
import com.ticketservice.ws.dao.SeatsAvailabilityRepository;
import com.ticketservice.ws.dto.ConfirmReservationDTO;
import com.ticketservice.ws.dto.ReservationDTO;
import com.ticketservice.ws.dto.SeatsAvailabilityDTO;
import com.ticketservice.ws.model.ReservationDetails;
import com.ticketservice.ws.model.SeatsAvailability;

@Service
public class TicketServiceInteraction {

	@Autowired
	private SeatsAvailabilityRepository seatsAvailabilityRepository;
	
	@Autowired
	private ReservationDetailsRepository reservationDetailsRepository;
	
	public SeatsAvailabilityDTO findAvailableSeats(int levelId) {
		SeatsAvailabilityDTO seatsAvailabilityDto=new SeatsAvailabilityDTO();
		SeatsAvailability seatsAvailability= seatsAvailabilityRepository.findByLevelId(levelId);
		seatsAvailabilityDto.setAvailableSeats(seatsAvailability.getAvailableSeats());
		
		return seatsAvailabilityDto;
	}
	
	/*
	 * This function is to find the available seats based on the 
	 * customer's interest and if they are available it holds them and
	 * reduce the number from the available list.
	 * It tries to find the maxlevel first and then goes lower levels
	 * maxlevel is 1 and remaining (2,3,4) comes in order
	 * @param ReservationDetailsObject
	 */
	public ReservationDetails findAndHoldSeats(ReservationDTO reservationDto){
		
		//if seats are not available for holding it has a default value 0 
		int confirmLevelForHolding=0;
		ReservationDetails reservationDetails=new ReservationDetails();
		if(reservationDto!=null){
			if(reservationDto.getMaxLevel()>0 && reservationDto.getMinLevel()>0){
				if(reservationDto.getMaxLevel()<reservationDto.getMinLevel()){
					for(int level=reservationDto.getMaxLevel();level<=reservationDto.getMinLevel();level++){
						if(seatsAvailabilityRepository.findByLevelId(level).getAvailableSeats()>=reservationDto.getNoOfSeats()){
							confirmLevelForHolding=level;
							break;
						}
					}
				}else if((reservationDto.getMaxLevel()==reservationDto.getMinLevel())||reservationDto.getMaxLevel()>reservationDto.getMinLevel()){
					if(reservationDto.getMaxLevel()==reservationDto.getMinLevel()){
						if(seatsAvailabilityRepository.findByLevelId(reservationDto.getMaxLevel()).getAvailableSeats()>=reservationDto.getNoOfSeats()){
							confirmLevelForHolding=reservationDto.getMaxLevel();
						}
					}else{
						if(seatsAvailabilityRepository.findByLevelId(reservationDto.getMinLevel()).getAvailableSeats()>=reservationDto.getNoOfSeats()){
							confirmLevelForHolding=reservationDto.getMinLevel();
						}
					}
				}
			}else{
				for(int level=4;level>=1;level--){
					if(seatsAvailabilityRepository.findByLevelId(level).getAvailableSeats()>=reservationDto.getNoOfSeats()){
						confirmLevelForHolding=level;
						break;
					}
				}
			}
			reservationDetails.setCustomeremail(reservationDto.getCustomerEmail());
			reservationDetails.setLevelid(confirmLevelForHolding);
			if(confirmLevelForHolding<=0){
				reservationDetails.setSeatscount(0);
				reservationDetails.setStatus("canthold");
			}else{
				reservationDetails.setStatus("hold");
				reservationDetails.setSeatscount(reservationDto.getNoOfSeats());
			}
			reservationDetails=reservationDetailsRepository.saveAndFlush(reservationDetails);
			//If the seats are available to hold then the number should be subtracted from seatsavailability table
			if(reservationDetails.getSeatscount()>0 && reservationDetails.getLevelid()>0 && reservationDetails.getStatus().equals("hold")){
				SeatsAvailability seatsAvailabilityAfterHolding=new SeatsAvailability();
				seatsAvailabilityAfterHolding=seatsAvailabilityRepository.findByLevelId(reservationDetails.getLevelid());
				seatsAvailabilityAfterHolding.setAvailableSeats(seatsAvailabilityAfterHolding.getAvailableSeats()-reservationDetails.getSeatscount());
				//after updating the availableseats it need to be saved again
				seatsAvailabilityRepository.saveAndFlush(seatsAvailabilityAfterHolding);
			}
		}
		return reservationDetails;
	}
	
	/*This function confirms the reservation for the seats that are held
	 * @param holdId the seat held identifier
	 * @param customerEmailAddress the email address of the customer
	 * */
	public ConfirmReservationDTO confirmTheHoldSeats(Long holdId,String customerEmailAddress){
		ConfirmReservationDTO confirmReservationDto=new ConfirmReservationDTO();
		ReservationDetails reservationDetails=reservationDetailsRepository.findByIdAndCustomeremail(holdId, customerEmailAddress.replace("\"", ""));
		int reservationId=0;
		if(reservationDetails!=null){
			if(reservationDetails.getStatus().equals("hold")){
				reservationDetails.setStatus("reserved");
				reservationDetailsRepository.saveAndFlush(reservationDetails);
				reservationId=reservationDetails.getId().intValue();
			}
		}
		confirmReservationDto.setReservationID(reservationId);
		return confirmReservationDto;
	}
	
	
}
