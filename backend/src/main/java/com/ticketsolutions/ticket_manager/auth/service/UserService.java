package com.ticketsolutions.ticket_manager.auth.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ticketsolutions.ticket_manager.auth.domain.AuthenticationDTO;
import com.ticketsolutions.ticket_manager.auth.domain.RegisterDTO;
import com.ticketsolutions.ticket_manager.auth.domain.User;
import com.ticketsolutions.ticket_manager.auth.repository.UserDao;

@Service
public class UserService {

	private final UserDao userDao;
	private final AuthenticationManager authenticationManager;
	private final TokenProvider tokenProvider;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserDao userDao, AuthenticationConfiguration authenticationConfiguration,
			TokenProvider tokenProvider, PasswordEncoder passwordEncoder) throws Exception {
		this.userDao = userDao;
		this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
		this.tokenProvider = tokenProvider;
		this.passwordEncoder = passwordEncoder;
	}

	public String fetchToken(AuthenticationDTO data) {
		var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
		var auth = this.authenticationManager.authenticate(usernamePassword);
		return tokenProvider.generateToken((User) auth.getPrincipal());
	}

	public List<User> fetchAllUsers() {
		return userDao.findAll();
	}

	public User fetchUserById(Long id) {
		return userDao.findById(id);
	}

	public User fetchUserByName(String name) {
		return userDao.findByName(name);
	}

	public User fetchUserByEmail(String email) {
		return userDao.findByEmail(email);
	}

	public boolean userExistsByNameOrEmail(String name, String email) {
		return userDao.findByNameOrEmail(name, email) != null;
	}

	public User registerUser(RegisterDTO registerDTO, boolean validateUserExistence) throws DataAccessException {
		if (validateUserExistence && userExistsByNameOrEmail(registerDTO.name(), registerDTO.email())) {
			throw new IllegalArgumentException("Name or email already in use");
		}

		String encryptedPassword = passwordEncoder.encode(registerDTO.password());
		User user = new User(null, registerDTO.name(), registerDTO.email(), encryptedPassword, registerDTO.birthDate(),
				registerDTO.role(), registerDTO.location());

		return userDao.save(user);
	}

	public User modifyUser(Long id, User userDetails) throws DataAccessException {
		return userDao.update(id, userDetails);
	}

	public void removeUser(Long id) throws DataAccessException {
		userDao.deleteById(id);
	}
}