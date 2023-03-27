package com.example.server.model;

import java.util.UUID;

public class Company {

    public class Stock {
        private int count;
        private double pricePerUnite;

        public Stock() {
            this.count = 0;
            this.pricePerUnite = 0;
        }

        public Stock(int count, double pricePerUnite) {
            this.count = count;
            this.pricePerUnite = pricePerUnite;
        }

        public double getPricePerUnite() {
            return pricePerUnite;
        }

        public int getCount() {
            return count;
        }

        public void updateCount(int value) {
            this.count += value;
        }

        public void updatePrice(double value) {
            this.pricePerUnite += value;
        }
    }

    private UUID id;
    private String name;
    private Stock stock;

    public Company(String name, Double price_per_unite) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.stock = new Stock(0, price_per_unite);
    }

    public UUID getId() {
        return id;
    }

    public Stock getStock() {
        return stock;
    }

    public String getName() {
        return name;
    }

}
