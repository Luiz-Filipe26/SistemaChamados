package com.ticketsolutions.ticket_manager.ticket.domain;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class TicketRequestDTO{
    private String title;
    private String description;
    private String userName;
}