package de.uni_potsdam.hpi.bpt.resource_management.search;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTApplicationUI;

@SuppressWarnings("serial")
public class BPTFullSearchComponent extends HorizontalLayout {
	
	private BPTApplicationUI applicationUI;
	private TextField searchInput;
	private Button searchButton;
	private Button resetButton;

	public BPTFullSearchComponent(BPTApplicationUI applicationUI) {
		this.applicationUI = applicationUI;
		init();
	}

	private void init() {
		setWidth("100%");
		setHeight("100%");
		buildMainLayout();
	}

	private void buildMainLayout() {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		
		searchInput = new TextField();
		searchInput.setWidth("300px");
		searchInput.setInputPrompt("Full-text search");
		searchInput.setImmediate(true);
		
		searchButton = new Button("Search");
		searchButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				refresh();
			}
		});
		searchButton.setClickShortcut(KeyCode.ENTER);
		
		resetButton = new Button("Reset");
		resetButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				searchInput.setValue("");
				((BPTSearchComponent) getParent().getParent()).getTagSearchComponent().restoreAllTags();
				refresh();
			}
		});
		
		addComponent(searchInput);
		buttonLayout.addComponent(searchButton);
		buttonLayout.addComponent(resetButton);
		addComponent(buttonLayout);
		setExpandRatio(searchInput, 1);
		setExpandRatio(buttonLayout, 1);
	}

	private void refresh() {
		applicationUI.refreshAndClean();
	}
	
	public String getQuery() {
		return (String) searchInput.getValue();
	}
}
