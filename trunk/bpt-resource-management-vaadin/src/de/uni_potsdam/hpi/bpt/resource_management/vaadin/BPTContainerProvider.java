package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class BPTContainerProvider {
	
	private static final String[] students = new String[] {"Tsun", "Benni", "Thommy", "Micha", "Susi"};
	private static final String[] cakes = new String[] {"Kaese-Sahne", "Schoko", "Apfel", "Kaese", "Kirsch-Schoko"};
	private static final String[] animals = new String[] {"Einhorn", "Katze", "Hund", "Vogel", "Mistkäfer", "Giraffe", "Koala"};

	public static IndexedContainer getContainer(){
		
		IndexedContainer container = new IndexedContainer();
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
		return container;
		
		
	}

	public static String[] getUniqueValues() {
		String[] uniqueValues = new String[students.length + cakes.length + animals.length];
		for (int i = 0; i < students.length; i++) uniqueValues[i] = students[i];
		for (int i = 0; i < cakes.length; i++) uniqueValues[i + students.length] = cakes[i];
		for (int i = 0; i < animals.length; i++) uniqueValues[i + students.length + cakes.length] = animals[i];
		return uniqueValues;
	}
}
