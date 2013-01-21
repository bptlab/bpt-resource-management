package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.discovery.Identifier;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.*;
import org.openid4java.OpenIDException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.io.IOException;


public class BPTLoginManager {

		    public ConsumerManager manager;

	    public BPTLoginManager() throws ConsumerException{
	    	
	    	// instantiate a ConsumerManager object
	        manager = new ConsumerManager();
	    	
	    }
	    
	 // --- placing the authentication request ---
	    public String authRequest(String userSuppliedString, HttpServletRequest httpReq, HttpServletResponse httpResp)
	            throws IOException
	    {
	        try
	        {
	            // configure the return_to URL where your application will receive
	            // the authentication responses from the OpenID provider
	            String returnToUrl = "http://example.com/openid";

	            // --- Forward proxy setup (only if needed) ---
	            // ProxyProperties proxyProps = new ProxyProperties();
	            // proxyProps.setProxyName("proxy.example.com");
	            // proxyProps.setProxyPort(8080);
	            // HttpClientFactory.setProxyProperties(proxyProps);

	            // perform discovery on the user-supplied identifier
	            List discoveries = manager.discover(userSuppliedString);

	            // attempt to associate with the OpenID provider
	            // and retrieve one service endpoint for authentication
	            DiscoveryInformation discovered = manager.associate(discoveries);

	            // store the discovery information in the user's session
	            httpReq.getSession().setAttribute("openid-disc", discovered);

	            // obtain a AuthRequest message to be sent to the OpenID provider
	            AuthRequest authReq = manager.authenticate(discovered, returnToUrl);

	            // Attribute Exchange example: fetching the 'email' attribute
	            FetchRequest fetch = FetchRequest.createFetchRequest();
	            fetch.addAttribute("email",
	                    // attribute alias
	                    "http://schema.openid.net/contact/email",   // type URI
	                    true);                                      // required

	            // attach the extension to the authentication request
	            authReq.addExtension(fetch);


	            if (! discovered.isVersion2() )
	            {
	                // Option 1: GET HTTP-redirect to the OpenID Provider endpoint
	                // The only method supported in OpenID 1.x
	                // redirect-URL usually limited ~2048 bytes
	                httpResp.sendRedirect(authReq.getDestinationUrl(true));
	                return null;
	            }
	            else
	            {
	                // Option 2: HTML FORM Redirection (Allows payloads >2048 bytes)

	                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("formredirection.jsp");
	                httpReq.setAttribute("parameterMap", authReq.getParameterMap());
	                httpReq.setAttribute("destinationUrl", authReq.getDestinationUrl(false));
	                dispatcher.forward(httpReq, httpResp);
	            }
	        }
	        catch (OpenIDException e)
	        {
	            // present error to the user
	        }

	        return null;
	    }

}