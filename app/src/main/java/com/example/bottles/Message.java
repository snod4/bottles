package com.example.bottles;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;
import org.bson.types.ObjectId;

public class Message extends RealmObject {
    @PrimaryKey
    private ObjectId _id = new ObjectId();
    private String title;
    private String author;
    private String body;
    private long lat;
    private long lon;

    public Message(String title, String author, String body, long lat, long lon) {
        this.title = title;
        this.author = author;
        this.body = body;
        this.lat = lat;
        this.lon = lon;
    }
}
