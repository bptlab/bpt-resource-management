package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentStatus;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTPropertyValueType;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

public abstract class BPTShowEntryComponent extends CustomComponent{
	
	private IndexedContainer dataSource;
	
	public void showEntries(IndexedContainer dataSource) {
		this.dataSource = dataSource;
		show(dataSource);
	}
	
	protected abstract void show(IndexedContainer tableEntries); 
	

	private void showSelectedEntry(Item item) {
		final Window popupWindow = new Window(item.getItemProperty("Name").getValue().toString());
		popupWindow.setWidth("600px");
		
		final String _id = item.getItemProperty("ID").getValue().toString();
		Map<String, Object> tool = ((BPTApplication)getApplication()).getToolRepository().readDocument(_id);
		
		for (Object[] entry : BPTVaadinResources.getEntries("BPTTool")){
			popupWindow.addComponent(new Label(entry[1] + ":"));
			Object value = BPTVaadinResources.generateComponent(((BPTApplication)getApplication()).getToolRepository(), tool, (String)entry[0], (BPTPropertyValueType)entry[3], (String)entry[4]);
			if (entry[2] == Component.class) {
				popupWindow.addComponent((Component)value);
			} else if (entry[2] == Embedded.class) {
				popupWindow.addComponent((Embedded)value);
			} else {
				popupWindow.addComponent(new Label(value.toString()));
			}
		}
		
		if (((BPTApplication)getApplication()).isLoggedIn()){
			
			HorizontalLayout layout = new HorizontalLayout();
			popupWindow.addComponent(layout);
			
			Button deleteButton = new Button("delete");
			deleteButton.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					((BPTApplication)getApplication()).getToolRepository().deleteDocument(_id);
					getWindow().removeWindow(popupWindow);
				}
			});
			layout.addComponent(deleteButton);
			
			BPTDocumentStatus actualState = ((BPTApplication)getApplication()).getToolRepository().getDocumentStatus(_id);
			
			if (actualState == BPTDocumentStatus.Unpublished){
				
				Button publishButton = new Button("publish");
				publishButton.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						((BPTApplication)getApplication()).getToolRepository().publishDocument(_id);
						getWindow().removeWindow(popupWindow);
					}
				});
				layout.addComponent(publishButton);
				
				Button rejectButton = new Button("reject");
				rejectButton.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						((BPTApplication)getApplication()).getToolRepository().rejectDocument(_id);
						getWindow().removeWindow(popupWindow);
					}
				});
				layout.addComponent(rejectButton);						
				
			}
			else if (actualState == BPTDocumentStatus.Published) {
				Button unpublishButton = new Button("unpublish");
				unpublishButton.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						((BPTApplication)getApplication()).getToolRepository().unpublishDocument(_id);
						getWindow().removeWindow(popupWindow);
					}
				});
				layout.addComponent(unpublishButton);	
			}
			else {
				Button proposeButton = new Button("propose");
				proposeButton.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						((BPTApplication)getApplication()).getToolRepository().unpublishDocument(_id);
						getWindow().removeWindow(popupWindow);
					}
				});
				layout.addComponent(proposeButton);	
			}
			
		}
		getWindow().addWindow(popupWindow);
		
		
	}
}




//TODO: implement Super-Class for BPTTable