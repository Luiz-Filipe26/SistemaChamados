package com.ticketsolutions.ticket_manager.message.controller;

import com.ticketsolutions.ticket_manager.message.domain.Message;
import com.ticketsolutions.ticket_manager.message.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public List<Message> getAllMessages() {
        logger.info("Iniciando a recuperação de todas as mensagens");

        List<Message> messages = messageService.getAllMessages();

        logger.info("Recuperação de todas as mensagens finalizada com sucesso");

        return messages;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        logger.info("Buscando mensagem com id: {}", id);

        Message message = messageService.getMessageById(id);

        if (message != null) {
            logger.info("Mensagem encontrada com id: {}", id);
            return ResponseEntity.ok(message);
        } else {
            logger.warn("Mensagem não encontrada com id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createMessage(@RequestBody Message message) {
        logger.info("Iniciando a criação de uma nova mensagem");

        try {
            Message createdMessage = messageService.createMessage(message);
            logger.info("Mensagem criada com sucesso, id: {}", createdMessage.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
        } catch (DataAccessException e) {
            logger.error("Erro ao criar a mensagem: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar a mensagem: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMessage(@PathVariable Long id, @RequestBody Message messageDetails) {
        logger.info("Iniciando a atualização da mensagem com id: {}", id);

        try {
            Message updatedMessage = messageService.modifyMessage(id, messageDetails);
            logger.info("Mensagem atualizada com sucesso, id: {}", id);
            return ResponseEntity.ok(updatedMessage);
        } catch (DataAccessException e) {
            logger.error("Erro ao atualizar a mensagem com id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar a mensagem: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMessage(@PathVariable Long id) {
        logger.info("Iniciando a exclusão da mensagem com id: {}", id);

        try {
            messageService.removeMessage(id);
            logger.info("Mensagem excluída com sucesso, id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException e) {
            logger.error("Erro ao deletar a mensagem com id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao deletar a mensagem: " + e.getMessage());
        }
    }

    @GetMapping("/ticket/{ticketId}")
    public List<Message> getMessagesByTicketId(@PathVariable Long ticketId) {
        logger.info("Buscando mensagens para o ticket com id: {}", ticketId);

        List<Message> messages = messageService.retrieveMessagesByTicketId(ticketId);

        logger.info("Recuperação de mensagens para o ticket com id: {} finalizada", ticketId);

        return messages;
    }
}