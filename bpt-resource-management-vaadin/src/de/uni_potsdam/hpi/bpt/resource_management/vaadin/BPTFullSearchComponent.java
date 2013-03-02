package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class BPTFullSearchComponent extends CustomComponent {
	
	private BPTApplication application;
	private HorizontalLayout layout;
	private TextField searchInput;
	private Label searchLabel;
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
		
		searchLabel = new Label("Full search ");
		
		searchInput = new TextField();
		searchInput.setWidth("90%");
		searchInput.setImmediate(true);
		
		searchButton = new Button("Apply");
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
				refresh();
			}
		});
		
		layout.addComponent(searchLabel);
		layout.addComponent(searchInput);
		buttonLayout.addComponent(searchButton);
		buttonLayout.addComponent(resetButton);
		layout.addComponent(buttonLayout);
		layout.setExpandRatio(searchLabel, 2);
		layout.setExpandRatio(searchInput, 6);
		layout.setExpandRatio(buttonLayout, 2);
	}

	private void refresh() {
		application.refresh();
	}
	
	public String getQuery() {
		return (String)searchInput.getValue();
	}
}
