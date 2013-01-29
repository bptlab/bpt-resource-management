package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openid4java.consumer.ConsumerException;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;


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
	    
	    public String loginRequest(String userSupportedString, ServletContext context, HttpServletRequest request, HttpServletResponse response){
	    	String authRequest = null;
	    	try {
	    		System.out.println("Identifier" + request.getParameter("identifier"));
				authRequest = loginConsumer.authRequest(userSupportedString, request, response, context);
	    		System.out.println(request);
	    		System.out.println(response);
				System.out.println(authRequest);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return authRequest;
//	    	Identifier identifier = loginConsumer.verifyResponse(request);
//	    	System.out.println("identifier: " + identifier);
	    }
	    

}