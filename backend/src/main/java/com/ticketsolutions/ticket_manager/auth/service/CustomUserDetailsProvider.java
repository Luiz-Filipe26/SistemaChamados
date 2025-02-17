package com.ticketsolutions.ticket_manager.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ticketsolutions.ticket_manager.auth.domain.User;
import com.ticketsolutions.ticket_manager.auth.repository.UserDao;

@Service
public class CustomUserDetailsProvider implements UserDetailsService {

	private final UserDao userDao;

	public CustomUserDetailsProvider(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    User user = userDao.findByName(username);
	    if (user == null) {
	        throw new UsernameNotFoundException("User not found with username: " + username);
	    }
	    return user;
	}

}