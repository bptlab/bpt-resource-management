package de.uni_potsdam.hpi.bpt.resource_management.vaadin.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentRepository;

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
public class BPTContainerProvider {
	
	private static BPTDocumentRepository toolRepository = new BPTDocumentRepository("bpt_resources");
	
	/**
	 * @return the container for the Vaadin table filled with database entries that are not marked as deleted
	 *
	 */
	public static IndexedContainer getContainer(){
		IndexedContainer container = new IndexedContainer();
		
		addContainerProperties(container);
		
		List<Map> tools = toolRepository.getAll();
		
		for (int i = 0; i < tools.size(); i++) {
			Map<String, Object> tool = tools.get(i);
			if (!(Boolean)tool.get("deleted")) {
				Item item = container.addItem(i);
				setItemPropertyValues(item, tool);
			}
		}
		
		return container;
		
	}
	
	/**
	 * @param tagColumn the colum(s) from which the unique values (= tags) shall be retrieved
	 * @return the unique values (= tags)
	 *
	 */
	public static Set<String> getUniqueValues(String tagColumn) {
		Set<String> uniqueValues = new HashSet<String>();
		List<Map> tools = toolRepository.getAll();
		
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
	
	private static void addContainerProperties(IndexedContainer container) {
		for (Object[] entry : BPTVaadinResources.getEntries("BPTTool")) {
			container.addContainerProperty(entry[1], (Class<?>)entry[2], null);
		}
	}
	
	private static void setItemPropertyValues(Item item, Map<String, Object> tool){
		for (Object[] entry : BPTVaadinResources.getEntries("BPTTool")) {
			item.getItemProperty(entry[1]).setValue(BPTVaadinResources.generateComponent(tool, (String)entry[0], (BPTPropertyValueType)entry[3]));
		}
	}
}
