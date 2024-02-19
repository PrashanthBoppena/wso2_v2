package com.uol.mediators;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
import com.uol.dto.ApiResponseDTO;
import com.uol.utils.GetApiConfigDetails;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DynamicRestSeqMediator extends AbstractMediator {

	private String subApiId;
	private String sourceRequestID;
	private JSONObject aggregateResponse;

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

	public boolean mediate(MessageContext messageContext) {
		try {
			aggregateResponse = new JSONObject();
			String apiRegistryConfigs = (String) messageContext.getProperty(DBinfoConstants.ROOT_APIRIGISTRY_PROPS);
			String compositeEndpoint = (String) messageContext.getProperty("compositeEndpoint");

			String jsonPayloadToString = JsonUtil.jsonPayloadToString(
					((Axis2MessageContext) messageContext).getAxis2MessageContext());

			ObjectMapper mapper = new ObjectMapper();
			List<ApiResponseDTO> apiList = new GetApiConfigDetails().getMasterApiConfigDetails(apiRegistryConfigs,
					subApiId);

			Map<String, String> clientResponse = null;
			if (apiList != null) {
				for (ApiResponseDTO apidto : apiList) {
					apidto.setSource_req_id(sourceRequestID);
					apidto.setChild_req_id(UUID.randomUUID().toString());
					apidto.setLogStatus(1);
					String apiInfoObj = mapper.writeValueAsString(apidto);

					clientResponse = makePostRequest(sourceRequestID,
							DBinfoConstants.COMPOSITE_HOST_ADDRESS + compositeEndpoint, apiInfoObj, jsonPayloadToString,
							aggregateResponse.toString());

					JSONObject jsonResponse = new JSONObject(clientResponse.get("response"));
					aggregateResponse.put(apidto.getProcess_name(), jsonResponse);

					if ((Integer.parseInt(clientResponse.get("status"))) > 200 && apidto.getResponseStatus()) {
						JSONObject jsonObj = new JSONObject();
						jsonObj.put("status", "Request API CALL FAILED");
						jsonObj.put("statusCode", clientResponse.get("status"));
						jsonObj.put("message", "request failed");
						aggregateResponse.put("Fault", jsonObj);
						messageContext.setProperty("statusCode", 500);
						break;
					}
					messageContext.setProperty("statusCode", 200);
				}
			} else {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("status", "BAD REQUEST");
				jsonObj.put("statusCode", "400");
				jsonObj.put("message", "Empty api config list:" + apiList);
				aggregateResponse.put("Fault", jsonObj);
				messageContext.setProperty("statusCode", 400);
			}
		} catch (Exception e) {
			handleException(e, messageContext);
		} finally {
			messageContext.setProperty("xmlResponse", convertResponse(aggregateResponse));
		}

		return true;
	}

	private void handleException(Exception e, MessageContext messageContext) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("status", "Internal Server Error:Functional");
		jsonObj.put("statusCode", "500");
		jsonObj.put("message", e.getMessage());
		aggregateResponse.put("Fault", jsonObj);
		messageContext.setProperty("statusCode", 500);
	}

	private Map<String, String> makePostRequest(String sourceRequestID, String endpoint, String apiConfig,
			String inputNBPayload, String params) {
		Map<String, String> clientResObj = new HashMap<>();
		Map<String, String> mapObj = new HashMap<>();
		mapObj.put("ApiConfig", apiConfig);
		mapObj.put("NBPayload", inputNBPayload);
		if (params != null) {
			mapObj.put("Params", params);
		}

		String responseObj = null;
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			String xmlObj = convert(mapObj);

			HttpPost post = new HttpPost(endpoint);
			post.setEntity(new StringEntity(xmlObj, ContentType.APPLICATION_XML));

			HttpResponse response = client.execute(post);
			clientResObj.put("status", Integer.toString(response.getStatusLine().getStatusCode()));

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				responseObj = EntityUtils.toString(entity, "UTF-8");

				if (response.getStatusLine().getStatusCode() != 200) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("status", "Target System Error");
					jsonObj.put("statusCode", response.getStatusLine().getStatusCode());
					jsonObj.put("message", responseObj);
					jsonObj.put("endpoint", endpoint);
					responseObj = jsonObj.toString();
				}
			} else {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("status", "Response NULL");
				jsonObj.put("statusCode", response.getStatusLine().getStatusCode());
				jsonObj.put("message", "Didn't get any response");
				jsonObj.put("endpoint", endpoint);
				responseObj = jsonObj.toString();
			}
		} catch (IOException | JSONException e) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("status", "Internal Server Error:REST CALL");
			jsonObj.put("statusCode", "500");
			jsonObj.put("message", e.getMessage());
			jsonObj.put("endpoint", endpoint);
			responseObj = jsonObj.toString();
		}

		clientResObj.put("response", responseObj);
		return clientResObj;
	}

	public static String convert(Map<String, String> json) throws JSONException {
		String res = "<?xml version=\"1.0\" encoding=\"ISO-8859-15\"?>\n<request>";
		for (Map.Entry<String, String> set : json.entrySet()) {
			JSONObject val = new JSONObject(set.getValue());
			res = res + "<" + set.getKey() + ">" + XML.toString(val) + "</" + set.getKey() + ">";
		}

		return res + "</request>";
	}

	public static String convertResponse(JSONObject json) throws JSONException {
		return XML.toString(json);
	}
}
