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
 * public String createDocument(String type, Map<String, Object> document)
 * public Map<String, Object> readDocument(String _id)
 * public Map<String, Object> updateDocument(Map<String, Object> document)
 * public Map<String, Object> deleteDocument(String _id)
 * 
 * public String createAttachment(String _id, String _rev, String attachmentId, File file, String contentType)
 * public AttachmentInputStream readAttachment(String _id, String attachmentId)
 * public String deleteAttachment(String _id, String _rev, String attachmentId)
 * 
 * @author tw
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
//public abstract class BPTDocumentRepository extends CouchDbRepositorySupport<Map> {
public abstract class BPTDocumentRepository extends CouchDbRepositorySupportWithLucene<Map> {
	
	protected String tableName;
	
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
	 * Creates a new document in the database.
	 * 
     * @param document java.util.Map containing the attributes and their values to be stored
     * @return id of the stored document
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
	 * 
	 * @param _id id of the document where the attachment has to be added to
	 * @param _rev trevision of the document where the attachment has to be added to
	 * @param attachmentId name of the new attachment - must be unique
	 * @param file attachment as file
	 * @param contentType content type of the file - also known as MIME type
	 * @return new revision of the document after adding the attachment
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
	 * @return file as java.io.InputStream - has to be closed after usage!
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
	 * @return new revision of the document after deleting the attachment
	 */
	public String deleteAttachment(String _id, String _rev, String attachmentId) {
		String revision = new String();
		revision = db.deleteAttachment(_id, _rev, attachmentId);
		return revision;
	}
	
	protected Integer nextAvailableId() {
		
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
