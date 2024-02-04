package application.toysocialnetwork.utils;

import application.toysocialnetwork.exceptions.DBConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    private final String url;
    private final String username;
    private final String password;

//    private final static DataBase instance = null;

    public DataBase(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection() throws DBConnectionException {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException sqlException) {
            throw new DBConnectionException(sqlException.getMessage());
        }
    }
}