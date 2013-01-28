package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

public class BPTSearchComponent extends BPTTagComponent{

	private BPTApplication application;
	private BPTBoxContainer box;
	private HorizontalLayout boxLayout;
	
	public BPTSearchComponent(BPTApplication application, String tagColumns, boolean newTagsAllowed) {
		super(tagColumns, newTagsAllowed);
	}
	@Override
	protected void addElements(boolean newTagsAllowed) {
		boxLayout = new HorizontalLayout();
		layout.addComponent(boxLayout);
		box = new BPTBoxContainer();
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
			boxLayout.addComponent(box);
//		}
	}
	
	public void logout(){
		boxLayout.removeComponent(box);
	}
	

	@Override
	public void refresh(){
		((BPTApplication) getApplication()).getTable().showEntries(BPTContainerProvider.getVisibleEntries(box.getSelectedStates(), searchTagBox.getTagValues()));
	}

}
