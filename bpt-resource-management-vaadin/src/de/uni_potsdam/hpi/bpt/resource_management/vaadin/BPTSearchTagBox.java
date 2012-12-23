package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class BPTSearchTagBox extends CustomComponent{
	
	private HorizontalLayout layout;

	public BPTSearchTagBox() {
		layout = new HorizontalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		setCompositionRoot(layout);
		//TODO Komponente erweitern, so dass löschbare Tags angezeigt werden 
		
	};
	public void addTag(String value){
		layout.addComponent(new Label(value));
		
	}

}
