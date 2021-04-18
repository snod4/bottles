package com.example.bottles;

public class DbConnection {
    private static DbConnection connection = null;
    private DbConnection(){

    }

    public static DbConnection getDbConnection(){
        if(connection == null){
            connection = new DbConnection();
        }
        return connection;
    }
}
