package com.ticketsolutions.ticket_manager.ticket.service;

import com.ticketsolutions.ticket_manager.ticket.repository.TicketDao;
import com.ticketsolutions.ticket_manager.ticket.model.Ticket;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketDao ticketDao;

    public TicketService(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }

    public List<Ticket> getAllTickets() {
        return ticketDao.findAll();
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketDao.findById(id);
    }

    public Ticket createTicket(Ticket ticket) {
        ticket.setCreationDate(LocalDate.now());
        ticket.setUpdateDate(LocalDate.now());
        return ticketDao.save(ticket);
    }

    public Optional<Ticket> updateTicket(Long id, Ticket ticketDetails) {
        return ticketDao.findById(id).map(existingTicket -> {
            existingTicket.setTitle(ticketDetails.getTitle());
            existingTicket.setDescription(ticketDetails.getDescription());
            existingTicket.setStatus(ticketDetails.getStatus());
            existingTicket.setUserId(ticketDetails.getUserId());
            existingTicket.setUpdateDate(LocalDate.now());
            return ticketDao.update(id, existingTicket);
        });
    }

    public boolean deleteTicket(Long id) {
        if (ticketDao.findById(id).isPresent()) {
            ticketDao.deleteById(id);
            return true;
        }
        return false;
    }
}
