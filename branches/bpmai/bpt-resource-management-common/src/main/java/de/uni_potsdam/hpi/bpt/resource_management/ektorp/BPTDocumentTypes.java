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
//		if (type.equals("bpmai_exercises")) {
//			return new String[] {
//					"set_id", "title", "language", "description", 
//					"topics", "modeling_languages", "task_types", "other_tags",
//					"exercise_url",
//					"contact_name", "contact_mail", "user_id",
//					"date_created", "last_update",
//					"notification_date", "names_of_attachments",
//					"number_of_url_validation_fails", "number_of_mails_for_expiry",
//					"title_lowercase"
//			};
//		}
		if (type.equals("bpmai_exercises")) {
			return new String[] {
					"set_id", "title", "language", "description",
					"exercise_url", "names_of_supplementary_files",
					"name_of_pdf_file", "name_of_doc_file"
			};
		}
		if (type.equals("bpmai_exercise_sets")) {
			return new String[] {
					"set_id", "languages", 
					"topics", "modeling_languages", "task_types", "other_tags",
					"contact_name", "contact_mail", "user_id",
					"date_created", "last_update"
//					"notification_date",
//					"number_of_url_validation_fails", "number_of_mails_for_expiry"
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
	public static String[] getDocumentKeysStoringURLs(String type) {
		if (type.equals("bpmai_exercises")) {
			return new String[] {
				"exercise_url"
			};
		}
		return null;
	}
}
