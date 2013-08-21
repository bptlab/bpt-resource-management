package de.uni_potsdam.hpi.bpt.resource_management.search;

import java.util.ArrayList;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseStatus;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTApplication;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTBoxContainer;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTLanguageSelector;

@SuppressWarnings("serial")
public class BPTSearchComponent extends CustomComponent {

	private VerticalLayout layout;
	private BPTBoxContainer box;
	private HorizontalLayout boxLayout;
	private BPTFullSearchComponent fullSearchComponent;
	private BPTTagSearchComponent tagSearchComponent;
//	private BPTLanguageSelector languageSelector;
	private BPTApplication application;

	public BPTSearchComponent(BPTApplication application, String tagColumns, boolean newTagsAllowed) {
		fullSearchComponent = new BPTFullSearchComponent(application);
		tagSearchComponent = new BPTTagSearchComponent(application, tagColumns, newTagsAllowed);
//		languageSelector = (new BPTLanguageSelector(application));
		this.application = application;
		init();
	}

	private void init() {
		layout = new VerticalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		setCompositionRoot(layout);
		boxLayout = new HorizontalLayout();
		layout.addComponent(boxLayout);
		layout.addComponent(fullSearchComponent);
		layout.addComponent(tagSearchComponent);
//		layout.addComponent(languageSelector);
	}

	public BPTFullSearchComponent getFullSearchComponent() {
		return fullSearchComponent;
	}

	public void setFullSearchComponent(BPTFullSearchComponent fullSearchComponent) {
		this.fullSearchComponent = fullSearchComponent;
	}

	public BPTTagSearchComponent getTagSearchComponent() {
		return tagSearchComponent;
	}

	public void setTagSearchComponent(BPTTagSearchComponent tagSearchComponent) {
		this.tagSearchComponent = tagSearchComponent;
	}
	
	public void login() {
		boxLayout.removeAllComponents();
		box = new BPTBoxContainer(application);
		boxLayout.addComponent(box);
	}
	
	public void logout() {
		boxLayout.removeAllComponents();
	}
	
	public ArrayList<BPTExerciseStatus> getSelectedStates() {
		return box.getSelectedStates();
	}
	
	public boolean isOwnEntriesOptionSelected() {
		return box.isOwnEntriesOptionSelected();
	}

//	public BPTLanguageSelector getLanguageSelector() {
//		return languageSelector;
//	}

}
