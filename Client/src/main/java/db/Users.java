package db;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import model.User;
import org.bson.Document;
import org.bson.types.ObjectId;

public class Users {
    private final MongoClient client;
    private final String database = "users";
    private final String collection = "users";

    public Users() {
        this.client = MongoClients.create("mongodb://localhost:27017");
    }

    public ObjectId createUser(String name) {
        var user = new User(name);
        var insertResult = client.getDatabase(database)
                .getCollection(collection)
                .insertOne(user.asDocument());
        if (insertResult.wasAcknowledged()) {
            return user.getId();
        }
        throw new RuntimeException("Can't register user with this name");
    }

    public User getUser(ObjectId id) {
        var res = client.getDatabase(database)
                .getCollection(collection)
                .find(new BasicDBObject("_id", id)).first();
        if (res == null) {
            throw new RuntimeException("Can't find user with this id");
        }
        return new User(res);
    }

    public void addAmount(ObjectId id, double amount) {
        getUser(id);
        var update = new Document().append("$inc", amount);
        var res = client.getDatabase(database)
                .getCollection(collection)
                .updateOne(new BasicDBObject("_id", id), update);
        if (!res.wasAcknowledged()) {
            throw new RuntimeException("Can't update users amount");
        }
    }

    public void updateUser(User user){
        var id = new BasicDBObject("_id", user.getId());
        var res = client.getDatabase(database)
                .getCollection(collection)
                .updateOne(id, user.asDocument());
        if(!res.wasAcknowledged()){
            throw new RuntimeException("Can't update user data");
        }
    }
}
