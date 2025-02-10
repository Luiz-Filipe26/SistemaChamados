package com.ticketsolutions.ticket_manager.ticket.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticketsolutions.ticket_manager.ticket.domain.Ticket;
import com.ticketsolutions.ticket_manager.ticket.service.TicketService;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<Ticket> getAllTickets() {
        logger.info("Iniciando a recuperação de todos os tickets");
        
        List<Ticket> tickets = ticketService.fetchAllTickets();
        
        logger.info("Recuperação de todos os tickets finalizada com sucesso");
        
        return tickets;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        logger.info("Buscando ticket com id: {}", id);
        
        Ticket ticket = ticketService.fetchTicketById(id);
        
        if (ticket != null) {
            logger.info("Ticket encontrado com id: {}", id);
            return ResponseEntity.ok(ticket);
        } else {
            logger.warn("Ticket não encontrado com id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody Ticket ticket) {
        logger.info("Iniciando a criação de um novo ticket");
        
        try {
            Ticket createdTicket = ticketService.createTicket(ticket);
            logger.info("Ticket criado com sucesso, id: {}", createdTicket.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
        } catch (DataAccessException e) {
            logger.error("Erro ao criar o ticket: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar o ticket: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTicket(@PathVariable Long id, @RequestBody Ticket ticketDetails) {
        logger.info("Iniciando a atualização do ticket com id: {}", id);
        
        try {
            Ticket updatedTicket = ticketService.modifyTicket(id, ticketDetails);
            logger.info("Ticket atualizado com sucesso, id: {}", id);
            return ResponseEntity.ok(updatedTicket);
        } catch (DataAccessException e) {
            logger.error("Erro ao atualizar o ticket com id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar o ticket: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable Long id) {
        logger.info("Iniciando a exclusão do ticket com id: {}", id);
        
        try {
            ticketService.removeTicket(id);
            logger.info("Ticket excluído com sucesso, id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException e) {
            logger.error("Erro ao deletar o ticket com id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao deletar o ticket: " + e.getMessage());
        }
    }
}