package com.example.client.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User {
    private UUID id;
    private String name;
    private double amount;
    private Map<UUID, Integer> stocks;

    public User(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.amount = 0;
        this.stocks = new HashMap<>();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public void addAmount(double amount) {
        this.amount += amount;
    }

    public Map<UUID, Integer> getStocks() {
        return stocks;
    }

}
