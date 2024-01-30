package com.knot.uol.updateLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import com.knot.uol.utils.JDBCConnUtil;

public class UpdateSystemRequest extends AbstractMediator { 
	@Override
	public boolean mediate(MessageContext context) {
		// TODO Implement your mediation logic here 
		String reqId=null, 
		processId=null, 
		responsePayload=null, 
		status=null; 
		
		// log params
		String driver=(String)context.getProperty("uolLogDriver"),
				url=(String)context.getProperty("uolLogUrl"),
				user=(String)context.getProperty("uolLogUser"),
				password=(String)context.getProperty("uolLogPassword");		
		
		 
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
        	processId=(String)context.getProperty("processId");
        	responsePayload=(String)context.getProperty("responsePayload");
        	
        	
        	
            con = JDBCConnUtil.getConnection(driver, url, user, password);
            if(con!=null) {
                System.out.println("update log conn obj====> "+con);
                
                String query = "update bscsreqloghandler set ResponsePayload=?, Status='Success' where ProcessId=?";
                pstmt = con.prepareStatement(query);
                pstmt.setString(1, responsePayload);
                
                pstmt.setString(2, processId);


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
