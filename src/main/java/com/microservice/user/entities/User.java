package com.microservice.user.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
}
