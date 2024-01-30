package com.uol.mediators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import com.uol.constants.DBinfoConstants;
import com.uol.utils.JDBCConnectionUtil;
import com.uol.utils.PropertiesUtil;

public class UOLRequestLogMediator extends AbstractMediator {

	private String NIC;
	private String MSISDN;
	private String custid;
	private String channel;
	private String source_req_id;
	private String child_req_id;
	private String api_name;
	private int processId;
	private String processName;
	private String request;
	private String status;
	private int logStatus;
	private String host_address;
	private String sub_api_id;
	
	






	public String getSub_api_id() {
		return sub_api_id;
	}

	public void setSub_api_id(String sub_api_id) {
		this.sub_api_id = sub_api_id;
	}

	public String getHost_address() {
		return host_address;
	}

	public void setHost_address(String host_address) {
		this.host_address = host_address;
	}

	public int getLogStatus() {
		return logStatus;
	}

	public void setLogStatus(int logStatus) {
		this.logStatus = logStatus;
	}

	public String getNIC() {
		return NIC;
	}

	public void setNIC(String nIC) {
		NIC = nIC;
	}

	public String getMSISDN() {
		return MSISDN;
	}

	public void setMSISDN(String mSISDN) {
		MSISDN = mSISDN;
	}

	public String getCustid() {
		return custid;
	}

	public void setCustid(String custid) {
		this.custid = custid;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
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

	public String getApi_name() {
		return api_name;
	}

	public void setApi_name(String api_name) {
		this.api_name = api_name;
	}

	public int getProcessId() {
		return processId;
	}

	public void setProcessId(int processId) {
		this.processId = processId;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
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
		try {
			if(logStatus==1)
			{
			String apiRegistryConfigs = (String)messageContext.getProperty(DBinfoConstants.ROOT_APIRIGISTRY_PROPS);
			Properties properties = PropertiesUtil.propertiesFileRead(apiRegistryConfigs);
			con = JDBCConnectionUtil.connectToDatabase(DBinfoConstants.API_MASTER_SYSTEM_NAME,DBinfoConstants.API_MASTER_SCHEMA_NAME , properties);

			//Connection con = DriverManager.getConnection("jdbc:mysql://172.16.110.240:3306/uol_api_registry","root","Adm!n@123");
			//log.info("Connection Obj = " +con);
			String insertStatement = "INSERT INTO `api_log_handler` "
					+ "( `NIC`, `MSISDN`, `custid`, `channel`, `source_req_id`, `child_req_id`, "
					+ "`api_name`, `processId`, `processName`, `request`,  `status`, `sub_api_id`) "
					+ "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
			pstmt = con.prepareStatement(insertStatement,Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, NIC);
			pstmt.setString(2, MSISDN);
			pstmt.setString(3, custid);
			pstmt.setString(4, channel);
			pstmt.setString(5, source_req_id);
			pstmt.setString(6, child_req_id);
			pstmt.setString(7, api_name);
			pstmt.setInt(8, processId);
			pstmt.setString(9, processName);
			pstmt.setString(10, request);
			pstmt.setString(11, status);
			pstmt.setString(12, sub_api_id);

			int affectedRows = pstmt.executeUpdate();
			log.error("UolInsertRequestLogHandler  query status ::"+affectedRows);
			if(affectedRows>0) {
				ResultSet rs = pstmt.getGeneratedKeys() ;

				if (rs.next()) 
				{ 
					Long id = rs.getLong(1);
					log.error("SRC_REQ_ID:"+source_req_id+":UolInsertRequestLogHandler  Row ID ::"+id);
				} 


			}
			pstmt.close();
			con.close();
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
