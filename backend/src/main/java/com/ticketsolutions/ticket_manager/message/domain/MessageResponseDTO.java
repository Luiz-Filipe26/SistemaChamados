package com.ticketsolutions.ticket_manager.message.domain;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class MessageResponseDTO {
    private Long id;
    private Long ticketId;
    private Long userId;
    private String userName;
    private String text;
    private LocalDate creationDate;
    private LocalTime creationTime;
    private String status;
    
    public MessageResponseDTO(Message message, String userName) {
    	this.id = message.getId();
    	this.ticketId = message.getTicketId();
    	this.userId = message.getUserId();
    	this.userName = userName;
    	this.text = message.getText();
    	this.creationDate = message.getCreationDate();
    	this.creationTime = message.getCreationTime();
    	this.status = message.getStatus();
    }
}