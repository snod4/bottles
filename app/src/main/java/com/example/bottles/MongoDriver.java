package com.example.bottles.mongo;

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

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> places;

    public static void main(String[] args) {
        MongoClientURI uri = new MongoClientURI(
                "mongodb+srv://lidenlia:HKKActkI9JRxVkPO@bottle1.pcbvl.mongodb.net/bottle1?retryWrites=true&w=majority");

        mongoClient = new MongoClient(uri);
        database = mongoClient.getDatabase("sample_geospatial");
        places = database.getCollection("shipwrecks");

        Point testPoint = new Point(9, -80);
        System.out.println(getNear(testPoint, 200));
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getMongoDatabase() {
        return database;
    }

    public void addPoint(long latitutde, long longitude, String title, String body, String author) {
        places.insertOne(
                {
                        author: author,
                title: title,
                body: body,
                location: {
            type: "Point",
                    coordinates: [latitutde, longitude]
        }
            }
        );
    }

    public Point getNear(Point origin, int minDist, int maxDist) {
        places.find(
                {
                        location: {
            $nearSphere: {
                $geometry: {
                    type: "Point",
                            coordinates: [origin.latitutde, origin.longitude]
                },
                $minDistance: minDist,
                        $maxDistance: maxDist
            }
        }
            }
        );
    }

}
