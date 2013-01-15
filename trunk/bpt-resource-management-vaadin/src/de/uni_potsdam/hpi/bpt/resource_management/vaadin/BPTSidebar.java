package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class BPTSidebar extends CustomComponent{
	
	private VerticalLayout layout;
	private BPTApplication application;
	private BPTLoginComponent loginComponent;
	private BPTSearchComponent searchComponent;
	private VerticalLayout spaceHolder;
	
	public BPTSidebar(BPTApplication application) {
		this.application = application;
		layout = new VerticalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		setCompositionRoot(layout);
		loginComponent = new BPTLoginComponent(application.getUsername(), application.isLoggedIn(), this);
		spaceHolder = new VerticalLayout();
		searchComponent = new BPTSearchComponent("all", false);
		layout.addComponent(loginComponent);
		layout.addComponent(spaceHolder);
		layout.addComponent(searchComponent);
		
	}
	public void refresh(ArrayList<String> tagValues) {
		application.refresh(tagValues);
		
	}
	public void login() {
		spaceHolder.addComponent(new BPTBoxContainer());
		
	}
	
	public void upload(){
		layout.removeComponent(searchComponent);
		spaceHolder.removeAllComponents();
		layout.removeComponent(spaceHolder);
	}
	public void finder() {
		layout.addComponent(spaceHolder);
		if (application.isLoggedIn()) spaceHolder.addComponent(new BPTBoxContainer());
		layout.addComponent(searchComponent);
	}
	
	public void logout(){
		spaceHolder.removeAllComponents();
	}

}
