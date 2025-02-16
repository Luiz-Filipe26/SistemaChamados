package com.ticketsolutions.ticket_manager.ticket.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.ticketsolutions.ticket_manager.auth.repository.UserDao;
import com.ticketsolutions.ticket_manager.ticket.domain.Ticket;
import com.ticketsolutions.ticket_manager.ticket.domain.TicketRequestDTO;
import com.ticketsolutions.ticket_manager.ticket.domain.TicketResponseDTO;
import com.ticketsolutions.ticket_manager.ticket.repository.TicketDao;

@Service
public class TicketService {

    private final TicketDao ticketDao;
    private final UserDao userDao;

    public TicketService(TicketDao ticketDao, UserDao userDao) {
        this.ticketDao = ticketDao;
		this.userDao = userDao;
    }

    public List<TicketResponseDTO> fetchAllTickets() {
        var tickets = ticketDao.findAll();

        // Obtém os userIds de todos os tickets e busca os nomes dos usuários.
        var userNames = userDao.getIdToUserNameById(
                tickets.stream().map(Ticket::getUserId).collect(Collectors.toSet()));

        return tickets.stream()
                .map(t -> new TicketResponseDTO(t, userNames.getOrDefault(t.getUserId(), "Nome não encontrado")))
                .toList();
    }

    public TicketResponseDTO fetchTicketById(Long id) {
        var ticket = ticketDao.findById(id);
        if (ticket != null) {
            // Obtém o userId do ticket e busca o nome do usuário.
            var userNames = userDao.getIdToUserNameById(List.of(ticket.getUserId()));
            String userName = userNames.getOrDefault(ticket.getUserId(), "Nome não encontrado");
            return new TicketResponseDTO(ticket, userName);
        }
        return null;
    }

    @SuppressWarnings("serial")
	public Ticket createTicket(TicketRequestDTO ticketRequest) throws DataAccessException {
    	var creationDate = LocalDate.now();
    	var updateDate = LocalDate.now();
    	var user = userDao.findByName(ticketRequest.getUserName());
    	if(user == null) {
    		throw new DataAccessException("Nome de usuário não encontrado!") {};
    	}
    	
    	Long userId = user.getId();
    	var ticket = new Ticket(null, ticketRequest.getTitle(), ticketRequest.getDescription(), "Aberto", userId, creationDate, updateDate);
        
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
