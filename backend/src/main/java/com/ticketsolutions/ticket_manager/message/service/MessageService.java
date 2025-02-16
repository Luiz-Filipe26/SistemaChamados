package com.ticketsolutions.ticket_manager.message.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.ticketsolutions.ticket_manager.auth.repository.UserDao;
import com.ticketsolutions.ticket_manager.message.domain.Message;
import com.ticketsolutions.ticket_manager.message.domain.MessageRequestDTO;
import com.ticketsolutions.ticket_manager.message.domain.MessageResponseDTO;
import com.ticketsolutions.ticket_manager.message.repository.MessageDao;
import com.ticketsolutions.ticket_manager.ticket.domain.Ticket;
import com.ticketsolutions.ticket_manager.ticket.repository.TicketDao;

@Service
public class MessageService {

	private final MessageDao messageDao;
	private final UserDao userDao;
	private final TicketDao ticketDao;

	public MessageService(MessageDao messageDao, UserDao userDao, TicketDao ticketDao) {
		this.messageDao = messageDao;
		this.userDao = userDao;
		this.ticketDao = ticketDao;
	}

	public List<MessageResponseDTO> getAllMessages() {
		var messages = messageDao.findAll();

		var userNames = userDao.getIdToUserNameById(messages.stream().map(Message::getId).collect(Collectors.toSet()));

		return messages.stream()
				.map(m -> new MessageResponseDTO(m, userNames.getOrDefault(m.getId(), "Nome não encontrado"))).toList();
	}

	public MessageResponseDTO getMessageById(Long id) {
		var message = messageDao.findById(id);
		if (message != null) {
			var userNames = userDao.getIdToUserNameById(List.of(message.getId()));
			String userName = userNames.getOrDefault(message.getId(), "Nome não encontrado");
			return new MessageResponseDTO(message, userName);
		}
		return null;
	}

	@SuppressWarnings("serial")
	public Message createMessage(MessageRequestDTO messagerequest) throws DataAccessException {
		var user = userDao.findByName(messagerequest.getUserName());
		if(user == null) {
			throw new DataAccessException("Nome de usuário não existe.") {};
		}
		
		var creationDate = LocalDate.now();
		var creationTime = LocalTime.now();
		var message = new Message(null, messagerequest.getTicketId(), user.getId(), messagerequest.getText(), creationDate,
				creationTime, messagerequest.getStatus());
		
		ticketDao.update(messagerequest.getTicketId(), new Ticket(messagerequest.getTicketId(), message.getStatus()));

		return messageDao.save(message);
	}

	public Message modifyMessage(Long id, Message messageDetails) throws DataAccessException {
		return messageDao.update(id, messageDetails);
	}

	public void removeMessage(Long id) throws DataAccessException {
		messageDao.deleteById(id);
	}

	public List<MessageResponseDTO> retrieveMessagesByTicketId(Long ticketId) {
		var messages = messageDao.findByTicketId(ticketId);

		var userNames = userDao
				.getIdToUserNameById(messages.stream().map(Message::getUserId).collect(Collectors.toSet()));

		return messages.stream()
				.map(m -> new MessageResponseDTO(m, userNames.getOrDefault(m.getUserId(), "Nome não encontrado")))
				.toList();
	}
}