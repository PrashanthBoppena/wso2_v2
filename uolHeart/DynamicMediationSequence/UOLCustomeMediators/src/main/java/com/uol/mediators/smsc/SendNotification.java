package com.uol.mediators.smsc;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.JSONArray;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendNotification extends AbstractMediator{

//	private String host,systemId,password;
//	
//	public String getHost() {
//		return host;
//	}
//
//	public void setHost(String host) {
//		this.host = host;
//	}
//
//	public String getSystemId() {
//		return systemId;
//	}
//
//	public void setSystemId(String systemId) {
//		this.systemId = systemId;
//	}
//
//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}

	//    private static final Logger log = LoggerFactory.getLogger(UOLSMSCClient.class);
	//    private static final TimeFormatter TIME_FORMATTER = new AbsoluteTimeFormatter();
	@Override
	public boolean mediate(MessageContext messageContext) {
		// TODO Auto-generated method stub
		try {
			log.info("========smsc mediatore called:::");
			
			messageContext.setProperty("smscResponse", makePostRequest());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("========smsc response mediayor exception:::"+e);
			e.printStackTrace();
			messageContext.setProperty("smscResponse", e);
			
		}

		return true;
	}

	private static String prepareSMSCRequest(String host,String systemId,String password) throws JSONException
	{
		
		 JSONObject jsonObject = new JSONObject();
		 if(true) {
		 jsonObject.put("destination", "+918309084117");
		 jsonObject.put("text", "hi ., this is uol testing.");
		 return jsonObject.toString();
	}
		// USER:    UOLTEST
		// PASS:    uol@test
		// PORT:   2775

	        // Create the smpp_account_config object
	        JSONObject smppAccountConfig = new JSONObject();
	        smppAccountConfig.put("host", host);
	        smppAccountConfig.put("port", 2775);
	        smppAccountConfig.put("system_id", systemId);
	        smppAccountConfig.put("password", password);
	        smppAccountConfig.put("smpp_version", "3.4");
	        smppAccountConfig.put("header", "10.10.92.115");
	        // Create the message object
	        JSONObject message = new JSONObject();
	        message.put("source_addr", "UOL");

	        // Create the short_message object
	        JSONObject shortMessage = new JSONObject();
	        shortMessage.put("text", "UOL sample api integration testing notification");

	        // Add short_message to message
	        message.put("short_message", shortMessage);

	        // Create the destinations array
	        JSONArray destinations = new JSONArray();
	        destinations.put("+9609111112");
//	        destinations.put("44");
//	        destinations.put("447892000002");
//	        destinations.put("44");
//	        destinations.put("44");
//	        destinations.put("447892000003");

	        // Add everything to the main JSON object
	        jsonObject.put("smpp_account_config", smppAccountConfig);
	        jsonObject.put("message", message);
	        jsonObject.put("destinations", destinations);

	        // Print the resulting JSON object
	       //(jsonObject.toString());
		return jsonObject.toString();

	}
	public static void main(String s[])
	{
		try {

		
	System.out.println("====>"+makePostRequest());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}	
	}
	
	private static String makePostRequest() throws JSONException {

		

		String responseObj=null;
		try
		{
			//System.out.println("xmlOb is ------------------------->"+xmlObj);
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost("https://melroselabs.com/tools/smppsender/?host=smscsim.smpp.org&port=2775&systemid=UOLTEST");
			post.addHeader("Authorization", "Basic VU9MVEVTVDp1b2xAdGVzdA==");
			post.addHeader("X-Amz-Date",LocalDateTime.now().toString());
			post.addHeader("x-amzn-requestid","23f16108-d537-41a8-be81-7dd2289a17ca");
			post.setEntity(new StringEntity(prepareSMSCRequest("smscsim.smpp.org","UOLTEST","uol@test"), ContentType.APPLICATION_JSON));


			//post.setEntity(new UR);
			HttpResponse response = client.execute(post);
			
			System.out.println("response is:"+response);
			if(response.getStatusLine().getStatusCode()==404)
			{
				JSONObject jsonObj=new JSONObject();
				jsonObj.put("status", "NOT FOUND");
				jsonObj.put("statusCode", "404");
				jsonObj.put("message", "UNKNOWN ENDPOINT");
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
					responseObj=jsonObj.toString();

				}
			}
		}catch (Exception e) {
			// TODO: handle exception
System.out.println(e);
			//log.info("when dynamicSBseq api call got error:"+e);
			JSONObject jsonObj=new JSONObject();
			jsonObj.put("status", "Internal Server Error:REST CALL");
			jsonObj.put("statusCode", "500");
			jsonObj.put("message",e);
			responseObj=jsonObj.toString();
			e.printStackTrace();
		}
		return responseObj;

	}
}
