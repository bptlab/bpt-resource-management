package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;

@View(
	name = "all_documents", 
	map = "function(doc) { if (doc.type == 'BPTTool') emit(doc._id, doc); }"
	)
public class BPTDocumentRepository extends CouchDbRepositorySupport<Map> {

	private CouchDbConnector database;
	
	public BPTDocumentRepository(CouchDbConnector database) {
        super(Map.class, database);
        this.database = database;
        initStandardDesignDocument();
	}
	
	@View(
		name = "number_of_documents", 
		map = "function(doc) { emit(\"count\", 1); }",
		reduce = "function(key, values, rereduce) { var count = 0; values.forEach(function(v) { count += 1; }); return count; }"
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
	
	public String createDocument(String type, ArrayList<Object> values) {
		
		Map<String, Object> document = new HashMap<String, Object>();
		String[] keys = BPTDocumentTypes.visibleColumns(type);
		String id;
		
		document.put("type", type);
		document.put("date_created", new Date());
		document.put("last_update", new Date());
		document.put("published", false);
		document.put("rejected", false);
		document.put("deleted", false);
		
		for (int i = 0; i < keys.length; i++) {
			document.put(keys[i], values.get(i));
		}
		
		id = nextAvailableId().toString();
		
		database.create(id, document);
		return id;
	}
	
	private Integer nextAvailableId() {
		
		List<String> allDocIdsString = database.getAllDocIds();
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

	public void readDocument(String id) {
		Map<String, Object> document = database.get(Map.class, id);
	}
	
	public void deleteDocument(String id) {
		Map<String, Object> document = database.get(Map.class, id);
		if (document.get(type) != null) {
			document.put("deleted", true);
			database.update(document);
		}
		
	}

}
