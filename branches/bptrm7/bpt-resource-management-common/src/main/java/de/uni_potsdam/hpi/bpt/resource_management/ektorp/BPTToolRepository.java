package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
 * Provides access to the Tools for BPM entries that are stored in the database.
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
		if (BPTToolStatus.Rejected == BPTToolStatus.valueOf((String)readDocument((String)document.get("_id")).get("status"))) {
			document.put("status", BPTToolStatus.Unpublished);
		}
		Map<String, Object> databaseDocument = super.updateDocument(document);
//		if (BPTToolStatus.valueOf((String) databaseDocument.get("status")) != BPTToolStatus.Unpublished) {
			mailProvider.sendEmailForUpdatedEntry((String)databaseDocument.get("name"), (String)databaseDocument.get("_id"), (String)databaseDocument.get("user_id"));
//		}
		return databaseDocument;
	}
	
	/**
	 * Marks a document as deleted. The document remains in the database.
	 * Depending on the parameter byModerator, the document's resource provider or the moderators are notified.
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
	 * Queries the number of documents in the database table.
	 * 
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
	 * Does a full text search on all entries that are stored in the database.
	 * Uses Apache Lucene via couchdb-lucene.
	 * 
	 * @param queryString handled by Lucene
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
	private List<Map> fullSearch(String queryString) {
		LuceneQuery query = new LuceneQuery("Map", "fullSearch2");
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
	
	/**
	 * Publishes an entry and notifies its resource provider.
	 * 
	 * @param _id id of the entry to be published
	 * @return entry with its status published
	 */
	public Map<String, Object> publishDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("status", BPTToolStatus.Published);
		db.update(databaseDocument);
		mailProvider.sendEmailForPublishedEntry((String)databaseDocument.get("name"), _id, (String)databaseDocument.get("user_id"));
		return databaseDocument;
	}
	
	private Map<String, Object> unpublishDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("status", BPTToolStatus.Unpublished);
		db.update(databaseDocument);
		return databaseDocument;
	}
	
	/**
	 * Unpublishes an entry.
	 * Notifies its resource provider if the entry was rejected previously.
	 * 
	 * @param _id id of the entry to be unpublished
	 * @param fromPublished true if it was published, false if it was rejected
	 * @return entry with its status updated
	 */
	public Map<String, Object> unpublishDocument(String _id, boolean fromPublished) {
		Map<String, Object> databaseDocument;
		if (fromPublished) { // byModerator = true by default
			databaseDocument = unpublishDocument(_id, fromPublished, true);
		} else { 
			databaseDocument = unpublishDocument(_id);
			mailProvider.sendEmailForUnpublishedEntryFromRejected((String)databaseDocument.get("name"), _id, (String)databaseDocument.get("user_id"));
		}
		return databaseDocument;
	}
	
	/** 
	 * Unpublishes an entry.
	 * Depending on the parameter byModerator, the document's resource provider or the moderators are notified.
	 * 
	 * @param _id id of the entry to be unpublished
	 * @param fromPublished true if it was published, false if it was rejected
	 * @param byModerator true if the request for unpublish originates from the moderator
	 * @return entry with its status updated
	 */
	public Map<String, Object> unpublishDocument(String _id, boolean fromPublished, boolean byModerator) {
		Map<String, Object> databaseDocument = unpublishDocument(_id);
		if (fromPublished) { 
			if (byModerator) {
				mailProvider.sendEmailForUnpublishedEntryFromPublishedToResourceProvider((String)databaseDocument.get("name"), _id, (String)databaseDocument.get("user_id"));
			} else {
				mailProvider.sendEmailForUnpublishedEntryFromPublishedToModerator((String)databaseDocument.get("name"), _id);
			}
		} else { // propose (by moderator if he has previously unpublished an entry by mistake, notify resource provider)
			mailProvider.sendEmailForUnpublishedEntryFromRejected((String)databaseDocument.get("name"), _id, (String)databaseDocument.get("user_id"));
		}
		return databaseDocument;
	}
	
	/**
	 * Rejects an entry.
	 * 
	 * @param _id id of the entry to be rejected
	 * @param reason reason for rejection, provided by the moderator
	 * @return entry with its status rejected
	 */
	public Map<String, Object> rejectDocument(String _id, String reason) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("status", BPTToolStatus.Rejected);
		db.update(databaseDocument);
		mailProvider.sendEmailForRejectedEntry((String)databaseDocument.get("name"), _id, (String)databaseDocument.get("user_id"), reason);
		return databaseDocument;
	}
	
	/**
	 * Queries the status of an entry.
	 * 
	 * @param _id id of the entry in question
	 * @return status of the entry
	 */
	public BPTToolStatus getDocumentStatus(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		return BPTToolStatus.valueOf((String) databaseDocument.get("status"));
	}
	
	/**
	 * Checks if a document with the given name (not id) exists in CouchDB.
	 * 
	 * @param name name of the document
	 * @return true if a document with the name exists in the database
	 */
	public Boolean containsName(String name) {
		List<Map> documents = getDocuments("all");
		for (int i = 0; i < documents.size(); i++) {
			if(name.equals(documents.get(i).get("name"))) return true;
		}
		return false;
	};
	
	/**
	 * Executes a composite search in entries that are not marked as deleted.
	 * 
	 * @param states search applies to the given states only
	 * @param tags tags that the entries shall contain
	 * @param query full text search query handled by Apache Lucene
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
	 * Executes a composite search in entries of an user.
	 * 
	 * @param user id of the user
	 * @param tags tags that the entries shall contain
	 * @param query full text search query handled by Apache Lucene
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
			String property = entry.get(propertyId).toString();
			String cutProperty = property.substring(1, property.length() -1);
			List<String> attributeTags = Arrays.asList(cutProperty.split("\\s*,\\s*"));
			for(int i = 0; i < attributeTags.size(); i++){
				entryAsArrayList.add(attributeTags.get(i));
			}
		}
		for (int i = 0; i < tags.size(); i++){
			if (!entryAsArrayList.contains(tags.get(i))) return false;
		}
		return true;
	}
	
	/**
	 * Checks if the mail provider is enabled.
	 * 
	 * @return true if the mail provider is enabled
	 */
	public boolean isMailProviderEnabled() {
		return mailProvider.isEnabled();
	}
	
	/**
	 * Enables the mail provider.
	 */
	public void enableMailProvider() {
		mailProvider.setEnabled(true);
	}
	
	/**
	 * Disables the mail provider.
	 */
	public void disableMailProvider() {
		mailProvider.setEnabled(false);
	}
	
	/**
	 * Reloads the documents from the database.
	 */
	// TODO: should not get all documents when refreshing
	public void refreshData() {
		tableEntries = getDocuments("all");
	}
	
	/**
	 * Returns the total number of entries based on the search using Apache Lucene via couchdb-lucene.
	 * 
	 * @param statusList list of document status
	 * @param userId OpenID of resource provider
	 * @param fullTextSearchString string from full text search
	 * @param availabilityTags availability tags as list
	 * @param modelTypeTags model type tags as list
	 * @param platformTags platform tags as list
	 * @param supportedFunctionalityTags supported functionality tags as list
	 * @return number of entries matching the given values
	 */
	public int getNumberOfEntries(List<BPTToolStatus> statusList, String userId, String fullTextSearchString, List<String> availabilityTags, List<String> modelTypeTags, List<String> platformTags, List<String> supportedFunctionalityTags) {
		String queryString = buildQueryString(statusList, userId, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags);
		if (queryString.isEmpty()) {
			return 0;
		}
		LuceneQuery query = new LuceneQuery("Map", "search");
		query.setStaleOk(false);
		query.setIncludeDocs(true);
		query.setQuery(queryString);
		return search(query).size();
	}
	
	/**
	 * Search in all entries that are stored in CouchDB.
	 * If no document status is provided, the returned list of entries is empty.
	 * Uses Apache Lucene via couchdb-lucene.
	 * 
	 * @param statusList list of document status
	 * @param userId OpenID of resource provider
	 * @param fullTextSearchString string from full text search
	 * @param availabilityTags availability tags as list
	 * @param modelTypeTags model type tags as list
	 * @param platformTags platform tags as list
	 * @param supportedFunctionalityTags supported functionality tags as list
	 * @param skip the number of entries to skip (offset)
	 * @param limit the maximum number of entries to return
	 * @param sortAttribute attribute used for sorting
	 * @param ascending true if ascending sort of attribute
	 * @return entries matching the given values
	 */
	public List<Map> search(List<BPTToolStatus> statusList, 
			String userId, String fullTextSearchString, 
			List<String> availabilityTags, List<String> modelTypeTags, 
			List<String> platformTags, List<String> supportedFunctionalityTags, 
			int skip, int limit, 
			String sortAttribute, boolean ascending) {
		String queryString = buildQueryString(statusList, userId, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags);
		if (queryString.isEmpty()) {
			return new ArrayList<Map>();
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
		
		return search(query);
	}
	
	private String buildQueryString(List<BPTToolStatus> statusList,
			String userId, String fullTextSearchString,
			List<String> availabilityTags, List<String> modelTypeTags,
			List<String> platformTags, List<String> supportedFunctionalityTags) {
		
		List<String> statusContent = new ArrayList<String>();
		if (statusList != null && !statusList.isEmpty()) {
			for (BPTToolStatus status : statusList) {
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
		if (fullTextSearchString != null && !fullTextSearchString.isEmpty()) {
			queryContent.add(fullTextSearchString);
		}
		if (userId != null && !userId.isEmpty()) {
			queryContent.add("user_id:\"" + userId + "\"");
		}
		if (availabilityTags != null) {
			for (String tag : availabilityTags) {
				queryContent.add("availabilities:\"" + tag + "\"");
			}
		}
		if (modelTypeTags != null) {
			for (String tag : modelTypeTags) {
				queryContent.add("model_types:\"" + tag + "\"");
			}
		}
		if (platformTags != null) {
			for (String tag : platformTags) {
				queryContent.add("platforms:\"" + tag + "\"");
			}
		}
		if (supportedFunctionalityTags != null) {
			for (String tag : supportedFunctionalityTags) {
				queryContent.add("supported_functionalities:\"" + tag + "\"");
			}
		}
		Iterator<String> queryContentIterator = queryContent.iterator();
		StringBuffer sbQuery = new StringBuffer();
		while (queryContentIterator.hasNext()) {
			sbQuery.append(queryContentIterator.next());
			sbQuery.append(" AND ");
		}
		sbQuery.append(sbStatus.toString());
		return sbQuery.toString();
	}

	@FullText({
	    @Index(
	        name = "search",
	        analyzer = "snowball:German",
	        index = "function(doc) { " +
	                    "var res = new Document(); " +
	                    "res.add(doc.name_lowercase, {\"field\": \"name\", \"index\": \"not_analyzed_no_norms\", \"type\": \"string\"});" + 
	                    "res.add(doc.description); " + 
	                    "res.add(doc.provider_lowercase, {\"field\": \"provider\", \"index\": \"not_analyzed_no_norms\", \"type\": \"string\"}); " + 
	                    "res.add(doc.download_url); " + 
	                    "res.add(doc.documentation_url); " + 
	                    "res.add(doc.screencast_url); " + 
	                    "res.add(doc.contact_name); " +
	                    "for (var i in doc.availabilities) { res.add(doc.availabilities[i], {\"field\": \"availabilities\"}); }" +
	                    "for (var i in doc.model_types) { res.add(doc.model_types[i], {\"field\": \"model_types\"}); }" +
	                    "for (var i in doc.platforms) { res.add(doc.platforms[i], {\"field\": \"platforms\"}); }" +
	                    "for (var i in doc.supported_functionalities) { res.add(doc.supported_functionalities[i], {\"field\": \"supported_functionalities\"}); }" +
	                    "res.add(doc.status, {\"field\": \"status\"});" +
	                    "res.add(doc.deleted, {\"field\": \"deleted\"});" +
	                    "res.add(doc.date_created, {\"field\": \"date_created\", \"type\": \"date\"});" + 
	                    "res.add(doc.last_update, {\"field\": \"last_update\", \"type\": \"date\"});" +
	                    "res.add(doc.user_id, {\"field\": \"user_id\"});" + 
	                    "return res; " +
	                "}")
	})
	private List<Map> search(LuceneQuery query) {
		
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
	
	/**
	 * Calculates the statistics for each tag of the given tag attribute.
	 * 
	 * @param tagCategory name of the tag attribute
	 * @return Map<String, Integer> containing pairs of tags and frequencies, empty if no values are available for the given tag attribute or if the attribute is not a tag attribute
	 */
	public Map<String, Integer> getTagStatisticFor(String tagCategory) {
		List<Map> entries = getDocuments(BPTToolStatus.Published.toString().toLowerCase());
		Map<String, Integer> tagStatistics = new HashMap<String, Integer>();
		for (Map entry : entries) {
			for (Object key : entry.keySet()) {
				if (tagCategory.equals((String) key)) {
					List<String> availability = (List) entry.get(key);
	        		for (String tag : availability) {
	        			tagStatistics = increaseCount(tag, tagStatistics);
        			}					
				}
			}

		}
		return tagStatistics;
	}
	
	private Map<String, Integer> increaseCount(String tag, Map<String, Integer> tagStatistics){
		String trimmedTag = (tag.trim().replaceAll(" +", " "));
		if (tagStatistics.keySet().contains(trimmedTag)) {
			tagStatistics.put(trimmedTag, tagStatistics.get(trimmedTag) + 1);
		} else {
			tagStatistics.put(trimmedTag, 1);
		}
		return tagStatistics;
	}
	
	/**
	 * Returns a random list of entries.
	 * @param numberOfEntries number of random entries
	 * @return entries as list
	 */
	public List<Map> getRandomEntries(int numberOfEntries) {
		Random random = new Random();
		List<Map> allPublishedEntries = getDocuments("published");
		int totalDocuments = allPublishedEntries.size();
		
		List<Map> randomEntries = new ArrayList<Map>();
		for (int i = 0; i < numberOfEntries; i++) {
			if (randomEntries.size() >= totalDocuments) {
				return randomEntries;
			}
			Map randomEntry = allPublishedEntries.get(random.nextInt(totalDocuments));
			if (!randomEntries.contains(randomEntry)) {
				randomEntries.add(randomEntry);
			} else {
				i--;
			}
		}
		return randomEntries;
	}
}