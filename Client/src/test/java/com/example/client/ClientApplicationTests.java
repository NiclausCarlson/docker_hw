package com.example.client;

import com.example.client.controller.Controllers;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.junit.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ClientApplicationTests {
    @Autowired
    private Controllers controllers;

    @Before
    public void init() {
        controllers = new Controllers();
    }

    @ClassRule
    public static GenericContainer simpleWebServer
            = new FixedHostPortGenericContainer("server:0.0.1-SNAPSHOT")
            .withFixedExposedPort(8080, 8080)
            .withExposedPorts(8080);

    public String GetUserWithMoney(double amount) {
        var user_id = controllers.addUser("user").getBody();
        controllers.addAmount(user_id, amount);
        return user_id;
    }

    public void addStocks(String companyId, Integer stocks) throws Exception {
        String req = "http://localhost:8080/burse/add-stocks?" +
                "companyId=" + companyId + "&count=" + stocks.toString();
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(new URI(req))
                .GET()
                .build();
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    public String createCompany(int stocks) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(new URI("http://localhost:8080/burse/add-company?companyName=CompanyName&stockPrice=30"))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        addStocks(response.body(), stocks);
        return response.body();
    }

    public String getStocksInfo() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(new URI("http://localhost:8080/burse/get-stocks-info"))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @Test
    public void testAddAmount() throws Exception {
        simpleWebServer.start();
        var userId = GetUserWithMoney(1000);
        Assert.assertEquals(String.valueOf(1000.), controllers.howMuchMoney(userId).getBody());
        controllers.addAmount(userId, 123.);
        Assert.assertEquals(String.valueOf(1123.), controllers.howMuchMoney(userId).getBody());
    }

    @Test
    public void testBuyStocks() throws Exception {
        simpleWebServer.start();
        var userId = GetUserWithMoney(1000);
        var companyId = createCompany(20);
        Assert.assertEquals(String.valueOf(1000.), controllers.howMuchMoney(userId).getBody());

        var res = controllers.buyStocks(userId, companyId, 5, 30.).getBody();
        Assert.assertEquals(res, "Success");

        var stocks = controllers.checkStocks(userId).getBody();
        Assert.assertTrue(stocks.contains("count: 5"));
    }

    @Test
    public void testSellStocks() throws Exception {
        simpleWebServer.start();
        var userId = GetUserWithMoney(1000);
        var companyId = createCompany(20);
        Assert.assertEquals(String.valueOf(1000.), controllers.howMuchMoney(userId).getBody());

        var res = controllers.buyStocks(userId, companyId, 5, 30.).getBody();
        Assert.assertEquals(res, "Success");

        var stocks = controllers.checkStocks(userId).getBody();
        Assert.assertTrue(stocks.contains("count: 5"));
        Assert.assertTrue(getStocksInfo().contains("\"count\":15"));

        res = controllers.sellStocks(userId, companyId, 4, 30.).getBody();
        Assert.assertEquals(res, "Success");

        stocks = controllers.checkStocks(userId).getBody();
        Assert.assertTrue(stocks.contains("count: 1"));
        Assert.assertTrue(getStocksInfo().contains("\"count\":19"));
    }

    @Test
    public void testBuyStocksFailure() throws Exception{
        simpleWebServer.start();
        var userId = GetUserWithMoney(10);
        var companyId = createCompany(20);

        var res = controllers.buyStocks(userId, companyId, 5, 30.).getBody();
        Assert.assertEquals(res, "You don't have enough amount");

        var stocks = controllers.checkStocks(userId).getBody();
        Assert.assertTrue(stocks.isEmpty());
    }

    @Test
    public void testSellStocksFailure() throws Exception{
        simpleWebServer.start();
        var userId = GetUserWithMoney(10);
        var companyId = createCompany(20);

        var res = controllers.sellStocks(userId, companyId, 4, 30.).getBody();
        Assert.assertEquals(res, "You don't have enough stocks");
    }
}
