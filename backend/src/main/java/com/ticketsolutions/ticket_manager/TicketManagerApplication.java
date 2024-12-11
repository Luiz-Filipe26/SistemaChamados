package com.ticketsolutions.ticket_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.converter.json.GsonBuilderUtils;

@SpringBootApplication
public class TicketManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketManagerApplication.class, args);
	}

}
