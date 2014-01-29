package de.uni_potsdam.hpi.bpt.resource_management.search;

import java.util.ArrayList;

import com.vaadin.ui.GridLayout;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTTagBox;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

@SuppressWarnings("serial")
public class BPTSearchTagBoxes extends BPTTagBox {

	private GridLayout availabilitiesLayout, modelTypesLayout, platformsLayout, supportedFunctionalitiesLayout;
	private ArrayList<String> availabilitiesTags, modelTypesTags, platformsTags, supportedFunctionalitiesTags;
	private ArrayList<BPTSearchTag> availabilityTagList, modelTypesTagList, platformsTagList, supportedFunctionalitiesTagList;

	public BPTSearchTagBoxes() {
		super();
		availabilityTagList = new ArrayList<BPTSearchTag>();
		modelTypesTagList = new ArrayList<BPTSearchTag>();
		platformsTagList = new ArrayList<BPTSearchTag>();
		supportedFunctionalitiesTagList = new ArrayList<BPTSearchTag>();
	}
	
	@Override
	protected void addGridsToComponent() {
		availabilitiesTags = BPTContainerProvider.getInstance().getUniqueValues("availabilities");
		availabilitiesLayout = new GridLayout(2,1);
		availabilitiesLayout.setWidth("100%");
		availabilitiesLayout.setHeight("100%");
		addComponent(availabilitiesLayout);
		
		modelTypesTags = BPTContainerProvider.getInstance().getUniqueValues("modelTypes");
		modelTypesLayout = new GridLayout(2,1);
		modelTypesLayout.setWidth("100%");
		modelTypesLayout.setHeight("100%");
		addComponent(modelTypesLayout);
		
		platformsTags = BPTContainerProvider.getInstance().getUniqueValues("platforms");
		platformsLayout = new GridLayout(2,1);
		platformsLayout.setWidth("100%");
		platformsLayout.setHeight("100%");
		addComponent(platformsLayout);
		
		supportedFunctionalitiesTags = BPTContainerProvider.getInstance().getUniqueValues("supportedFunctionalities");
		supportedFunctionalitiesLayout = new GridLayout(2,1);
		supportedFunctionalitiesLayout.setWidth("100%");
		supportedFunctionalitiesLayout.setHeight("100%");
		addComponent(supportedFunctionalitiesLayout);
	}
	
	@Override
	public void removeTag(BPTSearchTag searchTag) {
		String value = searchTag.getValue();
		if (availabilitiesTags.contains(value)) {
			availabilityTagList.remove(searchTag);
			removeTagFromLayout(searchTag, availabilitiesLayout);
		} else if (modelTypesTags.contains(value)) {
			modelTypesTagList.remove(searchTag);
			removeTagFromLayout(searchTag, modelTypesLayout);
		} else if (platformsTags.contains(value)) {
			platformsTagList.remove(searchTag);
			removeTagFromLayout(searchTag, platformsLayout);
		} else if (supportedFunctionalitiesTags.contains(value)) {
			supportedFunctionalitiesTagList.remove(searchTag);
			removeTagFromLayout(searchTag, supportedFunctionalitiesLayout);
		}
		
	}
	
	@Override
	public void addTag(String value){
		BPTSearchTag searchTag;
		if (availabilitiesTags.contains(value)) {
			searchTag = new BPTSearchTag(this, "Availability", value);
			availabilityTagList.add(searchTag);
			addTagToLayout(searchTag, availabilitiesLayout);
		} else if (modelTypesTags.contains(value)) {
			searchTag = new BPTSearchTag(this, "Model type", value);
			modelTypesTagList.add(searchTag);
			addTagToLayout(searchTag, modelTypesLayout);
		} else if (platformsTags.contains(value)) {
			searchTag = new BPTSearchTag(this, "Platform", value);
			platformsTagList.add(searchTag);
			addTagToLayout(searchTag, platformsLayout);
		} else { //if (supportedFunctionalitiesTags.contains(value)) {
			searchTag = new BPTSearchTag(this, "Supported functionality", value);
			supportedFunctionalitiesTagList.add(searchTag);
			addTagToLayout(searchTag, supportedFunctionalitiesLayout);
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
			availabilitiesLayout.removeAllComponents();
			modelTypesLayout.removeAllComponents();
			platformsLayout.removeAllComponents();
			supportedFunctionalitiesLayout.removeAllComponents();
			refresh();
		}
	}
	
	public ArrayList<String> getAvailabilityTagValues() {
		ArrayList<String> tagValues = new ArrayList<String>();
		for (BPTSearchTag searchTag : availabilityTagList) {
			tagValues.add(searchTag.getValue());
		}
		return tagValues;
	}
	
	public ArrayList<String> getModelTypesTagValues() {
		ArrayList<String> tagValues = new ArrayList<String>();
		for (BPTSearchTag searchTag : modelTypesTagList) {
			tagValues.add(searchTag.getValue());
		}
		return tagValues;
	}
	
	public ArrayList<String> getPlatformsTypesTagValues() {
		ArrayList<String> tagValues = new ArrayList<String>();
		for (BPTSearchTag searchTag : platformsTagList) {
			tagValues.add(searchTag.getValue());
		}
		return tagValues;
	}
	
	public ArrayList<String> getsupportedFunctionalitiesTypesTagValues() {
		ArrayList<String> tagValues = new ArrayList<String>();
		for (BPTSearchTag searchTag : supportedFunctionalitiesTagList) {
			tagValues.add(searchTag.getValue());
		}
		return tagValues;
	}
}
