package de.uni_potsdam.hpi.bpt.resource_management;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;

/**
 * Class providing methods to validate addresses.
 * 
 * @author tw
 *
 */
public class BPTValidator {
	
	private static final String[] schemes = new String[] {"http","https","ftp"};
	private static UrlValidator urlValidator = new UrlValidator(schemes);
	private static EmailValidator emailValidator = EmailValidator.getInstance();

	/**
	 * Checks if an url is valid, i.e. it is well-formed and points to available resources.
	 * 
	 * @param url the url to be checked as String
	 * @return true if the given url is valid
	 */
	public static boolean isValidUrl(String url) {
//		for (String protocol : schemes) {
//			if (url.equals(protocol + "://")) {
//				return true;
//			}
//		}
		return urlValidator.isValid(url) && pageIsAvailable(url);
	}
	
	/**
	 * Checks if an email address is well-formed.
	 * 
	 * @param email the mail address to be checked as String
	 * @return true if the mail address is well-formed
	 */
	public static boolean isValidEmail(String email) {
		return emailValidator.isValid(email);
	}
	
	/**
	 * Checks if an url points to available resources.
	 * Sends HTTP GET requests and checks the codes of response.
	 * 
	 * @param url the url to be checked as String
	 * @return true if the given url points to available resources, i.e. the HTTP response code is 2xx oder 3xx
	 */
	private static boolean pageIsAvailable(String url) {
		boolean isAvailable = false;
		HttpURLConnection httpConnection = null;
		try {
			httpConnection = (HttpURLConnection) new URL(url).openConnection();
			httpConnection.setRequestProperty("User-Agent", ""); 
			httpConnection.connect();
			String responseCode = (new Integer(httpConnection.getResponseCode())).toString();
			System.out.println("URL: " + url + " - Code: " + responseCode);
			if (!responseCode.startsWith("2") && !responseCode.startsWith("3")) {
				isAvailable = false;
			} else {
				isAvailable = true;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
		}
		return isAvailable;
	}
}
