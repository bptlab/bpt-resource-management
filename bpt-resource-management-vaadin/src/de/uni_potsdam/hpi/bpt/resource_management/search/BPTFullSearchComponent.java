package de.uni_potsdam.hpi.bpt.resource_management.search;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTApplication;

@SuppressWarnings("serial")
public class BPTFullSearchComponent extends CustomComponent {
	
	private BPTApplication application;
	private HorizontalLayout layout;
	private TextField searchInput;
	private Button searchButton;
	private Button resetButton;

	public BPTFullSearchComponent(BPTApplication application) {
		this.application = application;
		init();
	}

	private void init() {
		layout = new HorizontalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		setCompositionRoot(layout);
		buildMainLayout(layout);
	}

	private void buildMainLayout(HorizontalLayout layout) {
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
		
		layout.addComponent(searchInput);
		buttonLayout.addComponent(searchButton);
		buttonLayout.addComponent(resetButton);
		layout.addComponent(buttonLayout);
		layout.setExpandRatio(searchInput, 1);
		layout.setExpandRatio(buttonLayout, 1);
	}

	private void refresh() {
		application.refresh();
	}
	
	public String getQuery() {
		return (String) searchInput.getValue();
	}
}
