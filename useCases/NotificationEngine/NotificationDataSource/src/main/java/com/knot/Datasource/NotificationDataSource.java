package com.knot.Datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import com.github.fge.jsonschema.main.cli.Main;
import com.google.gson.JsonObject;

public class NotificationDataSource extends AbstractMediator { 
	private String templateId;
	
	
	public String getTemplateId() {
		return templateId;
	}


	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}


	public boolean mediate(MessageContext context) { 
		
		
		try {
			Context ctx  = new InitialContext();
	            // Lookup the DataSource
	            DataSource dataSource = (DataSource) ctx.lookup("MySQLConnectionSMS");
	           Connection connection = dataSource.getConnection();
	           //log.info(connection);
	           // Your database operations go here
	            String sqlQuery = "SELECT * FROM notificationtemplates where template_id = ? and is_enabled = true order by template_id";
	            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
	            System.out.println("templateId is "+ templateId);
	            preparedStatement.setString(1, templateId);
	            ResultSet resultSet = preparedStatement.executeQuery();
	            JsonObject jsonObject =  new JsonObject();
	            while (resultSet.next()) {
	                // Process the results
	                //System.out.println(resultSet.getString("template_name"));
	                
	                jsonObject.addProperty("templateId", resultSet.getString("template_id"));
	                jsonObject.addProperty("templateName", resultSet.getString("template_name"));
	                jsonObject.addProperty("languageId", resultSet.getString("language_id"));
	                jsonObject.addProperty("isDynamic", resultSet.getString("is_dynamic"));
	                jsonObject.addProperty("templateContent", resultSet.getString("template_content"));
	                jsonObject.addProperty("parameters", resultSet.getString("parameters"));
	                
	            } 
	            context.setProperty("dbResponse", jsonObject);
//	            System.out.println(jsonObject);
//	            if(jsonObject.size()==0) {
//	            	
//	            	jsonObject.addProperty("ERROR_MESSAGE","No data from Database/Invalid input");
//	            	context.setProperty("dbResponse", jsonObject);
//	            }else {
//	            	context.setProperty("dbResponse", jsonObject);
//	            	
//	            }
	            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			context.setProperty("ERROR_MESSAGE", e);
			System.out.println("DB ERROR template read "+e);
		}
 
		return true;
	}


}


