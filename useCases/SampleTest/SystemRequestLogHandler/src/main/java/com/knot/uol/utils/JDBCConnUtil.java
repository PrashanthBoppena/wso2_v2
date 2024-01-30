package com.knot.uol.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCConnUtil {
    private static Connection connection;

    private JDBCConnUtil() {
        // Private constructor to prevent instantiation
    }

    public static Connection getConnection(String driver, String url, String user, String password) {
        if (connection == null) {
            try {
                // Load the JDBC driver
                Class.forName(driver);

                // Create the connection
                connection = DriverManager.getConnection(url,user,password);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
    


}



