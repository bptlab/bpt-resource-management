package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Set;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

public class BPTTagComponent extends CustomComponent {
	
	protected ComboBox searchInput;
	private Set<String> uniqueValues;
	private Set<String> unselectedValues;
	protected BPTSearchTagBox searchTagBox;
	protected VerticalLayout layout;
	protected BPTApplication application;
	
	
	public BPTTagComponent(BPTApplication application, String tagColumns, boolean newTagsAllowed) {
		this.application = application;
		init(tagColumns, newTagsAllowed);
	}
	
	private void init(String tagColumns, boolean newTagsAllowed) {
		uniqueValues = BPTContainerProvider.getUniqueValues(tagColumns);
		layout = new VerticalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		setCompositionRoot(layout);
		addElements(newTagsAllowed);
	}
	
	protected void addElements(boolean newTagsAllowed) {
		createSearchInputBox(newTagsAllowed);
		searchTagBox = new BPTSearchTagBox();
		layout.addComponent(searchInput);
		layout.addComponent(searchTagBox);
		addListenerToSearchInputBox();	
	}

	private ComboBox createSearchInputBox(boolean newTagsAllowed){
		searchInput = new ComboBox();
		for (String uniqueValue: uniqueValues) {
			searchInput.addItem(uniqueValue);
		}
//		searchInput.setWidth("100%");
		searchInput.setImmediate(true);
		searchInput.setNewItemsAllowed(newTagsAllowed);
		unselectedValues = uniqueValues;
		return searchInput;
	}

	private void addListenerToSearchInputBox() {
		searchInput.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				Object value = searchInput.getValue();
				if (value == null) return;
				String valueString = (String) value;
				searchTagBox.addTag(valueString);
//				System.out.println(valueString);
				searchInput.setValue(null);
				unselectedValues.remove(valueString);
				searchInput.removeAllItems();
								
				for (String unselectedValue: unselectedValues) {
					searchInput.addItem(unselectedValue);
				}				
			}
		});
	}

	public void addTag(BPTSearchTag searchTag) {
		unselectedValues.add(searchTag.getValue());
		searchInput.removeAllItems();
		
		for (String unselectedValue: unselectedValues){
			searchInput.addItem(unselectedValue);
		}
	}
	public ArrayList<String> getTagValues() {
		
		return searchTagBox.getTagValues();
	}

	public void refresh() {
	}
	
	public void addChosenTag(String value) {
		searchTagBox.addTag(value);
	}

}
