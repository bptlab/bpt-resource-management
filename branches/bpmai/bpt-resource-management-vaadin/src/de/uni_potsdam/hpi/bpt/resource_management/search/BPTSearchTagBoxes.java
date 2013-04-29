package de.uni_potsdam.hpi.bpt.resource_management.search;

import java.util.ArrayList;

import com.vaadin.ui.GridLayout;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTTagBox;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

@SuppressWarnings("serial")
public class BPTSearchTagBoxes extends BPTTagBox{

	private GridLayout availabilitiesLayout, modelTypesLayout, platformsLayout, supportedFunctionalitiesLayout;
	private ArrayList<String> availabilitiesTags, modelTypesTags, platformsTags, supportedFunctionalitiesTags;

	@Override
	protected void addGridsToComponent() {
		availabilitiesTags = BPTContainerProvider.getUniqueTagValues("availabilities");
		availabilitiesLayout = new GridLayout(2,1);
		availabilitiesLayout.setWidth("100%");
		availabilitiesLayout.setHeight("100%");
		baseLayout.addComponent(availabilitiesLayout);
		
		modelTypesTags = BPTContainerProvider.getUniqueTagValues("modelTypes");
		modelTypesLayout = new GridLayout(2,1);
		modelTypesLayout.setWidth("100%");
		modelTypesLayout.setHeight("100%");
		baseLayout.addComponent(modelTypesLayout);
		
		platformsTags = BPTContainerProvider.getUniqueTagValues("platforms");
		platformsLayout = new GridLayout(2,1);
		platformsLayout.setWidth("100%");
		platformsLayout.setHeight("100%");
		baseLayout.addComponent(platformsLayout);
		
		supportedFunctionalitiesTags = BPTContainerProvider.getUniqueTagValues("supportedFunctionalities");
		supportedFunctionalitiesLayout = new GridLayout(2,1);
		supportedFunctionalitiesLayout.setWidth("100%");
		supportedFunctionalitiesLayout.setHeight("100%");
		baseLayout.addComponent(supportedFunctionalitiesLayout);
	}
	
	@Override
	public void removeTag(BPTSearchTag searchTag) {
		String value = searchTag.getValue();
		if(availabilitiesTags.contains(value)){
			removeTagFromLayout(searchTag, availabilitiesLayout);
		}
		else if (modelTypesTags.contains(value)){
			removeTagFromLayout(searchTag, modelTypesLayout);
		}
		else if (platformsTags.contains(value)){
			removeTagFromLayout(searchTag, platformsLayout);
		}
		else if (supportedFunctionalitiesTags.contains(value)){
			removeTagFromLayout(searchTag, supportedFunctionalitiesLayout);
		}
		
	}
	@Override
	public void addTag(String value){
		if(availabilitiesTags.contains(value)){
			addTagToLayout(value, "Availability", availabilitiesLayout);
		}
		else if (modelTypesTags.contains(value)){
			addTagToLayout(value, "Model type", modelTypesLayout);
		}
		else if (platformsTags.contains(value)){
			addTagToLayout(value, "Platform", platformsLayout);
		}
		else if (supportedFunctionalitiesTags.contains(value)){
			addTagToLayout(value, "Supported functionality", supportedFunctionalitiesLayout);
		}
		
	}
}
