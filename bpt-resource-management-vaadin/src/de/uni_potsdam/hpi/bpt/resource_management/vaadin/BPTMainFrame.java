package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

public class BPTMainFrame extends CustomComponent{

	private VerticalLayout layout;
	
	public BPTMainFrame(Component component){
		
		layout = new VerticalLayout();
		setCompositionRoot(layout);
		setWidth("100%");
		layout.addComponent(component);
	}
	
	public void add(Component component){
		layout.removeAllComponents();
		layout.addComponent(component);
	}
}
