package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class ContainerProvider {
	
	private static final String[] students = new String[] {"Tsun", "Benni", "Thommy", "Micha", "Susi"};
	private static final String[] cakes = new String[] {"Kaese", "Schoko", "Apfel", "Kaese", "Kirsch-Schoko"};

	public static IndexedContainer getContainer(){
		
		IndexedContainer container = new IndexedContainer();
		container.addContainerProperty("Student", String.class, null);
		container.addContainerProperty("Cake", String.class, null);
		for (int i = 0; i < students.length; i++) {
			String student = students[i];
			String cake = cakes[i];
			Item item = container.addItem(i);
			item.getItemProperty("Student").setValue(student);
			item.getItemProperty("Cake").setValue(cake);
		}
		return container;
		
		
	}

	public static String[] getUniqueValues() {
		return students;
	}
}
