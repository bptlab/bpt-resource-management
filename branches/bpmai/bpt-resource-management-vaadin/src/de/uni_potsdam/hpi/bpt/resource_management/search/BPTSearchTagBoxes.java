package de.uni_potsdam.hpi.bpt.resource_management.search;

import java.util.ArrayList;

import com.vaadin.ui.GridLayout;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTApplication;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTTagBox;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

@SuppressWarnings("serial")
public class BPTSearchTagBoxes extends BPTTagBox{

	private GridLayout topicssLayout, modelTypesLayout, taskTypesLayout, otherTagsLayout;
	private ArrayList<String> availabilitiesTags, modelTypesTags, platformsTags, supportedFunctionalitiesTags;
	private ArrayList<BPTSearchTag> availabilityTagList, modelTypesTagList, platformsTagList, supportedFunctionalitiesTagList;
	private BPTApplication application;

	public BPTSearchTagBoxes(BPTApplication application) {
		super();
		this.application = application;
		availabilityTagList = new ArrayList<BPTSearchTag>();
		modelTypesTagList = new ArrayList<BPTSearchTag>();
		platformsTagList = new ArrayList<BPTSearchTag>();
		supportedFunctionalitiesTagList = new ArrayList<BPTSearchTag>();
	}
	@Override
	protected void addGridsToComponent() {
		availabilitiesTags = BPTContainerProvider.getInstance().getUniqueValues("topics");
		topicssLayout = new GridLayout(2,1);
		topicssLayout.setWidth("100%");
		topicssLayout.setHeight("100%");
		baseLayout.addComponent(topicssLayout);
		
		modelTypesTags = BPTContainerProvider.getInstance().getUniqueValues("modelTypes");
		modelTypesLayout = new GridLayout(2,1);
		modelTypesLayout.setWidth("100%");
		modelTypesLayout.setHeight("100%");
		baseLayout.addComponent(modelTypesLayout);
		
		platformsTags = BPTContainerProvider.getInstance().getUniqueValues("taskTypes");
		taskTypesLayout = new GridLayout(2,1);
		taskTypesLayout.setWidth("100%");
		taskTypesLayout.setHeight("100%");
		baseLayout.addComponent(taskTypesLayout);
		
		supportedFunctionalitiesTags = BPTContainerProvider.getInstance().getUniqueValues("otherTags");
		otherTagsLayout = new GridLayout(2,1);
		otherTagsLayout.setWidth("100%");
		otherTagsLayout.setHeight("100%");
		baseLayout.addComponent(otherTagsLayout);
	}
	
	@Override
	public void removeTag(BPTSearchTag searchTag) {
		String value = searchTag.getValue();
		if(availabilitiesTags.contains(value)){
			availabilityTagList.remove(searchTag);
			removeTagFromLayout(searchTag, topicssLayout);
		}
		else if (modelTypesTags.contains(value)){
			modelTypesTagList.remove(searchTag);
			removeTagFromLayout(searchTag, modelTypesLayout);
		}
		else if (platformsTags.contains(value)){
			platformsTagList.remove(searchTag);
			removeTagFromLayout(searchTag, taskTypesLayout);
		}
		else if (supportedFunctionalitiesTags.contains(value)){
			supportedFunctionalitiesTagList.remove(searchTag);
			removeTagFromLayout(searchTag, otherTagsLayout);
		}
		
	}
	
	@Override
	public void addTag(String value){
		BPTSearchTag searchTag;
		if(availabilitiesTags.contains(value)){
			searchTag = new BPTSearchTag(this, "Availability", value);
			availabilityTagList.add(searchTag);
			addTagToLayout(searchTag, topicssLayout);
		}
		else if (modelTypesTags.contains(value)){
			searchTag = new BPTSearchTag(this, "Model type", value);
			modelTypesTagList.add(searchTag);
			addTagToLayout(searchTag, modelTypesLayout);
		}
		else if (platformsTags.contains(value)){
			searchTag = new BPTSearchTag(this, "Platform", value);
			platformsTagList.add(searchTag);
			addTagToLayout(searchTag, taskTypesLayout);
		}
		else {//if (supportedFunctionalitiesTags.contains(value)){
			searchTag = new BPTSearchTag(this, "Supported functionality", value);
			supportedFunctionalitiesTagList.add(searchTag);
			addTagToLayout(searchTag, otherTagsLayout);
		}
	}
	
	@Override
	public void removeAllTags() {
		if (!searchTagList.isEmpty()) {
			searchTagList.clear();
			availabilityTagList.clear();
			modelTypesTagList.clear();
			platformsTagList.clear();
			supportedFunctionalitiesTagList.clear();
			topicssLayout.removeAllComponents();
			modelTypesLayout.removeAllComponents();
			taskTypesLayout.removeAllComponents();
			otherTagsLayout.removeAllComponents();
			refresh();
		}
	}
	
	public ArrayList<String> getAvailabilityTagValues() {
		ArrayList<String> tagValues = new ArrayList<String>();
		for (BPTSearchTag searchTag : availabilityTagList){
			tagValues.add(searchTag.getValue());
		}
		return tagValues;
	}
	
	public ArrayList<String> getModelTypesTagValues() {
		ArrayList<String> tagValues = new ArrayList<String>();
		for (BPTSearchTag searchTag : modelTypesTagList){
			tagValues.add(searchTag.getValue());
		}
		return tagValues;
	}
	
	public ArrayList<String> getPlatformsTypesTagValues() {
		ArrayList<String> tagValues = new ArrayList<String>();
		for (BPTSearchTag searchTag : platformsTagList){
			tagValues.add(searchTag.getValue());
		}
		return tagValues;
	}
	
	public ArrayList<String> getsupportedFunctionalitiesTypesTagValues() {
		ArrayList<String> tagValues = new ArrayList<String>();
		for (BPTSearchTag searchTag : supportedFunctionalitiesTagList){
			tagValues.add(searchTag.getValue());
		}
		return tagValues;
	}
}
