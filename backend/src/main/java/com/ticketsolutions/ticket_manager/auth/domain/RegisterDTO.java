package com.ticketsolutions.ticket_manager.auth.domain;

import java.time.LocalDate;

public record RegisterDTO(String name, String email, String password, LocalDate birthDate, String role,
		String location) {

}
