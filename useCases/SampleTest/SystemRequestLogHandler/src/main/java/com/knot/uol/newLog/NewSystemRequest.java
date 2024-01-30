package com.knot.uol.newLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.synapse.MessageContext; 
import org.apache.synapse.mediators.AbstractMediator;

import com.knot.uol.utils.JDBCConnUtil;

public class NewSystemRequest extends AbstractMediator { 

	@Override
	public boolean mediate(MessageContext context) { 

		// request params
		String reqId=null, 
		processId=null, 
		process=null, 
		apiName=null, 
		requestPayload=null, 
		status=null; 
		
		
		// log params
		String driver=(String)context.getProperty("uolLogDriver"),
				url=(String)context.getProperty("uolLogUrl"),
				user=(String)context.getProperty("uolLogUser"),
				password=(String)context.getProperty("uolLogPassword");
		 
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
        	
        	reqId=(String)context.getProperty("reqId");
        	processId=(String)context.getProperty("processId");
        	process=(String)context.getProperty("process");
        	apiName=(String)context.getProperty("apiName");
        	requestPayload=(String)context.getProperty("requestPayload");
        	
        	
        	
            con = JDBCConnUtil.getConnection(driver, url, user, password);
            if(con!=null) {
                System.out.println("new log conn obj====> "+con);
                
                String query = "INSERT INTO bscsreqloghandler (ReqId, ProcessId, Process, Api, RequestPayload, Status, requested_on) VALUES (?, ?, ?, ?, ?, 'Inprocess', NOW())";
                pstmt = con.prepareStatement(query);
                pstmt.setString(1, reqId);
                pstmt.setString(2, processId);
                pstmt.setString(3, process);
                pstmt.setString(4, apiName);
                pstmt.setString(5, requestPayload);

                int count = pstmt.executeUpdate();
                System.out.println("New Log count=> "+count);            	
            }else {
            	System.out.println("UOL logs DB Connection Error");  
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        } finally {
            // Close the PreparedStatement and Connection in the finally block
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }      	

        }

        return true;
    }
	

}
