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
//		if (type.equals("bpt_resources_tools")) {
//			return new String[] {
//				"name", "description", "description_url", "provider", "provider_url", 
//				"download_url", "documentation_url", "screencast_url", "tutorial_url", 
//				"availabilities", "model_types", "platforms", "supported_functionalities", 
//				"contact_name", "contact_mail", "user_id", "date_created", "last_update", 
//				"notification_date"
//			};
//		}
//		if (type.equals("bpt_resources_users")) {
//			return new String[] {
//				"_id", "name", "mail_address"
//			};
//		}
		if (type.equals("bpmai_exercises")) {
			return new String[] {
					"set_id", "title", "language", "description", 
					"topics", "model_types", "task_types", "other_tags",
					"contact_name", "contact_mail", "user_id",
					"date_created", "last_update",
					"notification_date"
			};
		}
		if (type.equals("bpmai_users")) {
			return new String[] {
				"_id", "name", "mail_address"
			};
		}
		return null;
	}
	
//	/**
//	 * Returns document type specific attributes.
//	 * 
//	 * @param type the document type
//	 * @return attributes used for storing URLs as a String array
//	 * 
//	 */
//	public static String[] getDocumentKeysStoringURLs(String type) {
//		if (type.equals("bpt_resources_tools")) {
//			return new String[] {
//				"description_url", "provider_url", "download_url", 
//				"documentation_url", "screencast_url", "tutorial_url"
//			};
//		}
//		return null;
//	}
}
