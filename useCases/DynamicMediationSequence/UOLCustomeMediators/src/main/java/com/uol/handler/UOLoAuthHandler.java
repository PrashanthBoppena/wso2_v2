package com.uol.handler;

import org.apache.synapse.MessageContext;
import org.apache.synapse.rest.AbstractHandler;
import org.json.JSONObject;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.rest.AbstractHandler;

import java.util.Map;
public class UOLoAuthHandler extends AbstractHandler {



    public boolean handleRequest(MessageContext messageContext) {
    	 System.out.println("==========handler reqeust===========>");
        try {
            if (authenticate(messageContext)) {
                return true;
            }
        } catch (Exception e) {
        	  System.out.println("==========authHeader ex===========>"+e);
            e.printStackTrace();
        }
        return false;
    }

    public boolean handleResponse(MessageContext messageContext) {
    	System.out.println("==========handler response===========>");
        return true;  
    }

    public boolean authenticate(MessageContext synCtx)  {
        Map headers = getTransportHeaders(synCtx);
        
     
        
        String authHeader = getAuthorizationHeader(headers);
        System.out.println("==========authHeader===========>"+JSONObject.getNames(headers));
        
        if (authHeader.startsWith("userName")) {
            return true;
        }
        return false;
    }

    private String getAuthorizationHeader(Map headers) {
        return (String) headers.get("Authorization");
    }

    private Map getTransportHeaders(MessageContext messageContext) {
        return (Map) ((Axis2MessageContext) messageContext).getAxis2MessageContext().
        getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
    }
}