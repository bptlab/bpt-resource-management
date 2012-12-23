package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Arrays;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

public class BPTSearchComponent extends CustomComponent{
	
	private ComboBox searchInput;
	private String[] uniqueValues = ContainerProvider.getUniqueValues();
	private ArrayList<String> unselectedValues;
//	private SearchTagBox searchTagBox;
	
	public BPTSearchComponent() {
		VerticalLayout layout = new VerticalLayout();
		setCompositionRoot(layout);
		createSearchInputBox();
	//	searchTagBox = new SearchTagBox();
		layout.addComponent(searchInput);
	//	layout.addComponent(searchTagBox);
		addListenerToSearchInputBox();
	}
	
	private void addListenerToSearchInputBox() {
		searchInput.addListener( new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				String value = (String) searchInput.getValue();
			//	searchTagBox.addTag(value);		(if not null)
				System.out.println(value);
				searchInput.setValue(null);
				unselectedValues.remove(value);
				searchInput.removeAllItems();
				
				for (int i = 0; i < unselectedValues.size(); i++){
					searchInput.addItem(unselectedValues.get(i));
				}
				
								
			}
		});
		
	}

	private ComboBox createSearchInputBox(){
		searchInput= new ComboBox();
		for (int i = 0; i < uniqueValues.length; i++){
			searchInput.addItem(uniqueValues[i]);
		}
		searchInput.setImmediate(true);
		unselectedValues = new ArrayList<String>(Arrays.asList(uniqueValues));
		return searchInput;
	}
	
	
	
			
}
