package org.jenga.db;

import java.util.List;

import org.jenga.model.User;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    public User findByUsername(String username) {
        return find("username", username).firstResult();
    }

    public List<User> searchByUsernameStartsWith(String usernamePart) {
        return list("username LIKE ?1", usernamePart + "%");
    }
}
