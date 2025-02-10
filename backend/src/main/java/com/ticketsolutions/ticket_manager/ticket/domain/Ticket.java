package com.ticketsolutions.ticket_manager.ticket.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Ticket{
    private Long id;
    private String title;
    private String description;
    private String status;
    private Long userId;
    private LocalDate creationDate;
    private LocalDate updateDate;


}