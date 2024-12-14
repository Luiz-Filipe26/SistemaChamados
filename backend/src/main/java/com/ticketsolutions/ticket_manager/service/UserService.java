package com.ticketsolutions.ticket_manager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ticketsolutions.ticket_manager.dao.UserDao;
import com.ticketsolutions.ticket_manager.model.User;

@Service
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    // Método para retornar todos os usuários
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public Optional<User> getUserById(Long id) {
        return userDao.getUserById(id);
    }

    public boolean isUserExists(String name, String email) {
        return userDao.getUserByNameOrEmail(name, email).isPresent();
    }

    public User createUser(User user) {
        if (isUserExists(user.getName(), user.getEmail())) {
            throw new IllegalArgumentException("Name or email already in use");
        }
        return userDao.createUser(user);
    }

    public User updateUser(Long id, User userDetails) {
        Optional<User> existingUserOpt = userDao.getUserById(id);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            existingUser.setBirthDate(userDetails.getBirthDate());
            existingUser.setRole(userDetails.getRole());
            existingUser.setLocation(userDetails.getLocation());

            return userDao.updateUser(id, existingUser);
        }
        return null;
    }

    public void deleteUser(Long id) {
        userDao.deleteUser(id);
    }
}