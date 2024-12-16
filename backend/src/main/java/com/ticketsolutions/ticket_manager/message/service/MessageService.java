package com.ticketsolutions.ticket_manager.message.service;

import com.ticketsolutions.ticket_manager.message.repository.MessageDao;
import com.ticketsolutions.ticket_manager.message.domain.Message;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;


@Service
public class MessageService {

    private final MessageDao messageDao;

    public MessageService(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    // Buscar todas as mensagens de um ticket
    public List<Message> getMessagesByTicketId(Long ticketId) {
        return messageDao.findByTicketId(ticketId);
    }

    // Buscar uma mensagem por ID
    public Optional<Message> getMessageById(Long id) {
        return messageDao.findById(id);
    }

    // Criar uma nova mensagem
    public Message createMessage(Message message) {
        return messageDao.save(message);
    }
}
