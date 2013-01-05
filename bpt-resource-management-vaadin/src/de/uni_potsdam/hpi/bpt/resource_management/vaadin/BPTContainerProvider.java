package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.*;

import org.ektorp.CouchDbConnector;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Link;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.*;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.*;

public class BPTContainerProvider {
	
	private static BPTDocumentRepository toolRepository = new BPTDocumentRepository("bpt_resources");
	
	public static IndexedContainer getContainer(){
		IndexedContainer container = new IndexedContainer();
		
		addContainerProperties(container);
		
		List<Map> tools = toolRepository.getAll();
		
		for (int i = 0; i < tools.size(); i++) {
			Map<String, Object> tool = tools.get(i);
			Item item = container.addItem(i);
			setItemPropertyValues(item, tool);
		}
		
		return container;
		
	}

	public static Set<String> getUniqueValues(String tagColumn) {
		Set<String> uniqueValues = new HashSet<String>();
		
		List<Map> tools = toolRepository.getAll();
		
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
			item.getItemProperty(entry[1]).setValue(generateValue(tool, (String)entry[0], (BPTPropertyValueType)entry[3]));
		}
	}
	
	private static Object generateValue(Map<String, Object>tool, String documentColumnName, BPTPropertyValueType valueType) {
		switch (valueType) {
			case LINK : return asLink((String)tool.get(documentColumnName));
			case EMAIL : return asEmailLink((String)tool.get(documentColumnName));
			case SET : return asFormattedString((ArrayList<String>)tool.get(documentColumnName));
			default : return tool.get(documentColumnName);
		}
	}
	
	private static String asFormattedString(ArrayList<String> stringList) {
		return stringList.toString().replace("[", "").replace("]", "");
	}
	
	private static Link asLink(String linkString) {
		return new Link(linkString, new ExternalResource(linkString));
	}
	
	private static Link asEmailLink(String linkString) {
		return new Link(linkString, new ExternalResource("mailto:" + linkString));
	}
}
