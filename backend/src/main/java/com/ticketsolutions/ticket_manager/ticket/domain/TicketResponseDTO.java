package com.ticketsolutions.ticket_manager.ticket.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private Long userId;
    private String userName; // Adicionando o nome do usuário
    private LocalDate creationDate;
    private LocalDate updateDate;

    public TicketResponseDTO(Ticket ticket, String userName) {
        this.id = ticket.getId();
        this.title = ticket.getTitle();
        this.description = ticket.getDescription();
        this.status = ticket.getStatus();
        this.userId = ticket.getUserId();
        this.userName = userName; // Atribuindo o nome do usuário
        this.creationDate = ticket.getCreationDate();
        this.updateDate = ticket.getUpdateDate();
    }
}