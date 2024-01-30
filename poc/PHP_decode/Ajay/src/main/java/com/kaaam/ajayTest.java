package com.kaaam;

import org.apache.synapse.MessageContext; 
import org.apache.synapse.mediators.AbstractMediator;

public class ajayTest extends AbstractMediator { 

	public boolean mediate(MessageContext context) { 
		// TODO Implement your mediation logic here 
		return true;
	}
	public static void main(String[] args) {
		System.out.println("Ajay Hello");
	}
}
