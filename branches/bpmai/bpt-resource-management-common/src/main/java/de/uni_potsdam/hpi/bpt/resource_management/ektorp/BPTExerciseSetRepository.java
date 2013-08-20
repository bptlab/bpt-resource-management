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
public class BPTExerciseSetRepository extends BPTDocumentRepository {
	
	private static BPTExerciseSetRepository instance = null;
	private List<Map> tableEntries = new ArrayList<Map>();
	private BPTExerciseRepository exerciseRepository = BPTExerciseRepository.getInstance();
	private BPTMailProvider mailProvider = BPTMailProvider.getInstance();
	
	public BPTExerciseSetRepository() {
		super("bpmai_exercise_sets");
		disableMailProvider();
	}
	
	public static BPTExerciseSetRepository getInstance() {
		if (instance == null) {
                instance = new BPTExerciseSetRepository();
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
	 * Fetches next available exercise set identifier by topic.
	 * 
	 * @param topicName primary topic of the exercise set
	 * @return next available set_id
	 */
	@Views({
		@View(
			name = "next_available_set_id_foundbpm", 
			map = "function(doc) { if (doc.set_id.substring(0, 'FoundBPM'.length) === 'FoundBPM') emit(null, doc.set_id.substring('FoundBPM'.length, doc.set_id.length)); }",
			reduce = "function (key, values, rereduce) { var max = 0; for(var i = 0; i < values.length; i++) { max = Math.max(values[i], max); } return max; }"
			), 
		@View(
			name = "next_available_set_id_foundpm", 
			map = "function(doc) { if (doc.set_id.substring(0, 'FoundPM'.length) === 'FoundPM') emit(null, doc.set_id.substring('FoundPM'.length, doc.set_id.length)); }",
			reduce = "function (key, values, rereduce) { var max = 0; for(var i = 0; i < values.length; i++) { max = Math.max(values[i], max); } return max; }"
			),
		@View(
			name = "next_available_set_id_pmodlang", 
			map = "function(doc) { if (doc.set_id.substring(0, 'PModLang'.length) === 'PModLang') emit(null, doc.set_id.substring('PModLang'.length, doc.set_id.length)); }",
			reduce = "function (key, values, rereduce) { var max = 0; for(var i = 0; i < values.length; i++) { max = Math.max(values[i], max); } return max; }"
			),
		@View(
			name = "next_available_set_id_pchor", 
			map = "function(doc) { if (doc.set_id.substring(0, 'PChor'.length) === 'PChor') emit(null, doc.set_id.substring('PChor'.length, doc.set_id.length)); }",
			reduce = "function (key, values, rereduce) { var max = 0; for(var i = 0; i < values.length; i++) { max = Math.max(values[i], max); } return max; }"
			),
		@View(
			name = "next_available_set_id_dataproc", 
			map = "function(doc) { if (doc.set_id.substring(0, 'DataProc'.length) === 'DataProc') emit(null, doc.set_id.substring('DataProc'.length, doc.set_id.length)); }",
			reduce = "function (key, values, rereduce) { var max = 0; for(var i = 0; i < values.length; i++) { max = Math.max(values[i], max); } return max; }"
			), 
		@View(
			name = "next_available_set_id_panalys", 
			map = "function(doc) { if (doc.set_id.substring(0, 'PAnalys'.length) === 'PAnalys') emit(null, doc.set_id.substring('PAnalys'.length, doc.set_id.length)); }",
			reduce = "function (key, values, rereduce) { var max = 0; for(var i = 0; i < values.length; i++) { max = Math.max(values[i], max); } return max; }"
			),
		@View(
			name = "next_available_set_id_pmining", 
			map = "function(doc) { if (doc.set_id.substring(0, 'PMining'.length) === 'PMining') emit(null, doc.set_id.substring('PMining'.length, doc.set_id.length)); }",
			reduce = "function (key, values, rereduce) { var max = 0; for(var i = 0; i < values.length; i++) { max = Math.max(values[i], max); } return max; }"
			),
		@View(
			name = "next_available_set_id_pabstr", 
			map = "function(doc) { if (doc.set_id.substring(0, 'PAbstr'.length) === 'PAbstr') emit(null, doc.set_id.substring('PAbstr'.length, doc.set_id.length)); }",
			reduce = "function (key, values, rereduce) { var max = 0; for(var i = 0; i < values.length; i++) { max = Math.max(values[i], max); } return max; }"
			),
		@View(
			name = "next_available_set_id_pflex", 
			map = "function(doc) { if (doc.set_id.substring(0, 'PFlex'.length) === 'PFlex') emit(null, doc.set_id.substring('PFlex'.length, doc.set_id.length)); }",
			reduce = "function (key, values, rereduce) { var max = 0; for(var i = 0; i < values.length; i++) { max = Math.max(values[i], max); } return max; }"
			), 
		@View(
			name = "next_available_set_id_bpmarch", 
			map = "function(doc) { if (doc.set_id.substring(0, 'BPMArch'.length) === 'BPMArch') emit(null, doc.set_id.substring('BPMArch'.length, doc.set_id.length)); }",
			reduce = "function (key, values, rereduce) { var max = 0; for(var i = 0; i < values.length; i++) { max = Math.max(values[i], max); } return max; }"
			),
		@View(
			name = "next_available_set_id_bpmmethd", 
			map = "function(doc) { if (doc.set_id.substring(0, 'BPMMethd'.length) === 'BPMMethd') emit(null, doc.set_id.substring('BPMMethd'.length, doc.set_id.length)); }",
			reduce = "function (key, values, rereduce) { var max = 0; for(var i = 0; i < values.length; i++) { max = Math.max(values[i], max); } return max; }"
			)
	    })
	public String nextAvailableSetId(BPTTopic topicName) {
		ViewQuery query = createQuery("next_available_set_id_" + topicName.toString().toLowerCase());
		ViewResult result = db.queryView(query);
		try {
			return topicName.toString() + new Integer(result.getRows().get(0).getValueAsInt() + 1).toString();
		} catch (IndexOutOfBoundsException e) {
			return topicName.toString() + "1";
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
	
// TODO: How to count number of exercises that are distinct by set_id?
//	/**
//     * @return the number of exercise sets that are not marked as deleted
//     * 
//     */
//	@View(
//		name = "number_of_exercise_sets", 
//		map = "function(doc) { if (!doc.deleted) emit(\"count\", 1); }",
//		reduce = "function(key, values, rereduce) { var count = 0; values.forEach(function(v) { count += 1; }); return count; }"
//		/* NOTE: deleted documents will not be counted here */
//		)
//	public int numberOfExerciseSets() {
//		ViewQuery query = createQuery("number_of_exercise_sets");
//		ViewResult result = db.queryView(query);
//		try {
//			return result.getRows().get(0).getValueAsInt();
//		} catch (IndexOutOfBoundsException e) {
//			return 0;
//		}
//	}
	
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
	
//	/**
//	 * Fetches the documents by language.
//	 * 
//	 * @param language language of the entries
//	 * @return list of entries as database documents
//	 */
//	@View(
//			name = "entries_by_language", 
//			map = "function(doc) { emit(doc.language, doc); }"
//	)
//	public List<Map> getDocumentsByLanguage(String language) {
//		ViewQuery query = new ViewQuery()
//							  .designDocId("_design/Map")
//							  .viewName("entries_by_language")
//							  .key(language);
//		List<Map> result = db.queryView(query, Map.class);
//		return result;
//	}
	
//	/**
//	 * Fetches the documents by exercise set identifier.
//	 * 
//	 * @param set_id exercise set identifier
//	 * @return list of entries as database documents
//	 */
//	@View(
//			name = "entries_by_set_id", 
//			map = "function(doc) { emit(doc.set_id, doc); }"
//	)
//	public List<Map> getDocumentsBySetId(String set_id) {
//		ViewQuery query = new ViewQuery()
//							  .designDocId("_design/Map")
//							  .viewName("entries_by_set_id")
//							  .key(set_id);
//		List<Map> result = db.queryView(query, Map.class);
//		return result;
//	}
	
	@Override
	protected Map<String, Object> setDefaultValues(Map<String, Object> databaseDocument) {
		databaseDocument.put("status", BPTExerciseStatus.Published);
		databaseDocument.put("deleted", false);
		databaseDocument.put("number_of_url_validation_fails", 0);
		databaseDocument.put("number_of_mails_for_expiry", 0);
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
	
	public Map<String, Object> rejectDocument(String _id, String reason) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("status", BPTExerciseStatus.Rejected);
		db.update(databaseDocument);
//		mailProvider.sendEmailForRejectedEntry((String)databaseDocument.get("name"), (String)databaseDocument.get("user_id"), reason);
		return databaseDocument;
	}
	
	public BPTExerciseStatus getDocumentStatus(String _id){
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		return BPTExerciseStatus.valueOf((String) databaseDocument.get("status"));
	}
	
	/**
	 * Checks if an entry with the given title (not id) exists in CouchDB.
	 * 
	 * @param title tile of the entry
	 * @return true if a document with the title exists in the database
	 */
	public boolean contains(String title){
		List<Map> documents = getDocuments("all");
		for (int i = 0; i < documents.size(); i++) {
			if(title.equals(documents.get(i).get("title"))) return true;
		}
		return false;
	};
	
//	/**
//	 * Composite search in entries that are not deleted.
//	 * 
//	 * @param states search applies to the given states only
//	 * @param tags tags that the entries shall contain
//	 * @param query full text search query handled by Lucene
//	 * @return list of entries with the given states matching on tag search and full text search
//	 */
//	public List<Map> getVisibleEntries(List<BPTExerciseStatus> states, ArrayList<String> tags, String query) {
//		tableEntries.clear();
//		for (BPTExerciseStatus status : states) {
//			for (Map<String, Object> document : getDocuments(status.toString().toLowerCase())) {
//				tableEntries.add(document);
//			}
//		}
//		List<Map> newEntries = new ArrayList<Map>();
//		String[] tagAttributes = new String[] {"topics", "modeling_languages", "task_types", "other_tags"};
//		for (Map<String, Object> entry : tableEntries) {
//			if (containsAllTags(entry, tags, tagAttributes)) { 
//				newEntries.add(entry);
//			}
//		}
//		/* TODO: workflow ... 
//		 * should find all entries by full search first
//		 * and then check if they are in matching state or contain the right tags
//		 */
//		if (query != null && !query.isEmpty()) {
//			List<Map> entriesByFullSearch = fullSearch(query);
//			newEntries.retainAll(entriesByFullSearch);
//		}
//		return newEntries;
//	}
//	
//	/**
//	 * Composite search in entries that are not deleted.
//	 * 
//	 * @param language language of the entries
//	 * @param states search applies to the given states only
//	 * @param tags tags that the entries shall contain
//	 * @param query full text search query handled by Lucene
//	 * @return list of entries with the given states matching on tag search and full text search
//	 */
//	public List<Map> getVisibleEntries(String language, List<BPTExerciseStatus> states, ArrayList<String> tags, String query) {
//		List<Map> entries = getVisibleEntries(states, tags, query);
//		for (Map<String, Object> entry : entries) {
//			if (!((String) entry.get("language")).equals(language)) { 
//				entries.remove(entry);
//			}
//		}
//		return entries;
//	}
//	
//	/**
//	 * Composite search in entries of an user.
//	 * 
//	 * @param user id of the user
//	 * @param tags tags that the entries shall contain
//	 * @param query full text search query handled by Lucene
//	 * @return list of entries of the user matching on tag search and full text search
//	 */
//	public List<Map> getVisibleEntriesByUser(String user, ArrayList<String> tags, String query) {
//		tableEntries = getDocumentsByUser(user);
//		List<Map> newEntries = new ArrayList<Map>();
//		String[] tagAttributes = new String[] {"topics", "model_types", "task_types", "other_tags"};
//		for (Map<String, Object> entry : tableEntries){
//			if (containsAllTags(entry, tags, tagAttributes)) {
//				newEntries.add(entry);
//			}
//		}
//		if (query != null && !query.isEmpty()) {
//			List<Map> entriesByFullSearch = fullSearch(query);
//			newEntries.retainAll(entriesByFullSearch);
//		}
//		return newEntries;
//	}
//	
//	/**
//	 * Composite search in entries of an user.
//	 * 
//	 * @param language language of the entries
//	 * @param user id of the user
//	 * @param tags tags that the entries shall contain
//	 * @param query full text search query handled by Lucene
//	 * @return list of entries of the user matching on tag search and full text search
//	 */
//	public List<Map> getVisibleEntriesByUser(String language, String user, ArrayList<String> tags, String query) {
//		List<Map> entries = getVisibleEntriesByUser(user, tags, query);
//		for (Map<String, Object> entry : entries) {
//			if (!((String) entry.get("language")).equals(language)) { 
//				entries.remove(entry);
//			}
//		}
//		return entries;
//	}

//	private boolean containsAllTags(Map entry, ArrayList<String> tags, String[] tagAttributes) {
//		ArrayList<String> entryAsArrayList = new ArrayList<String>();
//		for (String propertyId : tagAttributes) {
////			System.out.println(propertyId);
//			String property = entry.get(propertyId).toString();
//			String cutProperty = property.substring(1, property.length() -1);
//			List<String> attributeTags = Arrays.asList(cutProperty.split("\\s*,\\s*"));
////			System.out.println("attribut: " + attributeTags);
//			for(int i = 0; i < attributeTags.size(); i++){
//				entryAsArrayList.add(attributeTags.get(i));
////				System.out.println("all entry tags: " + entryAsArrayList);
//			}
//		}
//		for (int i = 0; i < tags.size(); i++){
//			if (!entryAsArrayList.contains(tags.get(i))) return false;
//		}
//		return true;
//	}

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
	
//	/**
//	 * Returns the total number of entries based on the search using Apache Lucene via couchdb-lucene.
//	 * 
//	 * @param language language of the entries
//	 * @param statusList list of document status
//	 * @param userId OpenID of resource provider
//	 * @param fullTextSearchString string from full text search
//	 * @param topicTags topic tags as list
//	 * @param modelingLanguageTags modeling language tags as list
//	 * @param taskTypeTags task type tags as list
//	 * @param otherTags other tags as list
//	 * @return number of entries matching the given values
//	 */
//	public int getNumberOfEntries(String language, List<BPTExerciseStatus> statusList, String userId, String fullTextSearchString, List<String> topicTags, List<String> modelingLanguageTags, List<String> taskTypeTags, List<String> otherTags) {
//		String queryString = buildQueryString(language, statusList, userId, fullTextSearchString, topicTags, modelingLanguageTags, taskTypeTags, otherTags);
//		if (queryString.isEmpty()) {
//			return 0;
//		}
//		LuceneQuery query = new LuceneQuery("Map", "search");
//		query.setStaleOk(false);
//		query.setIncludeDocs(true);
//		query.setQuery(queryString);
//		return search(query).size();
//	}
	
	/**
	 * Search in all entries that are stored in CouchDB.
	 * If no document status is provided, the returned list of entries is empty.
	 * Uses Apache Lucene via couchdb-lucene.
	 * 
	 * @param language language of the entries
	 * @param statusList list of document status
	 * @param userId OpenID of resource provider
	 * @param fullTextSearchString string from full text search
	 * @param topicTags topic tags as list
	 * @param modelingLanguageTags modeling language tags as list
	 * @param taskTypeTags task type tags as list
	 * @param otherTags other tags as list
	 * @param skip the number of entries to skip (offset)
	 * @param limit the maximum number of entries to return
	 * @param sortAttribute attribute used for sorting
	 * @param ascending true if ascending sort of attribute
	 * @return entries matching the given values
	 */
	public List<String> search(List<String> languages, List<BPTExerciseStatus> statusList, 
			String userId, String fullTextSearchString, 
			List<String> topicTags, List<String> modelingLanguageTags, 
			List<String> taskTypeTags, List<String> otherTags, 
			int skip, int limit, 
			String sortAttribute, boolean ascending) {
		String queryString = buildQueryString(languages, statusList, userId, topicTags, modelingLanguageTags, taskTypeTags, otherTags);
		if (queryString.isEmpty()) {
			return new ArrayList<String>();
		}
		LuceneQuery query = new LuceneQuery("Map", "search");
		query.setStaleOk(false);
		query.setIncludeDocs(true);
		query.setQuery(queryString);
		if (skip >= 0) {
			query.setSkip(skip);
		}
		if (limit > 0) {
			query.setLimit(limit);
		}
		if (sortAttribute != null && !sortAttribute.isEmpty()) {
			StringBuffer sbSort = new StringBuffer();
			sbSort.append(ascending ? "/" : "\\");
			sbSort.append(sortAttribute);
			if (sortAttribute.equals("date_created") || sortAttribute.equals("last_update")) {
				sbSort.append("<date>");
			}
			query.setSort(sbSort.toString());
		}
		
		ArrayList<Map> exerciseSets = search(query);
		ArrayList<String> result = new ArrayList<String>();
		for (Map exerciseSet : exerciseSets) {
			String setId = (String) exerciseSet.get("set_id");
			if (fullTextSearchString != null && !fullTextSearchString.isEmpty() && !exerciseRepository.search(setId, null, fullTextSearchString).isEmpty()) {
				result.add(setId);
			}
		}
		return result;
	}
	
	private String buildQueryString(List<String> languages, List<BPTExerciseStatus> statusList,
			String userId, 
			List<String> topicTags, List<String> modelingLanguageTags,
			List<String> taskTypeTags, List<String> otherTags) {
		
		List<String> statusContent = new ArrayList<String>();
		if (statusList != null && !statusList.isEmpty()) {
			for (BPTExerciseStatus status : statusList) {
				statusContent.add("status:\"" + status + "\"");
			}
		} else {
			return new String();
		}
		Iterator<String> statusIterator = statusContent.iterator();
		StringBuffer sbStatus = new StringBuffer();
		sbStatus.append("(");
		while (statusIterator.hasNext()) {
			sbStatus.append(statusIterator.next());
			if (statusIterator.hasNext()) {
				sbStatus.append(" OR ");
			}
		}
		sbStatus.append(")");
		
		List<String> queryContent = new ArrayList<String>();
		// only entries that are not deleted
		queryContent.add("deleted:\"false\"");
		if (userId != null && !userId.isEmpty()) {
			queryContent.add("user_id:\"" + userId + "\"");
		}
		if (languages != null) {
			for (String language : languages) {
				queryContent.add("languages:\"" + language + "\"");
			}
		}
		if (topicTags != null) {
			for (String tag : topicTags) {
				queryContent.add("topics:\"" + tag + "\"");
			}
		}
		if (modelingLanguageTags != null) {
			for (String tag : modelingLanguageTags) {
				queryContent.add("modeling_languages:\"" + tag + "\"");
			}
		}
		if (taskTypeTags != null) {
			for (String tag : taskTypeTags) {
				queryContent.add("task_types:\"" + tag + "\"");
			}
		}
		if (otherTags != null) {
			for (String tag : otherTags) {
				queryContent.add("other_tags:\"" + tag + "\"");
			}
		}
		Iterator<String> queryContentIterator = queryContent.iterator();
		StringBuffer sbQuery = new StringBuffer();
		while (queryContentIterator.hasNext()) {
			sbQuery.append(queryContentIterator.next());
			sbQuery.append(" AND ");
		}
		return sbQuery.toString();
	}

	@FullText({
	    @Index(
	        name = "search",
	        analyzer = "snowball:German",
	        index = "function(doc) { " +
	                    "var res = new Document(); " +
	                    "for (var i in doc.languages) { res.add(doc.languages[i], {\"field\": \"languages\"}); }" +
	                    "for (var i in doc.topics) { res.add(doc.topics[i], {\"field\": \"topics\"}); }" +
	                    "for (var i in doc.modeling_languages) { res.add(doc.modeling_languages[i], {\"field\": \"modeling_languages\"}); }" +
	                    "for (var i in doc.task_types) { res.add(doc.task_types[i], {\"field\": \"task_types\"}); }" +
	                    "for (var i in doc.other_tags) { res.add(doc.other_tags[i], {\"field\": \"other_tags\"}); }" +
	                    "res.add(doc.status, {\"field\": \"status\"});" +
	                    "res.add(doc.deleted, {\"field\": \"deleted\"});" +
	                    "res.add(doc.date_created, {\"field\": \"date_created\", \"type\": \"date\"});" + 
	                    "res.add(doc.last_update, {\"field\": \"last_update\", \"type\": \"date\"});" +
	                    "res.add(doc.user_id, {\"field\": \"user_id\"});" + 
	                    "return res; " +
	                "}")
	})
	private ArrayList<Map> search(LuceneQuery query) {
		
		TypeReference resultDocType = new TypeReference<CustomLuceneResult<Map>>() {};
		try {
			CustomLuceneResult<Map> luceneResult = db.queryLucene(query, resultDocType);
	        List<CustomLuceneResult.Row<Map>> luceneResultRows = luceneResult.getRows();
	        
	        ArrayList<Map> result = new ArrayList<Map>();
	        for (CustomLuceneResult.Row<Map> row : luceneResultRows) {
	            result.add(row.getDoc());
	        }
	        return result;
		} catch (DbAccessException e) {
			return new ArrayList<Map>();
		}
	}

}