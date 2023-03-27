package com.example.server.controller;

import com.example.server.db.InMemoryCompanies;
import com.example.server.model.Company;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class Controllers {
    final InMemoryCompanies companies;

    public Controllers() {
        this.companies = new InMemoryCompanies();
    }

    @RequestMapping(value = "/burse/add-company", method = RequestMethod.GET)
    @ResponseBody
    // Регистрируем компанию с названием companyName и объявляем торги, с ценой акции в price
    public ResponseEntity<String> addCompany(@RequestParam(name = "companyName") String companyName,
                                             @RequestParam(name = "stockPrice") Double price) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(companies.registerCompany(companyName, price).toString());
    }

    @RequestMapping(value = "/burse/add-stocks", method = RequestMethod.GET)
    @ResponseBody
    // Добавляем компании count акций
    public ResponseEntity<String> addStocks(@RequestParam(name = "companyId") String companyId,
                                            @RequestParam(name = "count") Integer count) {
        if (count <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid count");
        }
        try {
            companies.changeStockCount(UUID.fromString(companyId), count);
        } catch (final RuntimeException err) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(err.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @RequestMapping(value = "/burse/get-stocks-info", method = RequestMethod.GET)
    @ResponseBody
    // Возвращаем список компаний
    public ResponseEntity<List<Company>> getStocksInfo() {
        var res = companies.getCompanies();
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @RequestMapping(value = "/burse/buy", method = RequestMethod.GET)
    @ResponseBody
    // Покупаем count акций по price_per_unite за штуку
    public ResponseEntity<String> buy(@RequestParam(name = "companyId") String companyId,
                                      @RequestParam(name = "price_per_unite") Double price_per_unite,
                                      @RequestParam(name = "count") Integer count) {
        if (count <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid count");
        }
        try {
            var id = UUID.fromString(companyId);
            var stocks = companies.getCompany(id).getStock();
            if (stocks.getPricePerUnite() != price_per_unite || stocks.getCount() < count) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Price or free stocks count were changed");
            }
            companies.changeStockCount(id, -count);
        } catch (final RuntimeException err) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(err.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @RequestMapping(value = "/burse/sell", method = RequestMethod.GET)
    @ResponseBody
    // Продаём count акций компании companyId за price_per_unite за штуку
    public ResponseEntity<String> sell(@RequestParam(name = "companyId") String companyId,
                                       @RequestParam(name = "price_per_unite") Double price_per_unite,
                                       @RequestParam(name = "count") Integer count){
        if (count <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid count");
        }
        try {
            var id =  UUID.fromString(companyId);
            var stocks = companies.getCompany(id).getStock();
            if (stocks.getPricePerUnite() != price_per_unite) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Price or free stocks count were changed");
            }
            companies.changeStockCount(id, count);
        } catch (final RuntimeException err) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(err.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }
}
