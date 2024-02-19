package com.uol.handler;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ConnectOverProxy {
    public static void main(String[] args) {
        new ConnectOverProxy();
    }

    public ConnectOverProxy() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = null;
            Properties info = new Properties();
            info.put("proxy_type", "4"); // SSL Tunneling
            info.put("proxy_host", "10.10.9.32");
            info.put("proxy_port", "8080");
            info.put("user", "sysadm");
            info.put("password", "Sysadm@2023#");
            conn = DriverManager.getConnection("jdbc:mysql://10.10.94.145:3306/useSSL=true",info);


            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("Select NOW()");
            rs.next();
            System.out.println("Data- " + rs.getString(1));
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException er) {
        	System.out.println("============er===="+er);
            er.printStackTrace();
        } catch (ClassNotFoundException e) {
        	System.out.println("==============e=="+e);
            e.printStackTrace();
        }

    }
}