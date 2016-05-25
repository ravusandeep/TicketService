package com.ticketservice.ws.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ReservationDetails {

	@Id
    @GeneratedValue
    private Long id;
	
	private String customeremail;
	
	private int levelid;
	
	private int seatscount;
	
	private String status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCustomeremail() {
		return customeremail;
	}

	public void setCustomeremail(String customeremail) {
		this.customeremail = customeremail;
	}

	public int getLevelid() {
		return levelid;
	}

	public void setLevelid(int levelid) {
		this.levelid = levelid;
	}

	public int getSeatscount() {
		return seatscount;
	}

	public void setSeatscount(int seatscount) {
		this.seatscount = seatscount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
