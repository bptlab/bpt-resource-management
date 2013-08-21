package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.List;
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

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseSetRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseStatus;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTPropertyValueType;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

@SuppressWarnings("serial")
public abstract class BPTShowEntryComponent extends VerticalLayout {
	
	protected String _id;
	protected BPTApplication application;
	protected BPTExerciseSetRepository exerciseSetRepository = BPTExerciseSetRepository.getInstance();
	private TextArea reasonForRejectionTextArea;
	
	public BPTShowEntryComponent(final BPTApplication application) {
		this.application = application;
		buildLayout();
		ArrayList<BPTExerciseStatus> statusList = new ArrayList<BPTExerciseStatus>();
		statusList.add(BPTExerciseStatus.Published);
		showNumberOfEntries(BPTContainerProvider.getInstance().getNumberOfEntries(new ArrayList<String>(), statusList, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), null));
		show(BPTContainerProvider.getInstance().getVisibleEntrieSets(application.getSidebar().getSearchComponent().getTagSearchComponent().getLanguageTags(), statusList, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), null, "Name", 0, 10));
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
	 * @param entryIDs
	 */
	protected abstract void show(IndexedContainer sets); 
	
	/**
	 * default solution (entries will be shown in popup), to be overwritten in subclass
	 * 
	 * @param item
	 */
	protected void showSelectedEntry(final Item item) {
		final Window popupWindow = new Window(item.getItemProperty("Name").getValue().toString());
		popupWindow.setWidth("600px");
		
		_id = item.getItemProperty("ID").getValue().toString();
		Map<String, Object> tool = exerciseSetRepository.readDocument(_id);
		
		Object[] attachmentEntry = ((ArrayList<Object[]>)BPTVaadinResources.getEntrySets()).get(1);
		Object value = BPTVaadinResources.generateComponent(exerciseSetRepository, tool, (String)attachmentEntry[0], (BPTPropertyValueType)attachmentEntry[3], (String)attachmentEntry[4], application);
		Embedded image = (Embedded)value;
		image.setWidth("");
		image.setHeight("");
		popupWindow.addComponent(image);
		
		for (Object[] entry : BPTVaadinResources.getEntrySets()) {
			if ((Boolean)entry[7]) {
				popupWindow.addComponent(new Label(entry[1] + ":"));
				value = BPTVaadinResources.generateComponent(exerciseSetRepository, tool, (String)entry[0], (BPTPropertyValueType)entry[3], (String)entry[4], application);
				if (entry[2] == Component.class) {
					popupWindow.addComponent((Component)value);
				} else {
					popupWindow.addComponent(new Label(value.toString()));
				}
			}
			
		}
		
		if ((((BPTApplication)getApplication()).isLoggedIn() && ((BPTApplication)getApplication()).getUser().equals(tool.get("user_id"))) || ((BPTApplication)getApplication()).isModerated()){
			
			HorizontalLayout layout = new HorizontalLayout();
			popupWindow.addComponent(layout);
			
			Button deleteButton = new Button("delete");
			deleteButton.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					addConfirmationWindow(popupWindow, "delete");
				}
			});
			layout.addComponent(deleteButton);
			
			if (((BPTApplication)getApplication()).isLoggedIn() && ((BPTApplication)getApplication()).getUser().equals(tool.get("user_id"))){
				Button editButton = new Button("edit");
				editButton.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						getWindow().removeWindow(popupWindow);
						((BPTApplication)getApplication()).edit(item);
					}
				});
				layout.addComponent(editButton);
			}
			BPTExerciseStatus actualState = exerciseSetRepository.getDocumentStatus(_id);
			
			if (actualState == BPTExerciseStatus.Unpublished && ((BPTApplication)getApplication()).isModerated()){
				
				Button publishButton = new Button("publish");
				publishButton.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						addConfirmationWindow(popupWindow, "publish");
					}
				});
				layout.addComponent(publishButton);
				
				Button rejectButton = new Button("reject");
				rejectButton.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						addConfirmationWindow(popupWindow, "reject");
					}
				});
				layout.addComponent(rejectButton);						
				
			}
			else if (actualState == BPTExerciseStatus.Published) {
				Button unpublishButton = new Button("unpublish");
				unpublishButton.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						addConfirmationWindow(popupWindow, "unpublish");
					}
				});
				layout.addComponent(unpublishButton);	
			}
			else if (actualState == BPTExerciseStatus.Rejected && ((BPTApplication)getApplication()).isModerated()){
				Button proposeButton = new Button("propose");
				proposeButton.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						addConfirmationWindow(popupWindow, "propose");
					}
				});
				layout.addComponent(proposeButton);	
			}
			
		}
		getWindow().addWindow(popupWindow);
		
		
	}
	
	protected void addConfirmationWindow(final Window popupWindow, final String status) {
		final Window confirmationWindow = new Window("Notification");
		confirmationWindow.setWidth("400px");
		confirmationWindow.setModal(true);
		if (status.equals("delete")) {
			confirmationWindow.addComponent(new Label("Deleting this entry - are you sure?"));
		} else if (status.equals("publish")) {
			confirmationWindow.addComponent(new Label("Publishing this entry - are you sure?"));
		} else if (status.equals("reject")) {
			confirmationWindow.addComponent(new Label("Rejecting this entry - are you sure?"));
			reasonForRejectionTextArea = new TextArea();
			reasonForRejectionTextArea.setInputPrompt("Please describe the reason for rejecting the entry and/or provide hints for improving it.");
			reasonForRejectionTextArea.setRows(5);
			reasonForRejectionTextArea.setWidth("95%");
			reasonForRejectionTextArea.setWordwrap(true);
			confirmationWindow.addComponent(reasonForRejectionTextArea);
		} else if (status.equals("unpublish")) { 
			confirmationWindow.addComponent(new Label("Unpublishing this entry - are you sure?"));
		} else { // if status.equals("propose")
			confirmationWindow.addComponent(new Label("Proposing this entry - are you sure?"));
		}
		Button confirmButton = new Button("Confirm");
		confirmationWindow.addComponent(confirmButton);
		confirmButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				if (status.equals("delete")) {
					exerciseSetRepository.deleteDocument(_id, ((BPTApplication)getApplication()).isModerated());
				} else if (status.equals("publish")) {
					exerciseSetRepository.publishDocument(_id);
				} else if (status.equals("reject")) {
					exerciseSetRepository.rejectDocument(_id, (String) reasonForRejectionTextArea.getValue());
				} else if (status.equals("unpublish")) { 
					boolean fromPublished = true;
					exerciseSetRepository.unpublishDocument(_id, fromPublished, ((BPTApplication)getApplication()).isModerated());
				} else { // if status.equals("propose"))
					boolean fromRejected = false;
					exerciseSetRepository.unpublishDocument(_id, fromRejected);
				}
				((BPTApplication) getApplication()).refreshAndClean();
				getWindow().removeWindow(confirmationWindow);
				if(popupWindow != null){
					getWindow().removeWindow(popupWindow);
				}
				
			}
		});
		getWindow().addWindow(confirmationWindow);
		
	}
}
