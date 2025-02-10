package com.ticketsolutions.ticket_manager.message.domain;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Message {
    private Long id;
    private Long ticketId;
    private Long userId;
    private String text;
    private LocalDate creationDate;
    private LocalTime creationTime;
    private String status;
}