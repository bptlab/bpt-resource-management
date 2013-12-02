package de.uni_potsdam.hpi.bpt.resource_management.search;

import java.util.ArrayList;

import com.vaadin.ui.GridLayout;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTTagBox;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

@SuppressWarnings("serial")
public class BPTSearchTagBoxes extends BPTTagBox{

	private GridLayout topicssLayout, modelingLanguagesLayout, taskTypesLayout, otherTagsLayout, languageTagLayout;
	private ArrayList<String> topicsTags, modelingLanguagesTags, taskTypesTags, otherTags, languageTags;
	private ArrayList<BPTSearchTag> topicsTagList, modelingLanguagesTagList, taskTypesTagList, otherTagList, languageTagList;

	public BPTSearchTagBoxes() {
		super();
		topicsTagList = new ArrayList<BPTSearchTag>();
		modelingLanguagesTagList = new ArrayList<BPTSearchTag>();
		taskTypesTagList = new ArrayList<BPTSearchTag>();
		otherTagList = new ArrayList<BPTSearchTag>();
		languageTagList = new ArrayList<BPTSearchTag>();
	}
	@Override
	protected void addGridsToComponent() {
		
		languageTags = BPTContainerProvider.getInstance().getUniqueValues("languages");
		languageTagLayout = new GridLayout(2,1);
		languageTagLayout.setWidth("100%");
		languageTagLayout.setHeight("100%");
		addComponent(languageTagLayout);
		
		topicsTags = BPTContainerProvider.getInstance().getUniqueValues("topics");
		topicssLayout = new GridLayout(2,1);
		topicssLayout.setWidth("100%");
		topicssLayout.setHeight("100%");
		addComponent(topicssLayout);
		
		modelingLanguagesTags = BPTContainerProvider.getInstance().getUniqueValues("modelTypes");
		modelingLanguagesLayout = new GridLayout(2,1);
		modelingLanguagesLayout.setWidth("100%");
		modelingLanguagesLayout.setHeight("100%");
		addComponent(modelingLanguagesLayout);
		
		taskTypesTags = BPTContainerProvider.getInstance().getUniqueValues("taskTypes");
		taskTypesLayout = new GridLayout(2,1);
		taskTypesLayout.setWidth("100%");
		taskTypesLayout.setHeight("100%");
		addComponent(taskTypesLayout);
		
		otherTags = BPTContainerProvider.getInstance().getUniqueValues("otherTags");
		otherTagsLayout = new GridLayout(2,1);
		otherTagsLayout.setWidth("100%");
		otherTagsLayout.setHeight("100%");
		addComponent(otherTagsLayout);
		
	}
	
	@Override
	public void removeTag(BPTSearchTag searchTag) {
		String value = searchTag.getValue();
		if(topicsTags.contains(value)){
			topicsTagList.remove(searchTag);
			removeTagFromLayout(searchTag, topicssLayout);
		}
		else if (modelingLanguagesTags.contains(value)){
			modelingLanguagesTagList.remove(searchTag);
			removeTagFromLayout(searchTag, modelingLanguagesLayout);
		}
		else if (taskTypesTags.contains(value)){
			taskTypesTagList.remove(searchTag);
			removeTagFromLayout(searchTag, taskTypesLayout);
		}
		else if (otherTags.contains(value)){
			otherTagList.remove(searchTag);
			removeTagFromLayout(searchTag, otherTagsLayout);
		}
		else{
			languageTagList.remove(searchTag);
			removeTagFromLayout(searchTag, languageTagLayout);
		}
		
	}
	
	@Override
	public void addTag(String value){
		BPTSearchTag searchTag;
		if(languageTags.contains(value)){
			searchTag = new BPTSearchTag(this, value);
			languageTagList.add(searchTag);
			addTagToLayout(searchTag, languageTagLayout);
		}
		else if(topicsTags.contains(value)){
			searchTag = new BPTSearchTag(this, value);
			topicsTagList.add(searchTag);
			addTagToLayout(searchTag, topicssLayout);
		}
		else if (modelingLanguagesTags.contains(value)){
			searchTag = new BPTSearchTag(this, value);
			modelingLanguagesTagList.add(searchTag);
			addTagToLayout(searchTag, modelingLanguagesLayout);
		}
		else if (taskTypesTags.contains(value)){
			searchTag = new BPTSearchTag(this, value);
			taskTypesTagList.add(searchTag);
			addTagToLayout(searchTag, taskTypesLayout);
		}
		else {//if (supportedFunctionalitiesTags.contains(value)){
			searchTag = new BPTSearchTag(this, value);
			otherTagList.add(searchTag);
			addTagToLayout(searchTag, otherTagsLayout);
		}
	}
	
	@Override
	public void removeAllTags() {
		if (!searchTagList.isEmpty()) {
			searchTagList.clear();
			languageTagList.clear();
			topicsTagList.clear();
			modelingLanguagesTagList.clear();
			taskTypesTagList.clear();
			otherTagList.clear();
			languageTagLayout.removeAllComponents();
			topicssLayout.removeAllComponents();
			modelingLanguagesLayout.removeAllComponents();
			taskTypesLayout.removeAllComponents();
			otherTagsLayout.removeAllComponents();
			refresh();
		}
	}
	
	public ArrayList<String> getTopicsTagValues() {
		ArrayList<String> tagValues = new ArrayList<String>();
		for (BPTSearchTag searchTag : topicsTagList){
			tagValues.add(searchTag.getValue());
		}
		return tagValues;
	}
	
	public ArrayList<String> getModelingLanguagesTagValues() {
		ArrayList<String> tagValues = new ArrayList<String>();
		for (BPTSearchTag searchTag : modelingLanguagesTagList){
			tagValues.add(searchTag.getValue());
		}
		return tagValues;
	}
	
	public ArrayList<String> getTaskTypesTagValues() {
		ArrayList<String> tagValues = new ArrayList<String>();
		for (BPTSearchTag searchTag : taskTypesTagList){
			tagValues.add(searchTag.getValue());
		}
		return tagValues;
	}
	
	public ArrayList<String> getOtherTagValues() {
		ArrayList<String> tagValues = new ArrayList<String>();
		for (BPTSearchTag searchTag : otherTagList){
			tagValues.add(searchTag.getValue());
		}
		return tagValues;
	}
	
	public ArrayList<String> getLanguageTagValues(){
		ArrayList<String> tagValues = new ArrayList<String>();
		for (BPTSearchTag searchTag : languageTagList){
			tagValues.add(searchTag.getValue());
		}
		return tagValues;
	}
}
