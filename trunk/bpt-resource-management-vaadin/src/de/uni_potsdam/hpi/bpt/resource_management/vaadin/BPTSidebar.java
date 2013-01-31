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
	
	public BPTSidebar(BPTApplication application) {
		this.application = application;
		layout = new VerticalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		setCompositionRoot(layout);
		loginComponent = new BPTLoginComponent(application.isLoggedIn(), this);
		setSearchComponent(new BPTSearchComponent(application, "all", false));
		layout.addComponent(loginComponent);
		layout.addComponent(getSearchComponent());
		
	}

	public void login(String name) {
		getSearchComponent().login();
		loginComponent.login(name);
	}
	
	public void upload(){
		layout.removeComponent(getSearchComponent());
	}
	public void finder() {
		setSearchComponent(new BPTSearchComponent(application, "all", false));
		layout.addComponent(getSearchComponent());
		if(application.isLoggedIn()) getSearchComponent().login();
	}
	
	public void logout(){
		getSearchComponent().logout();
	}

	public BPTSearchComponent getSearchComponent() {
		return searchComponent;
	}

	private void setSearchComponent(BPTSearchComponent searchComponent) {
		this.searchComponent = searchComponent;
	}

}
