package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;

public class BPTTable extends Table{

	private static final String[] students = new String[] {"Tsun", "Benni", "Thommy", "Micha", "Susi"};
	private static final String[] cakes = new String[] {"Kaese", "Schoko", "Apfel", "Kaese", "Kirsch-Schoko"};
	
	public BPTTable(){
		super();
		setImmediate(true);
		setSelectable(true);
		setColumnReorderingAllowed(true);
        setColumnCollapsingAllowed(true);
        setContainerDataSource(createContainer());
        setWidth("100%");
		
	}
	
	private IndexedContainer createContainer(){
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
	
	
	
}
