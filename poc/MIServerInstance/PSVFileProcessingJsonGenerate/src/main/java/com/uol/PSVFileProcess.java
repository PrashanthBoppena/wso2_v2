package com.uol;



import org.apache.synapse.MessageContext; 
import org.apache.synapse.mediators.AbstractMediator;
 
import com.fasterxml.jackson.databind.ObjectMapper;










public class PSVFileProcess extends AbstractMediator { 

	public boolean mediate(MessageContext mc) { 
		return true;
	}

}

