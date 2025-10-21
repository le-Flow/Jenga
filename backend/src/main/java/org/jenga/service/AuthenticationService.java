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
import io.smallrye.jwt.build.Jwt;

@ApplicationScoped
public class AuthenticationService {

    private static final int EXPIRATION_TIME_SECONDS = 3600;

    @Inject
    UserRepository userRepository;

    @Transactional
    public LoginResponseDTO register(RegisterRequestDTO registerRequest) {
        User existingUser = userRepository.findByUsername(registerRequest.getUsername());

        if (existingUser != null) {
            throw new RuntimeException("User already exists");
        }

        String username = registerRequest.getUsername();
        String email = registerRequest.getEmail();
        String hashedPassword = BcryptUtil.bcryptHash(registerRequest.getPassword());
        User user = new User(username, email, hashedPassword, null, null);
        userRepository.persist(user);

        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setUsername(username);
        loginResponse.setToken(generateToken(user));
        loginResponse.setExpiresIn(EXPIRATION_TIME_SECONDS);

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
        loginResponse.setToken(generateToken(user));
        loginResponse.setExpiresIn(EXPIRATION_TIME_SECONDS);

        return loginResponse;
    }

    public String generateToken(User user) {
        long expirationTime = (System.currentTimeMillis() + EXPIRATION_TIME_SECONDS * 1000L) / 1000L;
        expirationTime = (System.currentTimeMillis() / 1000L) + EXPIRATION_TIME_SECONDS;

        return Jwt.issuer("jenga")
                  .upn(user.getUsername())
                  .expiresAt(expirationTime)
                  .sign();
    }
}
