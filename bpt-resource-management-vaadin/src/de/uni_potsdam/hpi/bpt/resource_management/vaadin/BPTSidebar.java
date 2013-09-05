package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

import de.uni_potsdam.hpi.bpt.resource_management.search.BPTSearchComponent;

@SuppressWarnings("serial")
public class BPTSidebar extends CustomComponent {
	
	private HorizontalLayout layout;
	private BPTApplication application;
	private BPTLoginComponent loginComponent;
	private BPTSearchComponent searchComponent;
	
	public BPTSidebar(BPTApplication application) {
		this.application = application;
		
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

	private void init(HorizontalLayout layout) {
		layout.addComponent(searchComponent);
		layout.addComponent(loginComponent);
		layout.setExpandRatio(searchComponent, 75);
		layout.setExpandRatio(loginComponent, 25);
	}

	public void login(String name) {
		searchComponent.login();
		loginComponent.login(name);
	}

	public void logout(){
		searchComponent.logout();
		application.close();
	}
	
	public void upload() {
		layout.removeAllComponents();
//		layout = new HorizontalLayout();
		Label label = new Label("required fields marked with *<br/>", Label.CONTENT_XHTML);
		layout.addComponent(label);
		layout.addComponent(loginComponent);
		layout.setExpandRatio(label, 75);
		layout.setExpandRatio(loginComponent, 25);
	}
	
	public void finder() {
		layout.removeAllComponents();
//		layout = new HorizontalLayout();
		searchComponent = new BPTSearchComponent(application, "all", false);
		init(layout);
		if (application.isLoggedIn()) {
			searchComponent.login();
		}
	}
	
	public void showSpecificEntry(final String urlToEntry) {
		layout.removeAllComponents();
		VerticalLayout shareLayout = new VerticalLayout();
		
		Label label = new Label("URL to this page:&nbsp;", Label.CONTENT_XHTML);
		final TextField textField = new TextField();
		shareLayout.addComponent(label);
		
		textField.addStyleName("boldtextfield");
		textField.setWidth("90%");
		textField.setValue(urlToEntry);
		textField.setReadOnly(true);
		shareLayout.addComponent(textField);

		Button findButton = new Button("See all entries of Tools for BPM");
		findButton.setStyleName(BaseTheme.BUTTON_LINK);
		findButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				((BPTApplication)getApplication()).getUriFragmentUtility().setFragment("");
				((BPTApplication)getApplication()).finder();
			}
		});
		shareLayout.addComponent(findButton);
		layout.addComponent(shareLayout);
		layout.addComponent(loginComponent);
		layout.setExpandRatio(shareLayout, 75);
		layout.setExpandRatio(loginComponent, 25);
	}

}
