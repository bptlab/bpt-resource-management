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
import com.vaadin.ui.Link;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseSetRepository;
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
	private BPTExerciseSetRepository exerciseSetRepository;
	private BPTExerciseRepository exerciseRepository;
	private BPTApplication application;
	
	public BPTContainerProvider(BPTApplication application) {
		this.application = application;
		this.exerciseSetRepository = application.getExerciseSetRepository();
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
		List<Map> tools = exerciseSetRepository.getDocuments("all");
		
		// TODO: refactor to have it generic
		
		Collator comparator = Collator.getInstance();
		comparator.setStrength(Collator.PRIMARY);
		
		if(tagColumn == "all" || tagColumn == "languages"){
			uniqueValues.add("----- Languages -----");
			ArrayList<String> languageTags = new ArrayList<String>();
			for (Map<String, Object> tool : tools) {
				ArrayList<String> languageTagsOfTool = (ArrayList<String>)tool.get("languages");  // cast
				languageTags.addAll(languageTagsOfTool);
			}
			Collections.sort(languageTags, comparator);
			uniqueValues.addAll(languageTags);
		}
		if (tagColumn == "all" || tagColumn == "topics") {
			if (tagColumn == "all") uniqueValues.add("----- Topics -----");
			List<String> topicTags = new ArrayList<String>();
			topicTags = BPTTopic.getValues("English");
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
	
	private IndexedContainer initializeContainerWithProperties(Boolean isSetContainer) {
		IndexedContainer container = new IndexedContainer();
		List<Object[]> items;
		if(isSetContainer){
			items = BPTVaadinResources.getEntrySets();
		}
		else{
			items = BPTVaadinResources.getEntries();
		}
		for (Object[] entry : items) {
			container.addContainerProperty(entry[1], (Class<?>)entry[2], null);
		}
		return container;
	}
	
	public IndexedContainer generateContainer(List<Map> exercises, Boolean isSetContainer) {
		IndexedContainer container = initializeContainerWithProperties(isSetContainer);
		for (int i = 0; i < exercises.size(); i++) {
			Map<String, Object> tool = exercises.get(i);
			Item item = container.addItem(i);
//				System.out.println("print map here: " + tool);
			setItemPropertyValues(container, item, tool, isSetContainer);
//				System.out.println("print item here: " + item);
		}
		return container;
	}
	
	private void setItemPropertyValues(IndexedContainer container, Item item, Map<String, Object> tool, boolean isSetEntry) {
		List<Object[]> entrySets;
		BPTDocumentRepository repository;
		if(isSetEntry){
			entrySets = BPTVaadinResources.getEntrySets();
			repository = exerciseSetRepository;
		}
		else{
			entrySets = BPTVaadinResources.getEntries();
			repository = exerciseRepository;
		}
		for (Object[] entry : entrySets) {
			Object component = BPTVaadinResources.generateComponent(repository, tool, (String)entry[0], (BPTPropertyValueType)entry[3], (String)entry[4], application);
			if (entry[1].equals("Supplementary files")) {
				ArrayList<Link> links = (ArrayList<Link>) component;
				for (int i = 1; i <= links.size(); i++) {
					if (!container.getContainerPropertyIds().contains("Supplementary file" + i)) {
						container.addContainerProperty("Supplementary file" + i, Link.class, null);
					}
					item.getItemProperty("Supplementary file" + i).setValue(links.get(i-1));
				}
			} else {
				item.getItemProperty(entry[1]).setValue(component);
			}
		}
	}
	
//	public IndexedContainer getVisibleEntries(ArrayList<BPTToolStatus> statusList, ArrayList<String> tags, String query) {
//		List<Map> tools = exerciseRepository.getVisibleEntries(statusList, tags, query);
////		List<Map> tools = exerciseRepository.search(statusList, null, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags, skip, limit, sortAttribute, ascending)
//		IndexedContainer container = generateContainer(tools);
//		return container;
//	}
	
	public IndexedContainer getVisibleEntrieSets(ArrayList<String> languages, ArrayList<BPTExerciseStatus> statusList, ArrayList<String> availabilityTags, ArrayList<String> modelTypeTags, ArrayList<String> platformTags, ArrayList<String> supportedFunctionalityTags, String fullTextSearchString, String sortAttribute, int skip, int limit) {
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
		List<Map> exerciseSets = exerciseSetRepository.search(languages, statusList, null, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags, skip, limit, db_sortAttribute, ascending);
		return generateContainer(exerciseSets, true);
	}
	
	public IndexedContainer getVisibleEntriesSetsByUser(ArrayList<String> languages, String user, ArrayList<String> availabilityTags, ArrayList<String> modelTypeTags, ArrayList<String> platformTags, ArrayList<String> supportedFunctionalityTags, String fullTextSearchString, String sortAttribute, int skip, int limit) {
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
		List<Map> exerciseSets = exerciseSetRepository.search(languages, Arrays.asList(BPTExerciseStatus.Published, BPTExerciseStatus.Unpublished, BPTExerciseStatus.Rejected), user, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags, skip, limit, db_sortAttribute, ascending);
		return generateContainer(exerciseSets, true);
	}
	
	public int getNumberOfEntries(ArrayList<String> languages, ArrayList<BPTExerciseStatus> statusList, ArrayList<String> availabilityTags, ArrayList<String> modelTypeTags, ArrayList<String> platformTags, ArrayList<String> supportedFunctionalityTags, String fullTextSearchString){
		return exerciseSetRepository.getNumberOfEntries(languages, statusList, null, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags);
	}
	
	public int getNumberOfEntriesByUser(ArrayList<String> languages, String user, ArrayList<String> availabilityTags, ArrayList<String> modelTypeTags, ArrayList<String> platformTags, ArrayList<String> supportedFunctionalityTags, String fullTextSearchString){
		return exerciseSetRepository.getNumberOfEntries(languages, Arrays.asList(BPTExerciseStatus.Published, BPTExerciseStatus.Unpublished, BPTExerciseStatus.Rejected), user, fullTextSearchString, availabilityTags, modelTypeTags, platformTags, supportedFunctionalityTags);
	}
	
	public ArrayList<String> getUniqueLanguages(){
		LinkedHashSet<String> uniqueValues = new LinkedHashSet<String>();
		List<Map> tools = exerciseSetRepository.getDocuments("all");
		for (Map<String, Object> tool : tools) {
			String attributeString = (String) tool.get("language");
			uniqueValues.add(attributeString);
		}
		ArrayList<String> uniqueList = new ArrayList<String>(uniqueValues);
//		Collections.sort(uniqueList, Comparator<T>)
		return new ArrayList<String>(uniqueValues);
	}
}
