package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import com.vaadin.ui.HorizontalLayout;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;

@SuppressWarnings("serial")
public class BPTTagSearchComponent extends BPTTagComponent{

	private BPTBoxContainer box;
	private HorizontalLayout boxLayout;
	
	public BPTTagSearchComponent(BPTApplication application, String tagColumns, boolean newTagsAllowed) {
		// TODO: Label with caption "Tag search "
		super(application, tagColumns, newTagsAllowed);
//		searchInput.setWidth("70%");
	}
	
	@Override
	protected void addElements(boolean newTagsAllowed) {
		boxLayout = new HorizontalLayout();
		layout.addComponent(boxLayout);
		box = new BPTBoxContainer(application);
		super.addElements(newTagsAllowed);
	}
	
	public void login() {
		boxLayout.removeAllComponents();
		box = new BPTBoxContainer(application);
		boxLayout.addComponent(box);
	}
	
	public void logout() {
		boxLayout.removeComponent(box);
	}
	
	@Override
	public void refresh() {
		application.refresh();
	}
	
	public ArrayList<BPTToolStatus> getSelectedStates() {
		return box.getSelectedStates();
	}
	
	public ArrayList<String> getSelectedTags() {
		return searchTagBox.getTagValues();
	}
	
	public boolean isOwnEntriesOptionSelected() {
		return box.isOwnEntriesOptionSelected();
	}
	
	@Override
	public void addChosenTag(String value){
		searchTagBox.addTag(value);
	}

}
