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

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseStatus;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTTopic;

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
	
	private static BPTExerciseRepository exerciseRepository = BPTExerciseRepository.getInstance();
	
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
		List<Map> tools = exerciseRepository.getDocuments("all");
		
		// TODO: refactor to have it generic
		
		Collator comparator = Collator.getInstance();
		comparator.setStrength(Collator.PRIMARY);
		
		if (tagColumn == "all" || tagColumn == "topics") {
			if (tagColumn == "all") uniqueValues.add("----- Topics -----");
			ArrayList<String> topicTags = new ArrayList<String>();
			BPTTopic[] topics = BPTTopic.values();
			for(BPTTopic topicTag : topics){
				topicTags.add(topicTag.toString());
			}
			
			
//			for (Map<String, Object> tool : tools) {
//				ArrayList<String> topicTagsOfTool = (ArrayList<String>)tool.get("topics");  // cast
//				topicTags.addAll(topicTagsOfTool);
//			}
//			Collections.sort(topicTags, comparator);
			uniqueValues.addAll(topicTags);
		}
		if (tagColumn == "all" || tagColumn == "modelTypes") {
			if (tagColumn == "all") uniqueValues.add("----- Modeling languages -----");
			ArrayList<String> modelTypeTags = new ArrayList<String>();
			for (Map<String, Object> tool : tools) {
				ArrayList<String> modelTypeTagsOfTool = (ArrayList<String>)tool.get("modeling_languages");  // cast
				modelTypeTags.addAll(modelTypeTagsOfTool);
			}
			Collections.sort(modelTypeTags, comparator);
			uniqueValues.addAll(modelTypeTags);
		}
		if (tagColumn == "all" || tagColumn == "taskTypes") {
			if (tagColumn == "all") uniqueValues.add("----- Task types -----");
			ArrayList<String> taskTypeTags = new ArrayList<String>();
			for (Map<String, Object> tool : tools) {
				ArrayList<String> taskTypeTagsOfTool = (ArrayList<String>)tool.get("task_types");  // cast
				taskTypeTags.addAll(taskTypeTagsOfTool);
			}
			Collections.sort(taskTypeTags, comparator);
			uniqueValues.addAll(taskTypeTags);
		}
		if (tagColumn == "all" || tagColumn == "otherTags") {
			if (tagColumn == "all") uniqueValues.add("----- Other tags -----");
			ArrayList<String> otherTags = new ArrayList<String>();
			for (Map<String, Object> tool : tools) {
				ArrayList<String> otherTagsOfTool = (ArrayList<String>)tool.get("other_tags");  // cast
				otherTags.addAll(otherTagsOfTool);
			}
			Collections.sort(otherTags, comparator);
			uniqueValues.addAll(otherTags);
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
	
	public static IndexedContainer generateContainer(List<Map> exercises) {
		IndexedContainer container = initializeContainerWithProperties();
		for (int i = 0; i < exercises.size(); i++) {
			Map<String, Object> tool = exercises.get(i);
			Item item = container.addItem(i);
//				System.out.println("print map here: " + tool);
			setItemPropertyValues(item, tool);
//				System.out.println("print item here: " + item);
		}
		return container;
	}
	
	private static void setItemPropertyValues(Item item, Map<String, Object> tool) {
		for (Object[] entry : BPTVaadinResources.getEntries()) {
			item.getItemProperty(entry[1]).setValue(BPTVaadinResources.generateComponent(exerciseRepository, tool, (String)entry[0], (BPTPropertyValueType)entry[3], (String)entry[4]));
		}
	}
	
//	public static IndexedContainer getVisibleEntries(ArrayList<BPTToolStatus> statusList, ArrayList<String> tags, String query) {
//		List<Map> tools = toolRepository.getVisibleEntries(statusList, tags, query);
////		List<Map> tools = toolRepository.search(statusList, null, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags, skip, limit, sortAttribute, ascending)
//		IndexedContainer container = generateContainer(tools);
//		return container;
//	}
	
	public static IndexedContainer getVisibleEntries(String language, ArrayList<BPTExerciseStatus> statusList, ArrayList<String> availabilityTags, ArrayList<String> modelTypeTags, ArrayList<String> platformTags, ArrayList<String> supportedFunctionalityTags, String fullTextSearchString, String sortAttribute, int skip, int limit) {
		String db_sortAttribute;
		boolean ascending;
		if(sortAttribute.equals("ID")){
			db_sortAttribute = "set_id";
			ascending = true;
		}
		else if(sortAttribute.equals("Title")){
			db_sortAttribute = "title";
			ascending = true;
		}
		else{
			db_sortAttribute = "last_update";
			ascending = false;
		}
		List<Map> tools = exerciseRepository.search(language, statusList, null, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags, skip, limit, db_sortAttribute, ascending);
		IndexedContainer container = generateContainer(tools);
		return container;
	}
	
	public static IndexedContainer getVisibleEntriesByUser(String language, String user, ArrayList<String> availabilityTags, ArrayList<String> modelTypeTags, ArrayList<String> platformTags, ArrayList<String> supportedFunctionalityTags, String fullTextSearchString, String sortAttribute, int skip, int limit) {
		String db_sortAttribute;
		boolean ascending;
		if(sortAttribute.equals("ID")){
			db_sortAttribute = "set_id";
			ascending = true;
		}
		else if(sortAttribute.equals("Title")){
			db_sortAttribute = "title";
			ascending = true;
		}
		else{
			db_sortAttribute = "last_update";
			ascending = false;
		}
		List<Map> tools = exerciseRepository.search(language, Arrays.asList(BPTExerciseStatus.Published, BPTExerciseStatus.Unpublished, BPTExerciseStatus.Rejected), user, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags, skip, limit, db_sortAttribute, ascending);
		IndexedContainer container = generateContainer(tools);
		return container;
	}
	
	public static void refreshFromDatabase() {
		exerciseRepository.refreshData();
	}
	
	public static int getNumberOfEntries(String language, ArrayList<BPTExerciseStatus> statusList, ArrayList<String> availabilityTags, ArrayList<String> modelTypeTags, ArrayList<String> platformTags, ArrayList<String> supportedFunctionalityTags, String fullTextSearchString){
		return exerciseRepository.getNumberOfEntries(language, statusList, null, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags);
	}
	
	public static int getNumberOfEntriesByUser(String language, String user, ArrayList<String> availabilityTags, ArrayList<String> modelTypeTags, ArrayList<String> platformTags, ArrayList<String> supportedFunctionalityTags, String fullTextSearchString){
		return exerciseRepository.getNumberOfEntries(language, Arrays.asList(BPTExerciseStatus.Published, BPTExerciseStatus.Unpublished, BPTExerciseStatus.Rejected), user, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags);
	}
	
	public static ArrayList<String> getUniqueLanguages(){
		LinkedHashSet<String> uniqueValues = new LinkedHashSet<String>();
		List<Map> tools = exerciseRepository.getDocuments("all");
		for (Map<String, Object> tool : tools) {
			String attributeString = (String) tool.get("language");
			uniqueValues.add(attributeString);
		}
		ArrayList<String> uniqueList = new ArrayList<String>(uniqueValues);
//		Collections.sort(uniqueList, Comparator<T>)
		return new ArrayList<String>(uniqueValues);
	}
}
