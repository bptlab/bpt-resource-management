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
		searchComponent = new BPTSearchComponent(application, "all", false);
		layout.addComponent(loginComponent);
		layout.addComponent(searchComponent);
		
	}

	public void login() {
		searchComponent.login();
		
	}
	
	public void upload(){
		layout.removeComponent(searchComponent);
	}
	public void finder() {
		layout.addComponent(searchComponent);
	}
	
	public void logout(){
		searchComponent.logout();
	}

}
