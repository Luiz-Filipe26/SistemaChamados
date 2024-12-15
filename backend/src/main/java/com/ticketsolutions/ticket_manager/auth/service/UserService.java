package com.ticketsolutions.ticket_manager.auth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ticketsolutions.ticket_manager.auth.domain.User;
import com.ticketsolutions.ticket_manager.auth.repository.UserDao;

@Service
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> fetchAllUsers() {
        return userDao.findAll();
    }

    public Optional<User> fetchUserById(Long id) {
        return userDao.findById(id);
    }
    
    public Optional<User> fetchUserByName(String name) {
        return userDao.findByName(name);
    }

    public Optional<User> fetchUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public boolean userExistsByNameOrEmail(String name, String email) {
        return userDao.findByNameOrEmail(name, email).isPresent();
    }

    public User registerUser(User user) {
        if (userExistsByNameOrEmail(user.getName(), user.getEmail())) {
            throw new IllegalArgumentException("Name or email already in use");
        }
        return userDao.save(user);
    }

    public User modifyUser(Long id, User userDetails) {
        Optional<User> existingUserOpt = userDao.findById(id);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            existingUser.setBirthDate(userDetails.getBirthDate());
            existingUser.setRole(userDetails.getRole());
            existingUser.setLocation(userDetails.getLocation());

            return userDao.update(id, existingUser);
        }
        return null;
    }

    public void removeUser(Long id) {
        userDao.deleteById(id);
    }
}