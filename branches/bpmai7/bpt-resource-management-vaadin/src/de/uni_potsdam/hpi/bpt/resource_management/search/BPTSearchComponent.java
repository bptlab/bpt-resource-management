package de.uni_potsdam.hpi.bpt.resource_management.search;

import java.util.ArrayList;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseStatus;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTApplicationUI;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTBoxContainer;

@SuppressWarnings("serial")
public class BPTSearchComponent extends VerticalLayout {
	
	private BPTBoxContainer box;
	private HorizontalLayout boxLayout;
	private BPTFullSearchComponent fullSearchComponent;
	private BPTTagSearchComponent tagSearchComponent;
//	private BPTLanguageSelector languageSelector;
	private BPTApplicationUI applicationUI;

	public BPTSearchComponent(BPTApplicationUI applicationUI, String tagColumns, boolean newTagsAllowed) {
		fullSearchComponent = new BPTFullSearchComponent(applicationUI);
		tagSearchComponent = new BPTTagSearchComponent(applicationUI, tagColumns, newTagsAllowed);
//		languageSelector = (new BPTLanguageSelector(applicationUI));
		this.applicationUI = applicationUI;
		init();
	}

	private void init() {
		setSizeFull();
		boxLayout = new HorizontalLayout();
		addComponent(boxLayout);
		addComponent(fullSearchComponent);
		addComponent(tagSearchComponent);
//		addComponent(languageSelector);
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
		box = new BPTBoxContainer(applicationUI);
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
