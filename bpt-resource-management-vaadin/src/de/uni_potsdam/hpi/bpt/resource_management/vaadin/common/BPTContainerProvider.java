package de.uni_potsdam.hpi.bpt.resource_management.vaadin.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	private static BPTToolRepository toolRepository = new BPTToolRepository();
	
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
	 * @param tagColumn the colum(s) from which the unique values (= tags) shall be retrieved
	 * @return the unique values (= tags)
	 *
	 */
	public static Set<String> getUniqueValues(String tagColumn) {
		Set<String> uniqueValues = new HashSet<String>();
		// TODO: don't get "all" documents, just the ones with the selected status
		List<Map> tools = toolRepository.getDocuments("all");
		
		// TODO: refactor to have it generic
		for (Map<String, Object> tool : tools) {
			if (tagColumn == "all" || tagColumn == "availabilities")
			uniqueValues.addAll(new HashSet<String>((ArrayList<String>)tool.get("availabilities"))); // hard_coded
			if (tagColumn == "all" || tagColumn == "modelTypes")
			uniqueValues.addAll(new HashSet<String>((ArrayList<String>)tool.get("model_types"))); // cast
			if (tagColumn == "all" || tagColumn == "platforms")
			uniqueValues.addAll(new HashSet<String>((ArrayList<String>)tool.get("platforms")));
			if (tagColumn == "all" || tagColumn == "supportedFunctionalities")
			uniqueValues.addAll(new HashSet<String>((ArrayList<String>)tool.get("supported_functionalities")));
		}
		
		return uniqueValues;
	}
	
	private static IndexedContainer initializeContainerWithProperties() {
		IndexedContainer container = new IndexedContainer();
		for (Object[] entry : BPTVaadinResources.getEntries()) {
			container.addContainerProperty(entry[1], (Class<?>)entry[2], null);
		}
		return container;
	}
	
	private static IndexedContainer generateContainer(List<Map> tools) {
		IndexedContainer container = initializeContainerWithProperties();
		for (int i = 0; i < tools.size(); i++) {
			Map<String, Object> tool = tools.get(i);
			Item item = container.addItem(i);
//				System.out.println("print map here: " + tool);
			setItemPropertyValues(item, tool);
//				System.out.println("print item here: " + item);
		}
		return container;
	}
	
	private static void setItemPropertyValues(Item item, Map<String, Object> tool){
		for (Object[] entry : BPTVaadinResources.getEntries()) {
			item.getItemProperty(entry[1]).setValue(BPTVaadinResources.generateComponent(toolRepository, tool, (String)entry[0], (BPTPropertyValueType)entry[3], (String)entry[4]));
		}
	}
	
	public static IndexedContainer getVisibleEntries(ArrayList<BPTToolStatus> statusList, ArrayList<String> tags, String query){
		List<Map> tools = toolRepository.getVisibleEntries(statusList, tags, query);
		IndexedContainer container = generateContainer(tools);
		return container;
	}
	
	public static IndexedContainer getVisibleEntriesByUser(String user, ArrayList<String> tags, String query){
		List<Map> tools = toolRepository.getVisibleEntriesByUser(user, tags, query);
		IndexedContainer container = generateContainer(tools);
		return container;
	}
	
	public static void refreshFromDatabase(){
		toolRepository.refreshData();
	}
}
