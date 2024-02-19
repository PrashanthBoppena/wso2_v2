package com.uol.dto;

import org.apache.axis2.databinding.annotation.IgnoreNullElement;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiLogHandlerDto {

	
	private int sno;
	@JsonProperty("sub_api_id")
	private String sub_api_id;
	@JsonProperty("NIC")
	private String NIC;
	@JsonProperty("MSISDN")
	private String MSISDN;
	@JsonProperty("custid")
	private String custid;
	private String channel;
	private String source_req_id;
	private String child_req_id;
	private String request;
	private String response;
	private String api_name;
	private String processId;
	private String processName;
	private String status;
	private String createdOn;
	private String updatedOn;
	private int retry;
	
	public int getRetry() {
		return retry;
	}
	public void setRetry(int retry) {
		this.retry = retry;
	}
	public int getSno() {
		return sno;
	}
	public void setSno(int sno) {
		this.sno = sno;
	}
	public String getSub_api_id() {
		return sub_api_id;
	}
	public void setSub_api_id(String sub_api_id) {
		this.sub_api_id = sub_api_id;
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
		this.source_req_id = source_req_id;
	}
	public String getChild_req_id() {
		return child_req_id;
	}
	public void setChild_req_id(String child_req_id) {
		this.child_req_id = child_req_id;
	}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getApi_name() {
		return api_name;
	}
	public void setApi_name(String api_name) {
		this.api_name = api_name;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
	}
	
	
}
