package de.uni_potsdam.hpi.bpt.resource_management.search;

import java.util.ArrayList;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTApplicationUI;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTTagComponent;

@SuppressWarnings("serial")
public class BPTTagSearchComponent extends BPTTagComponent {
	
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
		addComponent(tagBox);
	}
	
	public boolean isNoTagSelected() {
		return getLanguageTags().isEmpty() && getTopicTags().isEmpty() && getModelingLanguagesTags().isEmpty() && getTaskTypesTags().isEmpty();
	}
	
	public ArrayList<String> getLanguageTags() {
		return ((BPTSearchTagBoxes)tagBox).getLanguageTagValues();
	}
	
	public ArrayList<String> getTopicTags() {
		return ((BPTSearchTagBoxes)tagBox).getTopicsTagValues();
	}
	
	public ArrayList<String> getModelingLanguagesTags() {
		return ((BPTSearchTagBoxes)tagBox).getModelingLanguagesTagValues();
	}
	
	public ArrayList<String> getTaskTypesTags() {
		return ((BPTSearchTagBoxes)tagBox).getTaskTypesTagValues();
	}
	
	public ArrayList<String> getOtherTags() {
		return ((BPTSearchTagBoxes)tagBox).getOtherTagValues();
	}
	
}
