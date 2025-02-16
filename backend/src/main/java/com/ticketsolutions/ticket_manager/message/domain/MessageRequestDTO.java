package com.ticketsolutions.ticket_manager.message.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageRequestDTO {
    private Long ticketId;
    private String userName;
    private String text;
    private String status;
}