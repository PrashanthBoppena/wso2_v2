package com.uol.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.synapse.MessageContext;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.uol.dto.ErrorResponse;
import com.uol.dto.MediatorResponse;

public class CommonUtils {
	
	
	public static JsonArray prepareResultSetJSONObject(ResultSet resultSet)
	{
		JsonArray jsonArray = new JsonArray();
		try {
		// Create a JSON array to hold the result rows
        // Get metadata about the ResultSet to obtain column names
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
		

        while (resultSet.next()) {
            JsonObject jsonObject = new JsonObject();

            // Iterate through columns and add them to the JSON object
            for (int i = 1; i <= columnCount; i++) {
            	
                String columnName = metaData.getColumnName(i);
                Object columnValue = resultSet.getObject(i);
                //System.out.println(columnName+ " Resultset building  "+columnValue );
                if (columnValue == null) {
                    jsonObject.addProperty(columnName, "");
                } else {
                    jsonObject.addProperty(columnName, columnValue.toString());
                }
//                Optional<Object> optionalColumnValue = Optional.ofNullable(resultSet.getObject(i));
//                optionalColumnValue.ifPresent(columnValue2 ->
//                        jsonObject.addProperty(columnName, columnValue2.toString()));
            
            }

            // Add the JSON object to the array
            jsonArray.add(jsonObject);
        }
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonArray;

	}
	
	public static MediatorResponse buildResponse(String queyName, String statusCode, String message, JsonArray response, String errorName, String errorCode) { 
		MediatorResponse mediatorRes = new MediatorResponse();
		mediatorRes.setQueryName(queyName);
		mediatorRes.setStatusCode(statusCode);
		mediatorRes.setMessage(message);
		mediatorRes.setResponse(response);
		
		
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorName(errorName);
		errorResponse.setErrorCode(errorCode);
		
		mediatorRes.setErrorResponse(errorResponse);
		

		
		
		
		
		
		return mediatorRes;
		
	}
	
	public static boolean setSynapseHttpResponseforRequest(MessageContext synCtx)
    {
        // Your condition logic here
        if (true) {
            try {
                org.apache.axis2.context.MessageContext axis2MessageContext = ((org.apache.synapse.core.axis2.Axis2MessageContext) synCtx).getAxis2MessageContext();

                // Set HTTP Bad Request response
               //axis2MessageContext.setProperty(HTTPConstants.HTTP_STATUS_CODE, 400); // HTTP Status Code: 400 Bad Request
                //axis2MessageContext.setProperty(HTTPConstants.NO_ENTITY_BODY, Boolean.FALSE); // Set to include a response body

                // Set your custom response message
                String customMessage = "This is a custom Bad Request message.";
                axis2MessageContext.setProperty("ERROR_MESSAGE", customMessage);

 

                // Terminate processing
                synCtx.setProperty("RESPONSE", "true");
                synCtx.setTo(null);
            }catch (Exception e) {
                // TODO: handle exception
            }
//            } catch (AxisFault axisFault) {
//                // Handle any exceptions that may occur
//                // Log or perform error handling as needed
//            }

 

            return false; // Terminate processing
        }
		return false;
        
    }

}
