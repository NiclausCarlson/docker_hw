package model;

import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.print.Doc;
import java.util.HashMap;
import java.util.Map;

public class User {
    private ObjectId id;
    private String name;
    private double amount;
    private Map<ObjectId, Integer> stocks;

    public User(String name) {
        this.id = new ObjectId();
        this.name = name;
        this.amount = 0;
        this.stocks = new HashMap<>();
    }

    public User(Document doc) {
        this.id = doc.getObjectId("_id");
        this.name = doc.getString("name");
        this.amount = doc.getDouble("amount");
        this.stocks = (Map<ObjectId, Integer>) doc.get("stocks");
    }

    public ObjectId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }
    public void addAmount(double amount){
        this.amount += amount;
    }
    public Map<ObjectId, Integer> getStocks() {
        return stocks;
    }

    public Document asDocument() {
        return new Document("_id", this.id)
                .append("name", this.name)
                .append("amount", this.amount)
                .append("stocks", new BasicDBObject(this.stocks));
    }
}
