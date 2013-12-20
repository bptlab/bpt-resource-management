package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.FileResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.uni_potsdam.hpi.bpt.resource_management.BPTValidator;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentType;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseSetRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseStatus;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTTopic;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

@SuppressWarnings("serial")
public class BPTUploader extends VerticalLayout implements TabSheet.SelectedTabChangeListener {
	
	private Item item;
	private String set_id;
	private BPTApplicationUI applicationUI;
	private BPTExerciseSetRepository exerciseSetRepository = BPTExerciseSetRepository.getInstance();
	private BPTExerciseRepository exerciseRepository = BPTExerciseRepository.getInstance();
	private TabSheet tabSheet;
	private Label topicLabel;
	private BPTTagComponent topic;
	private Label modelingLanguageLabel;
	private BPTTagComponent modelingLanguage;
	private Label taskTypeLabel;
	private BPTTagComponent taskType;
	private Label otherTagsLabel;
	private BPTTagComponent other;
	private Label contactNameLabel;
	private TextField contactNameInput;
	private Label contactMailLabel;
	private TextField contactMailInput;
	private Button finishUploadButton;
	private BPTUploadPanel lastPanel;
	private ArrayList<String> namesOfExistingSupplementaryFiles;
	private String nameOfExistingPdfFile, nameOfExistingDocFile;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BPTUploader(Item item, final BPTApplicationUI applicationUI) {
		super();
		this.applicationUI = applicationUI;
		this.item = item;
		
        addComponent(new Label("<br/> <hr/> <br/>", ContentMode.HTML));
//		if(applicationUI.isModerated()){
			Button multiUploadButton = new Button("multi-upload");
			multiUploadButton.addClickListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					BPTUploader.this.applicationUI.renderMultiUploader();
				}});
			addComponent(multiUploadButton);
			addComponent(new Label("<br/> <hr/> <br/>", ContentMode.HTML));
//		}
		
				
		this.tabSheet = new TabSheet();
		addComponent(tabSheet);
		tabSheet.addSelectedTabChangeListener(this);
		
		addTagComponents();
        
		addContactInputs();
		
		addSubmitButton();
		
        if (item != null) {
        	set_id = item.getItemProperty("Exercise Set ID").getValue().toString();
        	nameOfExistingPdfFile = (String) exerciseRepository.readDocument(item.getItemProperty("ID").getValue().toString()).get("name_of_pdf_file");
        	nameOfExistingDocFile = (String) exerciseRepository.readDocument(item.getItemProperty("ID").getValue().toString()).get("name_of_doc_file");
        	namesOfExistingSupplementaryFiles = (ArrayList<String>) exerciseRepository.readDocument(item.getItemProperty("ID").getValue().toString()).get("names_of_supplementary_files");
        	List<Map> map = exerciseRepository.getDocumentsBySetId(set_id);
        	IndexedContainer entries = BPTContainerProvider.getInstance().generateContainer(map, BPTDocumentType.BPMAI_EXERCISES);
     		for (Object id : entries.getItemIds()) {
 				Item nextItem = entries.getItem(id);
 				BPTUploadPanel nextPanel = new BPTUploadPanel(nextItem, applicationUI, this);
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
		if(tabSheet.getSelectedTab() == lastPanel){
			tabSheet.getTab(lastPanel).setCaption("new Entry");
			tabSheet.getTab(lastPanel).setClosable(true);
			BPTUploadPanel oldLastPanel = lastPanel;
			lastPanel = addNewUploadPanel("+");
			tabSheet.setSelectedTab(tabSheet.getTabPosition(tabSheet.getTab(oldLastPanel)));
		}
	}

	private void setContactDates(Item item) {
		contactNameInput.setValue(item.getItemProperty("Contact name").getValue().toString());
		contactMailInput.setValue(((Link)item.getItemProperty("Contact mail").getValue()).getCaption());
	}

	private void addContactInputs() {
		contactNameLabel = new Label("Contact name * <font color=\"#BBBBBB\">as shown on the website</font>", ContentMode.HTML);
		addComponent(contactNameLabel);
		contactNameInput = new TextField();
		contactNameInput.setValue(applicationUI.getName());
		contactNameInput.setWidth("100%");
		addComponent(contactNameInput);
		
		contactMailLabel = new Label("Contact mail * <font color=\"#BBBBBB\">as shown on the website - notifications will be sent to the mail address you have been using for logon</font>", ContentMode.HTML);
		addComponent(contactMailLabel);
		contactMailInput = new TextField();
		contactMailInput.setValue(applicationUI.getMailAddress());
		contactMailInput.setWidth("100%");
		addComponent(contactMailInput);
	}

	private void setTagValues(Item item) {
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
		topic = new BPTTagComponent(applicationUI, "topics", false);
		topic.setWidth("100%");
		addComponent(topic);
		
		modelingLanguageLabel = new Label("Modeling Languages");
		addComponent(modelingLanguageLabel);
		modelingLanguage = new BPTTagComponent(applicationUI, "modelTypes", true);
		modelingLanguage.setWidth("100%");
		addComponent(modelingLanguage);
		
		taskTypeLabel = new Label("Task Types");
		addComponent(taskTypeLabel);
		taskType = new BPTTagComponent(applicationUI, "taskTypes", true);
		taskType.setWidth("100%");
		addComponent(taskType);
		
		otherTagsLabel = new Label("Additional Tags");
		addComponent(otherTagsLabel);
		other = new BPTTagComponent(applicationUI, "otherTags", true);
		other.setWidth("100%");
		addComponent(other);
	}

	public BPTUploadPanel addNewUploadPanel(String caption) {
		BPTUploadPanel panel = new BPTUploadPanel(null, applicationUI, this);
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
		finishUploadButton.addClickListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				
//				if (((String)titleInput.getValue()).isEmpty()) {
//					Notification.show("'Title' field is empty", Notification.TYPE_ERROR_MESSAGE);
//				}
				if (((String)contactNameInput.getValue()).isEmpty()) {
					Notification.show("'Contact name' field is empty", Notification.Type.ERROR_MESSAGE);
				} else if (!BPTValidator.isValidEmail((String)contactMailInput.getValue())) {
					Notification.show("Invalid e-mail address", "in field 'Contact mail': " + (String)contactMailInput.getValue(), Notification.Type.ERROR_MESSAGE);
					//TODO: welche Felder muessen in jedem Panel gefuellt sein?
//				} else if (((String)descriptionInput.getValue()).isEmpty()) {
//					Notification.show("One of the fields 'Description' must be filled", Notification.Type.ERROR_MESSAGE);
				} else {
					finishUpload();
				}
			}});
	}
		
	private void finishUpload() {
		Boolean isNewEntry = false;
		if (set_id == null) {
			set_id = exerciseSetRepository.nextAvailableSetId(BPTTopic.getValueOf(topic.getTagValues().get(0), "English"));
			isNewEntry = true;
		}
		Iterator<Component> tabIterator = tabSheet.getComponentIterator();
		
		BPTUploadPanel uploadPanel;
		String documentId, title, language, description, exerciseUrl, documentRevision, nameOfPdfFile, nameOfDocFile;
		List<FileResource> supplementaryFiles;
		FileResource pdfFile, docFile;
		List<String> namesOfSupplementaryFiles;
		List<String> languages = new ArrayList<String>();
		while(tabIterator.hasNext()){
			uploadPanel = (BPTUploadPanel) tabIterator.next();
			if(!uploadPanel.equals(lastPanel)){
			
				documentId = uploadPanel.getDocumentId();
				title = uploadPanel.getTitleFromInput();
				language = uploadPanel.getLanguageFromInput();
				languages.add(language);
				description = uploadPanel.getDescriptionFromInput();
				exerciseUrl = uploadPanel.getExerciseURLFromInput();
				pdfFile = uploadPanel.getPdfFile();
				nameOfPdfFile = uploadPanel.getNameOfPdfFile();
				docFile = uploadPanel.getDocFile();
				nameOfDocFile = uploadPanel.getNameOfDocFile();
				supplementaryFiles = uploadPanel.getSupplementaryFiles();
				namesOfSupplementaryFiles = uploadPanel.getNamesOfSupplementaryFiles();
				
				if (documentId == null) { 
					documentId = exerciseRepository.createDocument(generateDocument(new Object[] {
						// order of parameters MUST accord to the one given in BPTDocumentTypes.java
						set_id,
						title,
						language,
						description,
						exerciseUrl,
//						new ArrayList<String>(topic.getTagValues()),
//						new ArrayList<String>(modelingLanguage.getTagValues()),
//						new ArrayList<String>(taskType.getTagValues()),
//						new ArrayList<String>(other.getTagValues()),
						nameOfPdfFile,
						nameOfDocFile,
						namesOfSupplementaryFiles
					}, BPTDocumentType.BPMAI_EXERCISES));

					Map<String, Object> document = exerciseRepository.readDocument(documentId);
					documentRevision = (String)document.get("_rev");
				} else {
					Map<String, Object> newValues = new HashMap<String, Object>();
					newValues.put("_id", documentId);
					newValues.put("set_id", set_id);
					newValues.put("title", title);
					newValues.put("language", language);
					newValues.put("description", description);
//					newValues.put("topics", new ArrayList<String>(topic.getTagValues()));
//					newValues.put("modeling_languages", new ArrayList<String>(modelingLanguage.getTagValues()));
//					newValues.put("task_types", new ArrayList<String>(taskType.getTagValues()));
//					newValues.put("other_tags", new ArrayList<String>(other.getTagValues()));
//					if (BPTExerciseStatus.Rejected == BPTExerciseStatus.valueOf((String) exerciseRepository.readDocument(documentId).get("status"))) {
//						newValues.put("status", BPTExerciseStatus.Unpublished);
//					}
					newValues.put("exercise_url", exerciseUrl);
//					newValues.put("last_update", new Date());
					newValues.put("name_of_pdf_file", nameOfPdfFile);
					newValues.put("name_of_doc_file", nameOfDocFile);
					newValues.put("names_of_supplementary_files", namesOfSupplementaryFiles);
					
					Map<String, Object> document = exerciseRepository.updateDocument(newValues);
//					for (Link oldAttachmentLink : uploadPanel.getOldAttachmentLinks()) {
//						DownloadStream stream = ((StreamResource) oldAttachmentLink.getResource()).getStream();
//						for (FileResource attachment : attachments) {
//							if (stream.equals(attachment.getStream())) {
//								
//							}
//						}
//					}
					documentRevision = (String)document.get("_rev");
					if (nameOfExistingPdfFile != null && !nameOfExistingPdfFile.isEmpty()) {
						documentRevision = exerciseRepository.deleteAttachment(documentId, documentRevision, nameOfExistingPdfFile);
					}
					if (nameOfExistingDocFile != null && !nameOfExistingDocFile.isEmpty()) {
						documentRevision = exerciseRepository.deleteAttachment(documentId, documentRevision, nameOfExistingDocFile);
					}
					for (String nameOfOldAttachment : namesOfExistingSupplementaryFiles) {
						documentRevision = exerciseRepository.deleteAttachment(documentId, documentRevision, nameOfOldAttachment);
					}
				}
				
				if (pdfFile != null) {
					documentRevision = exerciseRepository.createAttachment(documentId, documentRevision, pdfFile.getFilename(), pdfFile.getSourceFile(), pdfFile.getMIMEType());
				}
				if (docFile != null) {
					documentRevision = exerciseRepository.createAttachment(documentId, documentRevision, docFile.getFilename(), docFile.getSourceFile(), docFile.getMIMEType());
				}
				for (FileResource attachment : supplementaryFiles) {
					System.out.println("attachementId: " + attachment.getFilename());
					System.out.println("sourceFile (file): " + attachment.getSourceFile());
					System.out.println("mimeType: " + attachment.getMIMEType());
					documentRevision = exerciseRepository.createAttachment(documentId, documentRevision, attachment.getFilename(), attachment.getSourceFile(), attachment.getMIMEType());
				}
				
				uploadPanel.clearAttachments();
			}
		}
		if(isNewEntry){
			exerciseSetRepository.createDocument(generateDocument(new Object[] {
					// order of parameters MUST accord to the one given in BPTDocumentTypes.java
					set_id,
					new ArrayList<String>(topic.getTagValues()),
					new ArrayList<String>(modelingLanguage.getTagValues()),
					new ArrayList<String>(taskType.getTagValues()),
					new ArrayList<String>(other.getTagValues()),
					languages,
					(String)contactNameInput.getValue(),
					(String)contactMailInput.getValue(),
					(String)applicationUI.getUser(),
					new Date(),
					new Date(),
				}, BPTDocumentType.BPMAI_EXERCISE_SETS));
		}
		else{
			Map<String, Object> newValues = new HashMap<String, Object>();
			newValues.put("_id", item.getItemProperty("ID").getValue());
			newValues.put("set_id", set_id);
			newValues.put("topics", new ArrayList<String>(topic.getTagValues()));
			newValues.put("modeling_languages", new ArrayList<String>(modelingLanguage.getTagValues()));
			newValues.put("task_types", new ArrayList<String>(taskType.getTagValues()));
			newValues.put("other_tags", new ArrayList<String>(other.getTagValues()));
			newValues.put("languages", languages);
			newValues.put("contact_name", contactNameInput.getValue().toString());
			newValues.put("contact_mail", contactMailInput.getValue().toString());
			if (BPTExerciseStatus.Rejected == BPTExerciseStatus.valueOf((String) exerciseSetRepository.readDocument((String) item.getItemProperty("ID").getValue()).get("status"))) {
				newValues.put("status", BPTExerciseStatus.Unpublished);
			}
			exerciseSetRepository.updateDocument(newValues);
		}
		applicationUI.renderEntries();
	}
	
	private Map<String, Object> generateDocument(Object[] values, BPTDocumentType type) {
		Map<String, Object> document = new HashMap<String, Object>();
		ArrayList<String> keysList = BPTVaadinResources.getDocumentKeys(true, type);
		String[] keys = keysList.toArray(new String[keysList.size()]);
		for(int i = 0; i < keys.length; i++) {
				document.put(keys[i], values[i]);
		}
		return document;
	}
}