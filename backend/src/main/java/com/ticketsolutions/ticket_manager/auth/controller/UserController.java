package com.ticketsolutions.ticket_manager.auth.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticketsolutions.ticket_manager.auth.domain.AuthenticationDTO;
import com.ticketsolutions.ticket_manager.auth.domain.RegisterDTO;
import com.ticketsolutions.ticket_manager.auth.domain.User;
import com.ticketsolutions.ticket_manager.auth.repository.UserDao;
import com.ticketsolutions.ticket_manager.auth.security.TokenProvider;
import com.ticketsolutions.ticket_manager.auth.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
	private final AuthenticationManager authenticationManager;
	private final UserService userService;
	private final UserDao userDao;
	private final TokenProvider tokenProvider;
	private final PasswordEncoder passwordEncoder;

	public UserController(UserService userService, UserDao userDao, PasswordEncoder passwordEncoder,
			TokenProvider tokenProvider, AuthenticationConfiguration authenticationConfiguration) throws Exception {
		this.userService = userService;
		this.userDao = userDao;
		this.passwordEncoder = passwordEncoder;
		this.tokenProvider = tokenProvider;
		this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO data) {
	    try {
	        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
	        var auth = this.authenticationManager.authenticate(usernamePassword);
	        var token = tokenProvider.generateToken((User) auth.getPrincipal());

	        ResponseCookie cookie = ResponseCookie.from("auth_token", token)
	                .httpOnly(true)
	                .secure(false)
	                .path("/")
	                .maxAge(3600)
	                .build();

	        return ResponseEntity.ok()
	                .header(HttpHeaders.SET_COOKIE, cookie.toString())
	                .build();
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }
	}
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data) {
        if (this.userService.fetchUserByName(data.name()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        String encryptedPassword = passwordEncoder.encode(data.password());
        User newUser = new User(0L, data.name(), data.email(), encryptedPassword, data.birthDate(), data.role(), data.location());
        this.userDao.save(newUser);

        return ResponseEntity.ok().build();
	}

	@GetMapping
	public List<User> getAllUsers() {
		return userService.fetchAllUsers();
	}

	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Long id) {
		Optional<User> user = userService.fetchUserById(id);
		return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user) {
		if (userService.userExistsByNameOrEmail(user.getName(), user.getEmail())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		User createdUser = userService.registerUser(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
	}

	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
		User updatedUser = userService.modifyUser(id, userDetails);
		return ResponseEntity.ok(updatedUser);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.removeUser(id);
		return ResponseEntity.noContent().build();
	}
}
