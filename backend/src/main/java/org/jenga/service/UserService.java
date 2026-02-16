package org.jenga.service;

import java.util.List;

import org.jenga.db.UserRepository;
import org.jenga.model.User;
import org.jenga.dto.UserDTO;
import org.jenga.mapper.UserMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import io.quarkus.logging.Log;

@ApplicationScoped
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Inject
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper= userMapper;
    }

    @Transactional
    public UserDTO findByUsername(String username) {
        Log.infof("Fetch user with username %s", username);

        username = username.toLowerCase();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found with username: " + username);
        }
        return userMapper.userToUserDTO(user);
    }

    @Transactional
    public List<UserDTO> searchUsers(String usernamePart) {
        Log.infof("Search for user %s", usernamePart);

        List<User> users = userRepository.searchByUsernameStartsWith(usernamePart.toLowerCase());
        return users.stream().map(userMapper::userToUserDTO).toList();
    }

    @Transactional
    public List<UserDTO> findAll() {
        Log.info("Fetch all users");

        List<User> users = userRepository.findAll().list(); 
        return users.stream()
                    .map(userMapper::userToUserDTO).toList();
    }
}
