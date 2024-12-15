package com.ticketsolutions.ticket_manager.auth.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ticketsolutions.ticket_manager.auth.repository.UserDao;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {
	private final TokenProvider tokenProvider;
	private final UserDao userDao;

	public SecurityFilter(TokenProvider tokenProvider, UserDao userDao) {
		this.tokenProvider = tokenProvider;
		this.userDao = userDao;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		var token = this.recoverTokenFromRequest(request);
		if (token != null) {
			String login = tokenProvider.validateToken(token);
			UserDetails user = userDao.findByName(login).get();

			if (user != null) {
				var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);

			}
		}
		filterChain.doFilter(request, response);
	}

	private String recoverTokenFromRequest(HttpServletRequest request) {
		var authHeader = request.getHeader("Authorization");
		if (authHeader == null)
			return null;
		return authHeader.replace("Bearer ", "");
	}
}
