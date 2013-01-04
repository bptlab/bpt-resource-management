package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

public class BPTSearchComponent extends CustomComponent{
	
	private BPTSidebar sidebar;
	private ComboBox searchInput;
	private Set<String> uniqueValues = BPTContainerProvider.getUniqueValues();
	private Set<String> unselectedValues;
	private BPTSearchTagBox searchTagBox;
	private VerticalLayout layout;
	
	public BPTSearchComponent() {
		layout = new VerticalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		setCompositionRoot(layout);
		createSearchInputBox();
		searchTagBox = new BPTSearchTagBox();
		layout.addComponent(searchInput);
		layout.addComponent(searchTagBox);
		addListenerToSearchInputBox();
	}
	
	private void addListenerToSearchInputBox() {
		searchInput.addListener( new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				Object value = searchInput.getValue();
				if (value == null) return;
				String valueString = (String) value;
				searchTagBox.addTag(valueString);
				System.out.println(valueString);
				searchInput.setValue(null);
				unselectedValues.remove(valueString);
				searchInput.removeAllItems();
								
				for (String unselectedValue: unselectedValues){
					searchInput.addItem(unselectedValue);
				}
				
								
			}
		});
		
	}

	private ComboBox createSearchInputBox(){
		searchInput= new ComboBox();
		for (String uniqueValue: uniqueValues){
			searchInput.addItem(uniqueValue);
		}
		searchInput.setImmediate(true);
		unselectedValues = uniqueValues;
		return searchInput;
	}

	public void addTag(BPTSearchTag searchTag) {
		unselectedValues.add(searchTag.getValue());
		searchInput.removeAllItems();
		
		for (String unselectedValue: unselectedValues){
			searchInput.addItem(unselectedValue);
		}
		
	}
	public ArrayList<String> getTagValues(){
		
		return searchTagBox.getTagValues();
	}

}
