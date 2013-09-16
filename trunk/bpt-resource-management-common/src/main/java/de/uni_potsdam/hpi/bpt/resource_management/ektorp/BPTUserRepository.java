package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ektorp.ViewQuery;
import org.ektorp.support.View;

/**
 * 
 * Provides access to CouchDB's store for Tools for BPM users.
 * There are two roles of users - moderator and resource provider. 
 * A resource provider may submit and update his own entries.
 * In addition, a moderator may change the status of an entry or delete an entry.
 * 
 * @see de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentRepository
 * 
 * @author tw
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BPTUserRepository extends BPTDocumentRepository {
	
	private static BPTUserRepository instance = null;
	
	public BPTUserRepository() {
		super("bpt_resources_users");
	}
	
	public static BPTUserRepository getInstance() {
		if (instance == null) {
                instance = new BPTUserRepository();
            }		
		return instance;
	}
	
	public static boolean instanceIsCleared() {
		return instance == null;
	}
	
	public static void clearInstance() {
		instance = null;
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

	/**
	 * 
	 * @param _id OpenID of the user
	 * @return database document containing information about the user as java.util.Map
	 * 
	 */
	public Map<String, Object> getUser(String _id) {
		Map<String, Object> user = db.get(Map.class, _id);
		return user;
	}

	@Override
	public String createDocument(Map<String, Object> document) {
		String _id = (String) document.get("_id");
		document = setDefaultValues(document);
		db.create(_id, document);
		return _id;
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
		boolean isModerator = false;
		createDocument(generateDocument(new Object[] {_id, isModerator, name, mailAddress}));
		return isModerator;
	}
	
}
