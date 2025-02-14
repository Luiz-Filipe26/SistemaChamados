package com.ticketsolutions.ticket_manager.message.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.ticketsolutions.ticket_manager.auth.repository.UserDao;
import com.ticketsolutions.ticket_manager.message.domain.Message;
import com.ticketsolutions.ticket_manager.message.domain.MessageResponseDTO;
import com.ticketsolutions.ticket_manager.message.repository.MessageDao;

@Service
public class MessageService {

	private final MessageDao messageDao;
	private final UserDao userDao;

	public MessageService(MessageDao messageDao, UserDao userDao) {
		this.messageDao = messageDao;
		this.userDao = userDao;
	}

	public List<MessageResponseDTO> getAllMessages() {
	    var messages = messageDao.findAll();
	    
	    var userNames = userDao.getIdToUserNameById(
	            messages.stream().map(Message::getId).collect(Collectors.toSet()));
	    
	    return messages.stream()
	        .map(m -> new MessageResponseDTO(m, userNames.getOrDefault(m.getId(), "Nome não encontrado")))
	        .toList();
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

	public Message createMessage(Message message) throws DataAccessException {
		message.setCreationDate(LocalDate.now());
		message.setCreationTime(LocalTime.now());
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

	    var userNames = userDao.getIdToUserNameById(
	            messages.stream().map(Message::getUserId).collect(Collectors.toSet()));

	    return messages.stream()
	        .map(m -> new MessageResponseDTO(m, userNames.getOrDefault(m.getUserId(), "Nome não encontrado")))
	        .toList();
	}
}