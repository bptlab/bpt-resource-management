package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Label;

public class BPTSearchTagBox extends CustomComponent{
	
	private BPTSearchComponent searchComponent;
	private VerticalLayout layout;
	private ArrayList<BPTSearchTag> searchTagList;

	public BPTSearchTagBox(BPTSearchComponent searchComponent) {
		this.searchComponent = searchComponent;
		layout = new VerticalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		setCompositionRoot(layout);
		searchTagList = new ArrayList();
	};
	public void addTag(String value){
		BPTSearchTag searchTag = new BPTSearchTag(this, value);
		searchTagList.add(searchTag);
		layout.addComponent(searchTag);
	}
	
	public void removeTag(BPTSearchTag searchTag){
				searchTagList.remove(searchTag);
				layout.removeComponent(searchTag);
				searchComponent.addTag(searchTag);
	}
	
}
