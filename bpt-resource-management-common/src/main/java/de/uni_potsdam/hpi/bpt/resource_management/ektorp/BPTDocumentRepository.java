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
public abstract class BPTDocumentRepository extends CouchDbRepositorySupport<Map> {
	
	protected String tableName;
	private List<Map> tableEntries = new ArrayList<Map>();
	
	/**
     * @param table the name of the database to connect to
     * 
     */
	public BPTDocumentRepository(String tableName) {
		super(Map.class, BPTDatabase.connect(tableName));
		this.tableName = tableName;
        initStandardDesignDocument();
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
	public String createDocument(Map<String, Object> document) {
		
		Map<String, Object> databaseDocument = new HashMap<String, Object>();
		String _id;
		String[] keys = BPTDocumentTypes.getDocumentKeys(tableName);
		
		databaseDocument = setDefaultValues(databaseDocument);
		
		for (String key : keys) {
			databaseDocument.put(key, document.get(key));
		}
		
		_id = nextAvailableId().toString();
		
		db.create(_id, databaseDocument);
		return _id;
	}

	protected Map<String, Object> setDefaultValues(Map<String, Object> databaseDocument) {
		return databaseDocument;
	}

	public InputStream readAttachment(String _id, String attachmentId) {
		AttachmentInputStream inputStream = new AttachmentInputStream("null", null, "image/jpeg"); // default initialization
		try {
			inputStream = db.getAttachment(_id, attachmentId);
		} catch (DocumentNotFoundException e) {
			e.printStackTrace();
			return null;
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
		String[] keys = BPTDocumentTypes.getDocumentKeys(tableName);
		
		for (String key : keys) {
			if(!(document.get(key) == null)) databaseDocument.put(key, document.get(key));
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
	
	public String getDatabaseAddress() {
		return "http://" + BPTDatabase.getHost() + ":" + BPTDatabase.getPort() + "/";
	}
	
	public String getTableName() {
		return tableName;
	}
}
