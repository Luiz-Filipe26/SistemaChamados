package com.ticketsolutions.ticket_manager.auth.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticketsolutions.ticket_manager.auth.domain.AuthenticationDTO;
import com.ticketsolutions.ticket_manager.auth.domain.RegisterDTO;
import com.ticketsolutions.ticket_manager.auth.domain.User;
import com.ticketsolutions.ticket_manager.auth.service.TokenProvider;
import com.ticketsolutions.ticket_manager.auth.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
	private final TokenProvider tokenProvider;

    public UserController(UserService userService, TokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }
    
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        String tokenValue = token.substring(7); // Remove "Bearer "
        try {
            String userName = tokenProvider.validateToken(tokenValue);
            return ResponseEntity.ok().body(userName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO data) {
        logger.info("Iniciando login para o usuário: {}", data.login());
        try {
            var token = userService.fetchToken(data);
            logger.info("Login bem-sucedido para o usuário: {}", data.login());
            return ResponseEntity.ok().body("Bearer " + token);
        } catch (BadCredentialsException e) {
            logger.warn("Credenciais inválidas para o usuário: {}", data.login());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            logger.error("Erro inesperado durante o login para o usuário: {}", data.login(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data) {
        logger.info("Iniciando o registro do usuário: {}", data.name());
        if (this.userService.fetchUserByName(data.name()) != null) {
            logger.warn("Nome de usuário já está em uso: {}", data.name());
            return ResponseEntity.badRequest().body("Username already in use");
        }

        try {
            userService.registerUser(data, true);
            logger.info("Usuário registrado com sucesso: {}", data.name());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao registrar usuário com nome {}: {}", data.name(), e.getMessage());
            return ResponseEntity.badRequest().body("Invalid arguments: " + e.getMessage());
        } catch (DataAccessException e) {
            logger.error("Erro ao acessar dados ao tentar registrar o usuário: {}", data.name(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao acessar dados ao tentar registrar o usuário");
        } catch (Exception e) {
            logger.error("Erro inesperado ao registrar usuário: {}", data.name(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public List<User> getAllUsers() {
        logger.info("Iniciando a recuperação de todos os usuários");
        List<User> users = userService.fetchAllUsers();
        logger.info("Recuperação de todos os usuários finalizada com sucesso");
        return users;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info("Buscando usuário com id: {}", id);
        User user = userService.fetchUserById(id);
        if (user != null) {
            logger.info("Usuário encontrado com id: {}", id);
            return ResponseEntity.ok(user);
        } else {
            logger.warn("Usuário não encontrado com id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        logger.info("Iniciando a atualização do usuário com id: {}", id);
        try {
            User updatedUser = userService.modifyUser(id, userDetails);
            logger.info("Usuário atualizado com sucesso, id: {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (DataAccessException e) {
            logger.error("Erro ao atualizar o usuário com id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar o usuário: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado ao atualizar o usuário com id {}: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("Iniciando a exclusão do usuário com id: {}", id);
        try {
            userService.removeUser(id);
            logger.info("Usuário excluído com sucesso, id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (DataAccessException e) {
            logger.error("Erro ao deletar o usuário com id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao deletar o usuário: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado ao deletar o usuário com id {}: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
