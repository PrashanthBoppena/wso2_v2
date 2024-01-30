package com.uol.mediators;

import org.apache.synapse.Mediator;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.config.SynapseConfiguration;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.mediators.base.SequenceMediator;
import org.apache.synapse.util.JSONMergeUtils;
import org.apache.synapse.util.MessageHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import java.io.InputStream;
import java.util.List;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.context.ConfigurationContext;

public class DynamicSeqMediator extends AbstractMediator { 

	
	private String sequenceKey;
	private String checkKey;
	
	
	public String getSequenceKey() {
		return sequenceKey;
	}


	public void setSequenceKey(String sequenceKey) {
		this.sequenceKey = sequenceKey;
	}


	public boolean mediate(MessageContext messageContext) { 
		// TODO Implement your mediation logic here 
		try {
			String resData=(String) messageContext.getProperty("reqData");
			if(resData==null) {
		// Getting the json payload to string
		String jsonPayloadToString = JsonUtil.jsonPayloadToString(((Axis2MessageContext) messageContext).getAxis2MessageContext());
		
		log.info("jsonPayloadToString JSON body:\n" + jsonPayloadToString);
//		// Make a json object
//		JSONArray jsonBody = new JSONArray(jsonPayloadToString);
//
//
//		log.info("Transformed JSON array bodyjsonBody:\n" + jsonBody);
//		
//		  // Create a SynapseEnvironment to access sequence repository
        	
			List<ApiResponseDTO> apiList = new ObjectMapper().readValue(jsonPayloadToString, new TypeReference<List<ApiResponseDTO>>() {});
			log.info("Transformed JSON array apiList:\n" + apiList);
			// Retrieve the sequence
	       
			for(ApiResponseDTO apidto: apiList)
			{
				boolean status=false;
				
				if(status|| apiList.size()<1)
				{
				SynapseEnvironment synapseEnvironment = messageContext.getEnvironment();

		        

			       
				log.info("Transformed JSON array ApiResponseDTO:\n" + apidto);
				
				log.info("======================================sequence is not null::"+sequenceKey);
				
				 SequenceMediator sequenceMediator = (SequenceMediator) synapseEnvironment.getSynapseConfiguration().getSequence(sequenceKey);


	        if (sequenceMediator != null) {
	        	log.info("======================================sequence is not null::"+sequenceKey);
	        	messageContext.setProperty("API_Name",apidto.getSub_endpoint());
				messageContext.setProperty("System_Name",apidto.getSystem_name());
				messageContext.setProperty("Process_ID",apidto.getProcess_id());
				messageContext.setProperty("Process_Name",apidto.getProcess_name());
				messageContext.setProperty("Enable_Flag",apidto.isEnable_flag());				
				messageContext.setProperty("Endpoint",apidto.getSub_endpoint());
				messageContext.setProperty("HTTP_Method",apidto.getHttp_method());
				messageContext.setProperty("Request_Type",apidto.getRequest_type());
				messageContext.setProperty("req_template_ID",apidto.getRequest_template_id());
				

	        	log.info("=======================i===============sequence is not apidto.getSub_endpoint()::"+apidto.getSub_endpoint());
				 status=sequenceMediator.mediate(messageContext);
				 
	           log.info("status check ============>"+status);
	        
				if(status)
				{
					String resData2=(String) messageContext.getProperty("reqData");
					 log.info("status check ============>"+resData2);
					 messageContext.setProperty("repData", resData2);
				}
	        } else {
	            // Sequence not found or other handling
	        	log.info("==================sequence is null");
	        }
	        
			}
			}
			}
		} catch (Exception e) {
			log.info("==================Exception e::-"+e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	        
	        return true;
	    }


	public String getCheckKey() {
		return checkKey;
	}


	public void setCheckKey(String checkKey) {
		this.checkKey = checkKey;
	}
	}
		
		
		
		
		
