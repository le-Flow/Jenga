package org.jenga.service;

import org.jenga.model.User;
import org.jenga.db.UserRepository;
import org.jenga.dto.RegisterRequestDTO;
import org.jenga.dto.LoginRequestDTO;
import org.jenga.dto.LoginResponseDTO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;

import javax.security.auth.login.LoginException;
import jakarta.ws.rs.BadRequestException;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import io.quarkus.logging.Log;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class AuthenticationService {

    private static final int EXPIRATION_TIME_SECONDS = 3600;

    private final UserRepository userRepository;

    @Context
    SecurityContext securityContext;

    @Transactional
    public LoginResponseDTO register(RegisterRequestDTO registerRequest) {
        Log.infof("Attempt registration with E-Mail %s and username %s", registerRequest.getEmail(), registerRequest.getUsername());

        String username = registerRequest.getUsername().toLowerCase();

        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            throw new BadRequestException("User already exists");
        }

        if (username == null || username.isEmpty()) {
            throw new BadRequestException("Username cannot be empty");
        }

        if (username.contains(" ")) {
            throw new BadRequestException("Username cannot contain spaces");
        }

        if (!username.matches("[a-zA-Z0-9]+")) {
            throw new BadRequestException("Username must only contain letters and numbers");
        } 

        String displayName = registerRequest.getDisplayName();
        if (displayName == null || displayName.isEmpty()) {
            displayName = username;
        }

        String email = registerRequest.getEmail();
        String hashedPassword = BcryptUtil.bcryptHash(registerRequest.getPassword());
        User user = new User(username, displayName, email, hashedPassword, null, null);
        userRepository.persist(user);

        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setUsername(username);
        loginResponse.setDisplayName(displayName);
        loginResponse.setToken(generateToken(user));
        loginResponse.setExpiresIn(EXPIRATION_TIME_SECONDS);

        Log.infof("User created with username %s", username);

        return loginResponse;
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequest) throws LoginException {
        User user = userRepository.findByUsername(loginRequest.getUsername().toLowerCase());

        Log.infof("Login attempt with username %s", user.getUsername());

        if (user == null) {
            Log.warnf("Failed login attempt with username %s: Invalid username", user.getUsername());
            throw new BadRequestException("Invalid username or password");
        }

        if (!BcryptUtil.matches(loginRequest.getPassword(), user.getPassword())) {
            Log.warnf("Failed login attempt with username %s: Invalid password", user.getUsername());
            throw new BadRequestException("Invalid username or password");
        }

        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setUsername(user.getUsername());
        loginResponse.setToken(generateToken(user));
        loginResponse.setExpiresIn(EXPIRATION_TIME_SECONDS);

        return loginResponse;
    }

    public String generateToken(User user) {
        long expirationTime = (System.currentTimeMillis() / 1000L) + EXPIRATION_TIME_SECONDS;

        Log.infof("Generating JWT token for user %s", user.getUsername());

        return Jwt.issuer("jenga")
                  .upn(user.getUsername())
                  .expiresAt(expirationTime)
                  .sign();
    }

    public User getCurrentUser() {
        String username = securityContext.getUserPrincipal().getName();

        User currentUser = userRepository.findByUsername(username);

        if (currentUser == null) {
            throw new AuthenticationFailedException("Failed to get user from security context: " + username);
        }

        return currentUser;
    }
}
