package com.uol.mediators;

import java.util.Date;

public class ApiResponseDTO {
  
    
    private int api_id;
    private String sub_api_id;
    private String system_name;
    private String market;
    private String subtype;
    private int process_id;
    private String process_name;
    private boolean enable_flag;
    private String request_type;
    private String http_method;
    private String sub_endpoint;
    private String request_template_id;
    private String dep_proc_id;
    private String output_params;
    private String retry;
    private String error_code;
    private String api_desc;
    private String updated_by;
    private String created_date;
    private String updated_date;
    private String source_req_id;
    private String child_req_id;
    private int logStatus;
    
    
  
	public String getChild_req_id() {
		return child_req_id;
	}
	public void setChild_req_id(String child_req_id) {
		this.child_req_id = child_req_id;
	}
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
		this.source_req_id = source_req_id;
	}
	private boolean responseStatus;
	public boolean getResponseStatus() {
		return responseStatus;
	}
	public void setResponseStatus(boolean responseStatus) {
		this.responseStatus = responseStatus;
	}
	public String getUpdated_date() {
		return updated_date;
	}
	public void setUpdated_date(String updated_date) {
		this.updated_date = updated_date;
	}
	public int getApi_id() {
		return api_id;
	}
	public void setApi_id(int api_id) {
		this.api_id = api_id;
	}
	public String getSub_api_id() {
		return sub_api_id;
	}
	public void setSub_api_id(String sub_api_id) {
		this.sub_api_id = sub_api_id;
	}
	public String getSystem_name() {
		return system_name;
	}
	public void setSystem_name(String system_name) {
		this.system_name = system_name;
	}
	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	public String getSubtype() {
		return subtype;
	}
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
	public int getProcess_id() {
		return process_id;
	}
	public void setProcess_id(int process_id) {
		this.process_id = process_id;
	}
	public String getProcess_name() {
		return process_name;
	}
	public void setProcess_name(String process_name) {
		this.process_name = process_name;
	}
	public boolean isEnable_flag() {
		return enable_flag;
	}
	public void setEnable_flag(boolean enable_flag) {
		this.enable_flag = enable_flag;
	}
	public String getRequest_type() {
		return request_type;
	}
	public void setRequest_type(String request_type) {
		this.request_type = request_type;
	}
	public String getHttp_method() {
		return http_method;
	}
	public void setHttp_method(String http_method) {
		this.http_method = http_method;
	}
	public String getSub_endpoint() {
		return sub_endpoint;
	}
	public void setSub_endpoint(String sub_endpoint) {
		this.sub_endpoint = sub_endpoint;
	}
	public String getRequest_template_id() {
		return request_template_id;
	}
	public void setRequest_template_id(String request_template_id) {
		this.request_template_id = request_template_id;
	}
	public String getDep_proc_id() {
		return dep_proc_id;
	}
	public void setDep_proc_id(String dep_proc_id) {
		this.dep_proc_id = dep_proc_id;
	}
	public String getOutput_params() {
		return output_params;
	}
	public void setOutput_params(String output_params) {
		this.output_params = output_params;
	}
	public String getRetry() {
		return retry;
	}
	public void setRetry(String retry) {
		this.retry = retry;
	}
	public String getError_code() {
		return error_code;
	}
	public void setError_code(String error_code) {
		this.error_code = error_code;
	}
	public String getApi_desc() {
		return api_desc;
	}
	public void setApi_desc(String api_desc) {
		this.api_desc = api_desc;
	}
	public String getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}
	public String getCreated_date() {
		return created_date;
	}
	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}
    

    

    // Getters and setters for each field
}
