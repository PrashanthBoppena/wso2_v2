package com.uol.constants;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.net.http.HttpClient.Version;
import java.time.Duration;
import java.util.Base64;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;

public class RestApiClient {
	
	//static String customercreate_ep="https://localhost:8243/uol/nb/1.0/createCustomer";
  static String customercreate_ep="http://localhost:8280/uol/nb/1.0/createCustomer";
		
	
   //below endpoint is APIM token default endpoints (http & https) to get the authorization token
	//static String APIM_TOKEN_API="https://localhost:9443/oauth2/token";
  static String APIM_TOKEN_API="http://localhost:9763/oauth2/token";
	
	
	//Below are the token api client credentials to get access token
	static String clientId = "XvBQsRUOcFALrQlFwojr4sgQIfUa";
	static String clientSecret = "67byzod2X7EknA9SDCHzaRRAdcMa";

	public static void main(String[] args) throws IOException, InterruptedException {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("adrLname", "suresh");
		jsonObject.put("currency", "USD");
		jsonObject.put("tbAmount", "2000");
		jsonObject.put("adrName", "CH");
		jsonObject.put("adrFname", "babu");
		jsonObject.put("NIC", "A84774L");
		jsonObject.put("channel", "Viya");

		String response=callRestApi(customercreate_ep,jsonObject.toString());

		System.out.println(response);
	}

	public static String callRestApi(String endpoint,String jsonPayload)
	{

		JSONObject jsonObject = new JSONObject();

		try
		{
			String accesstoken=getAccessToken();
			if(accesstoken==null)
			{
				jsonObject.put("Response", "");
				jsonObject.put("Message", "Invalid access token credentials");
				jsonObject.put("Status", "500");
				return jsonObject.toString();
			}


			HttpClient client = HttpClient.newBuilder()
					.version(Version.HTTP_1_1) // Specify HTTP version explicitly
					.connectTimeout(Duration.ofSeconds(30)) // Set a reasonable timeout
					.sslContext(getSSLContext())
					.build();


			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(customercreate_ep))
					.POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
					.header("Authorization", "Bearer " + accesstoken)
					.header("Content-Type", "application/json")
					.build();

			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());


			jsonObject.put("Response", response.body());
			jsonObject.put("Status", response.statusCode());

		}catch (Exception e) {
			// TODO: handle exception
			jsonObject.put("Response", "");
			jsonObject.put("Message", e.getMessage());
			jsonObject.put("Status", "500");
		}
		return jsonObject.toString();
	}

	public static String getAccessToken() throws Exception
	{
		
		String accessToken=null;
		HttpClient client = HttpClient.newBuilder()
				.version(Version.HTTP_1_1) // Specify HTTP version explicitly
				.connectTimeout(Duration.ofSeconds(30)) // Set a reasonable timeout
				.sslContext(getSSLContext())
				.build();

		//here we we cancoding the credentials into base64 format
		String encodedCredentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(APIM_TOKEN_API))
				.POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
				.header("Authorization", "Basic " + encodedCredentials)
				.header("Content-Type", "application/x-www-form-urlencoded")
				.build();

		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		JSONObject jsonObject = new JSONObject(response.body().toString());
		if (response.statusCode() == 200) {
			System.out.println("Access token response: " + jsonObject);
			
//			sample response format "
			
//			{
//		    "access_token": "eyJ4NXQiOiJNV0l5TkRJNVlqRTJaV1kxT0RNd01XSTNOR1ptTVRZeU5UTTJOVFZoWlRnMU5UTTNaVE5oTldKbVpERTFPVEE0TldFMVlUaGxNak5sTldFellqSXlZUSIsImtpZCI6Ik1XSXlOREk1WWpFMlpXWTFPRE13TVdJM05HWm1NVFl5TlRNMk5UVmhaVGcxTlRNM1pUTmhOV0ptWkRFMU9UQTROV0UxWVRobE1qTmxOV0V6WWpJeVlRX1JTMjU2IiwidHlwIjoiYXQrand0IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJiYTE4NTA4Mi1iOWMyLTRjOTktOTYxYy0xOTg3YmU5YjMwOGQiLCJhdXQiOiJBUFBMSUNBVElPTiIsImF1ZCI6Ilh2QlFzUlVPY0ZBTHJRbEZ3b2pyNHNnUUlmVWEiLCJuYmYiOjE3MDQ1NDM1MjksImF6cCI6Ilh2QlFzUlVPY0ZBTHJRbEZ3b2pyNHNnUUlmVWEiLCJzY29wZSI6ImRlZmF1bHQiLCJpc3MiOiJodHRwczpcL1wvbG9jYWxob3N0Ojk0NDNcL29hdXRoMlwvdG9rZW4iLCJleHAiOjE3MDQ1NDcxMjksImlhdCI6MTcwNDU0MzUyOSwianRpIjoiNGM1MDY4YzYtM2ZkMi00ZmM1LThmMWUtOTBkMDFkZThkOTAzIiwiY2xpZW50X2lkIjoiWHZCUXNSVU9jRkFMclFsRndvanI0c2dRSWZVYSJ9.dcTp5BU53-iqG1njc3rluBK1eekCLCVmiiPPvIBgG9Wjbs7zsfTJo0ieCaYTufsILMhcov-XVhqb4SBOHwo1vQq-ebVJKsnPTJ_auH42P0pe2Mvx84qXUXyxeKaXYnxm2iGSOsAS9Ci27_BaMx-gbV6w_kU74fR6YI4R1bjbCd9E8JDI8m4uGY8df8X7LWf97vtrJ3foLjId390qYw8lDkKVOKbxFOK3dihqUIVZwOforXnFp9abWtfDyZOpyLnLOnNaaV0kErqmEAk_9vrIiFj948OsAJNST3hOjA-5EAo8YQHN5XHniCu9E0Y6JucOw5xhVDT0L7TBmklAn18TNA",
//		    "scope": "default",
//		    "token_type": "Bearer",
//		    "expires_in": 3600
//		}
			
			// Accessing individual values:
			accessToken = jsonObject.getString("access_token");

		} else {
			System.err.println("Failed to get access token: " + response.statusCode() + " " + jsonObject);
		}

		return accessToken;
	}
	
	public static SSLContext getSSLContext() throws Exception
	{
		
		TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					@Override
					public void checkClientTrusted(X509Certificate[] chain, String authType) {}
					@Override
					public void checkServerTrusted(X509Certificate[] chain, String authType) {}
					@Override
					public X509Certificate[] getAcceptedIssuers() { return null; }
				}
		};

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new SecureRandom());
        return sc;
	}
}