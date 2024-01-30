package com.knot.uol.mediators;

import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.synapse.MessageContext;

import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.knot.uol.dto.MediatorResponse;
import com.knot.uol.dto.QueryConfig;
import com.knot.uol.utils.CommonUtils;
import com.knot.uol.utils.JDBCConnectionUtil;
import com.knot.uol.utils.PropertiesUtil;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Arrays;

import java.util.List;

import java.util.Properties;


public class DynamicXMLQueryMediator extends AbstractMediator {

	
	
	private PreparedStatement preparedStatement=null;
	private Connection targetDBConnection=null;
	@Override
	public boolean mediate(MessageContext messageContext) {

		 String queryName = null, statusCode = null, message = null, errorName = null, errorCode = null;
		 JsonArray dbResponse = null;

		try {
			//org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();
			
			
			String apiRegistryConfigs = (String)messageContext.getProperty("apiregistryConfigPath");
			String inputPayload = (String) messageContext.getProperty("dynamicQueryMediatorRequestPayload");

			Properties properties = PropertiesUtil.propertiesFileRead(apiRegistryConfigs);

			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(inputPayload);
			queryName = jsonElement.getAsJsonObject().get("queryName").getAsString();
			
			// Fetch dynamic query configuration from the database based on query name
			QueryConfig queryConfig = JDBCConnectionUtil.getQueryConfigFromDatabase(queryName, properties);
			//log.info("QueryConfig Object info :: " + queryConfig);
			if (queryConfig == null || org.apache.commons.lang.StringUtils.isEmpty(queryConfig.getPropertiesFile())) {
				// Handle error: Query not found in the database
			//log.info("QueryConfig Object info is null /not existed in DB");

				// Error Response
				errorCode = "QUE_NM_001";
				errorName = "QueryName Configuration Not Found in DB.";
				statusCode = "400";
				message = "BAD REQUEST";
		        // Get the Axis2 response object
				
				
				
				
				
				
//	             org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();
//	             HttpResponse response = (HttpResponse) axis2MessageContext.getProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE);
//	             response.setStatusCode(HttpStatus.SC_BAD_REQUEST);

				return true;
			}

			// Load the properties file containing dynamic queries
			Properties readQueryProperties = PropertiesUtil.propertiesFileRead(queryConfig.getPropertiesFile());

			// Get the dynamic query from the properties file based on query name
			String dynamicQuery = readQueryProperties.getProperty(queryName);

			if (dynamicQuery == null) {
				// Handle error: Query not found in the properties file
				//log.info("Dynamic Query Object not found in properties file");

				// Error Response
				errorCode = "QUE_DNM_002";
				errorName = "Dynamic Query Object not found in properties file";
				statusCode = "400";
				message = "BAD REQUEST";

				return true;
			}

			if (queryConfig.getParameters() != null) {
				List<String> inputParams = Arrays.asList(queryConfig.getParameters().split(","));
				// Replace placeholders in the query with request values
				// Check if the parsed element is an object, jsonElement is client given values
				if (jsonElement.isJsonObject()) {

					if (!jsonElement.getAsJsonObject().get("parameters").equals(null)) {

						JsonObject jsonObject = jsonElement.getAsJsonObject().getAsJsonObject("parameters");
						for (String keys : inputParams) {
							// Access key-value pairs
							if (jsonObject.get(keys) != null) {
								dynamicQuery = dynamicQuery.replace("{" + keys + "}", jsonObject.get(keys).getAsString());
							}
						}
					}
				}
				else
				{
					//log.info("Invalid Input Payload Object");

					// Error Response
					errorCode = "INV_JSON_003";
					errorName = "Invalid json object format";
					statusCode = "400";
					message = "Invalid JSON: Not an object.";

					return true;
				}

				//log.info("Dynamic Query after the query prepared in format::" + dynamicQuery);
			}
			
			// Connect to the database based on system name and schema name
			String systemName = queryConfig.getSystemName();
			String schemaName = queryConfig.getSchemaName();

			// Execute the dynamic query using the database connection
		     targetDBConnection = JDBCConnectionUtil.connectToDatabase(systemName, schemaName, properties);
			//log.info("Target DB Query  Connection Object ::" + targetDBConnection);
			if (targetDBConnection != null) {
			//log.info("===>Dynmc query::" + dynamicQuery);
			messageContext.setProperty("dynamicQuery", dynamicQuery);
			preparedStatement = targetDBConnection.prepareStatement(dynamicQuery);
			boolean execute = preparedStatement.execute();
			JsonArray resultsetJSONArray;
			if (execute) {
				ResultSet resultSet = preparedStatement.getResultSet();
				resultsetJSONArray = CommonUtils.prepareResultSetJSONObject(resultSet);
				//log.info("Target DB Query resultsetJSONArray===>" + resultsetJSONArray);
				messageContext.setProperty("dbResponse", resultsetJSONArray);
				dbResponse = resultsetJSONArray;
				statusCode = "200";
				message = "Success";
				resultSet.close();
			} else {

				int effectedRows = preparedStatement.getUpdateCount();
				//log.info("Target DB Query info ::"+ effectedRows + " rows effected Successfully.");
				String singleElement = effectedRows + " rows effected ";
				statusCode = "200";
				message = singleElement +"Successfully.";

			}
			preparedStatement.close();
			targetDBConnection.close();
			} else 
			{
				// Handle error: Unable to connect to the database
				//log.info("Target DB system Connection object is null");
				// Error Response
				errorCode = "QUE_TRG_004";
				errorName = "Target DB system Connection object is null";
				statusCode = "500";
				message = "Query DB connection ERROR";	
			}
		} catch (Exception e) {
			
			//log.info("Exception occured during the db call->"+e);
			// Error Response
			errorCode = "500";
			errorName = "Exception occured during the db call";
			statusCode = "500";
			message = e.getMessage();

		} finally {
            MediatorResponse obj = CommonUtils.buildResponse(queryName, statusCode, message, dbResponse, errorName,errorCode);
			Gson gson = new Gson();
			String json = gson.toJson(obj);
			messageContext.setProperty("dynamicQueryMediatorResponsePayload", json);
		}
		return true;

	}
	


}
