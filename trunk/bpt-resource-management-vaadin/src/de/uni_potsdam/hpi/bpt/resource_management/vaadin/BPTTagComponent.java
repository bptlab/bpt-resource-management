package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

@SuppressWarnings({ "serial", "unchecked" })
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
		// TODO: update unique values on entry addition or deletion
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
		searchInput.setWidth("300px");
		searchInput.setImmediate(true);
		searchInput.setNullSelectionAllowed(false);
		searchInput.setNewItemsAllowed(newTagsAllowed);
		unselectedValues = (Set<String>)((HashSet<String>) uniqueValues).clone();
		return searchInput;
	}

	private void addListenerToSearchInputBox() {
		searchInput.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				Object value = searchInput.getValue();
				if (value == null) return;
				String valueString = ((String) value).trim().replaceAll(" +", " ");
				searchTagBox.addTag(valueString);
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
		refresh();
	}
	
	public void restoreAllTags() {
		unselectedValues = (Set<String>)((HashSet<String>) uniqueValues).clone();
		searchTagBox.removeAllTags();
		refresh();
	}
	
	public ArrayList<String> getTagValues() {
		return searchTagBox.getTagValues();
	}

	public void refresh() {
		searchInput.removeAllItems();
		for (String unselectedValue : unselectedValues){
			searchInput.addItem(unselectedValue);
		}
	}
	
	public void addChosenTag(String value) {
		searchTagBox.addTag(value);
	}
}
