package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Arrays;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.uni_potsdam.hpi.bpt.resource_management.search.BPTSearchInputField;
import de.uni_potsdam.hpi.bpt.resource_management.search.BPTSearchTag;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

@SuppressWarnings({ "serial", "unchecked" })
public class BPTTagComponent extends CustomComponent {
	
	protected BPTSearchInputField searchInput;
	private ArrayList<String> uniqueValues;
	private ArrayList<String> unselectedValues;
	protected BPTTagBox tagBox;
	protected VerticalLayout layout;
	protected BPTApplication application;
	protected final ArrayList<String> categories = new ArrayList<String>(Arrays.asList("----- Topics -----", "----- Modeling languages -----", "----- Task types -----", "----- Other tags -----")); 
		
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
		layout.addComponent(searchInput);
		addTagBox();
		addListenerToSearchInputBox();	
	}

	protected void addTagBox() {
		tagBox = new BPTTagBox();
		layout.addComponent(tagBox);
	}

	private ComboBox createSearchInputBox(boolean newTagsAllowed){
		searchInput = new BPTSearchInputField();
		for (String uniqueValue: uniqueValues) {
			Label label = new Label(uniqueValue);
			if(categories.contains(uniqueValue)) {
//				label = new Label("<b>" + uniqueValue + "</b>");
				label.setContentMode(Label.CONTENT_XHTML);
			}
			searchInput.addItem(label);
		}
		searchInput.setNewItemsAllowed(newTagsAllowed);
		unselectedValues = (ArrayList<String>) uniqueValues.clone();
		return searchInput;
	}

	private void addListenerToSearchInputBox() {
		searchInput.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				Object value = searchInput.getValue();
				if (value == null) return;
				String valueString;
				if (value instanceof String) {
					valueString = ((String) value).trim().replaceAll(" +", " ");
				}
				else {
					valueString = ((Label) value).getValue().toString().trim().replaceAll(" +", " ");
				}
				
				if (!categories.contains(valueString) && !tagBox.getTagValues().contains(valueString)) {
					tagBox.addTag(valueString);
					unselectedValues.remove(valueString);
					searchInput.removeAllItems();
					
					refresh();
//					for (String unselectedValue: unselectedValues) {
//						Label label = new Label(unselectedValue);
//						if(categories.contains(unselectedValue)) label.addStyleName(unselectedValue);
//						searchInput.addItem(label);
//					}
				}
				searchInput.setValue(null);
			}
		});
	}

	public void addTag(BPTSearchTag searchTag) {
		unselectedValues.add(searchTag.getValue());
		refresh();
	}
	
	public void restoreAllTags() {
		unselectedValues = (ArrayList<String>) uniqueValues.clone();
		tagBox.removeAllTags();
		refresh();
	}
	
	public ArrayList<String> getTagValues() {
		return tagBox.getTagValues();
	}

	public void refresh() {
		searchInput.removeAllItems();
		for (String uniqueValue : uniqueValues){
			if(unselectedValues.contains(uniqueValue)){
				searchInput.addItem(new Label(uniqueValue));
			}
		}
		application.refreshAndClean();
	}
	
	public void addChosenTag(String value) {
		tagBox.addTag(value);
	}
}
