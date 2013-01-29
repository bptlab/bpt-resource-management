package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BPTUserRepository extends BPTDocumentRepository {
	
	public BPTUserRepository() {
		super("bpt_resources_users");
	}
	
	@Override
	protected Map<String, Object> setDefaultValues(Map<String, Object> databaseDocument) {
		databaseDocument.put("is_moderator", false);
		return databaseDocument;
	}
	
	public boolean isModerator(String username, String mailAddress) {
		List<Map> users = getAll();
		for(Map<String, Object> user : users) {
			if (username.equals((String)user.get("name")) && mailAddress.equals((String)user.get("mail_address"))) {
				return (Boolean)user.get("is_moderator");
			}
		}
		createDocument(generateDocument(new Object[] {username, mailAddress}));
		return false;
	}
	
	private Map<String, Object> generateDocument(Object[] values) {
		Map<String, Object> document = new HashMap<String, Object>();
		String[] keys = BPTDocumentTypes.getDocumentKeys("bpt_resources_users");
		for(int i = 0; i < keys.length; i++) {
			document.put(keys[i], values[i]);
		}
		return document;
	}
	
}
