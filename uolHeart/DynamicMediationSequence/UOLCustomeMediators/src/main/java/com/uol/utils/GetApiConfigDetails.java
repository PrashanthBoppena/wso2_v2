package com.uol.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.uol.constants.DBinfoConstants;
import com.uol.dto.ApiResponseDTO;

public class GetApiConfigDetails {

    private PreparedStatement preparedStatement = null;
    private Connection targetDBConnection = null;
    private List<ApiResponseDTO> apiInfoList = null;

    public List<ApiResponseDTO> getMasterApiConfigDetails(String apiRegistryConfigs, String subApiId, String queryType, String tmfParams) throws SQLException {
        try {
            Properties properties = PropertiesUtil.propertiesFileRead(apiRegistryConfigs);
            System.out.println("Properties status::==================" + properties);

            targetDBConnection = JDBCConnectionUtil.connectToDatabase(DBinfoConstants.API_MASTER_SYSTEM_NAME,
                    DBinfoConstants.API_MASTER_SCHEMA_NAME, properties);

            // Execute the dynamic query using the database connection
            System.out.println("targetDBConnection status::==================" + targetDBConnection);

            if (targetDBConnection != null) {
            	
            	if(queryType.isEmpty() || queryType == null) {
                    preparedStatement = targetDBConnection.prepareStatement(DBinfoConstants.API_MASTER_QUERY);
                    preparedStatement.setString(1, subApiId);            		
            	}else {
            		System.out.println("Executing Procedure");
                    preparedStatement = targetDBConnection.prepareCall(DBinfoConstants.TMF_PROCEDURE_CALL);
                    preparedStatement.setString(1, subApiId);   
                    preparedStatement.setString(2, tmfParams); 
            	}

                boolean execute = preparedStatement.execute();
                System.out.println("execution status::==================" + execute);

                JsonArray resultsetJSONArray = null;

                if (execute) {
                    ResultSet resultSet = preparedStatement.getResultSet();
                    System.out.println("resultSet==================" + resultSet);

                    resultsetJSONArray = CommonUtils.prepareResultSetJSONObject(resultSet);

                    ObjectMapper mapper = new ObjectMapper();
                    apiInfoList = mapper.readValue(resultsetJSONArray.toString(),
                            new TypeReference<List<ApiResponseDTO>>() {});
                    resultSet.close();
                }
            }
        } catch (Exception e) {
            // Handle exception appropriately
            System.out.println("exception==================" + e);
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return apiInfoList;
    }

    private void closeResources() {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (targetDBConnection != null) {
                targetDBConnection.close();
            }
        } catch (SQLException e) {
            // Handle exception appropriately or log it
            e.printStackTrace();
        }
    }
}
