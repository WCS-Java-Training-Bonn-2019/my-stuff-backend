package de.telekom.sea.mystuff.backend.model;

import java.sql.Date;

import javax.persistence.Entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
public class Item {

	private String name;
	private int amount;
	private String location;
	private String description;
	private Date lastUsed;
	private byte[] photo;
	
}
