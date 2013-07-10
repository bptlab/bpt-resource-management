package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.type.TypeReference;
import org.ektorp.DbAccessException;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.View;
import org.ektorp.support.Views;

import com.github.ldriscoll.ektorplucene.CustomLuceneResult;
import com.github.ldriscoll.ektorplucene.LuceneQuery;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.FullText;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.Index;

import de.uni_potsdam.hpi.bpt.resource_management.mail.BPTMailProvider;

/**
 * 
 * Provides access to CouchDB's store for Tools for BPM entries
 * 
 * @see de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentRepository
 * 
 * @author tw
 * @author bu
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BPTToolRepository extends BPTDocumentRepository {
	
	private static BPTToolRepository instance = null;
	private List<Map> tableEntries = new ArrayList<Map>();
	private BPTMailProvider mailProvider = BPTMailProvider.getInstance();
	
	public BPTToolRepository() {
		super("bpt_resources_tools");
		disableMailProvider();
	}
	
	public static BPTToolRepository getInstance() {
		if (instance == null) {
                instance = new BPTToolRepository();
            }		
		return instance;
	}
	
	public static boolean instanceIsCleared() {
		return instance == null;
	}
	
	public static void clearInstance() {
		instance = null;
	}
	
	@Override
	public String createDocument(Map<String, Object> document) {
		String documentId = super.createDocument(document);
		mailProvider.sendEmailForNewEntry((String)document.get("name"), documentId, (String)document.get("user_id"));
		return documentId;
	}
	
	@Override
	public Map<String, Object> updateDocument(Map<String, Object> document) {
		Map<String, Object> databaseDocument = super.updateDocument(document);
		if (BPTToolStatus.valueOf((String) databaseDocument.get("status")) != BPTToolStatus.Unpublished) {
			mailProvider.sendEmailForUpdatedEntry((String)databaseDocument.get("name"), (String)databaseDocument.get("_id"), (String)databaseDocument.get("user_id"));
		}
		return databaseDocument;
	}
	
	/**
	 * Deletes a document by marking it as deleted but keeping it in the database.
	 * 
     * @param _id id of the document to be deleted
	 * @param byModerator true if the request for deletion originates from the moderator
     * @return deleted database document as java.util.Map
     * 
     */
	public Map<String, Object> deleteDocument(String _id, boolean byModerator) {
		Map<String, Object> databaseDocument = super.deleteDocument(_id);
		if (byModerator) {
			mailProvider.sendEmailForDeletedEntryToResourceProvider((String)databaseDocument.get("name"), (String)databaseDocument.get("user_id"));
		} else {
			mailProvider.sendEmailForDeletedEntryToModerator((String)databaseDocument.get("name"), _id, (String)databaseDocument.get("user_id"));
		}
		
		return databaseDocument;
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
	
	/**
	 * Fetches the documents by status.
	 * 
	 * @param status String value - possible values: all, published, unpublished, rejected
	 * @return list of entries as database documents
	 */
	@Views({
	    @View(
	    	name = "all_tools", 
			map = "function(doc) { if (!doc.deleted) emit(doc._id, doc); }"
	    	), 
	    @View(
	       	name = "published_tools", 
	    	map = "function(doc) { if (!doc.deleted && doc.status == 'Published') emit(doc._id, doc); }"
	       	), 
	   	@View(
	   		name = "unpublished_tools", 
	    	map = "function(doc) { if (!doc.deleted && doc.status == 'Unpublished') emit(doc._id, doc); }"
	    	), 
	    @View(
	    	name = "rejected_tools", 
	    	map = "function(doc) { if (!doc.deleted && doc.status == 'Rejected') emit(doc._id, doc); }"
	    	)
	    })
	public List<Map> getDocuments(String status) {
		ViewQuery query = new ViewQuery()
							.designDocId("_design/Map")
							.viewName(status + "_tools");
		List<Map> result = db.queryView(query, Map.class);	
		return result;
	}
	
	/**
	 * Fetches the documents of an user.
	 * 
	 * @param user OpenID of the user
	 * @return list of entries as database documents
	 */
	@View(
			name = "tools_by_user_id", 
			map = "function(doc) { if (!doc.deleted) emit(doc.user_id, doc); }"
	)
	public List<Map> getDocumentsByUser(String user) {
		ViewQuery query = new ViewQuery()
							  .designDocId("_design/Map")
							  .viewName("tools_by_user_id")
							  .key(user);
		List<Map> result = db.queryView(query, Map.class);
		return result;
	}
	
	/**
	 * 
	 * Full text search in all entries that are stored in CouchDB.
	 * Uses Apache Lucene via couchdb-lucene.
	 * 
	 * @param queryString handled by Lucene
	 * @return list of entries matching on the query
	 */
	@FullText({
	    @Index(
	        name = "fullSearch",
	        index = "function(doc) { " +
	                    "var res = new Document(); " +
	                    "res.add(doc.name); " + 
	                    "res.add(doc.description); " + 
	                    "res.add(doc.provider); " + 
	                    "res.add(doc.download_url); " + 
	                    "res.add(doc.documentation_url); " + 
	                    "res.add(doc.screencast_url); " + 
	                    "res.add(doc.contact_name); " +
//	                    "res.add(doc._id, {field: \"_id\", store: \"yes\"} ); " +
//	                    "res.add(doc.name, {field: \"name\", store: \"yes\"} ); " +
	                    "return res; " +
	                "}")
	})
	private List<Map> fullSearch(String queryString) {
		LuceneQuery query = new LuceneQuery("Map", "fullSearch");
		query.setStaleOk(false);
		query.setQuery(queryString);
		query.setIncludeDocs(true);
		
		TypeReference resultDocType = new TypeReference<CustomLuceneResult<Map>>() {};
		try {
			CustomLuceneResult<Map> luceneResult = db.queryLucene(query, resultDocType);
	        List<CustomLuceneResult.Row<Map>> luceneResultRows = luceneResult.getRows();
	        
	        List<Map> result = new ArrayList<Map>();
	        for (CustomLuceneResult.Row<Map> row : luceneResultRows) {
	            result.add(row.getDoc());
	        }
	        return result;
		} catch (DbAccessException e) {
			return new ArrayList<Map>();
		}
	}
	
	@Override
	protected Map<String, Object> setDefaultValues(Map<String, Object> databaseDocument) {
		databaseDocument.put("status", BPTToolStatus.Unpublished);
		databaseDocument.put("deleted", false);
		databaseDocument.put("number_of_url_validation_fails", 0);
		databaseDocument.put("number_of_mails_for_expiry", 0);
		return databaseDocument;
	}
	
	public Map<String, Object> publishDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("status", BPTToolStatus.Published);
		db.update(databaseDocument);
		mailProvider.sendEmailForPublishedEntry((String)databaseDocument.get("name"), (String)databaseDocument.get("user_id"));
		return databaseDocument;
	}
	
	private Map<String, Object> unpublishDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("status", BPTToolStatus.Unpublished);
		db.update(databaseDocument);
		return databaseDocument;
	}
	
	/**
	 * 
	 * @param _id id of the document to be unpublished
	 * @param fromPublished true if it was published, false if it was rejected
	 * @return entry with its status updated
	 */
	public Map<String, Object> unpublishDocument(String _id, boolean fromPublished) {
		Map<String, Object> databaseDocument;
		if (fromPublished) { // byModerator = true by default
			databaseDocument = unpublishDocument(_id, fromPublished, true);
		} else { 
			databaseDocument = unpublishDocument(_id);
			mailProvider.sendEmailForUnpublishedEntryFromRejected((String)databaseDocument.get("name"), (String)databaseDocument.get("user_id"));
		}
		return databaseDocument;
	}
	
	/**
	 * 
	 * @param _id id of the document to be unpublished
	 * @param fromPublished true if it was published, false if it was rejected
	 * @param byModerator true if the request for unpublish originates from the moderator
	 * @return entry with its status updated
	 */
	public Map<String, Object> unpublishDocument(String _id, boolean fromPublished, boolean byModerator) {
		Map<String, Object> databaseDocument = unpublishDocument(_id);
		if (fromPublished) { 
			if (byModerator) {
				mailProvider.sendEmailForUnpublishedEntryFromPublishedToResourceProvider((String)databaseDocument.get("name"), (String)databaseDocument.get("user_id"));
			} else {
				mailProvider.sendEmailForUnpublishedEntryFromPublishedToModerator((String)databaseDocument.get("name"), _id, (String)databaseDocument.get("user_id"));
			}
		} else { // propose (by moderator if he has previously unpublished an entry by mistake, notify resource provider)
			mailProvider.sendEmailForUnpublishedEntryFromRejected((String)databaseDocument.get("name"), (String)databaseDocument.get("user_id"));
		}
		return databaseDocument;
	}
	
	public Map<String, Object> rejectDocument(String _id, String reason) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("status", BPTToolStatus.Rejected);
		db.update(databaseDocument);
		mailProvider.sendEmailForRejectedEntry((String)databaseDocument.get("name"), (String)databaseDocument.get("user_id"), reason);
		return databaseDocument;
	}
	
	public BPTToolStatus getDocumentStatus(String _id){
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		return BPTToolStatus.valueOf((String) databaseDocument.get("status"));
	}
	
	/**
	 * Checks if an entry with the given name (not id) exists in CouchDB.
	 * 
	 * @param name name of the entry
	 * @return true if a document with the name exists in the database
	 */
	public Boolean containsName(String name){
		List<Map> documents = getDocuments("all");
		for (int i = 0; i < documents.size(); i++) {
			if(name.equals(documents.get(i).get("name"))) return true;
		}
		return false;
	};
	
	/**
	 * Composite search in entries that are not deleted.
	 * 
	 * @param states search applies to the given states only
	 * @param tags tags that the entries shall contain
	 * @param query full text search query handled by Lucene
	 * @return list of entries with the given states matching on tag search and full text search
	 */
	public List<Map> getVisibleEntries(List<BPTToolStatus> states, ArrayList<String> tags, String query) {
		tableEntries.clear();
		for (BPTToolStatus status : states) {
			for (Map<String, Object> document : getDocuments(status.toString().toLowerCase())) {
				tableEntries.add(document);
			}
		}
		List<Map> newEntries = new ArrayList<Map>();
		String[] tagAttributes = new String[] {"availabilities", "model_types", "platforms", "supported_functionalities"};
		for (Map<String, Object> entry : tableEntries){
			if (containsAllTags(entry, tags, tagAttributes)) { 
				newEntries.add(entry);
			}
		}
		/* TODO: workflow ... 
		 * should find all entries by full search first
		 * and then check if they are in matching state or contain the right tags
		 */
		if (query != null) {
			if (!query.isEmpty()) {
				List<Map> entriesByFullSearch = fullSearch(query);
				newEntries.retainAll(entriesByFullSearch);
			}
		}
		return newEntries;
	}
	
	/**
	 * Composite search in entries of an user.
	 * 
	 * @param user id of the user
	 * @param tags tags that the entries shall contain
	 * @param query full text search query handled by Lucene
	 * @return list of entries of the user matching on tag search and full text search
	 */
	public List<Map> getVisibleEntriesByUser(String user, ArrayList<String> tags, String query) {
		tableEntries = getDocumentsByUser(user);
		List<Map> newEntries = new ArrayList<Map>();
		String[] tagAttributes = new String[] {"availabilities", "model_types", "platforms", "supported_functionalities"};
		for (Map<String, Object> entry : tableEntries){
			if (containsAllTags(entry, tags, tagAttributes)) {
				newEntries.add(entry);
			}
		}
		if (query != null) {
			if (!query.isEmpty()) {
				List<Map> entriesByFullSearch = fullSearch(query);
				newEntries.retainAll(entriesByFullSearch);
			}
		}
		return newEntries;
	}

	private boolean containsAllTags(Map entry, ArrayList<String> tags, String[] tagAttributes) {
		ArrayList<String> entryAsArrayList = new ArrayList<String>();
		for (String propertyId : tagAttributes) {
//			System.out.println(propertyId);
			String property = entry.get(propertyId).toString();
			String cutProperty = property.substring(1, property.length() -1);
			List<String> attributeTags = Arrays.asList(cutProperty.split("\\s*,\\s*"));
//			System.out.println("attribut: " + attributeTags);
			for(int i = 0; i < attributeTags.size(); i++){
				entryAsArrayList.add(attributeTags.get(i));
//				System.out.println("all entry tags: " + entryAsArrayList);
			}
		}
		for (int i = 0; i < tags.size(); i++){
			if (!entryAsArrayList.contains(tags.get(i))) return false;
		}
		return true;
	}

	public boolean isMailProviderEnabled() {
		return mailProvider.isEnabled();
	}

	public void enableMailProvider() {
		mailProvider.setEnabled(true);
	}
	
	public void disableMailProvider() {
		mailProvider.setEnabled(false);
	}
	
	// TODO: should not get all documents when refreshing
	public void refreshData() {
		tableEntries = getDocuments("all");
	}
	
	/**
	 * Full text search in all entries that are stored in CouchDB.
	 * Uses Apache Lucene via couchdb-lucene.
	 * 
	 * @param queryString handled by Lucene
	 * @param skip the number of entries to skip (offset)
	 * @param limit the maximum number of entries to return
	 * @param sortString attribute used for sorting - example: /name for ascending sort by name, 
	 * \provider for descending sort by provider, /last_update<date> for ascending sort by last update. 
	 * See BPTDocumentTypes for the exact name of an attribute.
	 * @return list of entries matching on the query
	 */
	@FullText({
	    @Index(
	        name = "fullSearch2",
	        index = "function(doc) { " +
	                    "var res = new Document(); " +
	                    "res.add(doc.name); " + 
	                    "res.add(doc.description); " + 
	                    "res.add(doc.provider); " + 
	                    "res.add(doc.download_url); " + 
	                    "res.add(doc.documentation_url); " + 
	                    "res.add(doc.screencast_url); " + 
	                    "res.add(doc.contact_name); " +
//	                    "res.add(doc._id, {field: \"_id\", store: \"yes\"} ); " +
//	                    "res.add(doc.name, {field: \"name\", store: \"yes\"} ); " +
	                    "return res; " +
	                "}")
	})
	public List<Map> fullSearch(String queryString, int skip, int limit, String sortString) {
		LuceneQuery query = new LuceneQuery("Map", "fullSearch2");
		query.setStaleOk(false);
		if (queryString != null) {
			query.setQuery(queryString);			
		}
		query.setSkip(skip);
		query.setLimit(limit);
		query.setSort(sortString);
		query.setIncludeDocs(true);
		
		TypeReference resultDocType = new TypeReference<CustomLuceneResult<Map>>() {};
		try {
			CustomLuceneResult<Map> luceneResult = db.queryLucene(query, resultDocType);
	        List<CustomLuceneResult.Row<Map>> luceneResultRows = luceneResult.getRows();
	        
	        List<Map> result = new ArrayList<Map>();
	        for (CustomLuceneResult.Row<Map> row : luceneResultRows) {
	            result.add(row.getDoc());
	        }
	        return result;
		} catch (DbAccessException e) {
			return new ArrayList<Map>();
		}
	}

}