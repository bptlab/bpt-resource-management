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
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseStatus;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTTopic;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTUserRepository;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTApplication;

/**
 * Provides data for the table and the search component.
 * 
 * public IndexedContainer getContainer()
 * public Set<String> getUniqueValues(String tagColumn)
 * 
 * @author bu
 * @author tw
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BPTContainerProvider {
	
	private static BPTContainerProvider instance;
	private BPTExerciseRepository exerciseRepository;
	private BPTApplication application;
	
	public BPTContainerProvider(BPTApplication application) {
		this.application = application;
		this.exerciseRepository = application.getExerciseRepository();
		BPTContainerProvider.instance = this;
	}
	
	public static BPTContainerProvider getInstance() {	
		return instance;
	}
	
//	/**
//	 * @return the container for the Vaadin table filled with database entries that are not marked as deleted
//	 *
//	 */
//	public IndexedContainer createContainerWithDatabaseData(BPTDocumentStatus[] statusArray){
//		
//		IndexedContainer container = createContainerWithProperties();
//		
//		List<Map> tools = exerciseRepository.getAll();
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
	public ArrayList<String> getUniqueValues(String tagColumn) {
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
	
	private IndexedContainer initializeContainerWithProperties() {
		IndexedContainer container = new IndexedContainer();
		for (Object[] entry : BPTVaadinResources.getEntries()) {
			container.addContainerProperty(entry[1], (Class<?>)entry[2], null);
		}
		return container;
	}
	
	public IndexedContainer generateContainer(List<Map> exercises) {
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
	
	private void setItemPropertyValues(Item item, Map<String, Object> tool) {
		for (Object[] entry : BPTVaadinResources.getEntries()) {
			// TODO: Caused by: com.vaadin.data.Property$ConversionException: Conversion for value '[com.vaadin.ui.Link@8f2102]' of class java.util.ArrayList to com.vaadin.ui.Component failed
			if (!entry[1].equals("Names of Attachments")) {
				item.getItemProperty(entry[1]).setValue(BPTVaadinResources.generateComponent(exerciseRepository, tool, (String)entry[0], (BPTPropertyValueType)entry[3], (String)entry[4], application));
			}
		}
	}
	
//	public IndexedContainer getVisibleEntries(ArrayList<BPTToolStatus> statusList, ArrayList<String> tags, String query) {
//		List<Map> tools = exerciseRepository.getVisibleEntries(statusList, tags, query);
////		List<Map> tools = exerciseRepository.search(statusList, null, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags, skip, limit, sortAttribute, ascending)
//		IndexedContainer container = generateContainer(tools);
//		return container;
//	}
	
	public IndexedContainer getVisibleEntries(String language, ArrayList<BPTExerciseStatus> statusList, ArrayList<String> availabilityTags, ArrayList<String> modelTypeTags, ArrayList<String> platformTags, ArrayList<String> supportedFunctionalityTags, String fullTextSearchString, String sortAttribute, int skip, int limit) {
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
	
	public IndexedContainer getVisibleEntriesByUser(String language, String user, ArrayList<String> availabilityTags, ArrayList<String> modelTypeTags, ArrayList<String> platformTags, ArrayList<String> supportedFunctionalityTags, String fullTextSearchString, String sortAttribute, int skip, int limit) {
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
	
	public void refreshFromDatabase() {
		exerciseRepository.refreshData();
	}
	
	public int getNumberOfEntries(String language, ArrayList<BPTExerciseStatus> statusList, ArrayList<String> availabilityTags, ArrayList<String> modelTypeTags, ArrayList<String> platformTags, ArrayList<String> supportedFunctionalityTags, String fullTextSearchString){
		return exerciseRepository.getNumberOfEntries(language, statusList, null, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags);
	}
	
	public int getNumberOfEntriesByUser(String language, String user, ArrayList<String> availabilityTags, ArrayList<String> modelTypeTags, ArrayList<String> platformTags, ArrayList<String> supportedFunctionalityTags, String fullTextSearchString){
		return exerciseRepository.getNumberOfEntries(language, Arrays.asList(BPTExerciseStatus.Published, BPTExerciseStatus.Unpublished, BPTExerciseStatus.Rejected), user, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags);
	}
	
	public ArrayList<String> getUniqueLanguages(){
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
