package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class BPTSidebar extends CustomComponent{
	
	private VerticalLayout layout;
	public BPTSidebar() {
		layout = new VerticalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		setCompositionRoot(layout);
		BPTLoginComponent loginComponent = new BPTLoginComponent();
		BPTSearchComponent searchComponent = new BPTSearchComponent();
		layout.addComponent(loginComponent);
		layout.addComponent(searchComponent);
		
		
		
	}
	
	

}
