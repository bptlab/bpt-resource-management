package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.View;
import org.ektorp.support.Views;

public class BPTToolRepository extends BPTDocumentRepository {
	
	private List<Map> tableEntries = new ArrayList<Map>();

	public BPTToolRepository() {
		super("bpt_resources_tools");
	}
	
	/**
	* @return the number of database documents that are not marked as deleted
	*
	*/
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

	@Views({
		@View(
			name = "all_documents",
			map = "function(doc) { if (!doc.deleted) emit(doc._id, doc); }"
		),
		@View(
			name = "published_documents",
			map = "function(doc) { if (!doc.deleted && doc.status == 'Published') emit(doc._id, doc); }"
		),
		@View(
			name = "unpublished_documents",
			map = "function(doc) { if (!doc.deleted && doc.status == 'Unpublished') emit(doc._id, doc); }"
		),
		@View(
			name = "rejected_documents",
			map = "function(doc) { if (!doc.deleted && doc.status == 'Rejected') emit(doc._id, doc); }"
		)
	})
	public List<Map> getDocuments(String status) {
		ViewQuery query = createQuery(status + "_documents");
		List<Map> result = db.queryView(query, Map.class);      
		return result;
	}
	
	@Override
	protected Map<String, Object> setDefaultValues(Map<String, Object> databaseDocument) {
		databaseDocument.put("status", BPTToolStatus.Unpublished);
		databaseDocument.put("deleted", false);
		return databaseDocument;
	}
	
	public Map<String, Object> publishDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("status", BPTToolStatus.Published);
		db.update(databaseDocument);
		return databaseDocument;
	}

	public Map<String, Object> unpublishDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("status", BPTToolStatus.Unpublished);
		db.update(databaseDocument);
		return databaseDocument;
	}

	public Map<String, Object> rejectDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("status", BPTToolStatus.Rejected);
		db.update(databaseDocument);
		return databaseDocument;
	}

	public BPTToolStatus getDocumentStatus(String _id){
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		return BPTToolStatus.valueOf((String) databaseDocument.get("status"));
	}
	
	public Boolean containsName(String Name){
		List<Map> Docs = getAll();
		for (int i = 0; i < Docs.size(); i++){
        	if (Name.equals(Docs.get(i).get("name"))) {
        		return true;
        	}
		}
		return false;
	}
	
	public ArrayList<Map> getVisibleEntries(List<BPTToolStatus> states, ArrayList<String> tags){
		tableEntries.clear();
		for (BPTToolStatus status : states) {
			tableEntries.addAll(getDocuments(status.toString().toLowerCase()));
		}
		ArrayList<Map> newEntries = new ArrayList<Map>();
		String[] tagAttributes = new String[] {"availabilities", "model_types", "platforms", "supported_functionalities"};
		for (Map<String, Object> entry : tableEntries) {
			if (containsAllTags(entry, tags, tagAttributes)) {
				newEntries.add(entry);
			}
		}
		return newEntries;
	}

	private boolean containsAllTags(Map entry, ArrayList<String> tags, String[] tagAttributes) {
		ArrayList<String> entryAsArrayList = new ArrayList<String>();
		for (String propertyId : tagAttributes) {
			System.out.println(propertyId);
			String property = entry.get(propertyId).toString();
			String cutProperty = property.substring(1, property.length() -1);
			List<String> attributeTags = Arrays.asList(cutProperty.split("\\s*,\\s*"));
			System.out.println("attribut: " + attributeTags);
			for(int i = 0; i < attributeTags.size(); i++){
				entryAsArrayList.add(attributeTags.get(i));
				System.out.println("all entry tags: " + entryAsArrayList);
			}
		}
		for (int i = 0; i < tags.size(); i++){
			if (!entryAsArrayList.contains(tags.get(i))) return false;
		}
		return true;
	}
	
	// TODO: should not get all documents when refreshing
	public void refreshData(){
		tableEntries = getDocuments("all");
	}

}
