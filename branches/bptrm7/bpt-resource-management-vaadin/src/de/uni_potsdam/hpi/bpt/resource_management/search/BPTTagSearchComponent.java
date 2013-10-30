package de.uni_potsdam.hpi.bpt.resource_management.search;

import java.util.ArrayList;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTApplicationUI;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTTagComponent;

@SuppressWarnings("serial")
public class BPTTagSearchComponent extends BPTTagComponent{
	
	public BPTTagSearchComponent(BPTApplicationUI applicationUI, String tagColumns, boolean newTagsAllowed) {
		super(applicationUI, tagColumns, newTagsAllowed);
		searchInput.setInputPrompt("Tag search");
//		searchInput.setWidth("70%");
	}
	
	@Override
	public void refresh() {
		super.refresh();
		applicationUI.refreshAndClean();
	}
	
	public ArrayList<String> getSelectedTags() {
		return tagBox.getTagValues();
	}
	
	@Override
	public void addChosenTag(String value){
		tagBox.addTag(value);
	}
	
	@Override
	protected void addTagBox(){
		tagBox = new BPTSearchTagBoxes();
		layout.addComponent(tagBox);
	}
	
	public boolean isNoTagSelected() {
		return getAvailabiltyTags().isEmpty() && getModelTypeTags().isEmpty() && getPlatformsTags().isEmpty() && getSupportedFunctionalityTags().isEmpty();
	}
	
	public ArrayList<String> getAvailabiltyTags() {
		return ((BPTSearchTagBoxes)tagBox).getAvailabilityTagValues();
	}
	
	public ArrayList<String> getModelTypeTags() {
		return ((BPTSearchTagBoxes)tagBox).getModelTypesTagValues();
	}
	
	public ArrayList<String> getPlatformsTags() {
		return ((BPTSearchTagBoxes)tagBox).getPlatformsTypesTagValues();
	}
	
	public ArrayList<String> getSupportedFunctionalityTags() {
		return ((BPTSearchTagBoxes)tagBox).getsupportedFunctionalitiesTypesTagValues();
	}

}
