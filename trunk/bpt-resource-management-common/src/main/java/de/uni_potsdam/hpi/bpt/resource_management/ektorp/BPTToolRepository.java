package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import com.github.ldriscoll.ektorplucene.CustomLuceneResult;
import com.github.ldriscoll.ektorplucene.CustomLuceneResult.Row;
import com.github.ldriscoll.ektorplucene.LuceneQuery;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.FullText;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.Index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.type.TypeReference;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.View;
import org.ektorp.support.Views;

import de.uni_potsdam.hpi.bpt.resource_management.mail.BPTMailProvider;

public class BPTToolRepository extends BPTDocumentRepository {
	
	private List<Map> tableEntries = new ArrayList<Map>();
	private BPTMailProvider mailProvider;
	
	public BPTToolRepository() {
		super("bpt_resources_tools");
		mailProvider = new BPTMailProvider();
		disableMailProvider();
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
			mailProvider.sendEmailForUpdatedEntry((String)document.get("name"), (String)document.get("_id"), (String)document.get("user_id"));
		}
		return databaseDocument;
	}
	
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
	
	@View(
			name = "tools_by_user_id", 
			map = "function(doc) { emit(doc.user_id, doc); }"
	)
	public List<Map> getDocumentsByUser(String user) {
		ViewQuery query = new ViewQuery()
							  .designDocId("_design/Map")
							  .viewName("tools_by_user_id")
							  .key(user);
		List<Map> result = db.queryView(query, Map.class);
		return result;
	}
	
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
		databaseDocument.put("status", BPTToolStatus.Unpublished);
		databaseDocument.put("deleted", false);
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
	
	public Map<String, Object> rejectDocument(String _id) {
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		databaseDocument.put("status", BPTToolStatus.Rejected);
		db.update(databaseDocument);
		mailProvider.sendEmailForRejectedEntry((String)databaseDocument.get("name"), (String)databaseDocument.get("user_id"));
		return databaseDocument;
	}
	
	public BPTToolStatus getDocumentStatus(String _id){
		Map<String, Object> databaseDocument = db.get(Map.class, _id);
		return BPTToolStatus.valueOf((String) databaseDocument.get("status"));
	}
	
	public Boolean containsName(String name){
		List<Map> Docs = getDocuments("all");
		for (int i = 0; i < Docs.size(); i++){
			if(name.equals(Docs.get(i).get("name"))) return true;
		}
		return false;
	};
	
	public List<Map> getVisibleEntries(List<BPTToolStatus> states, ArrayList<String> tags, String query) {
		tableEntries.clear();
		for (BPTToolStatus status : states) {
			tableEntries.addAll(getDocuments(status.toString().toLowerCase()));
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

}