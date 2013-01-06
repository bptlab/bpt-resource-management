package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

public class BPTSearchTagBox extends CustomComponent{
	
	private VerticalLayout layout;
	private ArrayList<BPTSearchTag> searchTagList;

	public BPTSearchTagBox() {
		layout = new VerticalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		setCompositionRoot(layout);
		searchTagList = new ArrayList<BPTSearchTag>();
	};
	public void addTag(String value){
		BPTSearchTag searchTag = new BPTSearchTag(this, value);
		searchTagList.add(searchTag);
		layout.addComponent(searchTag);
		refresh();
	}
	
	public void removeTag(BPTSearchTag searchTag){
				searchTagList.remove(searchTag);
				layout.removeComponent(searchTag);
				((BPTSearchComponent) getParent().getParent()).addTag(searchTag);
				refresh();
	}
	private void refresh() {
		((BPTApplication) getApplication()).refresh(getTagValues());
	}
	public ArrayList<String> getTagValues(){
		ArrayList<String> tagValues = new ArrayList<String>();
		for (int i = 0; i < searchTagList.size(); i++){
			tagValues.add(searchTagList.get(i).getValue());
		}
		return tagValues;
	}
	
}
