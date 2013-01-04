package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

public class BPTDocumentTypes {

	public static String[] visibleColumns(String type) {
		
		if (type.equals("BPTTool")) {
			return new String[] {
				"name", "description", "provider", 
				"download_url", "documentation_url", "screencast_url", 
				"availabilities", "model_types", "platforms", "supported_functionalities", 
				"contact_name", "contact_mail"
			};
		}
		
		return null;
	}
}
