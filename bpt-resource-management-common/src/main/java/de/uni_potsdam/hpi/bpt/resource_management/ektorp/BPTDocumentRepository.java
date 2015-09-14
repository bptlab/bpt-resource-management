package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.ektorp.AttachmentInputStream;
import org.ektorp.DocumentNotFoundException;

import com.github.ldriscoll.ektorplucene.CouchDbRepositorySupportWithLucene;

/**
 * Provides querying methods based on CouchDB views for documents and their attachments.
 * Provides methods for CRUD operations based on java.util.Map - may be used directly by front-end.
 * 
 * @author tw
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class BPTDocumentRepository extends CouchDbRepositorySupportWithLucene<Map> {
	
	protected String tableName;
	
	/**
	 * Constructor.
	 * 
     * @param table the name of the database to connect to
     * 
     */
	public BPTDocumentRepository(String tableName) {
		super(Map.class, BPTDatabase.connect(tableName));
		this.tableName = tableName;
        initStandardDesignDocument();
	}
	
	/**
	 * Creates a new document in the database.
	 * 
     * @param document java.util.Map containing the attributes and their values to be stored
     * @return id of the stored document
     * 
     */
	public String createDocument(Map<String, Object> document) {
		
		Map<String, Object> databaseDocument = new HashMap<String, Object>();
		String _id;
		String[] keys = BPTDocumentType.getDocumentKeys(BPTDocumentType.valueOf(tableName.toUpperCase()));
		
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

	/**
	 * Fetches an existing document from the database.
	 * 
     * @param _id id of the document to be fetched
     * @return database document as java.util.Map
     * 
     */
	public Map<String, Object> readDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		return databaseDocument;
	}
	
	/**
	 * Updates an existing document in the database.
	 * 
     * @param java.util.Map with updated values
     * @return updated database document as java.util.Map
     * 
     */
	public Map<String, Object> updateDocument(Map<String, Object> document) {
		Map<String, Object> databaseDocument = db.get(Map.class, (String)document.get("_id"));
		String[] keys = BPTDocumentType.getDocumentKeys(BPTDocumentType.valueOf(tableName.toUpperCase()));
		
		for (String key : keys) {
			if(!(document.get(key) == null)) databaseDocument.put(key, document.get(key));
		}
		
		db.update(databaseDocument);
		return databaseDocument;
	}
	
	/**
	 * Marks a document as deleted. The document remains in the database.
	 * 
     * @param _id id of the document to be deleted
     * @return deleted database document as java.util.Map
     * 
     */
	public Map<String, Object> deleteDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("deleted", true);
		db.update(databaseDocument);
		return databaseDocument;
	}
	
	/**
	 * Adds an attachment to an existing document.
	 * 
	 * @param _id id of the document to which the attachment is added
	 * @param _rev revision of the document to which the attachment is added
	 * @param attachmentId name of the new attachment, must be unique per document
	 * @param file attachment
	 * @param contentType MIME type of the file
	 * @return new revision of the document
	 */
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
	 * Fetches an existing attachment from the database.
	 * 
	 * @param _id id of the document where the attachment is stored
	 * @param attachmentId name of the attachment
	 * @return file as java.io.InputStream, stream must be closed after usage!
	 */
	public AttachmentInputStream readAttachment(String _id, String attachmentId) {
		AttachmentInputStream inputStream = new AttachmentInputStream("null", null, "image/jpeg"); // default initialization
		try {
			inputStream = db.getAttachment(_id, attachmentId);
		} catch (DocumentNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return inputStream;
	}
	
	/**
	 * Deletes the attachment of a document.
	 * 
	 * @param _id id of the document where the attachment is stored
	 * @param _rev revision of the document where the attachment is stored
	 * @param attachmentId name of the attachment
	 * @return new revision of the document
	 */
	public String deleteAttachment(String _id, String _rev, String attachmentId) {
		String revision = new String();
		revision = db.deleteAttachment(_id, _rev, attachmentId);
		return revision;
	}
	
	/**
	 * Generates a document from an array of values that are in a certain order.
	 * The order is defined in the enum BPTDocumentType.
	 * 
	 * @param values values of the document
	 * @return document as Map<String, Object>
	 */
	public Map<String, Object> generateDocument(Object[] values) {
		Map<String, Object> document = new HashMap<String, Object>();
		String[] keys = BPTDocumentType.getDocumentKeys(BPTDocumentType.valueOf(tableName.toUpperCase()));
		for(int i = 0; i < keys.length; i++) {
			document.put(keys[i], values[i]);
		}
		return document;
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
	
	/**
	 * Returns the public database address.
	 * 
	 * @return the public database address
	 */
	public String getDatabaseAddress() {
		return "http://" + BPTDatabase.getHost() + ":" + BPTDatabase.getPort() + "/";
	}
	
	/**
	 * Returns the name of the database table.
	 * 
	 * @return name of the database table
	 */
	public String getTableName() {
		return tableName;
	}
}
