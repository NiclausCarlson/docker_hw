package com.example.server.db;

import com.example.server.logic.UpdatePrices;
import com.example.server.model.Company;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCompanies {
    private final Map<UUID, Company> companies;

    public InMemoryCompanies() {
        this.companies = new ConcurrentHashMap<>();
    }

    public UUID registerCompany(String name, Double price) {
        var company = new Company(name, price);
        companies.put(company.getId(), company);
        return company.getId();
    }

    public Company getCompany(UUID id) {
        if (UpdatePrices.check()) {
            updatePrices();
        }
        var company = companies.get(id);
        if (company == null) {
            throw new RuntimeException("Can't find company with this id");
        }
        return company;
    }

    public List<Company> getCompanies() {
        if (UpdatePrices.check()) {
            updatePrices();
        }
        return new ArrayList<>(companies.values());
    }

    public void changeStockCount(UUID companyId, int count) {
        var res = companies.computeIfPresent(companyId, (k, v) -> {
                    v.getStock().updateCount(count);
                    return v;
                }
        );
        if (res == null) {
            throw new RuntimeException("Can't find company with this id");
        }
    }

    private void updatePrices() {
        var rand = new Random();
        companies.forEach((k, v) -> {
            var min = -v.getStock().getPricePerUnite() / 2;
            var max = v.getStock().getPricePerUnite() / 2;
            double change = min + (max - min) * rand.nextDouble();
            if (change < 0) {
                change -= 2 * change;
            }
            v.getStock().updatePrice(change);
        });
    }
}
