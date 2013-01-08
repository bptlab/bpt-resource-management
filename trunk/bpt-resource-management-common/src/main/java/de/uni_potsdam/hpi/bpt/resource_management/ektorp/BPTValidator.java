package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;

public class BPTValidator {
	
	private static final String[] schemes = new String[] {"http","https","ftp"};
	private static UrlValidator urlValidator = new UrlValidator(schemes);
	private static EmailValidator emailValidator = EmailValidator.getInstance();

	public static boolean isValidURL(String url) {
		return urlValidator.isValid(url);
	}
	
	public static boolean isValidEmail(String email) {
		return emailValidator.isValid(email);
	}
	
}
