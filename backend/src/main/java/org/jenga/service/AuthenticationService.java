package org.jenga.service;

import org.jenga.model.User;
import org.jenga.db.UserRepository;
import org.jenga.dto.RegisterRequestDTO;
import org.jenga.dto.LoginRequestDTO;
import org.jenga.dto.LoginResponseDTO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import javax.security.auth.login.LoginException;
import io.quarkus.elytron.security.common.BcryptUtil;

@ApplicationScoped
public class AuthenticationService {

    @Inject
    UserRepository userRepository;

    @Transactional
    public LoginResponseDTO register(RegisterRequestDTO registerRequest) {
        User user = userRepository.findByUsername(registerRequest.getUsername());

        if (user != null) {
            throw new RuntimeException("User already exists");
        }

        String username = registerRequest.getUsername();
        String email = registerRequest.getEmail();
        String hashedPassword = BcryptUtil.bcryptHash(registerRequest.getPassword());
        User newUser = new User(username, email, hashedPassword, null, null);
        userRepository.persist(newUser);


        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setUsername(username);
        loginResponse.setToken("Token");
        loginResponse.setExpirationDate("2025-12-25");

        return loginResponse;
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequest) throws LoginException {
        User user = userRepository.findByUsername(loginRequest.getUsername());

        if (user == null) {
            throw new RuntimeException("Invalid username or password");
        }

        if (!BcryptUtil.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setUsername(loginRequest.getUsername());
        loginResponse.setToken("Token");
        loginResponse.setExpirationDate("2025-12-25");

        return loginResponse;
    }
}
