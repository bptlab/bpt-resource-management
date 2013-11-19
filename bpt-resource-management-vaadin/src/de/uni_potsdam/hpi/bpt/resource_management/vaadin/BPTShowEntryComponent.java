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
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentType;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTPropertyValueType;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

@SuppressWarnings({"serial"})
public abstract class BPTShowEntryComponent extends VerticalLayout {
	
	protected String _id, entryId;
	protected BPTApplicationUI applicationUI;
	protected BPTToolRepository toolRepository = BPTToolRepository.getInstance();
	
	public BPTShowEntryComponent(BPTApplicationUI applicationUI, boolean loadEntries){
		if(loadEntries){
			init(applicationUI);
		}
		else {
			this.applicationUI = applicationUI;
			buildLayout();
			showNumberOfEntries(0);
		}
	}
	
	public BPTShowEntryComponent(BPTApplicationUI applicationUI, String entryId){
		this.entryId = entryId;
		init(applicationUI);
	}
	
	public BPTShowEntryComponent(BPTApplicationUI applicationUI) {
		init(applicationUI);
	}
	
	protected void init(BPTApplicationUI applicationUI) {
		this.applicationUI = applicationUI;
		buildLayout();
		ArrayList<BPTToolStatus> statusList = new ArrayList<BPTToolStatus>();
		statusList.add(BPTToolStatus.Published);
		showNumberOfEntries(BPTContainerProvider.getInstance().getNumberOfEntries(statusList, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), null));
		show(getEntries(statusList));
	}
	
	protected IndexedContainer getEntries(ArrayList<BPTToolStatus> statusList) {
		return BPTContainerProvider.getInstance().getVisibleEntries(statusList, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), null, "Name", 0, 10);
	}
	
	protected abstract void buildLayout();
	
	
	/**
	 * @abstract to be overwritten by subclass
	 * 
	 * @param numberOfEntries
	 */
	protected abstract void showNumberOfEntries(int numberOfEntries);
	
	/**
	 * @abstract to be overwritten by subclass
	 * 
	 * @param tableEntries
	 */
	protected abstract void show(IndexedContainer tableEntries); 
	
	/**
	 * default solution (entries will be shown in popup), to be overwritten in subclass
	 * 
	 * @param item
	 */
	protected void showSelectedEntry(final Item item) {
		final Window popupWindow = new Window(item.getItemProperty("Name").getValue().toString());
		popupWindow.setWidth("600px");
		VerticalLayout popupWindowLayout = new VerticalLayout();
		
		_id = item.getItemProperty("ID").getValue().toString();
		Map<String, Object> tool = toolRepository.readDocument(_id);
		
		Object[] attachmentEntry = ((ArrayList<Object[]>)BPTVaadinResources.getPropertyArray(BPTDocumentType.BPT_RESOURCES_TOOLS)).get(1);
		Object value = BPTVaadinResources.generateComponent(toolRepository, tool, (String)attachmentEntry[0], (BPTPropertyValueType)attachmentEntry[3], (String)attachmentEntry[4]);
		Embedded image = (Embedded)value;
		image.setWidth("");
		image.setHeight("");
		popupWindowLayout.addComponent(image);
		
		for (Object[] entry : BPTVaadinResources.getPropertyArray(BPTDocumentType.BPT_RESOURCES_TOOLS)) {
			if ((Boolean)entry[7]) {
				popupWindowLayout.addComponent(new Label(entry[1] + ":"));
				value = BPTVaadinResources.generateComponent(toolRepository, tool, (String)entry[0], (BPTPropertyValueType)entry[3], (String)entry[4]);
				if (entry[2] == Component.class) {
					popupWindowLayout.addComponent((Component)value);
				} else {
					popupWindowLayout.addComponent(new Label(value.toString()));
				}
			}
			
		}
		
		if ((applicationUI.isLoggedIn() && applicationUI.getUser().equals(tool.get("user_id"))) || applicationUI.isModerated()){
			
			HorizontalLayout layout = new HorizontalLayout();
			popupWindowLayout.addComponent(layout);
			
			Button deleteButton = new Button("delete");
			deleteButton.addClickListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					addConfirmationWindow(popupWindow, "delete");
				}
			});
			layout.addComponent(deleteButton);
			
			if (applicationUI.isLoggedIn() && applicationUI.getUser().equals(tool.get("user_id"))){
				Button editButton = new Button("edit");
				editButton.addClickListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						getUI().removeWindow(popupWindow);
						((BPTApplicationUI)getUI()).edit(item);
					}
				});
				layout.addComponent(editButton);
			}
			BPTToolStatus actualState = toolRepository.getDocumentStatus(_id);
			
			if (actualState == BPTToolStatus.Unpublished && applicationUI.isModerated()){
				
				Button publishButton = new Button("publish");
				publishButton.addClickListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						addConfirmationWindow(popupWindow, "publish");
					}
				});
				layout.addComponent(publishButton);
				
				Button rejectButton = new Button("reject");
				rejectButton.addClickListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						addConfirmationWindow(popupWindow, "reject");
					}
				});
				layout.addComponent(rejectButton);						
				
			}
			else if (actualState == BPTToolStatus.Published) {
				Button unpublishButton = new Button("unpublish");
				unpublishButton.addClickListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						addConfirmationWindow(popupWindow, "unpublish");
					}
				});
				layout.addComponent(unpublishButton);	
			}
			else if (actualState == BPTToolStatus.Rejected && ((BPTApplicationUI)getUI()).isModerated()){
				Button proposeButton = new Button("propose");
				proposeButton.addClickListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						addConfirmationWindow(popupWindow, "propose");
					}
				});
				layout.addComponent(proposeButton);	
			}
			
		}
		popupWindow.setContent(popupWindowLayout);
		getUI().addWindow(popupWindow);
	}
	
	protected void addConfirmationWindow(final Window popupWindow, final String status) {
		final TextArea reasonForRejectionTextArea = new TextArea();
		final Window confirmationWindow = new Window("Notification");
		VerticalLayout confirmationWindowLayout = new VerticalLayout();
		confirmationWindow.setWidth("400px");
		confirmationWindow.setModal(true);
		if (status.equals("delete")) {
			confirmationWindowLayout.addComponent(new Label("Deleting this entry - are you sure?"));
		} else if (status.equals("publish")) {
			confirmationWindowLayout.addComponent(new Label("Publishing this entry - are you sure?"));
		} else if (status.equals("reject")) {
			confirmationWindowLayout.addComponent(new Label("Rejecting this entry - are you sure?"));
			reasonForRejectionTextArea.setInputPrompt("Please describe the reason for rejecting the entry and/or provide hints for improving it.");
			reasonForRejectionTextArea.setRows(5);
			reasonForRejectionTextArea.setWidth("95%");
			reasonForRejectionTextArea.setWordwrap(true);
			confirmationWindowLayout.addComponent(reasonForRejectionTextArea);
		} else if (status.equals("unpublish")) { 
			confirmationWindowLayout.addComponent(new Label("Unpublishing this entry - are you sure?"));
		} else { // if status.equals("propose")
			confirmationWindowLayout.addComponent(new Label("Proposing this entry - are you sure?"));
		}
		Button confirmButton = new Button("Confirm");
		confirmationWindowLayout.addComponent(confirmButton);
		confirmButton.addClickListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				if (status.equals("delete")) {
					toolRepository.deleteDocument(_id, ((BPTApplicationUI)getUI()).isModerated());
				} else if (status.equals("publish")) {
					toolRepository.publishDocument(_id);
				} else if (status.equals("reject")) {
					toolRepository.rejectDocument(_id, (String) reasonForRejectionTextArea.getValue());
				} else if (status.equals("unpublish")) { 
					boolean fromPublished = true;
					toolRepository.unpublishDocument(_id, fromPublished, ((BPTApplicationUI)getUI()).isModerated());
				} else { // if status.equals("propose"))
					boolean fromRejected = false;
					toolRepository.unpublishDocument(_id, fromRejected);
				}
				BPTContainerProvider.getInstance().refreshFromDatabase();
				applicationUI.refreshAndClean();
				getUI().removeWindow(confirmationWindow);
				if(popupWindow != null){
					getUI().removeWindow(popupWindow);
				}
				
			}
		});
		confirmationWindow.setContent(confirmationWindowLayout);
		getUI().addWindow(confirmationWindow);
	}
}
