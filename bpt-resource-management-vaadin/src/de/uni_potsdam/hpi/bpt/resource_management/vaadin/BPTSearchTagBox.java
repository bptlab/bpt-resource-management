package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;

@SuppressWarnings("serial")
public class BPTSearchTagBox extends CustomComponent{
	
	private GridLayout layout;
	private ArrayList<BPTSearchTag> searchTagList;

	public BPTSearchTagBox() {
		layout = new GridLayout(2,1);
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
		int x = layout.getComponentArea(searchTag).getColumn1();
		int y = layout.getComponentArea(searchTag).getRow1();
		int xn = 0;
		int yn = y + 1;
		layout.removeComponent(searchTag);
		if ((x + 1)< layout.getColumns()){
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
	
	private void refresh() {
		((BPTTagComponent) getParent().getParent()).refresh();
	}
	
	public ArrayList<String> getTagValues(){
		ArrayList<String> tagValues = new ArrayList<String>();
		for (int i = 0; i < searchTagList.size(); i++){
			tagValues.add(searchTagList.get(i).getValue());
		}
		return tagValues;
	}
	
}
