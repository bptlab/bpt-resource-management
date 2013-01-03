package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Arrays;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

public class BPTSearchComponent extends CustomComponent{
	
	private BPTSidebar sidebar;
	private ComboBox searchInput;
	private ArrayList<String> uniqueValues = BPTContainerProvider.getUniqueValues();
	private ArrayList<String> unselectedValues;
	private BPTSearchTagBox searchTagBox;
	private VerticalLayout layout;
	
	public BPTSearchComponent(BPTSidebar sidebar) {
		this.sidebar = sidebar;
		layout = new VerticalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		setCompositionRoot(layout);
		createSearchInputBox();
		searchTagBox = new BPTSearchTagBox(this);
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
								
				for (int i = 0; i < unselectedValues.size(); i++){
					searchInput.addItem(unselectedValues.get(i));
				}
				
								
			}
		});
		
	}

	private ComboBox createSearchInputBox(){
		searchInput= new ComboBox();
		for (int i = 0; i < uniqueValues.size(); i++){
			searchInput.addItem(uniqueValues.get(i));
		}
		searchInput.setImmediate(true);
		unselectedValues = uniqueValues;
		return searchInput;
	}

	public void addTag(BPTSearchTag searchTag) {
		unselectedValues.add(searchTag.getValue());
		searchInput.removeAllItems();
		
		for (int i = 0; i < unselectedValues.size(); i++){
			searchInput.addItem(unselectedValues.get(i));
		}
		
	}

	public void refresh(ArrayList<String> tagValues) {
		sidebar.refresh(tagValues);
		
	}
	
	
	
			
}
