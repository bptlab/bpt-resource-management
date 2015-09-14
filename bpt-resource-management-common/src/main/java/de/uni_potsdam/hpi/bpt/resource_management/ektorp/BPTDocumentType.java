package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

/**
 * Defines the attributes of all available documents.
 *
 * @author tw
 *
 */
public enum BPTDocumentType {
	
	BPT_RESOURCES_TOOLS, BPT_RESOURCES_USERS;
	
	/**
	 * Returns the attributes that are specific to the given document type.
	 *
	 * @param type document type
	 * @return attributes of the document type in a specific order
	 * 
	 */
	public static String[] getDocumentKeys(BPTDocumentType type) {
		switch (type) {
			case BPT_RESOURCES_TOOLS : return new String[] {
				"name", "description", "description_url", "provider", "provider_url", 
				"download_url", "documentation_url", "screencast_url", "tutorial_url", 
				"availabilities", "model_types", "platforms", "supported_functionalities", 
				"contact_name", "contact_mail", "user_id", "date_created", "last_update", 
				"notification_date", "number_of_url_validation_fails", "number_of_mails_for_expiry",
				"name_lowercase", "provider_lowercase"
			};
			case BPT_RESOURCES_USERS : return new String[] {
				"_id", "is_moderator", "name", "mail_address"
			};
			default : return null;
		}	
	}
	/**
	 * Returns the attributes that store URLs and that are specific to the given document type.
	 * 
	 * @param type the document type
	 * @return attributes used for storing URLs in a specific order
	 * 
	 */
	public static String[] getDocumentKeysStoringURLs(BPTDocumentType type) {
		switch (type) {
			case BPT_RESOURCES_TOOLS : return new String[] {
				"description_url", "provider_url", "download_url", 
				"documentation_url", "screencast_url", "tutorial_url"
			};
			default : return null;
		}
	}
}
