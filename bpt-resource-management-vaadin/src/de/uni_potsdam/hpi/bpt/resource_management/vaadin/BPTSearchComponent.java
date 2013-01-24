package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

public class BPTSearchComponent extends BPTTagComponent{

	private BPTBoxContainer box;
	private HorizontalLayout boxLayout;
	
	public BPTSearchComponent(String tagColumns, boolean newTagsAllowed) {
		super(tagColumns, newTagsAllowed);
	}
	@Override
	protected void addElements(boolean newTagsAllowed) {
		boxLayout = new HorizontalLayout();
		layout.addComponent(boxLayout);
		super.addElements(newTagsAllowed);
	}
	
	public void login(){
		box = new BPTBoxContainer();
		boxLayout.addComponent(box);
	}
	
	public void logout(){
		boxLayout.removeComponent(box);
	}
	
	public void refreshContent(){
		((BPTApplication) getApplication()).getTable().setContent(BPTContainerProvider.getVisibleEntries(box.getSelectedStates(), searchTagBox.getTagValues()));
	}
	
	@Override
	public void refresh(){
		((BPTApplication) getApplication()).getTable().setContent(BPTContainerProvider.getVisibleEntries(box.getSelectedStates(), searchTagBox.getTagValues()));
	}

}
