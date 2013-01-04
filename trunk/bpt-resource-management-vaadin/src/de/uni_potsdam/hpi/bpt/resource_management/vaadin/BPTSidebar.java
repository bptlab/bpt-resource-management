package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class BPTSidebar extends CustomComponent{
	
	private VerticalLayout layout;
	private BPTApplication application;
	private BPTLoginComponent loginComponent;
	
	public BPTSidebar(BPTApplication application) {
		this.application = application;
		layout = new VerticalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		setCompositionRoot(layout);
		loginComponent = new BPTLoginComponent(application.getUsername(), application.isLoggedIn());
		BPTSearchComponent searchComponent = new BPTSearchComponent();
		layout.addComponent(loginComponent);
		layout.addComponent(searchComponent);
		
	}
	public void refresh(ArrayList<String> tagValues) {
		application.refresh(tagValues);
		
	}
	

}
