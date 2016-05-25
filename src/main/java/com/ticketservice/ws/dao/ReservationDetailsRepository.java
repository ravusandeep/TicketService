package com.ticketservice.ws.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticketservice.ws.model.ReservationDetails;


public interface ReservationDetailsRepository extends JpaRepository<ReservationDetails, Long> {
	ReservationDetails findByIdAndCustomeremail(Long holdId,String customerEmailAddress);
}
