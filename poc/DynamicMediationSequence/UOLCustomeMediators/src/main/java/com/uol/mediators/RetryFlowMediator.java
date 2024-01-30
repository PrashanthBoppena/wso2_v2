package com.uol.mediators;

import java.util.List;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.map.HashedMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uol.constants.DBinfoConstants;
import com.uol.dto.ApiLogHandlerDto;
import com.uol.utils.GetApiConfigDetails;
import com.uol.utils.RetryApiConfigDetails;

public class RetryFlowMediator extends AbstractMediator  {


	private String sourceRequestID;

	public String getSourceRequestID() {
		return sourceRequestID;
	}

	public void setSourceRequestID(String sourceRequestID) {
		this.sourceRequestID = sourceRequestID;
	}

	JSONObject aggregateResponse=null;
	@Override
	public boolean mediate(MessageContext messageContext) {

		aggregateResponse=new JSONObject();
		String apiRegistryConfigs = (String)messageContext.getProperty(DBinfoConstants.ROOT_APIRIGISTRY_PROPS);		
		String jsonPayloadToString = JsonUtil.jsonPayloadToString(((Axis2MessageContext) messageContext).getAxis2MessageContext());
		ObjectMapper mapper=new ObjectMapper();
		try
		{
			List<ApiLogHandlerDto> requestLogApiInfo=new RetryApiConfigDetails().getApiRequestLogDetails(apiRegistryConfigs, sourceRequestID);
			Map<String,String> clientResponse =null;
						
			ApiLogHandlerDto requestApiInfo=requestLogApiInfo.get(0);

			List<ApiResponseDTO> apiMasterList=new GetApiConfigDetails().getMasterApiConfigDetails(apiRegistryConfigs, requestApiInfo.getSub_api_id());
			
			
			System.out.println("apiMasterList size::"+apiMasterList.size());
			if(apiMasterList!=null)
			{
				for (int i = 0; i < apiMasterList.size(); i++) {
				    ApiResponseDTO apidto = apiMasterList.get(i);
				 
				    
					

					ApiLogHandlerDto apiResponseObj=requestLogApiInfo.get(i);
					
					 System.out.println("apiResponseObj dto obje::"+apiResponseObj);
					if(apiResponseObj==null )
						
					{
						
						System.out.println("apiResponseObj is null");
						apidto.setSource_req_id(requestApiInfo.getSource_req_id());
						apidto.setChild_req_id(UUID.randomUUID().toString());
						apidto.setLogStatus(1);
						String apiInfoObj = mapper.writeValueAsString(apidto);
						clientResponse = makePostRequest(sourceRequestID,apiResponseObj.getApi_name(), apiInfoObj,requestApiInfo.getRequest(),aggregateResponse.toString());
	
						JSONObject jsonResponse = new JSONObject(clientResponse.get("response"));
						log.info("response output is:===========================jsonResponse======:"+jsonResponse);
						//	aggregateResponse.put(apidto.getProcess_name(), jsonResponse);

						if((Integer.parseInt(clientResponse.get("status")))>200)
						{
							//log.info("Http called failed::"+clientResponse.get("status"));
							JSONObject jsonObj=new JSONObject();
							jsonObj.put("status", "Request API CALL FAILED");
							jsonObj.put("statusCode", clientResponse.get("status"));
							jsonObj.put("message","request failed");
							aggregateResponse.put("Fault", jsonObj);
							messageContext.setProperty("statusCode",500);
							
						}else {
						messageContext.setProperty("statusCode",200);   
						}
					}
					else if( (apiResponseObj.getStatus().equals("500")  && !(apiResponseObj.getSource_req_id().equalsIgnoreCase(apiResponseObj.getChild_req_id())) ))
					{
						System.out.println("apiResponseObj is ::500");
						apidto.setSource_req_id(requestApiInfo.getSource_req_id());
						apidto.setChild_req_id(requestApiInfo.getChild_req_id());
						apidto.setLogStatus(2); // 0-disabled,1-enabled,2-response-update
						String apiInfoObj = mapper.writeValueAsString(apidto);
						clientResponse = makePostRequest(sourceRequestID,apiResponseObj.getApi_name(), apiInfoObj,requestApiInfo.getRequest(),aggregateResponse.toString());
                         
						JSONObject jsonResponse = new JSONObject(clientResponse.get("response"));
						log.info("response output is:===========================jsonResponse======:"+jsonResponse);
						//	aggregateResponse.put(apidto.getProcess_name(), jsonResponse);

						if((Integer.parseInt(clientResponse.get("status")))>200)
						{
							//log.info("Http called failed::"+clientResponse.get("status"));
							JSONObject jsonObj=new JSONObject();
							jsonObj.put("status", "Request API CALL FAILED");
							jsonObj.put("statusCode", clientResponse.get("status"));
							jsonObj.put("message","request failed");
							aggregateResponse.put("Fault", jsonObj);
							messageContext.setProperty("statusCode",500);
							
						}else {
						messageContext.setProperty("statusCode",200);
						}
					
					
					}else
					{			System.out.println("apiResponseObj is success::200");		
							aggregateResponse.put(apiResponseObj.getProcessName(), apiResponseObj.getResponse());			
					}

					
					
				}


			}else
			{
				//log.info("Response from master api configs at ::"+apiList);
				JSONObject jsonObj=new JSONObject();
				jsonObj.put("status", "BAD REQUEST");
				jsonObj.put("statusCode", "400");
				jsonObj.put("message","Empty api config list:"+apiMasterList);
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
