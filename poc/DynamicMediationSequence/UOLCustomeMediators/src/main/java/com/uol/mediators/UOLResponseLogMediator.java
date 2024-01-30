package com.uol.mediators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import com.uol.constants.DBinfoConstants;
import com.uol.utils.JDBCConnectionUtil;
import com.uol.utils.PropertiesUtil;

public class UOLResponseLogMediator extends AbstractMediator {
	
	    private String source_req_id;
	    private String child_req_id;
	    private String response;
	    private String status;
	    private int logStatus;






		public int getLogStatus() {
			return logStatus;
		}

		public void setLogStatus(int logStatus) {
			this.logStatus = logStatus;
		}

	    public String getSource_req_id() {
			return source_req_id;
		}

		public void setSource_req_id(String source_req_id) {
			if(source_req_id!=null)
			{
				source_req_id=source_req_id.replace("urn:uuid:", "");
			}
			this.source_req_id = source_req_id;
		}

		public String getChild_req_id() {
			return child_req_id;
		}

		public void setChild_req_id(String child_req_id) {
			if(child_req_id!=null)
			{
				child_req_id=child_req_id.replace("urn:uuid:", "");
			}
			this.child_req_id = child_req_id;
		}

		public String getResponse() {
			return response;
		}

		public void setResponse(String response) {
			this.response = response;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

	@Override
	public boolean mediate(MessageContext messageContext) {
		Connection con=null;PreparedStatement pstmt=null;
		
		//System.out.println("==================requestID:"+source_req_id+"\n======childrequestid::"+child_req_id);
		//System.out.println("==================response:"+response+"\n==============status::"+status);
		
		try {
			if(logStatus!=0)
			{
			String apiRegistryConfigs = (String)messageContext.getProperty(DBinfoConstants.ROOT_APIRIGISTRY_PROPS);
			Properties properties = PropertiesUtil.propertiesFileRead(apiRegistryConfigs);
			 con = JDBCConnectionUtil.connectToDatabase(DBinfoConstants.API_MASTER_SYSTEM_NAME,DBinfoConstants.API_MASTER_SCHEMA_NAME , properties);
	           
			//Connection con = DriverManager.getConnection("jdbc:mysql://172.16.110.240:3306/uol_api_registry","root","Adm!n@123");
			//log.info("Connection Obj = " +con);
			String updateQuery = "UPDATE `api_log_handler` "
                    + "SET `response` = ?, `status` = ?  WHERE `source_req_id` = ? AND `child_req_id` = ?";
			 pstmt = con.prepareStatement(updateQuery);
			pstmt.setString(1,response);
			pstmt.setString(2,status);
			pstmt.setString(3,source_req_id);
			pstmt.setString(4,child_req_id);
			
			
			int execute = pstmt.executeUpdate();
			log.error("requestLogUpdate=Meditor query status::"+execute);
			
			pstmt.close();
			con.close();
//			if(!execute) {
//				log.error(new String("The Insert Statement Not Executed Properly"));
//				return false;
//			}
			}
		} catch (SQLException e) {
			log.error("exception raised in requestLogUpdate=Meditor::"+e);
			e.printStackTrace();
		}
		finally
		{
			return true;
		}
	}
}
