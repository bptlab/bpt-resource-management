package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import java.util.*;

import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;

@View(
	name = "all_documents", 
	map = "function(doc) { if (doc.type == 'BPTTool') emit(doc._id, doc); }"
	)
public class BPTDocumentRepository extends CouchDbRepositorySupport<Map> {
	
	public BPTDocumentRepository(String table) {
		super(Map.class, BPTDatabase.connect(table));
        initStandardDesignDocument();
	}
	
	@View(
		name = "number_of_documents", 
		map = "function(doc) { if (!doc.deleted) emit(\"count\", 1); }",
		reduce = "function(key, values, rereduce) { var count = 0; values.forEach(function(v) { count += 1; }); return count; }"
		/* NOTE: deleted documents will not be counted here */
		)
	public int numberOfDocuments() {
		ViewQuery query = createQuery("number_of_documents");
		ViewResult result = db.queryView(query);
		try {
			return result.getRows().get(0).getValueAsInt();
		} catch (IndexOutOfBoundsException e) {
			return 0;
		}
	}
	
	public String createDocument(String type, Map<String, Object> document) {
		
		Map<String, Object> databaseDocument = new HashMap<String, Object>();
		String[] keys = BPTDocumentTypes.getDocumentKeys(type);
		String _id;
		
		databaseDocument.put("type", type);
		databaseDocument.put("published", false);
		databaseDocument.put("rejected", false);
		databaseDocument.put("deleted", false);
		
		for (String key : keys) {
			databaseDocument.put(key, document.get(key));
		}
		
		databaseDocument.put("date_created", new Date());
		databaseDocument.put("last_update", new Date());
		
		_id = nextAvailableId().toString();
		
		db.create(_id, databaseDocument);
		return _id;
	}

	public Map<String, Object> readDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		return databaseDocument;
	}
	
	public Map<String, Object> updateDocument(Map<String, Object> document) {
		Map<String, Object> databaseDocument = db.get(Map.class, (String)document.get("_id"));
		String[] keys = BPTDocumentTypes.getDocumentKeys((String)document.get("type"));
		
		for (String key : keys) {
			databaseDocument.put(key, document.get(key));
		}
		
		db.update(databaseDocument);
		return databaseDocument;
	}
	
	public Map<String, Object> deleteDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("deleted", true);
		db.update(databaseDocument);
		return databaseDocument;
	}
	
	private Integer nextAvailableId() {
		
		List<String> allDocIdsString = db.getAllDocIds();
		List<Integer> allDocIdsConverted = new ArrayList<Integer>();
		int value, highestId;
		
		for(String docId : allDocIdsString) {
			try {
			    value = Integer.parseInt(docId);
			} catch (NumberFormatException e) {
			    continue;
			}
			allDocIdsConverted.add(value); 
		}
		
		try {
			highestId = Collections.max(allDocIdsConverted);
		} catch (NoSuchElementException e) {
		    highestId = 0;
		};
		
		return highestId + 1;
	}

}
