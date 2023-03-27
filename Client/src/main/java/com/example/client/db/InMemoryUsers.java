package com.example.client.db;

import com.example.client.model.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUsers {
    private final Map<UUID, User> users;

    public InMemoryUsers() {
        this.users = new ConcurrentHashMap<>();
    }

    public UUID createUser(String name) {
        var user = new User(name);
        users.put(user.getId(), user);
        return user.getId();
    }

    public User getUser(UUID id) {
        var res = users.get(id);
        if (res == null) {
            throw new RuntimeException("Can't find user with this id");
        }
        return res;
    }

    public void addAmount(UUID id, double amount) {
        var res = users.computeIfPresent(id, (k, v) -> {
            v.addAmount(amount);
            return v;
        });
        if (res == null) {
            throw new RuntimeException("Can't update users amount");
        }
    }

    public void updateUser(User user) {
        var res = users.put(user.getId(), user);
        if (res == null) {
            throw new RuntimeException("Can't update user data");
        }
    }
}
