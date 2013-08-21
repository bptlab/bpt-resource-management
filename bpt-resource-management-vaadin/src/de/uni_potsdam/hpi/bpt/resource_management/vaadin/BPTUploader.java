package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

import de.uni_potsdam.hpi.bpt.resource_management.BPTValidator;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseStatus;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTTopic;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTPropertyValueType;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

@SuppressWarnings("serial")
public class BPTUploader extends VerticalLayout implements TabSheet.SelectedTabChangeListener {
	
	private String set_id;
	private BPTApplication application;
	private BPTExerciseRepository exerciseRepository = BPTExerciseRepository.getInstance();
	private TabSheet tabSheet;
	private Label topicLabel;
	private BPTTagComponent topic;
	private Label modelingLanguageLabel;
	private BPTTagComponent modelingLanguage;
	private Label taskTypeLabel;
	private BPTTagComponent taskType;
	private Label additionalTagsLabel;
	private BPTTagComponent other;
	private Label contactNameLabel;
	private TextField contactNameInput;
	private Label contactMailLabel;
	private TextField contactMailInput;
	private Button finishUploadButton;
	private BPTUploadPanel lastPanel;
	private ArrayList<String> namesOfOldAttachments;
	
	public BPTUploader(Item item, final BPTApplication application) {
		super();
		this.application = application;
		
        Label label = new Label("<br/> <hr/> <br/>", Label.CONTENT_XHTML);
        addComponent(label);
		
		this.tabSheet = new TabSheet();
		addComponent(tabSheet);
		tabSheet.addListener(this);
		addTagComponents();
        
		addContactInputs();
		
		addSubmitButton();
		
        if (item != null) {
        	set_id = item.getItemProperty("Exercise Set ID").getValue().toString();
        	namesOfOldAttachments = (ArrayList<String>) exerciseRepository.readDocument(item.getItemProperty("ID").getValue().toString()).get("names_of_attachments");
        	List<Map> map = exerciseRepository.getDocumentsBySetId(set_id);
        	IndexedContainer entries = BPTContainerProvider.getInstance().generateContainer(map);
     		for (Object id : entries.getItemIds()) {
 				Item nextItem = entries.getItem(id);
 				BPTUploadPanel nextPanel = new BPTUploadPanel(nextItem, application, this);
 				this.tabSheet.addComponent(nextPanel);
 				tabSheet.getTab(nextPanel).setClosable(true);
 				nextPanel.putLanguageInput(nextItem.getItemProperty("Language").getValue().toString());
     		}
     		setTagValues(item);
        	setContactDates(item);
        } else {
        	//XXX
        	set_id = null;
        	BPTUploadPanel newEntryPanel = addNewUploadPanel("new Entry");
        	tabSheet.getTab(newEntryPanel).setClosable(true);
        }
        lastPanel = addNewUploadPanel("+");
        
		
	}
	
	public void selectedTabChange(SelectedTabChangeEvent event) {
		System.out.println("tab changed");
		if(tabSheet.getSelectedTab() == lastPanel){
			tabSheet.getTab(lastPanel).setCaption("new Entry");
			tabSheet.getTab(lastPanel).setClosable(true);
			lastPanel = addNewUploadPanel("+");
		}
	}

	private void setContactDates(Item item) {
		contactNameInput.setValue(item.getItemProperty("Contact name").getValue().toString());
		contactMailInput.setValue(((Link)item.getItemProperty("Contact mail").getValue()).getCaption());
	}

	private void addContactInputs() {
		contactNameLabel = new Label("Contact name * <font color=\"#BBBBBB\">as shown on the website</font>", Label.CONTENT_XHTML);
		addComponent(contactNameLabel);
		contactNameInput = new TextField();
		contactNameInput.setValue(application.getName());
		contactNameInput.setWidth("100%");
		addComponent(contactNameInput);
		
		contactMailLabel = new Label("Contact mail * <font color=\"#BBBBBB\">as shown on the website - notifications will be sent to the mail address you have been using for logon</font>", Label.CONTENT_XHTML);
		addComponent(contactMailLabel);
		contactMailInput = new TextField();
		contactMailInput.setValue(application.getMailAddress());
		contactMailInput.setWidth("100%");
		addComponent(contactMailInput);
	}

	private void setTagValues(Item item) {
		Object x = item.getItemProperty("Topics").getValue();
		if(!(item.getItemProperty("Topics").getValue().toString().equals(""))){
			String[] model_type = ((String) item.getItemProperty("Topics").getValue()).split(",");
			for(int i = 0; i < model_type.length; i++) topic.addChosenTag(model_type[i].trim().replaceAll(" +", " "));
		}
		if(!(item.getItemProperty("Modeling Languages").getValue().toString().equals(""))){
			String[] platform = ((String) item.getItemProperty("Modeling Languages").getValue()).split(",");
			for(int i = 0; i < platform.length; i++) modelingLanguage.addChosenTag(platform[i].trim().replaceAll(" +", " "));
		}
		if(!(item.getItemProperty("Task Types").getValue().toString().equals(""))){
			String[] supported_functionality = ((String) item.getItemProperty("Task Types").getValue()).split(",");
			for(int i = 0; i < supported_functionality.length; i++) taskType.addChosenTag(supported_functionality[i].trim().replaceAll(" +", " "));
		}
		if(!(item.getItemProperty("Other tags").getValue().toString().equals(""))){
			String[] availability = ((String) item.getItemProperty("Other tags").getValue()).split(",");
			for(int i = 0; i < availability.length; i++) other.addChosenTag(availability[i].trim().replaceAll(" +", " "));
		}
	}

	public void addTagComponents() {
		topicLabel = new Label("Topics *");
		addComponent(topicLabel);
		topic = new BPTTagComponent(application, "topics", false);
		topic.setWidth("100%");
		addComponent(topic);
		
		modelingLanguageLabel = new Label("Modeling Languages");
		addComponent(modelingLanguageLabel);
		modelingLanguage = new BPTTagComponent(application, "modelTypes", true);
		modelingLanguage.setWidth("100%");
		addComponent(modelingLanguage);
		
		taskTypeLabel = new Label("Task Types");
		addComponent(taskTypeLabel);
		taskType = new BPTTagComponent(application, "taskTypes", true);
		taskType.setWidth("100%");
		addComponent(taskType);
		
		additionalTagsLabel = new Label("Additional Tags");
		addComponent(additionalTagsLabel);
		other = new BPTTagComponent(application, "otherTags", true);
		other.setWidth("100%");
		addComponent(other);
	}

	public BPTUploadPanel addNewUploadPanel(String caption) {
		BPTUploadPanel panel = new BPTUploadPanel(null, application, this);
		this.tabSheet.addComponent(panel);
		this.tabSheet.getTab(panel).setCaption(caption);
		return panel;
	}

	public String getSetId() {
		return set_id;
	}
	
	public TabSheet getTabSheet(){
		return tabSheet;
	}
	
	private void addSubmitButton() {
		finishUploadButton = new Button("Submit");
		addComponent(finishUploadButton);
		finishUploadButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				
//				if (((String)titleInput.getValue()).isEmpty()) {
//					getWindow().showNotification("'Title' field is empty", Notification.TYPE_ERROR_MESSAGE);
//				}
				if (((String)contactNameInput.getValue()).isEmpty()) {
					getWindow().showNotification("'Contact name' field is empty", Notification.TYPE_ERROR_MESSAGE);
				} else if (!BPTValidator.isValidEmail((String)contactMailInput.getValue())) {
					getWindow().showNotification("Invalid e-mail address", "in field 'Contact mail': " + (String)contactMailInput.getValue(), Notification.TYPE_ERROR_MESSAGE);
					//TODO: welche Felder müssen in jedem Panel gefüllt sein?
//				} else if (((String)descriptionInput.getValue()).isEmpty()) {
//					getWindow().showNotification("One of the fields 'Description' must be filled", Notification.TYPE_ERROR_MESSAGE);
				} else {
					finishUpload();
				}
			}});
	}
		
	private void finishUpload() {
		if (set_id == null) {
			set_id = exerciseRepository.nextAvailableSetId(BPTTopic.getValueOf(topic.getTagValues().get(0), "English"));
		}
		Iterator<Component> tabIterator = tabSheet.getComponentIterator();
		
		BPTUploadPanel uploadPanel;
		String documentId, title, language, description, exerciseUrl;
		List<FileResource> attachments;
		List<String> namesOfAttachments;
		while(tabIterator.hasNext()){
			uploadPanel = (BPTUploadPanel) tabIterator.next();
			if(!uploadPanel.equals(lastPanel)){
			
				documentId = uploadPanel.getDocumentId();
				title = uploadPanel.getTitleFromInput();
				language = uploadPanel.getLanguageFromInput();
				description = uploadPanel.getDescriptionFromInput();
				exerciseUrl = uploadPanel.getExerciseURLFromInput();
				attachments = uploadPanel.getAttachments();
				namesOfAttachments = uploadPanel.getNamesOfAttachments();
				if (documentId == null) { 
					
					documentId = exerciseRepository.createDocument(generateDocument(new Object[] {
						// order of parameters MUST accord to the one given in BPTDocumentTypes.java
						set_id,
						title,
						language,
						description,
						new ArrayList<String>(topic.getTagValues()),
						new ArrayList<String>(modelingLanguage.getTagValues()),
						new ArrayList<String>(taskType.getTagValues()),
						new ArrayList<String>(other.getTagValues()),
						exerciseUrl,
						(String)contactNameInput.getValue(),
						(String)contactMailInput.getValue(),
						(String)application.getUser(),
						new Date(),
						new Date(),
						namesOfAttachments
					}));
					
					for (FileResource attachment : attachments) {
						 Map<String, Object> document = exerciseRepository.readDocument(documentId);
						 String documentRevision = (String)document.get("_rev");
						 exerciseRepository.createAttachment(documentId, documentRevision, attachment.getFilename(), attachment.getSourceFile(), attachment.getMIMEType());
					}
					uploadPanel.clearAttachments();
				}
				else {
					Map<String, Object> newValues = new HashMap<String, Object>();
					newValues.put("_id", documentId);
					newValues.put("set_id", set_id);
//					newValues.put("title", (String)titleInput.getValue());
					newValues.put("subtitle", title);
					newValues.put("language", language);
					newValues.put("description", description);
					newValues.put("topics", new ArrayList<String>(topic.getTagValues()));
					newValues.put("modeling_languages", new ArrayList<String>(modelingLanguage.getTagValues()));
					newValues.put("task_types", new ArrayList<String>(taskType.getTagValues()));
					newValues.put("other_tags", new ArrayList<String>(other.getTagValues()));
					if (BPTExerciseStatus.Rejected == BPTExerciseStatus.valueOf((String) exerciseRepository.readDocument(documentId).get("status"))) {
						newValues.put("status", BPTExerciseStatus.Unpublished);
					}
					newValues.put("exercise_url", exerciseUrl);
					newValues.put("contact_name", contactNameInput.getValue().toString());
					newValues.put("contact_mail", contactMailInput.getValue().toString());
					newValues.put("last_update", new Date());
					newValues.put("names_of_attachments", namesOfAttachments);
					
					Map<String, Object> document = exerciseRepository.updateDocument(newValues);
//					for (Link oldAttachmentLink : uploadPanel.getOldAttachmentLinks()) {
//						DownloadStream stream = ((StreamResource) oldAttachmentLink.getResource()).getStream();
//						for (FileResource attachment : attachments) {
//							if (stream.equals(attachment.getStream())) {
//								
//							}
//						}
//					}
					String documentRevision = (String)document.get("_rev");;
					for (String nameOfOldAttachment : namesOfOldAttachments) {
						documentRevision = exerciseRepository.deleteAttachment(documentId, documentRevision, nameOfOldAttachment);
					}
					
					for (FileResource attachment : attachments) {
						 documentRevision = exerciseRepository.createAttachment(documentId, documentRevision, attachment.getFilename(), attachment.getSourceFile(), attachment.getMIMEType());
					}
					
					uploadPanel.clearAttachments();
				}
			}
		}
		((BPTApplication)getApplication()).renderEntries();
	}
	
	private Map<String, Object> generateDocument(Object[] values) {
		Map<String, Object> document = new HashMap<String, Object>();
		ArrayList<String> keysList = BPTVaadinResources.getDocumentKeys(true);
		String[] keys = keysList.toArray(new String[keysList.size()]);
		for(int i = 0; i < keys.length; i++) {
				document.put(keys[i], values[i]);
		}
		return document;
	}
}