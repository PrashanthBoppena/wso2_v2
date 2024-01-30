package com.uol.mediators;

import org.apache.commons.collections.map.HashedMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.uol.constants.DBinfoConstants;
import com.uol.utils.GetApiConfigDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
public class DynamicRestSeqMediator extends AbstractMediator { 


	private String subApiId;

	private String sourceRequestID;

	public String getSourceRequestID() {
		return sourceRequestID;
	}

	public void setSourceRequestID(String sourceRequestID) {
		this.sourceRequestID = sourceRequestID;
	}

	public String getSubApiId() {
		return subApiId;
	}

	public void setSubApiId(String subApiId) {
		this.subApiId = subApiId;
	}
	JSONObject aggregateResponse=null;
	public boolean mediate(MessageContext messageContext) { 
		// TODO Implement your mediation logic here 
		try {
			//System.out.println("source req id is====================================>"+sourceRequestID);
			

			aggregateResponse=new JSONObject();
			String apiRegistryConfigs = (String)messageContext.getProperty(DBinfoConstants.ROOT_APIRIGISTRY_PROPS);
			String compositeEndpoint = (String)messageContext.getProperty("compositeEndpoint");

			String jsonPayloadToString = JsonUtil.jsonPayloadToString(((Axis2MessageContext) messageContext).getAxis2MessageContext());

			//log.info("jsonPayloadToString JSON body:\n" + jsonPayloadToString);

			ObjectMapper mapper=new ObjectMapper();

			//List<ApiResponseDTO> apiList = mapper.readValue(jsonPayloadToString, new TypeReference<List<ApiResponseDTO>>() {});

			// Retrieve the sequence
			//log.info("apiRegistryConfigs======>" + apiRegistryConfigs);
			List<ApiResponseDTO> apiList=new GetApiConfigDetails().getMasterApiConfigDetails(apiRegistryConfigs, subApiId);
			//log.info("Transformed JSON array apiList:\n" + apiList);

			Map<String,String> clientResponse =null;
			if(apiList!=null)
			{
				for (int i = 0; i < apiList.size(); i++) {
					ApiResponseDTO apidto=apiList.get(i);
					apidto.setSource_req_id(sourceRequestID);
					apidto.setChild_req_id(UUID.randomUUID().toString());
					apidto.setLogStatus(1);
					String apiInfoObj = mapper.writeValueAsString(apidto);

					//log.info("===================>endpoint"+DBinfoConstants.COMPOSITE_HOST_ADDRESS+compositeEndpoint);
					clientResponse = makePostRequest(sourceRequestID,DBinfoConstants.COMPOSITE_HOST_ADDRESS+compositeEndpoint, apiInfoObj,jsonPayloadToString,aggregateResponse.toString());


					JSONObject jsonResponse = new JSONObject(clientResponse.get("response"));
					//log.info("response output is:===========================jsonResponse======:"+jsonResponse);
					aggregateResponse.put(apidto.getProcess_name(), jsonResponse);

					if((Integer.parseInt(clientResponse.get("status")))>200 && apidto.getResponseStatus())
					{
						//log.info("Http called failed::"+clientResponse.get("status"));
						JSONObject jsonObj=new JSONObject();
						jsonObj.put("status", "Request API CALL FAILED");
						jsonObj.put("statusCode", clientResponse.get("status"));
						jsonObj.put("message","request failed");
						aggregateResponse.put("Fault", jsonObj);
						messageContext.setProperty("statusCode",500);
						break;
					}
					messageContext.setProperty("statusCode",200);
					//requestPayload=constructRequestPayload(response);
					// Process the response as needed
					//log.info("Response from API at " + endpoint + ":\n" + clientResponse);
				}


			}else
			{
				//log.info("Response from master api configs at ::"+apiList);
				JSONObject jsonObj=new JSONObject();
				jsonObj.put("status", "BAD REQUEST");
				jsonObj.put("statusCode", "400");
				jsonObj.put("message","Empty api config list:"+apiList);
				aggregateResponse.put("Fault", jsonObj);
				messageContext.setProperty("statusCode",400);
			}
		} catch (Exception e) {
			//log.info("==================Exception e::-" + e);
			JSONObject jsonObj=new JSONObject();
			jsonObj.put("status", "Internal Server Error:Functional");
			jsonObj.put("statusCode", "500");
			jsonObj.put("message",e);
			aggregateResponse.put("Fault", jsonObj);
			e.printStackTrace();
			messageContext.setProperty("statusCode",500);
		}
		finally {
			//Gson gson = new Gson();
			//	String json = gson.toJson(aggregateResponse);

			messageContext.setProperty("xmlResponse",convertResponse(aggregateResponse));
			//messageContext.setProperty("responsePayload", json);
		}

		//log.info("map output is:=================================:"+messageContext.getProperty("responsePayload"));
		return true;
	}

	private Map<String,String> makePostRequest(String sourceRequestID,String endpoint, String apiConfig,String inputNBPayload,String params) throws Exception {

		Map<String,String> clientResObj=new HashedMap();
		//log.info("=======request object inputNBPayload::"+inputNBPayload);
		//log.info("=======request object apiConfig::"+apiConfig);
		//log.info("=======request object params::"+params);

		Map<String,String> mapObj=new HashedMap();
		mapObj.put("ApiConfig", apiConfig);
		mapObj.put("NBPayload", inputNBPayload);
		//mapObj.put("source_req_id", sourceRequestID);
		if(params!=null)
		{
			mapObj.put("Params", params);
		}


		String responseObj=null;
		try
		{
			String xmlObj=convert(mapObj);
			//System.out.println("xmlOb is ------------------------->"+xmlObj);
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(endpoint);
			post.setEntity(new StringEntity(xmlObj, ContentType.APPLICATION_XML));


			//post.setEntity(new UR);
			HttpResponse response = client.execute(post);
			clientResObj.put("status", Integer.toString(response.getStatusLine().getStatusCode()));
			if(response.getStatusLine().getStatusCode()==404)
			{
				JSONObject jsonObj=new JSONObject();
				jsonObj.put("status", "NOT FOUND");
				jsonObj.put("statusCode", "404");
				jsonObj.put("message", "UNKNOWN ENDPOINT");
				jsonObj.put("endpoint", endpoint);
				responseObj=jsonObj.toString();

			}else {

				HttpEntity entity = response.getEntity();
				if (entity != null) {
					responseObj = EntityUtils.toString(entity, "UTF-8");

					if(response.getStatusLine().getStatusCode()!=200)
					{
						JSONObject	jsonObj=new JSONObject();
						jsonObj.put("status", "Target System Error");
						jsonObj.put("statusCode", response.getStatusLine().getStatusCode());
						jsonObj.put("message", responseObj);
						jsonObj.put("endpoint", endpoint);
						responseObj=jsonObj.toString();


					}
					//  System.out.println( response.getStatusLine().getStatusCode()+"::json respons eobjevt ius========>"+responseObj);
					// do something with the JSON object
				}else
				{
					JSONObject	jsonObj=new JSONObject();
					jsonObj.put("status", "Response NULL");
					jsonObj.put("statusCode", response.getStatusLine().getStatusCode());
					jsonObj.put("message", "Didn't get any response");
					jsonObj.put("endpoint", endpoint);
					responseObj=jsonObj.toString();

				}
			}
		}catch (Exception e) {
			// TODO: handle exception

			//log.info("when dynamicSBseq api call got error:"+e);
			JSONObject jsonObj=new JSONObject();
			jsonObj.put("status", "Internal Server Error:REST CALL");
			jsonObj.put("statusCode", "500");
			jsonObj.put("message",e);
			jsonObj.put("endpoint", endpoint);
			responseObj=jsonObj.toString();
			e.printStackTrace();
		}
		clientResObj.put("response", responseObj);
		return clientResObj;

	}
	public static String convert(Map<String,String> json) throws JSONException {

		String res="<?xml version=\"1.0\" encoding=\"ISO-8859-15\"?>\n<request>";
		for(Map.Entry<String, String> set :json.entrySet())
		{
			JSONObject val=new JSONObject(set.getValue());
			res  = res+"<"+set.getKey()+">"+ XML.toString(val)+"</"+set.getKey()+">"; 
		}

		return res+ "</request>";
	}

	public static String convertResponse(JSONObject json) throws JSONException {

		return  XML.toString(json);


	}


}








