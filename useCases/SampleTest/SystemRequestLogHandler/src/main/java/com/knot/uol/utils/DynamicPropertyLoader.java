package com.knot.uol.utils;
import java.io.FileInputStream;

import java.io.IOException;
import java.util.Properties;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DynamicPropertyLoader extends AbstractMediator {
	
	static {
		System.out.println("Executing Static block");
		loadProperties();
		
	}
    private static final Log log = LogFactory.getLog(DynamicPropertyLoader.class);

    private static Properties properties;

    public DynamicPropertyLoader() {
        properties = new Properties();
        loadProperties();
    }
    
    private static void loadProperties() {
        try {
        	log.info("welcome to classLoader!");
        	System.out.println("welcome to classLoader!");
            // Load property files from a standard directory (e.g., /path/to/properties/)
            String propertyFilePath = "F://JavaNotes//DynamicQuery//api-registry-configs.properties";
            properties.load(new FileInputStream(propertyFilePath));
            
            String var =properties.getProperty("apiregistry.db.password");
            
            System.out.println("Class Loder==> "+var);

            // Load more property files if needed
            // properties.load(new FileInputStream("/path/to/properties/property2.properties"));
            // properties.load(new FileInputStream("/path/to/properties/property3.properties"));
        } catch (Exception e) {
            // Handle exceptions
        	System.out.println(e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

	@Override
	public boolean mediate(MessageContext arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}