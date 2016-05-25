package com.ticketservice.ws.dao;

import com.ticketservice.ws.model.SeatsAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatsAvailabilityRepository extends JpaRepository<SeatsAvailability, Long> {
	SeatsAvailability findByLevelId(int levelId);
}
