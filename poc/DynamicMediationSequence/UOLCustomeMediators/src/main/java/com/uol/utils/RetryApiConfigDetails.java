package com.uol.utils;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.synapse.MessageContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.uol.constants.DBinfoConstants;
import com.uol.dto.ApiLogHandlerDto;
import com.uol.mediators.ApiResponseDTO;

public class RetryApiConfigDetails {

	private PreparedStatement preparedStatement=null;
	private Connection targetDBConnection=null;
	private List<ApiLogHandlerDto> apiInfoList=null;
	public List<ApiLogHandlerDto> getApiRequestLogDetails(String apiRegistryConfigs,String sourceRequestId ) throws SQLException
	{
		try {
		
		Properties properties = PropertiesUtil.propertiesFileRead(apiRegistryConfigs);
	    System.out.println(" getApiRequestLogDetails Properties status::=================="+properties);
	    targetDBConnection=JDBCConnectionUtil.connectToDatabase(DBinfoConstants.API_MASTER_SYSTEM_NAME, DBinfoConstants.API_MASTER_SCHEMA_NAME, properties);
		// Execute the dynamic query using the database connection
	    System.out.println(" getApiRequestLogDetails targetDBConnection status::=================="+targetDBConnection);
		if (targetDBConnection != null) {
		preparedStatement = targetDBConnection.prepareStatement(DBinfoConstants.REQUEST_RETRY_LOG_HANDLER_QUERY);
		preparedStatement.setString(1, sourceRequestId);
		boolean execute = preparedStatement.execute();
		System.out.println("getApiRequestLogDetails execution status::=================="+execute);
		JsonArray resultsetJSONArray=null;
		if (execute) {
			ResultSet resultSet = preparedStatement.getResultSet();
			System.out.println("getApiRequestLogDetails resultSet=================="+resultSet);
			resultsetJSONArray = CommonUtils.prepareResultSetJSONObject(resultSet);	
			//System.out.println("getApiRequestLogDetails resultsetJSONArray=================="+resultsetJSONArray);
			ObjectMapper mapper = new ObjectMapper();
			apiInfoList = mapper.readValue(resultsetJSONArray.toString(), new TypeReference<List<ApiLogHandlerDto>>() {});
			resultSet.close();
		} 
		
		} 
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("getApiRequestLogDetails exception=================="+e);
			e.printStackTrace();
		}finally
		{
			preparedStatement.close();
			targetDBConnection.close();
		}
		return apiInfoList;
	}
}
