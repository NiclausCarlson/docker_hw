package com.example.client.controller;

import com.example.client.db.InMemoryUsers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@RestController
public class Controllers {
    final InMemoryUsers users;

    public Controllers() {
        this.users = new InMemoryUsers();
    }

    @RequestMapping(value = "/users/add-user", method = RequestMethod.GET)
    // Создаём пользователя и возвращаем его Id
    public ResponseEntity<String> addUser(@RequestParam(name = "userName") String userName) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(users.createUser(userName).toString());
    }

    @RequestMapping(value = "/users/add-amount", method = RequestMethod.GET)
    // Добавляем пользователю userId amount денег
    public ResponseEntity<String> addAmount(@RequestParam(name = "userId") String userId, @RequestParam(name = "amount") Double amount) {
        try {
            users.addAmount(UUID.fromString(userId), amount);
        } catch (final RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body("Success");
    }

    @RequestMapping(value = "/users/check-stocks", method = RequestMethod.GET)
    // Смотрим акции пользователя
    public ResponseEntity<String> checkStocks(@RequestParam(name = "userId") String userId) {
        try {
            var user = users.getUser(UUID.fromString(userId));
            StringBuilder result = new StringBuilder();
            for (var stock : user.getStocks().entrySet()) {
                result.append("CompanyId: ")
                        .append(stock.getKey())
                        .append(" count: ")
                        .append(stock.getValue());
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(result.toString());
        } catch (final RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @RequestMapping(value = "/users/how-much-money", method = RequestMethod.GET)
    public ResponseEntity<String> howMuchMoney(@RequestParam(name = "userId") String userId) {
        try {
            var user = users.getUser(UUID.fromString(userId));
            Double result = user.getAmount();
            for (var stock : user.getStocks().entrySet()) {
                result += stock.getValue();
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(result.toString());
        } catch (final RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @RequestMapping(value = "/users/buy-stocks", method = RequestMethod.GET)
    // Пользователь userId покупает акции companyId на весь amount
    // Предполагается примерно следующий сценарий работы: из приложения идёт запрос в биржу за ценами акций
    // на основании этой информации пользователь решает сколько он будет покупать.
    // Далее происходит запрос в биржу с числом акций и ценой, за которую пользователь согласен произвести покупку
    // Если не получилось(на бирже нет столько акций или цена изменилась), то цикл повторяется
    public ResponseEntity<String> buyStocks(@RequestParam(name = "userId") String userId,
                                            @RequestParam(name = "companyId") String companyId,
                                            @RequestParam(name = "count") Integer count,
                                            @RequestParam(name = "price_per_unite") Double price_per_unite) {
        try {
            var userObjectId = UUID.fromString(userId);
            var user = users.getUser(userObjectId);
            if(user.getAmount() < count * price_per_unite){
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("You don't have enough amount");
            }
            String req = "http://localhost:8080/burse/buy?" + "companyId=" + companyId + "&" +
                    "price_per_unite=" + price_per_unite + "&" +
                    "count=" + count;

            HttpRequest request = HttpRequest.newBuilder().uri(new URI(req)).GET().build();

            var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpStatus.OK.value()) {
                var stocks = user.getStocks();
                var cId = UUID.fromString(companyId);
                stocks.put(cId, stocks.getOrDefault(cId, 0) + count);
                users.updateUser(user);
            }
            return ResponseEntity.status(HttpStatus.valueOf(response.statusCode()))
                    .body(response.body());
        } catch (final URISyntaxException | IOException | InterruptedException | RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @RequestMapping(value = "/users/sell-stocks", method = RequestMethod.GET)
    // Пользователь userId продаёт count акций компании companyId
    // Пользователь пытается продать count акций за price_per_unite.
    // Если цена изменилась, то продать не получится(например, если цена уменьшилась, то
    // пользователь будет недоволен).
    public ResponseEntity<String> sellStocks(@RequestParam(name = "userId") String userId,
                                             @RequestParam(name = "companyId") String companyId,
                                             @RequestParam(name = "count") Integer count,
                                             @RequestParam(name = "price_per_unite") Double price_per_unite) {
        if (count <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid count");
        }
        try {
            var userObjectId = UUID.fromString(userId);
            var companyObjectId = UUID.fromString(companyId);
            var user = users.getUser(userObjectId);
            var stocks = user.getStocks();
            var stocksCount = stocks.getOrDefault(companyObjectId, 0);
            if (stocksCount < count) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("You don't have enough stocks");
            }
            stocksCount -= count;
            String req = "http://localhost:8080/burse/sell?" + "companyId=" + companyId + "&" +
                    "price_per_unite=" + price_per_unite + "&" +
                    "count=" + count;

            HttpRequest request = HttpRequest.newBuilder().uri(new URI(req)).GET().build();

            var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpStatus.OK.value()) {
                if (stocksCount == 0) {
                    stocks.remove(companyObjectId);
                } else {
                    stocks.put(companyObjectId, stocksCount);
                }
                user.addAmount(count * price_per_unite);
                users.updateUser(user);
            }
            return ResponseEntity.status(HttpStatus.valueOf(response.statusCode()))
                    .body(response.body());
        } catch (final URISyntaxException | IOException | InterruptedException | RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

}
