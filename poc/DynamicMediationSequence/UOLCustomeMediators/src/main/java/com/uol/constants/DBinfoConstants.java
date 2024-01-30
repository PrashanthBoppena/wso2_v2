package com.uol.constants;

public class DBinfoConstants {
	public static final String ROOT_APIRIGISTRY_PROPS = "apiregistryConfigPath";
	  
	  public static final String ROOT_PAYLOAD_KEY = "requestPayload";
	  
	  public static final String DB_SYSTEM = "db_system";
	  
	  public static final String QUERY_SCHEMA = "query_schema";
	  
	  public static final String DB_PARAMS = "params";
	  
	  public static final String QUERY_PROPERTIES = "properties_path";
	  
	  public static final String ROOT_QUERY_PATH = "SELECT * FROM uol_api_registry.query_config WHERE query_name = ?";
	  
	  public static final String dbUrl = "apiregistry.db.url";
	  
	  public static final String dbUser = "apiregistry.db.username";
	  
	  public static final String dbPassword = "apiregistry.db.password";
	  
	  public static final String queryConfigPath = "apiregistry.db.queries.basic.path";
	  
	  public static final String dbDriver = "apiregistry.db.driver";
	  
	  public static final String COMPOSITE_HOST_ADDRESS="http://localhost:8285/uol/composite/";
	  
	  
      public static final String API_MASTER_SYSTEM_NAME = "uol";
	  
	  public static final String API_MASTER_SCHEMA_NAME = "uol_api_registry";
	  public static final String API_MASTER_QUERY ="SELECT * FROM uol_api_registry.sub_api_master where sub_api_id=? and enable_flag=1;";
		
	  public static final String REQUEST_RETRY_LOG_HANDLER_QUERY ="SELECT * FROM uol_api_registry.api_log_handler where source_req_id=?";
		
	  
	  
	 
	  
	  
}
