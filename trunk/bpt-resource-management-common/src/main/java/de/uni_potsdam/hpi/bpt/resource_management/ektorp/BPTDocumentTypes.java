package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

/**
 * Class to define document type specific attributes.
 * 
 * public static String[] getDocumentKeys(String type)
 *
 * @author tw
 *
 */
public class BPTDocumentTypes {
	
	/**
	 * Returns document type specific attributes.
	 * 
	 * @param type the document type
	 * @return attributes of the document type as a String array
	 * 
	 */
	public static String[] getDocumentKeys(String type) {
		if (type.equals("bpt_resources_tools")) {
			return new String[] {
				"name", "description", "provider", 
				"download_url", "documentation_url", "screencast_url", 
				"availabilities", "model_types", "platforms", "supported_functionalities", 
				"contact_name", "contact_mail", "date_created", "last_update"
			};
		}
		if (type.equals("bpt_resources_users")) {
			return new String[] {
				"name", "mail_address"
			};
		}		
		return null;
	}
}
