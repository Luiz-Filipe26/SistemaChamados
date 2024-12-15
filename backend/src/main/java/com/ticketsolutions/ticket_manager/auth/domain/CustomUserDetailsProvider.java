package com.ticketsolutions.ticket_manager.auth.domain;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ticketsolutions.ticket_manager.auth.repository.UserDao;

@Service
public class CustomUserDetailsProvider implements UserDetailsService {

	private final UserDao userDao;

	public CustomUserDetailsProvider(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userDao.findByName(username).orElseThrow(() -> {
			return new UsernameNotFoundException("User not found with username: " + username);
		});
	}
}