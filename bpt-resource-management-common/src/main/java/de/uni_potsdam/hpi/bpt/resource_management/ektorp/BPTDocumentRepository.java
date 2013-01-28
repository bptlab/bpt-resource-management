package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.ektorp.AttachmentInputStream;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.ektorp.support.Views;

/**
 * Provides querying methods based on CouchDB views.
 * Provides methods for CRUD operations based on java.util.Map - may be directly used by front-end.
 * 
 * public int numberOfDocuments()
 * public String createDocument(String type, Map<String, Object> document)
 * public Map<String, Object> readDocument(String _id)
 * public Map<String, Object> updateDocument(Map<String, Object> document)
 * public Map<String, Object> deleteDocument(String _id)
 *
 * @author tw
 *
 */
public class BPTDocumentRepository extends CouchDbRepositorySupport<Map> {
	
	private List<Map> tableEntries;
	private String tableName;
	
	/**
     * @param table the name of the database to connect to
     * 
     */
	public BPTDocumentRepository(String tableName) {
		super(Map.class, BPTDatabase.connect(tableName));
		this.tableName = tableName;
        initStandardDesignDocument();
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
			map = "function(doc) { if (doc.type == 'BPTTool' && !doc.deleted) emit(doc._id, doc); }"
	    	), 
	    @View(
	       	name = "published_documents", 
	    	map = "function(doc) { if (doc.type == 'BPTTool' && !doc.deleted && doc.status == 'Published') emit(doc._id, doc); }"
	       	), 
	   	@View(
	   		name = "unpublished_documents", 
	    	map = "function(doc) { if (doc.type == 'BPTTool' && !doc.deleted && doc.status == 'Unublished') emit(doc._id, doc); }"
	    	), 
	    @View(
	    	name = "rejected_documents", 
	    	map = "function(doc) { if (doc.type == 'BPTTool' && !doc.deleted && doc.status == 'Rejected') emit(doc._id, doc); }"
	    	)
	    })
	public List<Map> getDocuments(String status) {
		ViewQuery query = createQuery(status + "_documents");
		List<Map> result = db.queryView(query, Map.class);	
		return result;
	}
	
	public String createAttachment(String _id, String _rev, String attachmentId, File file, String contentType) {
		String revision = new String();
		
		try {
			InputStream inputStream = new FileInputStream(file);
			AttachmentInputStream attachmentStream = new AttachmentInputStream(attachmentId, inputStream, contentType);
			revision = db.createAttachment(_id, _rev, attachmentStream);
			inputStream.close();
			attachmentStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return revision;
	}
	
	/**
	 * Creates a new document in the database.
	 * 
     * @param type the type of the document to be stored
     * @param document java.util.Map containing the attributes and their values to be stored
     * @return the id of the stored document
     * 
     */
	public String createDocument(String type, Map<String, Object> document) {
		
		Map<String, Object> databaseDocument = new HashMap<String, Object>();
		String[] keys = BPTDocumentTypes.getDocumentKeys(type);
		String _id;
		
		databaseDocument.put("type", type);
		databaseDocument.put("status", BPTDocumentStatus.Unpublished);
		databaseDocument.put("deleted", false);
		
		for (String key : keys) {
			databaseDocument.put(key, document.get(key));
		}
		
		_id = nextAvailableId().toString();
		
		db.create(_id, databaseDocument);
		return _id;
	}

	public InputStream readAttachment(String _id, String attachmentId) {
		AttachmentInputStream inputStream = new AttachmentInputStream("null", null, "image/jpeg"); // default initialization
		try {
			inputStream = db.getAttachment(_id, attachmentId);
		} catch (DocumentNotFoundException e) {
			e.printStackTrace();
		}
		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputStream;
	}

	/**
	 * Fetches an existing document in the database.
	 * 
     * @param _id the id of the document to be fetched
     * @return database document as java.util.Map
     * 
     */
	public Map<String, Object> readDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		return databaseDocument;
	}
	
	/**
	 * Fetches an existing document in the database.
	 * 
     * @param java.util.Map with updated values
     * @return updated database document as java.util.Map
     * 
     */
	public Map<String, Object> updateDocument(Map<String, Object> document) {
		Map<String, Object> databaseDocument = db.get(Map.class, (String)document.get("_id"));
		String[] keys = BPTDocumentTypes.getDocumentKeys((String)document.get("type"));
		
		for (String key : keys) {
			databaseDocument.put(key, document.get(key));
		}
		
		db.update(databaseDocument);
		return databaseDocument;
	}
	
	/**
	 * Deletes a document by marking it as deleted but keeping it in the database.
	 * 
     * @param _id the id of the document to be deleted
     * @return deleted database document as java.util.Map
     * 
     */
	public Map<String, Object> deleteDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("deleted", true);
		db.update(databaseDocument);
		return databaseDocument;
	}
	
	
	public Map<String, Object> publishDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("status", BPTDocumentStatus.Published);
		db.update(databaseDocument);
		return databaseDocument;
	}
	
	public Map<String, Object> unpublishDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("status", BPTDocumentStatus.Unpublished);
		db.update(databaseDocument);
		return databaseDocument;
	}
	
	public Map<String, Object> rejectDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("status", BPTDocumentStatus.Rejected);
		db.update(databaseDocument);
		return databaseDocument;
	}
	
	public BPTDocumentStatus getDocumentStatus(String _id){
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		return BPTDocumentStatus.valueOf((String) databaseDocument.get("status"));
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
	public Boolean containsName(String Name){
		List<Map> Docs = getAll();
		for (int i = 0; i < Docs.size(); i++){
			if(Name.equals(Docs.get(i).get("name"))) return true;
		}
		return false;
	};
	public ArrayList<Map> getVisibleEntries(List<BPTDocumentStatus> states, ArrayList<String> tags){
		for (BPTDocumentStatus status : states) {
			tableEntries.addAll(getDocuments(status.toString().toLowerCase()));
		}
		ArrayList<Map> newEntries = new ArrayList<Map>();
		String[] tagAttributes = new String[] {"availabilities", "model_types", "platforms", "supported_functionalities"};
		for (Map<String, Object> entry : tableEntries){
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
	
	public void refreshData(){
		tableEntries = getAll();
	}
	
	public String getDatabaseAddress() {
		return "http://" + BPTDatabase.getHost() + ":" + BPTDatabase.getPort() + "/";
	}
	
	public String getTableName() {
		return tableName;
	}
}
