package com.microservice.user.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "micro_users")
public class User {
	@Id
	@Column(name = "ID")
	private String userId;
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "EMAIL", nullable = false, unique = true)
	private String email;

    @Column(name = "ABOUT")
    private String about;

    // store the rating which this user provided to the hotels
    @Transient // this will make sures Rating table will not be created in this DB, this will be fetched from the rating service
    private List<Rating> ratings = new ArrayList<Rating>();

}
