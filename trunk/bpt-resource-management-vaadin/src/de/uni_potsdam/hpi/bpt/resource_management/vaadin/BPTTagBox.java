package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.VerticalLayout;

import de.uni_potsdam.hpi.bpt.resource_management.search.BPTSearchTag;

@SuppressWarnings("serial")
public class BPTTagBox extends CustomComponent{
	
	protected VerticalLayout baseLayout;
	private GridLayout layout;
	protected ArrayList<BPTSearchTag> searchTagList;

	public BPTTagBox() {
		baseLayout = new VerticalLayout();
		setCompositionRoot(baseLayout);
		addGridsToComponent();
		searchTagList = new ArrayList<BPTSearchTag>();
	}

	protected void addGridsToComponent() {
		layout = new GridLayout(2,1);
		layout.setWidth("100%");
		layout.setHeight("100%");
		baseLayout.addComponent(layout);
	};
	
	public void addTag(String value){
		addTagToLayout(value, layout);
	}
	
	public void addTagToLayout(String value, GridLayout layout){
		BPTSearchTag searchTag = new BPTSearchTag(this, value);
		addSearchTag(searchTag, layout);
	}

	public void addTagToLayout(String value, String type, GridLayout layout){
		BPTSearchTag searchTag = new BPTSearchTag(this, type, value);
		addSearchTag(searchTag, layout);
	}
	
	private void addSearchTag(BPTSearchTag searchTag, GridLayout layout) {
		searchTagList.add(searchTag);
		layout.addComponent(searchTag);
		refresh();
	}
	
	public void removeTag(BPTSearchTag searchTag) {
		removeTagFromLayout(searchTag, layout);
	}
	
	protected void removeTagFromLayout(BPTSearchTag searchTag, GridLayout layout) {
		searchTagList.remove(searchTag);
		int x = layout.getComponentArea(searchTag).getColumn1();
		int y = layout.getComponentArea(searchTag).getRow1();
		int xn = 0;
		int yn = y + 1;
		layout.removeComponent(searchTag);
		if ((x + 1)< layout.getColumns()) {
			xn = x +1;
			yn = y;
		}
		Component nextComponent = layout.getComponent(xn, yn);
		while(nextComponent != null){
			layout.removeComponent(xn, yn);
			layout.addComponent(nextComponent, x, y);
			x = xn;
			y = yn;
			xn = 0;
			yn = y + 1;
			if ((x + 1) < layout.getColumns()){
				xn = x +1;
				yn = y;
			}
			nextComponent = layout.getComponent(xn, yn);
		}
		layout.setCursorX(0);
		layout.setCursorY(0);
		
		((BPTTagComponent) getParent().getParent()).addTag(searchTag);
		refresh();
	}
	
	public void removeAllTags() {
		if (!searchTagList.isEmpty()) {
			searchTagList.clear();
			layout.removeAllComponents();
			refresh();
		}
	}
	
	protected void refresh() {
		((BPTTagComponent) getParent().getParent()).refresh();
	}
	
	public ArrayList<String> getTagValues() {
		ArrayList<String> tagValues = new ArrayList<String>();
		for (int i = 0; i < searchTagList.size(); i++){
			tagValues.add(searchTagList.get(i).getValue());
		}
		return tagValues;
	}
	
}
