package com.ticketsolutions.ticket_manager.ticket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
public class Ticket(){
    private Long id;
    private String title;
    private String description;
    private String status;
    private Long userId;
    private LocalDate creationDate;
    private LocalDate updateDate;




}