package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import java.io.IOException;

import javax.servlet.ServletContext;

import org.openid4java.consumer.ConsumerException;


public class BPTLoginManager {
	
	private BPTLoginConsumer loginConsumer;

	    public BPTLoginManager() {
	    	
	    	// instantiate a ConsumerManager object
	    	try {
				loginConsumer = new BPTLoginConsumer();
			} catch (ConsumerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }
	    
	    public void loginRequest(String userSupportedString, ServletContext context){ 
	    	try {
				loginConsumer.authRequest(userSupportedString, null, null, context);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    

}