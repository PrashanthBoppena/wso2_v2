package com.knot.uol.mediators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.google.gson.JsonArray;
import com.knot.uol.utils.CommonUtils;
import com.mysql.cj.jdbc.CallableStatement;

public class TestSql {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connection connectionObj = null;

		try {
			
			String dbConnectionUrl = "jdbc:mysql://172.16.110.240:3306/uol_api_registry?user=root&password=Adm!n@123";

			System.out.println("db url name::" + dbConnectionUrl);
			
			// connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			Class.forName("com.mysql.cj.jdbc.Driver");
			String query = "call uol_api_registry.SearchRecordsByTmfParams(?,?);";
			
			connectionObj = DriverManager.getConnection(dbConnectionUrl);
			String tmf = "account,contactMedium";
			PreparedStatement ps=connectionObj.prepareCall(query);
			ps.setString(1, "read_customer");
			ps.setString(2, tmf);
			
			boolean b=ps.execute();
			
			System.out.println("result============"+b);
			
	
			if (b) {
				ResultSet re=ps.getResultSet();
				JsonArray ar= CommonUtils.prepareResultSetJSONObject(re);

				
				System.out.println("re=======>"+ar);
				
			}
	}catch (Exception e) {
		// TODO: handle exception
		
		e.printStackTrace();
	}

}
}