package de.uni_potsdam.hpi.bpt.resource_management.search;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTApplicationUI;

@SuppressWarnings({"serial"})
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
		setSizeFull();
		buildMainLayout();
	}

	private void buildMainLayout() {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		
		searchInput = new TextField();
		searchInput.setWidth("300px");
		searchInput.setInputPrompt("Full-text search");
		searchInput.setImmediate(true);
		
		searchButton = new Button("Search");
		searchButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				refresh();
			}
		});
		searchButton.setClickShortcut(KeyCode.ENTER);
		
		resetButton = new Button("Reset");
		resetButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				searchInput.setValue("");
				((BPTSearchComponent) getParent()).getTagSearchComponent().restoreAllTags();
				refresh();
			}
		});
		
		addComponent(searchInput);
		buttonLayout.addComponent(searchButton);
		buttonLayout.addComponent(resetButton);
		addComponent(buttonLayout);
		setExpandRatio(searchInput, 7);
		setExpandRatio(buttonLayout, 5);
	}

	private void refresh() {
		applicationUI.refreshAndClean();
	}
	
	public String getQuery() {
		return (String) searchInput.getValue();
	}
}
