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
    private String location;

    public Message(String title, String author, String body, String location) {
        this.title = title;
        this.author = author;
        this.body = body;
        this.location = location;
    }
}
