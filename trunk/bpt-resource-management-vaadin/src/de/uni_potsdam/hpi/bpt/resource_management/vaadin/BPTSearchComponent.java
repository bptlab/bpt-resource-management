package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import com.vaadin.ui.HorizontalLayout;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;

public class BPTSearchComponent extends BPTTagComponent{

	private BPTBoxContainer box;
	private HorizontalLayout boxLayout;
	
	public BPTSearchComponent(BPTApplication application, String tagColumns, boolean newTagsAllowed) {
		super(application, tagColumns, newTagsAllowed);
	}
	
	@Override
	protected void addElements(boolean newTagsAllowed) {
		boxLayout = new HorizontalLayout();
		layout.addComponent(boxLayout);
		box = new BPTBoxContainer(application);
		super.addElements(newTagsAllowed);
	}
	
	public void login(){
		/*  TODO: 
		 *  non-moderators should not see checkboxes 
		 *  or should only see the non-published documents they have submitted 
		 *  plus all published documents when clicking on checkboxes
		 */
//		System.out.println("SearchComponent: " + application.isModerator());
//		if (application.isModerator()) {
			box = new BPTBoxContainer(application);
			boxLayout.addComponent(box);
//		}
	}
	
	public void logout(){
		boxLayout.removeComponent(box);
	}
	
	@Override
	public void refresh() {
		((BPTApplication) getApplication()).refresh();
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
