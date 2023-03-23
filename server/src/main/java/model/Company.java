package model;


import org.bson.types.ObjectId;
import org.bson.Document;

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
    }

    private ObjectId id;
    private String name;
    private Stock stock;

    public Company(String name) {
        this.id = new ObjectId();
        this.name = name;
        this.stock = new Stock();
    }

    public Company(Document document) {
        this.id = document.getObjectId("_id");
        this.name = document.getString("name");
        this.stock = new Stock(document.getInteger("count"), document.getDouble("pricePerUnite"));
    }

    public ObjectId getId() {
        return id;
    }

    public Stock getStock() {
        return stock;
    }

    public String getName() {
        return name;
    }

    public Document asDocument() {
        return new Document("_id", this.id)
                .append("name", this.name)
                .append("count", this.stock.getCount())
                .append("pricePerUnite", this.stock.getPricePerUnite());
    }
}
