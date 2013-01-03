package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.*;

import org.ektorp.CouchDbConnector;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.*;

public class BPTContainerProvider {
	
	/*
	private static final String[] students = new String[] {"Tsun", "Benni", "Thommy", "Micha", "Susi"};
	private static final String[] cakes = new String[] {"Kaese-Sahne", "Schoko", "Apfel", "Kaese", "Kirsch-Schoko"};
	private static final String[] animals = new String[] {"Einhorn", "Katze", "Hund", "Vogel", "Mistk�fer", "Giraffe", "Koala"};
	*/
	
	// private static HashMap<String,Object[]> toolPropertiesAndAccess = new HashMap<String,String[]>();
	// toolPropertiesAndAccess.put("name", {String.class, tool.getName()});
	
	public static IndexedContainer getContainer(){
		IndexedContainer container = new IndexedContainer();
		
		/* part for real data - BEGIN */
		
		container.addContainerProperty("Name", String.class, null);
		container.addContainerProperty("Description", String.class, null);
		container.addContainerProperty("Provider", String.class, null);
		container.addContainerProperty("Download", String.class, null);
		container.addContainerProperty("Documentation", String.class, null);
		container.addContainerProperty("Screencast", String.class, null);
		container.addContainerProperty("Availability", String.class, null);
		container.addContainerProperty("Model type", String.class, null);
		container.addContainerProperty("Platform", String.class, null);
		container.addContainerProperty("Supported functionality", String.class, null);
		container.addContainerProperty("Contact name", String.class, null);
		container.addContainerProperty("Contact mail", String.class, null);
		container.addContainerProperty("Date created", Date.class, null);
		container.addContainerProperty("Last update", Date.class, null);
		
		CouchDbConnector database = BPTDatabase.connect();
		BPTToolRepository repository = new BPTToolRepository(database);
		
		List<BPTTool> tools = repository.getAll();
		
		for (int i = 0; i < tools.size(); i++) {
			BPTTool tool = tools.get(i);
			Item item = container.addItem(i);
			item.getItemProperty("Name").setValue(tool.getName());
			item.getItemProperty("Description").setValue(tool.getDescription());
			item.getItemProperty("Provider").setValue(tool.getProvider());
			item.getItemProperty("Download").setValue(tool.getDownloadURL());
			item.getItemProperty("Documentation").setValue(tool.getDocumentationURL());
			item.getItemProperty("Screncast").setValue(tool.getScreencastURL());
			item.getItemProperty("Availability").setValue(tool.getAvailabilitiesAsString());
			item.getItemProperty("Model type").setValue(tool.getModelTypesAsString());
			item.getItemProperty("Platform").setValue(tool.getPlatformsAsString());
			item.getItemProperty("Supported functionality").setValue(tool.getSupportedFunctionalitiesAsString());
			item.getItemProperty("Contact name").setValue(tool.getContactName());
			item.getItemProperty("Contact mail").setValue(tool.getContactMail());
			item.getItemProperty("Date created").setValue(tool.getDateCreated());
			item.getItemProperty("Last update").setValue(tool.getLastUpdate());
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

	public static ArrayList<String> getUniqueValues() {
		ArrayList<String> uniqueValues = new ArrayList<String>();
		
		// TODO: fetch unique values from availability, model types, platfor, supported functionality
		
		/* 
		String[] uniqueValues = new String[students.length + cakes.length + animals.length];
		for (int i = 0; i < students.length; i++) uniqueValues[i] = students[i];
		for (int i = 0; i < cakes.length; i++) uniqueValues[i + students.length] = cakes[i];
		for (int i = 0; i < animals.length; i++) uniqueValues[i + students.length + cakes.length] = animals[i];
		*/
		return uniqueValues;
	}
}