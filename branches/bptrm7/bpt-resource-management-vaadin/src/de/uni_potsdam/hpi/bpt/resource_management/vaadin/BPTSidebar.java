package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

import de.uni_potsdam.hpi.bpt.resource_management.search.BPTSearchComponent;

@SuppressWarnings({"serial"})
public class BPTSidebar extends HorizontalLayout {
	
	private BPTApplicationUI applicationUI;
	private BPTLoginComponent loginComponent;
	private BPTSearchComponent searchComponent;
	private int numberOfEntries;
	
	public BPTSidebar(BPTApplicationUI applicationUI) {
		super();
		this.applicationUI = applicationUI;
		setSizeFull();
		setWidth("100%");
		setHeight("100%");
		loginComponent = new BPTLoginComponent(applicationUI, this);
		searchComponent = new BPTSearchComponent(applicationUI, "all", false);
		init();
	}
	
	private void init() {
		addComponent(searchComponent);
		addComponent(loginComponent);
		setExpandRatio(searchComponent, 75);
		setExpandRatio(loginComponent, 25);
	}
	
	public BPTSearchComponent getSearchComponent() {
		return searchComponent;
	}

	public void setSearchComponent(BPTSearchComponent searchComponent) {
		this.searchComponent = searchComponent;
	}


	public void login(String name, boolean moderated) {
		searchComponent.login();
		loginComponent.login(name, moderated);
	}

	public void logout(){
		searchComponent.logout();
//		applicationUI.close();
	}
	
	public void renderUploader() {
		removeAllComponents();
		Label label = new Label("required fields marked with *<br/>", ContentMode.HTML);
		addComponent(label);
		addComponent(loginComponent);
		setExpandRatio(label, 75);
		setExpandRatio(loginComponent, 25);
	}
	
	public void renderAdministrator() {
		removeAllComponents();
		Label label = new Label("Administration page <br/>", ContentMode.HTML);
		addComponent(label);
		addComponent(loginComponent);
		setExpandRatio(label, 75);
		setExpandRatio(loginComponent, 25);
}
	
	public void showAll() {
		removeAllComponents();
		searchComponent = new BPTSearchComponent(applicationUI, "all", false);
		init();
		if (applicationUI.isLoggedIn()) {
			searchComponent.login();
		}
	}
	
	public void showSpecificEntry(final String urlToEntry) {
		removeAllComponents();
		VerticalLayout shareLayout = new VerticalLayout();
		
		Label label = new Label("URL to this page:&nbsp;", ContentMode.HTML);
		final TextField textField = new TextField();
		shareLayout.addComponent(label);
		
		textField.addStyleName("boldtextfield");
		textField.setWidth("90%");
		textField.setValue(urlToEntry);
		textField.setReadOnly(true);
		shareLayout.addComponent(textField);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		
		Button startButton = new Button("back to start page");
		startButton.setStyleName(BaseTheme.BUTTON_LINK);
		startButton.addClickListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				Page.getCurrent().setUriFragment("");
				applicationUI.showStartPage();
			}
		});
		buttonLayout.addComponent(startButton);
		
		buttonLayout.addComponent(new Label("&nbsp;&nbsp; or go &nbsp;&nbsp;", ContentMode.HTML));
		
		Button findButton = new Button("See all " + this.numberOfEntries + " entries of Tools for BPM");
		findButton.setStyleName(BaseTheme.BUTTON_LINK);
		findButton.addClickListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				Page.getCurrent().setUriFragment("");
				applicationUI.showAllAndRefreshSidebar(true);
			}
		});
		buttonLayout.addComponent(findButton);
		
		shareLayout.addComponent(buttonLayout);
		addComponent(shareLayout);
		addComponent(loginComponent);
		setExpandRatio(shareLayout, 75);
		setExpandRatio(loginComponent, 25);
	}

	public void setNumberOfEntries(int numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
	}

}