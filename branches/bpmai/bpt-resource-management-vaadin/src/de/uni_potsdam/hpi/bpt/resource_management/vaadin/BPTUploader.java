package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.uni_potsdam.hpi.bpt.resource_management.BPTValidator;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseStatus;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTPropertyValueType;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

@SuppressWarnings("serial")
public class BPTUploader extends CustomComponent implements Upload.SucceededListener, Upload.FailedListener, Upload.Receiver {
	
	private TabSheet languageTabSheet;
	//TODO: alle einträge mit gleicher set_id finden und als tabs anzeigen
	private VerticalLayout layout;
	private Upload upload;
	private TextField titleInput, contactNameInput, contactMailInput;
	private ComboBox languageInput;
	private TextArea descriptionInput;
	private Button finishUploadButton;
	private BPTTagComponent topic, modellingLanguage, taskType, additional;
	
	
	private File document;
	private FileOutputStream outputStream;
	private final String[] supportedDocumentTypes = new String[] {"application/pdf"};
	private String documentId;
	private BPTApplication application;
	private BPTExerciseRepository toolRepository = BPTExerciseRepository.getInstance();
	private String documentType;
	private Panel documentPanel;
	
	public BPTUploader(Item item, final BPTApplication application) {
		this.application = application;
		layout = new VerticalLayout();
		setCompositionRoot(layout);
		
		documentId = null;
		
        Label label = new Label("<br/> <hr/> <br/>", Label.CONTENT_XHTML);
        layout.addComponent(label);
		
		layout.addComponent(new Label("Title *"));
		titleInput = new TextField();
		titleInput.setWidth("100%");
		layout.addComponent(titleInput);
		
		layout.addComponent(new Label("Description", Label.CONTENT_XHTML));
		descriptionInput = new TextArea();
		descriptionInput.setWidth("100%");
		layout.addComponent(descriptionInput);
		
		layout.addComponent(new Label("Topic"));
		topic = new BPTTagComponent(application, "topic", true);
		topic.setWidth("100%");
		layout.addComponent(topic);
		
		layout.addComponent(new Label("Modelling Language"));
		modellingLanguage = new BPTTagComponent(application, "modellingLanguage", true);
		modellingLanguage.setWidth("100%");
		layout.addComponent(modellingLanguage);
		
		layout.addComponent(new Label("Task Type"));
		taskType = new BPTTagComponent(application, "taskType", true);
		taskType.setWidth("100%");
		layout.addComponent(taskType);
		
		layout.addComponent(new Label("Additional Tags"));
		additional = new BPTTagComponent(application, "additional", true);
		additional.setWidth("100%");
		layout.addComponent(additional);
		
		layout.addComponent(new Label("Contact name * <font color=\"#BBBBBB\">as shown on the website</font>", Label.CONTENT_XHTML));
		contactNameInput = new TextField();
		contactNameInput.setValue(application.getName());
		contactNameInput.setWidth("100%");
		layout.addComponent(contactNameInput);
		
		layout.addComponent(new Label("Contact mail * <font color=\"#BBBBBB\">as shown on the website - notifications will be sent to the mail address you have been using for logon</font>", Label.CONTENT_XHTML));
		contactMailInput = new TextField();
		contactMailInput.setValue(application.getMailAddress());
		contactMailInput.setWidth("100%");
		layout.addComponent(contactMailInput);
		
		documentPanel = new Panel("Documents");
		createUploadComponent(documentPanel);
        layout.addComponent(documentPanel);
        
        if (item != null) {
        	documentId = item.getItemProperty("ID").toString();
        	titleInput.setValue((item.getItemProperty("Name").getValue()));
        	descriptionInput.setValue((item.getItemProperty("Description").getValue().toString()));
        	
        	if(!(item.getItemProperty("Model type").getValue().toString().equals(""))){
        		String[] model_type = ((String) item.getItemProperty("Model type").getValue()).split(",");
        		for(int i = 0; i < model_type.length; i++) topic.addChosenTag(model_type[i].trim().replaceAll(" +", " "));
        	}
        	if(!(item.getItemProperty("Platform").getValue().toString().equals(""))){
        		String[] platform = ((String) item.getItemProperty("Platform").getValue()).split(",");
        		for(int i = 0; i < platform.length; i++) modellingLanguage.addChosenTag(platform[i].trim().replaceAll(" +", " "));
        	}
        	if(!(item.getItemProperty("Supported functionality").getValue().toString().equals(""))){
        		String[] supported_functionality = ((String) item.getItemProperty("Supported functionality").getValue()).split(",");
        		for(int i = 0; i < supported_functionality.length; i++) taskType.addChosenTag(supported_functionality[i].trim().replaceAll(" +", " "));
        	}
        	if(!(item.getItemProperty("Availability").getValue().toString().equals(""))){
        		String[] availability = ((String) item.getItemProperty("Availability").getValue()).split(",");
        		for(int i = 0; i < availability.length; i++) additional.addChosenTag(availability[i].trim().replaceAll(" +", " "));
        	}
        	contactNameInput.setValue(item.getItemProperty("Contact name").getValue().toString());
        	contactMailInput.setValue(((Link)item.getItemProperty("Contact mail").getValue()).getCaption());
        	Embedded image = (Embedded) BPTVaadinResources.generateComponent(toolRepository, toolRepository.readDocument(documentId), "_attachments", BPTPropertyValueType.IMAGE, "logo");
			image.setWidth("");
			image.setHeight("");
//			if (image.getMimeType() != null) { // only if picture exists
//				addImageToPanel(image);
//			}
        }

		finishUploadButton = new Button("Submit");
		layout.addComponent(finishUploadButton);
		finishUploadButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				
				if (((String)titleInput.getValue()).isEmpty()) {
					getWindow().showNotification("'Title' field is empty", Notification.TYPE_ERROR_MESSAGE);
				} else if (toolRepository.containsName((String)titleInput.getValue()) && documentId == null) {
					addWarningWindow(getWindow());
				} else if (((String)descriptionInput.getValue()).isEmpty()) {
					getWindow().showNotification("One of the fields 'Description' must be filled", Notification.TYPE_ERROR_MESSAGE);
				} else if (((String)contactNameInput.getValue()).isEmpty()) {
					getWindow().showNotification("'Contact name' field is empty", Notification.TYPE_ERROR_MESSAGE);
				} else if (!BPTValidator.isValidEmail((String)contactMailInput.getValue())) {
					getWindow().showNotification("Invalid e-mail address", "in field 'Contact mail': " + (String)contactMailInput.getValue(), Notification.TYPE_ERROR_MESSAGE);
				} else {
					finishUpload();
				}
			}

			private void finishUpload() {
				if (documentId == null) { 
				
					documentId = toolRepository.createDocument(generateDocument(new Object[] {
						// order of parameters MUST accord to the one given in BPTDocumentTypes.java
						(String)titleInput.getValue(),
						(String)descriptionInput.getValue(),
						new ArrayList<String>(topic.getTagValues()),
						new ArrayList<String>(modellingLanguage.getTagValues()),
						new ArrayList<String>(taskType.getTagValues()),
						new ArrayList<String>(additional.getTagValues()),
						(String)contactNameInput.getValue(),
						(String)contactMailInput.getValue(),
						(String)application.getUser(),
						new Date(),
						new Date(), 
						null
					}));
						
					getWindow().showNotification("New entry submitted: " + (String)titleInput.getValue());

				} else {
					Map<String, Object> newValues = new HashMap<String, Object>();
					newValues.put("_id", documentId);
					newValues.put("name", titleInput.getValue().toString());
					newValues.put("description", descriptionInput.getValue().toString());
					newValues.put("topic", new ArrayList<String>(topic.getTagValues()));
					newValues.put("modelling_language", new ArrayList<String>(modellingLanguage.getTagValues()));
					newValues.put("task_type", new ArrayList<String>(taskType.getTagValues()));
					newValues.put("additional", new ArrayList<String>(additional.getTagValues()));
					if (BPTExerciseStatus.Rejected == BPTExerciseStatus.valueOf((String) toolRepository.readDocument(documentId).get("status"))) {
						newValues.put("status", BPTExerciseStatus.Unpublished);
					}
					newValues.put("contact_name", contactNameInput.getValue().toString());
					newValues.put("contact_mail", contactMailInput.getValue().toString());
					newValues.put("last_update", new Date());
					newValues.put("notification_date", null);
					toolRepository.updateDocument(newValues);
					
					Map<String, Object> document = toolRepository.updateDocument(newValues);
					String documentRevision = (String)document.get("_rev");
					
					getWindow().showNotification("Updated entry: " + (String)titleInput.getValue());
				}
				
				((BPTApplication)getApplication()).finder();
				
			}

			private void addWarningWindow(final Window window) {
				final Window warningWindow = new Window("Warning");
				warningWindow.setWidth("400px");
				warningWindow.setModal(true);
				warningWindow.addComponent(new Label("The name you have chosen is already taken - continue?"));
				Button yesButton = new Button("Yes");
				Button noButton = new Button("No");
				warningWindow.addComponent(yesButton);
				warningWindow.addComponent(noButton);
				noButton.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						window.removeWindow(warningWindow);
					}
				});
				yesButton.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						window.removeWindow(warningWindow);
						finishUpload();
					}
				});
				window.addWindow(warningWindow);
				
			}
		});
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
	
	private void createUploadComponent(Panel parent) {
		upload = new Upload("Upload at least one Document (*.pdf, *.doc, *.docx)", this);
		upload.setImmediate(false);
		upload.setWidth("-1px");
		upload.setHeight("-1px");
		upload.addListener((Upload.SucceededListener)this);
        upload.addListener((Upload.FailedListener)this);
		parent.addComponent(upload);
	}
	
	public OutputStream receiveUpload(String filename, String mimeType) {
		documentType = mimeType;
		document = new File(filename);
		
        try {
//        	if (Arrays.asList(supportedDocumentTypes).contains(documentType)) {
        		outputStream = new FileOutputStream(document);
//        	}
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        return outputStream;
	}
	
	public void uploadSucceeded(final SucceededEvent event) {
		final FileResource documentRessource = new FileResource(document, getApplication());
		addDocumentToPanel(documentRessource);
		System.out.println(documentRessource);
        application.refresh();
	}
private void addDocumentToPanel(FileResource documentRessource) {
		// TODO Auto-generated method stub
		
	}

	public void uploadFailed(FailedEvent event) {
		getWindow().showNotification(
                "Upload failed",
                "The type of the file you have submitted is not supported or the file was not found.",
                Notification.TYPE_ERROR_MESSAGE);
	}

}
