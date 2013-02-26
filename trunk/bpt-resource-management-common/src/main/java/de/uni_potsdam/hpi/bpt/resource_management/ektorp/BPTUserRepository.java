package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ektorp.ViewQuery;
import org.ektorp.support.View;

public class BPTUserRepository extends BPTDocumentRepository {
	
	public BPTUserRepository() {
		super("bpt_resources_users");
	}
	
	@View(
			name = "moderators", 
			map = "function(doc) { if (doc.is_moderator) emit(doc._id, doc); }"
	)
	public List<Map> getModerators() {
		ViewQuery query = new ViewQuery()
							  .designDocId("_design/Map")
							  .viewName("moderators");
		List<Map> result = db.queryView(query, Map.class);
		return result;
	}
	
	@View(
			name = "resource_providers", 
			map = "function(doc) { if (!doc.is_moderator) emit(doc._id, doc); }"
	)
	public List<Map> getResourceProviders() {
		ViewQuery query = new ViewQuery()
							  .designDocId("_design/Map")
							  .viewName("resource_providers");
		List<Map> result = db.queryView(query, Map.class);
		return result;
	}
	
	@Override
	protected Map<String, Object> setDefaultValues(Map<String, Object> databaseDocument) {
		databaseDocument.put("is_moderator", false);
		return databaseDocument;
	}
	
	public boolean isModerator(String _id, String name, String mailAddress) {
		List<Map> users = getAll();
		for(Map<String, Object> user : users) {
			if (_id.equals((String)user.get("_id"))) {
				return (Boolean)user.get("is_moderator");
			}
		}
		createDocument(generateDocument(new Object[] {_id, name, mailAddress}));
		return false;
	}
	
	@Override
	public String createDocument(Map<String, Object> document) {
		String _id = (String) document.get("_id");
		document = setDefaultValues(document);
		db.create(_id, document);
		return _id;
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
