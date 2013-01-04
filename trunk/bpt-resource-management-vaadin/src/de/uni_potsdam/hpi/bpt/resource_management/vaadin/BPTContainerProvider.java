package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.*;

import org.ektorp.CouchDbConnector;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Link;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.*;

public class BPTContainerProvider {
	
	/*
	private static final String[] students = new String[] {"Tsun", "Benni", "Thommy", "Micha", "Susi"};
	private static final String[] cakes = new String[] {"Kaese-Sahne", "Schoko", "Apfel", "Kaese", "Kirsch-Schoko"};
	private static final String[] animals = new String[] {"Einhorn", "Katze", "Hund", "Vogel", "Mistkäfer", "Giraffe", "Koala"};
	*/
	
	private static CouchDbConnector database = BPTDatabase.connect();
	private static BPTToolRepository repository = new BPTToolRepository(database);
	
	// private static HashMap<String,Object[]> toolPropertiesAndAccess = new HashMap<String,String[]>();
	// toolPropertiesAndAccess.put("name", {String.class, tool.getName()});
	
	public static IndexedContainer getContainer(){
		IndexedContainer container = new IndexedContainer();
		
		/* part for real data - BEGIN */
		
		addContainerProperties(container);
		
		List<BPTTool> tools = repository.getAll();
		
		for (int i = 0; i < tools.size(); i++) {
			BPTTool tool = tools.get(i);
			Item item = container.addItem(i);
			setItemPropertyValues(item, tool);
		}
		
		/* part for real data - END */
		
		/* part for dummy data - BEGIN */
		/*
		container.addContainerProperty("Student", String.class, null);
		container.addContainerProperty("Cake", String.class, null);
		container.addContainerProperty("Animal", String.class, null);
		for (int i = 0; i < students.length; i++) {
			for (int j = 0; j < cakes.length; j++){
				for(int k = 0; k < animals.length; k++){
				
					String student = students[i];
					String cake = cakes[j];
					String animal = animals[k];
					int index = (i * cakes.length * animals.length) + (j * animals.length) + k;
					Item item = container.addItem(index);
					item.getItemProperty("Student").setValue(student);
					item.getItemProperty("Cake").setValue(cake);
					item.getItemProperty("Animal").setValue(animal);
				}
			}
		}
		*/
		/* part for dummy data - END */
		
		return container;
		
	}

	public static Set<String> getUniqueValues(String tagColumn) {
		Set<String> uniqueValues = new HashSet<String>();
		
		List<BPTTool> tools = repository.getAll();
		
		for (BPTTool tool : tools) {
			if (tagColumn == "all" || tagColumn == "availabilities")
			uniqueValues.addAll(tool.getAvailabilities());
			if (tagColumn == "all" || tagColumn == "modelTypes")
			uniqueValues.addAll(tool.getModelTypes());
			if (tagColumn == "all" || tagColumn == "platforms")
			uniqueValues.addAll(tool.getPlatforms());
			if (tagColumn == "all" || tagColumn == "supportedFunctionalities")
			uniqueValues.addAll(tool.getSupportedFunctionalities());
		}
		
		/* 
		String[] uniqueValues = new String[students.length + cakes.length + animals.length];
		for (int i = 0; i < students.length; i++) uniqueValues[i] = students[i];
		for (int i = 0; i < cakes.length; i++) uniqueValues[i + students.length] = cakes[i];
		for (int i = 0; i < animals.length; i++) uniqueValues[i + students.length + cakes.length] = animals[i];
		*/
		
		return uniqueValues;
	}
	
	private static void addContainerProperties(IndexedContainer container) {
		container.addContainerProperty("Name", String.class, null);
		container.addContainerProperty("Description", String.class, null);
		container.addContainerProperty("Provider", String.class, null);
		container.addContainerProperty("Download", Component.class, null);
		container.addContainerProperty("Documentation", Component.class, null);
		container.addContainerProperty("Screencast", Component.class, null);
		container.addContainerProperty("Availability", String.class, null);
		container.addContainerProperty("Model type", String.class, null);
		container.addContainerProperty("Platform", String.class, null);
		container.addContainerProperty("Supported functionality", String.class, null);
		container.addContainerProperty("Contact name", String.class, null);
		container.addContainerProperty("Contact mail", Component.class, null);
		container.addContainerProperty("Date created", Date.class, null);
		container.addContainerProperty("Last update", Date.class, null);
	}
	
	private static void setItemPropertyValues(Item item, BPTTool tool){
		item.getItemProperty("Name").setValue(tool.getName());
		item.getItemProperty("Description").setValue(tool.getDescription());
		item.getItemProperty("Provider").setValue(tool.getProvider());
		item.getItemProperty("Download").setValue(asLink(tool.getDownloadURL()));
		item.getItemProperty("Documentation").setValue(asLink(tool.getDocumentationURL()));
		item.getItemProperty("Screencast").setValue(asLink(tool.getScreencastURL()));
		item.getItemProperty("Availability").setValue(asFormattedString(tool.getAvailabilities()));
		item.getItemProperty("Model type").setValue(asFormattedString(tool.getModelTypes()));
		item.getItemProperty("Platform").setValue(asFormattedString(tool.getPlatforms()));
		item.getItemProperty("Supported functionality").setValue(asFormattedString(tool.getSupportedFunctionalities()));
		item.getItemProperty("Contact name").setValue(tool.getContactName());
		item.getItemProperty("Contact mail").setValue(asEmailLink(tool.getContactMail()));
		item.getItemProperty("Date created").setValue(tool.getDateCreated());
		item.getItemProperty("Last update").setValue(tool.getLastUpdate());
	}
	
	private static String asFormattedString(Set<String> stringSet) {
		return stringSet.toString().replace("[", "").replace("]", "");
	}
	
	private static Link asLink(String linkString) {
		return new Link(linkString, new ExternalResource(linkString));
	}
	
	private static Link asEmailLink(String linkString) {
		return new Link(linkString, new ExternalResource("mailto:" + linkString));
	}
}
