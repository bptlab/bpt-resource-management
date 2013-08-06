package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
 * Provides access to CouchDB's store for BPMAI entries
 * 
 * @see de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentRepository
 * 
 * @author tw
 * @author bu
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BPTExerciseRepository extends BPTDocumentRepository {
	
	private static BPTExerciseRepository instance = null;
	private List<Map> tableEntries = new ArrayList<Map>();
//	private BPTMailProvider mailProvider = BPTMailProvider.getInstance();
	
	public BPTExerciseRepository() {
		super("bpmai_exercises");
	}
	
	public static BPTExerciseRepository getInstance() {
		if (instance == null) {
                instance = new BPTExerciseRepository();
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
//		mailProvider.sendEmailForNewEntry((String)document.get("name"), documentId, (String)document.get("user_id"));
		return documentId;
	}
	
	/**
	 * Fetches next available exercise set identifier.
	 * 
	 * @return next available set_id
	 */
	@View(
		name = "next_available_set_id", 
		map = "function(doc) { emit(null, doc.set_id); }",
		reduce = "function (key, values, rereduce) { var max = 0; for(var i = 0; i < values.length; i++) { max = Math.max(values[i], max); } return max; }"
		)
	public String nextAvailableSetId() {
		ViewQuery query = createQuery("next_available_set_id");
		ViewResult result = db.queryView(query);
		try {
			return new Integer(result.getRows().get(0).getValueAsInt() + 1).toString();
		} catch (IndexOutOfBoundsException e) {
			return "1";
		}
	}
	
	@Override
	public Map<String, Object> updateDocument(Map<String, Object> document) {
		Map<String, Object> databaseDocument = super.updateDocument(document);
//		if (BPTEntryStatus.valueOf((String) databaseDocument.get("status")) != BPTEntryStatus.Unpublished
//				&& document.get("notification_url") == null) {
//			mailProvider.sendEmailForUpdatedEntry((String)document.get("name"), (String)document.get("_id"), (String)document.get("user_id"));
//		}
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
//		if (byModerator) {
//			mailProvider.sendEmailForDeletedEntryToResourceProvider((String)databaseDocument.get("name"), (String)databaseDocument.get("user_id"));
//		} else {
//			mailProvider.sendEmailForDeletedEntryToModerator((String)databaseDocument.get("name"), _id, (String)databaseDocument.get("user_id"));
//		}
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
	    	name = "all_entries", 
			map = "function(doc) { if (!doc.deleted) emit(doc._id, doc); }"
	    	), 
	    @View(
	       	name = "published_entries", 
	    	map = "function(doc) { if (!doc.deleted && doc.status == 'Published') emit(doc._id, doc); }"
	       	), 
	   	@View(
	   		name = "unpublished_entries", 
	    	map = "function(doc) { if (!doc.deleted && doc.status == 'Unpublished') emit(doc._id, doc); }"
	    	), 
	    @View(
	    	name = "rejected_entries", 
	    	map = "function(doc) { if (!doc.deleted && doc.status == 'Rejected') emit(doc._id, doc); }"
	    	)
	    })
	public List<Map> getDocuments(String status) {
		ViewQuery query = new ViewQuery()
							.designDocId("_design/Map")
							.viewName(status + "_entries");
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
			name = "entries_by_user_id", 
			map = "function(doc) { emit(doc.user_id, doc); }"
	)
	public List<Map> getDocumentsByUser(String user) {
		ViewQuery query = new ViewQuery()
							  .designDocId("_design/Map")
							  .viewName("entries_by_user_id")
							  .key(user);
		List<Map> result = db.queryView(query, Map.class);
		return result;
	}
	
	/**
	 * Fetches the documents by language.
	 * 
	 * @param language language of the entries
	 * @return list of entries as database documents
	 */
	@View(
			name = "entries_by_user_id", 
			map = "function(doc) { emit(doc.language, doc); }"
	)
	public List<Map> getDocumentsByLanguage(String language) {
		ViewQuery query = new ViewQuery()
							  .designDocId("_design/Map")
							  .viewName("entries_by_user_id")
							  .key(language);
		List<Map> result = db.queryView(query, Map.class);
		return result;
	}
	
	/**
	 * Fetches the documents by exercise set identifier.
	 * 
	 * @param set_id exercise set identifier
	 * @return list of entries as database documents
	 */
	@View(
			name = "entries_by_set_id", 
			map = "function(doc) { emit(doc.set_id, doc); }"
	)
	public List<Map> getDocumentsBySetId(String set_id) {
		ViewQuery query = new ViewQuery()
							  .designDocId("_design/Map")
							  .viewName("entries_by_set_id")
							  .key(set_id);
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
	                    "res.add(doc.title); " + 
	                    "res.add(doc.description); " + 
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
        CustomLuceneResult<Map> luceneResult = db.queryLucene(query, resultDocType);
        List<CustomLuceneResult.Row<Map>> luceneResultRows = luceneResult.getRows();
        
        List<Map> result = new ArrayList<Map>();
        for (CustomLuceneResult.Row<Map> row : luceneResultRows) {
            result.add(row.getDoc());
        }
        return result;
	}
	
	@Override
	protected Map<String, Object> setDefaultValues(Map<String, Object> databaseDocument) {
		databaseDocument.put("status", BPTExerciseStatus.Unpublished);
		databaseDocument.put("deleted", false);
		return databaseDocument;
	}
	
	public Map<String, Object> publishDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("status", BPTExerciseStatus.Published);
		db.update(databaseDocument);
//		mailProvider.sendEmailForPublishedEntry((String)databaseDocument.get("name"), (String)databaseDocument.get("user_id"));
		return databaseDocument;
	}
	
	private Map<String, Object> unpublishDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("status", BPTExerciseStatus.Unpublished);
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
//			mailProvider.sendEmailForUnpublishedEntryFromRejected((String)databaseDocument.get("name"), (String)databaseDocument.get("user_id"));
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
//				mailProvider.sendEmailForUnpublishedEntryFromPublishedToResourceProvider((String)databaseDocument.get("name"), (String)databaseDocument.get("user_id"));
			} else {
//				mailProvider.sendEmailForUnpublishedEntryFromPublishedToModerator((String)databaseDocument.get("name"), _id, (String)databaseDocument.get("user_id"));
			}
		} else { // propose (by moderator if he has previously unpublished an entry by mistake, notify resource provider)
//			mailProvider.sendEmailForUnpublishedEntryFromRejected((String)databaseDocument.get("name"), (String)databaseDocument.get("user_id"));
		}
		return databaseDocument;
	}
	
	public Map<String, Object> rejectDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("status", BPTExerciseStatus.Rejected);
		db.update(databaseDocument);
//		mailProvider.sendEmailForRejectedEntry((String)databaseDocument.get("name"), (String)databaseDocument.get("user_id"));
		return databaseDocument;
	}
	
	public BPTExerciseStatus getDocumentStatus(String _id){
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		return BPTExerciseStatus.valueOf((String) databaseDocument.get("status"));
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
	public List<Map> getVisibleEntries(String language, List<BPTExerciseStatus> states, ArrayList<String> tags, String query) {
		tableEntries.clear();
		for (BPTExerciseStatus status : states) {
			tableEntries.addAll(getDocuments(status.toString().toLowerCase()));
		}
		
		List<Map> newEntries = new ArrayList<Map>();
		String[] tagAttributes = new String[] {"topics", "modelling_languages", "task_types", "other_tags"};
		for (Map<String, Object> entry : tableEntries) {
			if (containsAllTags(entry, tags, tagAttributes) && ((String) entry.get("language")).equals(language)) { 
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
		String[] tagAttributes = new String[] {"topics", "model_types", "task_types", "other_tags"};
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
	
	/**
	 * Composite search in entries of an user.
	 * 
	 * @param language language of the entries
	 * @param user id of the user
	 * @param tags tags that the entries shall contain
	 * @param query full text search query handled by Lucene
	 * @return list of entries of the user matching on tag search and full text search
	 */
	public List<Map> getVisibleEntriesByUser(String language, String user, ArrayList<String> tags, String query) {
		List<Map> entries = getVisibleEntriesByUser(user, tags, query);
		for (Map<String, Object> entry : entries) {
			if (!((String) entry.get("language")).equals(language)) { 
				entries.remove(entry);
			}
		}
		return entries;
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

//	public boolean isMailProviderEnabled() {
//		return mailProvider.isEnabled();
//	}

	public void enableMailProvider() {
//		mailProvider.setEnabled(true);
	}
	
	public void disableMailProvider() {
//		mailProvider.setEnabled(false);
	}
	
	// TODO: should not get all documents when refreshing
	public void refreshData() {
		tableEntries = getDocuments("all");
	}

}