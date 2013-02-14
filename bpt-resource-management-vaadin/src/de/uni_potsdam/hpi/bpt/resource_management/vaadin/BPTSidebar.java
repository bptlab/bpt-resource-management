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
		setSearchComponent(new BPTSearchComponent(application, "all", false));
		layout.addComponent(getSearchComponent());
		layout.addComponent(loginComponent);
		layout.setExpandRatio(getSearchComponent(), 8);
		layout.setExpandRatio(loginComponent, 2);
		
	}

	public void login(String name) {
		getSearchComponent().login();
		loginComponent.login(name);
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
		setSearchComponent(new BPTSearchComponent(application, "all", false));
		layout.addComponent(getSearchComponent());
		layout.addComponent(loginComponent);
		layout.setExpandRatio(getSearchComponent(), 8);
		layout.setExpandRatio(loginComponent, 2);
		if(application.isLoggedIn()) getSearchComponent().login();
	}
	
	public void logout(){
		getSearchComponent().logout();
		application.close();
	}

	public BPTSearchComponent getSearchComponent() {
		return searchComponent;
	}

	private void setSearchComponent(BPTSearchComponent searchComponent) {
		this.searchComponent = searchComponent;
	}

}
