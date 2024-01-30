package com.knot.uol.utils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import com.knot.uol.mediators.DynamicXMLQueryMediator;
import com.knot.uol.utils.JDBCConnectionUtil;

public class SystemRequestNewLog extends AbstractMediator  {
	
    // Private class variables
	private String apiregistryConfigPath;
    private String parentId;
    private String childId;
    private String processId;
    private String process;
    private String apiName;
    private String requestPayload;



    // Setters for private class variables
    
    


	public void setApiregistryConfigPath(String apiregistryConfigPath) {
		this.apiregistryConfigPath = apiregistryConfigPath;
	}

	public void setProcessId(String processId) {
        this.processId = processId;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public void setRequestPayload(String requestPayload) {
        this.requestPayload = requestPayload;
    }

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}


	public void setChildId(String childId) {
		this.childId = childId;
	}

	// Getters for private class variables


    public String getChildId() {
		return childId;
	}
    
    public String getParentId() {
		return parentId;
	}


	public String getApiregistryConfigPath() {
 		return apiregistryConfigPath;
 	}
    

    public String getProcessId() {
        return processId;
    }

    public String getProcess() {
        return process;
    }

    public String getApiName() {
        return apiName;
    }

    public String getRequestPayload() {
        return requestPayload;
    }





    
    public boolean mediate(MessageContext context) {
    	
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
        	Properties properties = PropertiesUtil.propertiesFileRead(apiregistryConfigPath);
            con = JDBCConnectionUtil.connectToDatabase("UOLLogs", "wso2", properties);
            if (con != null) {
                System.out.println("New log connection object: " + con);

                String query = "INSERT INTO bscsreqloghandler (parentId,ChildId, ProcessId, Process, Api, RequestPayload, Status, requested_on) VALUES (?, ?, ?, ?, ?, ?, 'Inprocess', NOW())";
                pstmt = con.prepareStatement(query);
                pstmt.setString(1, parentId);
                pstmt.setString(2, childId);
                pstmt.setString(3, processId);
                pstmt.setString(4, process);
                pstmt.setString(5, apiName);
                pstmt.setString(6, requestPayload);

                int count = pstmt.executeUpdate();
                System.out.println("New Log count=> "+count);
            } else {
                System.out.println("UOL logs DB Connection Error");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        } finally {
            // Close the PreparedStatement and Connection in the finally block   MessageContext context
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
    
//    public static void main(String[] args) throws ClassNotFoundException {
//    	SystemRequestNewLog systemRequestNewLog = new SystemRequestNewLog();
//    	
//    	systemRequestNewLog.mediate();
//	}
}
