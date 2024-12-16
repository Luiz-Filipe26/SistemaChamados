package com.ticketsolutions.ticket_manager.message.controller;

import com.ticketsolutions.ticket_manager.message.domain.Message;
import com.ticketsolutions.ticket_manager.message.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // Endpoint para criar uma nova mensagem
    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        Message createdMessage = messageService.createMessage(message);
        return ResponseEntity.status(201).body(createdMessage);
    }

    // Endpoint para buscar todas as mensagens de um ticket
    @GetMapping("/ticket/{ticketId}")
    public List<Message> getMessagesByTicketId(@PathVariable Long ticketId) {
        return messageService.getMessagesByTicketId(ticketId);
    }

    // Endpoint para buscar uma mensagem por ID
    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        Optional<Message> message = messageService.getMessageById(id);
        return message.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
