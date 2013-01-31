package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTPropertyValueType;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

public abstract class BPTShowEntryComponent extends VerticalLayout{
	
	protected IndexedContainer dataSource;
	
	public BPTShowEntryComponent(){
		ArrayList<BPTToolStatus> statusList = new ArrayList<BPTToolStatus>();
		statusList.add(BPTToolStatus.Published);
		dataSource = BPTContainerProvider.getVisibleEntries(statusList, new ArrayList<String>());
	}
	
	public void showEntries(IndexedContainer dataSource) {
		this.dataSource = dataSource;
		show(dataSource);
	}
	// to be overwritten in subclass
	protected abstract void show(IndexedContainer tableEntries); 
	
	// default solution (entries will be shown in popup), can be overwritten in Subclasses
	protected void showSelectedEntry(Item item) {
		final Window popupWindow = new Window(item.getItemProperty("Name").getValue().toString());
		popupWindow.setWidth("600px");
		
		final String _id = item.getItemProperty("ID").getValue().toString();
		Map<String, Object> tool = ((BPTApplication)getApplication()).getToolRepository().readDocument(_id);
		
		for (Object[] entry : BPTVaadinResources.getEntries()){
			popupWindow.addComponent(new Label(entry[1] + ":"));
			Object value = BPTVaadinResources.generateComponent(((BPTApplication)getApplication()).getToolRepository(), tool, (String)entry[0], (BPTPropertyValueType)entry[3], (String)entry[4]);
			if (entry[2] == Component.class) {
				popupWindow.addComponent((Component)value);
			} else if (entry[2] == Embedded.class) {
				Embedded image = (Embedded)value;
				image.setWidth("");
				image.setHeight("");
				popupWindow.addComponent(image);
			} else {
				popupWindow.addComponent(new Label(value.toString()));
			}
		}
		
		// TODO: use openid.identity (_id) here to identify the user
		if ((((BPTApplication)getApplication()).isLoggedIn() && (((BPTApplication)getApplication()).getName().equals(tool.get("contact_name"))) && (((BPTApplication)getApplication()).getMailAddress().equals(tool.get("contact_mail")))) || ((BPTApplication)getApplication()).isModerated()){
			
			HorizontalLayout layout = new HorizontalLayout();
			popupWindow.addComponent(layout);
			
			Button deleteButton = new Button("delete");
			deleteButton.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					((BPTApplication)getApplication()).getToolRepository().deleteDocument(_id);
					BPTContainerProvider.refreshFromDatabase();
					getWindow().removeWindow(popupWindow);
				}
			});
			layout.addComponent(deleteButton);
			
			BPTToolStatus actualState = ((BPTApplication)getApplication()).getToolRepository().getDocumentStatus(_id);
			
			if (actualState == BPTToolStatus.Unpublished && ((BPTApplication)getApplication()).isModerated()){
				
				Button publishButton = new Button("publish");
				publishButton.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						((BPTApplication)getApplication()).getToolRepository().publishDocument(_id);
						BPTContainerProvider.refreshFromDatabase();
						getWindow().removeWindow(popupWindow);
					}
				});
				layout.addComponent(publishButton);
				
				Button rejectButton = new Button("reject");
				rejectButton.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						((BPTApplication)getApplication()).getToolRepository().rejectDocument(_id);
						BPTContainerProvider.refreshFromDatabase();
						getWindow().removeWindow(popupWindow);
					}
				});
				layout.addComponent(rejectButton);						
				
			}
			else if (actualState == BPTToolStatus.Published) {
				Button unpublishButton = new Button("unpublish");
				unpublishButton.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						((BPTApplication)getApplication()).getToolRepository().unpublishDocument(_id);
						BPTContainerProvider.refreshFromDatabase();
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
						BPTContainerProvider.refreshFromDatabase();
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