package com.t4a.examples;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.function.Consumer;

public class MongoExample {

    public static void main(String[] args) {


        String connectionString = "mongodb://localhost:27017";

        // Create MongoDB client
        MongoClient mongoClient = MongoClients.create(connectionString);

        // Get reference to the database
        MongoDatabase database = mongoClient.getDatabase("testdb");

        // Get reference to the collection
        MongoCollection<Document> collection = database.getCollection("messages");

        // Create a lambda function to inject a message into MongoDB
        Consumer<String> injectMessage = message -> {
            // Create a document to represent the message
            Document document = new Document("message", message);
            // Insert the document into the collection
            collection.insertOne(document);
            System.out.println("Message injected into MongoDB: " + message);
        };

        // Call the lambda function to inject a message
        injectMessage.accept("Hello");

                // Close the MongoDB client
                        mongoClient.close();
    }
}