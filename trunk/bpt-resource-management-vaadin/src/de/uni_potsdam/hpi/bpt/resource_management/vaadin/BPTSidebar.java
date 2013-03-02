package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class BPTSidebar extends CustomComponent{
	
//	private VerticalLayout layout;
	private HorizontalLayout layout;
	private BPTApplication application;
	private BPTLoginComponent loginComponent;
	private BPTSearchComponent searchComponent;
	
	public BPTSidebar(BPTApplication application) {
		this.application = application;
		
//		layout = new VerticalLayout();
		layout = new HorizontalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		setCompositionRoot(layout);
		loginComponent = new BPTLoginComponent(application.isLoggedIn(), this);
		searchComponent = new BPTSearchComponent(application, "all", false);
		init(layout);		
	}

	public BPTSearchComponent getSearchComponent() {
		return searchComponent;
	}

	public void setSearchComponent(BPTSearchComponent searchComponent) {
		this.searchComponent = searchComponent;
	}

	private void init(HorizontalLayout layout2) {
		layout.addComponent(searchComponent);
		layout.addComponent(loginComponent);
		layout.setExpandRatio(searchComponent, 75);
		layout.setExpandRatio(loginComponent, 25);
	}

	public void login(String name) {
		searchComponent.getTagSearchComponent().login();
		loginComponent.login(name);
	}

	public void logout(){
		searchComponent.getTagSearchComponent().logout();
		application.close();
	}
	
	public void upload(){
		layout.removeAllComponents();
		Label label = new Label("");
		layout.addComponent(label);
		layout.addComponent(loginComponent);
		layout.setExpandRatio(label, 8);
		layout.setExpandRatio(loginComponent, 2);
	}
	
	public void finder() {
		layout.removeAllComponents();
		searchComponent = new BPTSearchComponent(application, "all", false);
		init(layout);
		if (application.isLoggedIn()) {
			searchComponent.getTagSearchComponent().login();
		}
	}

}
