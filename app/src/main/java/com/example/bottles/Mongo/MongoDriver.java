package com.example.bottles;

import com.mongodb.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;


public class MongoDriver {
    
    private MongoClient mongoClient;
    private MongoDatabase database;

    public static void main(String[] args) {
	    MongoClientURI uri = new MongoClientURI(
        "mongodb+srv://lidenlia:HKKActkI9JRxVkPO@bottle1.pcbvl.mongodb.net/bottle1?retryWrites=true&w=majority");

        mongoClient = new MongoClient(uri);
        database = mongoClient.getDatabase("sample_geospatial");
    }

    public MongoClient getMongoClient {
        return mongoClient;
    }

    public MongoDatabase getMongoDatabase {
        return database;
    }

}
