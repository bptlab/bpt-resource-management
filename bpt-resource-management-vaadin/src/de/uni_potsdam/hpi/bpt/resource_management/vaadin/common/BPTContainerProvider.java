package de.uni_potsdam.hpi.bpt.resource_management.vaadin.common;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;

/**
 * Provides data for the table and the search component.
 * 
 * public static IndexedContainer getContainer()
 * public static Set<String> getUniqueValues(String tagColumn)
 * 
 * @author bu
 * @author tw
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BPTContainerProvider {
	
	private static BPTToolRepository toolRepository = BPTToolRepository.getInstance();
	
//	/**
//	 * @return the container for the Vaadin table filled with database entries that are not marked as deleted
//	 *
//	 */
//	public static IndexedContainer createContainerWithDatabaseData(BPTDocumentStatus[] statusArray){
//		
//		IndexedContainer container = createContainerWithProperties();
//		
//		List<Map> tools = toolRepository.getAll();
//		
//		for (int i = 0; i < tools.size(); i++) {
//			Map<String, Object> tool = tools.get(i);
//			if (!(Boolean)tool.get("deleted")) {
//				for (int j = 0; j < statusArray.length; j++){
//					System.out.println("Array:" + statusArray[j]);
//					System.out.println("db_status:" + tool.get("status"));
//					if ((statusArray[j] == BPTDocumentStatus.valueOf((String) (tool.get("status"))))){
//						Item item = container.addItem(i);
//						setItemPropertyValues(item, tool);
//					}
//				}
//			}
//		}
//		
//		return container;
//		
//	}
	
	/**
	 * @param tagColumn the column(s) from which the unique values (= tags) shall be retrieved
	 * @return the unique values (= tags)
	 *
	 */
	public static ArrayList<String> getUniqueValues(String tagColumn) {
		LinkedHashSet<String> uniqueValues = new LinkedHashSet<String>();
		// TODO: don't get "all" documents, just the ones with the selected status
		List<Map> tools = toolRepository.getDocuments("all");
		
		// TODO: refactor to have it generic
		
		Collator comparator = Collator.getInstance();
		comparator.setStrength(Collator.PRIMARY);
		
		if (tagColumn == "all" || tagColumn == "availabilities") {
			uniqueValues.add("----- Availabilities -----");
			ArrayList<String> availabilityTags = new ArrayList<String>();
			for (Map<String, Object> tool : tools) {
				ArrayList<String> availabilityTagsOfTool = (ArrayList<String>)tool.get("availabilities");  // cast
				availabilityTags.addAll(availabilityTagsOfTool);
			}
			Collections.sort(availabilityTags, comparator);
			uniqueValues.addAll(availabilityTags);
		}
		if (tagColumn == "all" || tagColumn == "modelTypes") {
			uniqueValues.add("----- Model types -----");
			ArrayList<String> modelTypeTags = new ArrayList<String>();
			for (Map<String, Object> tool : tools) {
				ArrayList<String> modelTypeTagsOfTool = (ArrayList<String>)tool.get("model_types");  // cast
				modelTypeTags.addAll(modelTypeTagsOfTool);
			}
			Collections.sort(modelTypeTags, comparator);
			uniqueValues.addAll(modelTypeTags);
		}
		if (tagColumn == "all" || tagColumn == "platforms") {
			uniqueValues.add("----- Platforms -----");
			ArrayList<String> platformTags = new ArrayList<String>();
			for (Map<String, Object> tool : tools) {
				ArrayList<String> platformTagsOfTool = (ArrayList<String>)tool.get("platforms");  // cast
				platformTags.addAll(platformTagsOfTool);
			}
			Collections.sort(platformTags, comparator);
			uniqueValues.addAll(platformTags);
		}
		if (tagColumn == "all" || tagColumn == "supportedFunctionalities") {
			uniqueValues.add("----- Supported functionalities -----");
			ArrayList<String> supportedFunctionalityTags = new ArrayList<String>();
			for (Map<String, Object> tool : tools) {
				ArrayList<String> supportedFunctionalityTagsOfTool = (ArrayList<String>)tool.get("supported_functionalities");  // cast
				supportedFunctionalityTags.addAll(supportedFunctionalityTagsOfTool);
			}
			Collections.sort(supportedFunctionalityTags, comparator);
			uniqueValues.addAll(supportedFunctionalityTags);
		}
		
		return new ArrayList<String>(uniqueValues);
	}
	
	private static IndexedContainer initializeContainerWithProperties() {
		IndexedContainer container = new IndexedContainer();
		for (Object[] entry : BPTVaadinResources.getEntries()) {
			container.addContainerProperty(entry[1], (Class<?>)entry[2], null);
		}
		return container;
	}
	
	public static IndexedContainer generateContainer(List<Map> tools) {
		IndexedContainer container = initializeContainerWithProperties();
		for (int i = 0; i < tools.size(); i++) {
			Map<String, Object> tool = tools.get(i);
			Item item = container.addItem(i);
//			System.out.println("print map here: " + tool);
			setItemPropertyValues(item, tool);
//			System.out.println("print item here: " + item);
		}
		return container;
	}
	
	private static void setItemPropertyValues(Item item, Map<String, Object> tool) {
		for (Object[] entry : BPTVaadinResources.getEntries()) {
			item.getItemProperty(entry[1]).setValue(BPTVaadinResources.generateComponent(toolRepository, tool, (String)entry[0], (BPTPropertyValueType)entry[3], (String)entry[4]));
		}
	}
	
//	public static IndexedContainer getVisibleEntries(ArrayList<BPTToolStatus> statusList, ArrayList<String> tags, String query) {
//		List<Map> tools = toolRepository.getVisibleEntries(statusList, tags, query);
////		List<Map> tools = toolRepository.search(statusList, null, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags, skip, limit, sortAttribute, ascending)
//		IndexedContainer container = generateContainer(tools);
//		return container;
//	}
	
	public static IndexedContainer getVisibleEntries(ArrayList<BPTToolStatus> statusList, ArrayList<String> availabilityTags, ArrayList<String> modelTypeTags, ArrayList<String> platformTags, ArrayList<String> supportedFunctionalityTags, String fullTextSearchString, String sortAttribute, int skip, int limit) {
		String db_sortAttribute;
		boolean ascending;
		if(sortAttribute.equals("Name")){
			db_sortAttribute = "name";
			ascending = true;
		}
		else if(sortAttribute.equals("Provider")){
			db_sortAttribute = "provider";
			ascending = true;
		}
		else if(sortAttribute.equals("Last Update")){
			db_sortAttribute = "last_update";
			ascending = false;
		}
		else{
			db_sortAttribute = "date_created";
			ascending = false;
		}
		List<Map> tools = toolRepository.search(statusList, null, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags, skip, limit, db_sortAttribute, ascending);
		IndexedContainer container = generateContainer(tools);
		return container;
	}
	
	public static IndexedContainer getVisibleEntriesByUser(String user, ArrayList<String> availabilityTags, ArrayList<String> modelTypeTags, ArrayList<String> platformTags, ArrayList<String> supportedFunctionalityTags, String fullTextSearchString, String sortAttribute, int skip, int limit) {
		String db_sortAttribute;
		boolean ascending;
		if(sortAttribute.equals("Name")){
			db_sortAttribute = "name";
			ascending = true;
		}
		else if(sortAttribute.equals("Provider")){
			db_sortAttribute = "provider";
			ascending = true;
		}
		else if(sortAttribute.equals("Last Update")){
			db_sortAttribute = "last_update";
			ascending = false;
		}
		else{
			db_sortAttribute = "date_created";
			ascending = false;
		}
		List<Map> tools = toolRepository.search(Arrays.asList(BPTToolStatus.Published, BPTToolStatus.Unpublished, BPTToolStatus.Rejected), user, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags, skip, limit, db_sortAttribute, ascending);
		IndexedContainer container = generateContainer(tools);
		return container;
	}
	
	public static void refreshFromDatabase() {
		toolRepository.refreshData();
	}
	
	public static int getNumberOfEntries(ArrayList<BPTToolStatus> statusList, ArrayList<String> availabilityTags, ArrayList<String> modelTypeTags, ArrayList<String> platformTags, ArrayList<String> supportedFunctionalityTags, String fullTextSearchString){
		return toolRepository.getNumberOfEntries(statusList, null, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags);
	}
	
	public static int getNumberOfEntriesByUser(String user, ArrayList<String> availabilityTags, ArrayList<String> modelTypeTags, ArrayList<String> platformTags, ArrayList<String> supportedFunctionalityTags, String fullTextSearchString){
		return toolRepository.getNumberOfEntries(Arrays.asList(BPTToolStatus.Published, BPTToolStatus.Unpublished, BPTToolStatus.Rejected), user, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags);
	}
}
