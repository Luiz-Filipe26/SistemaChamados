package com.ticketsolutions.ticket_manager.ticket.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.ticketsolutions.ticket_manager.ticket.domain.Ticket;
import com.ticketsolutions.ticket_manager.ticket.repository.TicketDao;

@Service
public class TicketService {

    private final TicketDao ticketDao;

    public TicketService(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }

    public List<Ticket> fetchAllTickets() {
        return ticketDao.findAll();
    }
    
    public Ticket fetchTicketById(Long id) {
        return ticketDao.findById(id);
    }

    public Ticket createTicket(Ticket ticket) throws DataAccessException {
        ticket.setCreationDate(LocalDate.now());
        ticket.setUpdateDate(LocalDate.now());
        return ticketDao.save(ticket);
    }
    
    public Ticket modifyTicket(Long id, Ticket ticketDetails) throws DataAccessException {
        ticketDetails.setUpdateDate(LocalDate.now());
        return ticketDao.update(id, ticketDetails);
    }

    public void removeTicket(Long id) throws DataAccessException {
        ticketDao.deleteById(id);
    }
}
