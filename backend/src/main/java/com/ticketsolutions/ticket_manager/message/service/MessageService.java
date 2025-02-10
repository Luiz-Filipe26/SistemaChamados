package com.ticketsolutions.ticket_manager.message.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.ticketsolutions.ticket_manager.message.domain.Message;
import com.ticketsolutions.ticket_manager.message.repository.MessageDao;

@Service
public class MessageService {

    private final MessageDao messageDao;

    public MessageService(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    public List<Message> getAllMessages() {
        return messageDao.findAll();
    }

    public Message getMessageById(Long id) {
        return messageDao.findById(id);
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

    public List<Message> retrieveMessagesByTicketId(Long ticketId) {
        return messageDao.findByTicketId(ticketId);
    }
}