package com.ticketsolutions.ticket_manager.ticket.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ticketsolutions.ticket_manager.ticket.domain.Ticket;
import com.ticketsolutions.ticket_manager.ticket.repository.TicketDao;

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

    public Ticket updateTicket(Long id, Ticket ticketDetails) {
    	Optional<Ticket> existingTicketOpt = ticketDao.findById(id);
    	if(existingTicketOpt.isPresent()) {
    		Ticket existingTicket = existingTicketOpt.get();
            existingTicket.setTitle(ticketDetails.getTitle());
            existingTicket.setDescription(ticketDetails.getDescription());
            existingTicket.setStatus(ticketDetails.getStatus());
            existingTicket.setUserId(ticketDetails.getUserId());
            existingTicket.setCreationDate(ticketDetails.getCreationDate());
            existingTicket.setUpdateDate(LocalDate.now());
            
            return ticketDao.update(id, existingTicket);
    	}
        return null;
    }

    public boolean deleteTicket(Long id) {
        if (ticketDao.findById(id).isPresent()) {
            ticketDao.deleteById(id);
            return true;
        }
        return false;
    }
}
