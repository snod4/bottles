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
import java.io.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import com.mongodb.client.model.geojson.*;


public class MongoDriver {

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> places;

    public static void main(String[] args) {
        String uri = "mongodb+srv://lidenlia:HKKActkI9JRxVkPO@bottle1.pcbvl.mongodb.net/bottle1?retryWrites=true&w=majority";

        mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase("sample_geospatial");
        places = database.getCollection("shipwrecks");

        Point testPoint = new Point( new Position(9, -80));

    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getMongoDatabase() {
        return database;
    }

    public void getNear(long latitude, long longitude, double minDist, double maxDist) {
        Point origin = new Point (new Position(latitude, longitude));
        places.find(Filters.near("location", origin, maxDist, minDist))
                .forEach(doc -> System.out.println(doc.toJson()));
    }

}
