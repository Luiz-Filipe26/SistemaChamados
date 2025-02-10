package com.ticketsolutions.ticket_manager.auth.domain;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@SuppressWarnings("serial")
public class User implements UserDetails {

	private Long id;
    private String name;
    private String email;
    private String password;
    private LocalDate birthDate;
    private String role;
    private String location;
    
	@Override
	public List<SimpleGrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + this.role));
	}
	
	@Override
	public String getUsername() {
		return this.name;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

}
