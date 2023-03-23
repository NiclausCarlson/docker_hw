package db;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import model.Company;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Random;

public class Companies {
    private final MongoClient client;
    private final String database = "companies";
    private final String collection = "companies";

    public Companies() {
        this.client = MongoClients.create("mongodb://localhost:27017");
    }

    public ObjectId registerCompany(String name) {
        var company = new Company(name);
        var insertResult = client.getDatabase(database)
                .getCollection(collection)
                .insertOne(company.asDocument());
        if (insertResult.wasAcknowledged()) {
            return company.getId();
        }
        throw new RuntimeException("Can't register company with this name");
    }

    public Company getCompany(ObjectId id) {
        var res = client.getDatabase(database)
                .getCollection(collection)
                .find(new BasicDBObject("_id", id)).first();
        if (res == null) {
            throw new RuntimeException("Can't find company with this id");
        }
        return new Company(res);
    }

    public void addActions(ObjectId companyId, int count) {
        getCompany(companyId);
        var update = new Document().append("$inc", count);
        var res = client.getDatabase(database)
                .getCollection(collection)
                .updateOne(new BasicDBObject("_id", companyId), update);
        if (!res.wasAcknowledged()) {
            throw new RuntimeException("Can't update stock count");
        }
    }

    public void updatePrices() {
        var rand = new Random();
        var coll = client.getDatabase(database).getCollection(collection);
        var iter = coll.find();
        for (var doc : iter) {
            var company = new Company(doc);
            var min = -company.getStock().getPricePerUnite() / 2;
            var max = company.getStock().getPricePerUnite() / 2;
            double change = rand.nextDouble((max - min) + 1) + min;
            var update = new Document().append("$inc", change);
            coll.updateOne(
                    new Document("_id", company.getId()),
                    update
            );
        }
    }
}
