package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentTypes;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTValidator;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTPropertyValueType;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

public class BPTUploader extends CustomComponent implements Upload.SucceededListener, Upload.FailedListener, Upload.Receiver {
	
	private VerticalLayout layout;
	private Upload upload;
	private TextField nameInput, providerInput, downloadInput, documentationInput, screencastInput, contactNameInput, contactMailInput;
	private TextArea descriptionInput;
	private Button finishUploadButton, removeImageButton;
	private BPTTagComponent availabilitiesTagComponent, modelTagComponent, platformTagComponent, functionalityTagComponent;
	private Panel imagePanel;
	private File logo;
	private FileOutputStream outputStream;
	private final String[] supportedImageTypes = new String[] {"image/jpeg", "image/gif", "image/png"};
	private String documentId, imageName, imageType;
	private Date creationDate;
	private BPTApplication application;
	
	public BPTUploader(Item item, final BPTApplication application){
		this.application = application;
		layout = new VerticalLayout();
		setCompositionRoot(layout);
		
		documentId = null;
		
		layout.addComponent(new Label("Name:"));
		nameInput = new TextField();
		layout.addComponent(nameInput);
		
		layout.addComponent(new Label("Description:"));
		descriptionInput = new TextArea();
		layout.addComponent(descriptionInput);
		
		layout.addComponent(new Label("Provider:"));
		providerInput = new TextField();
		layout.addComponent(providerInput);
		
		layout.addComponent(new Label("Download:"));
		downloadInput = new TextField();
		layout.addComponent(downloadInput);
		
		layout.addComponent(new Label("Documentation:"));
		documentationInput = new TextField();
		layout.addComponent(documentationInput);
		
		layout.addComponent(new Label("Screencast:"));
		screencastInput = new TextField();
		layout.addComponent(screencastInput);
		
		layout.addComponent(new Label("Availabilities:"));
		availabilitiesTagComponent = new BPTTagComponent("availabilities", true);
		layout.addComponent(availabilitiesTagComponent);
		
		layout.addComponent(new Label("Model Type:"));
		modelTagComponent = new BPTTagComponent("modelTypes", true);
		layout.addComponent(modelTagComponent);
		
		layout.addComponent(new Label("Platform:"));
		platformTagComponent = new BPTTagComponent("platforms", true);
		layout.addComponent(platformTagComponent);
		
		layout.addComponent(new Label("Supported functionality:"));
		functionalityTagComponent = new BPTTagComponent("supportedFunctionalities", true);
		layout.addComponent(functionalityTagComponent);
		
		layout.addComponent(new Label("Contact name:"));
		contactNameInput = new TextField();
		contactNameInput.setValue(application.getName());
		layout.addComponent(contactNameInput);
		
		layout.addComponent(new Label("Contact mail:"));
		contactMailInput = new TextField();
		contactMailInput.setValue(application.getMailAddress());
		layout.addComponent(contactMailInput);
		
		imagePanel = new Panel("Logo");
		
		createUploadComponent(imagePanel);
		
        imagePanel.addComponent(new Label("No image uploaded yet"));
        layout.addComponent(imagePanel);
        
        if(!(item == null)){
        	//TODO: Bild einfügen
        	
        	documentId = item.getItemProperty("ID").toString();
        	creationDate = (Date) item.getItemProperty("Date created").getValue();
        	nameInput.setValue((item.getItemProperty("Name").getValue()));
        	descriptionInput.setValue((item.getItemProperty("Description").getValue()));
        	providerInput.setValue(item.getItemProperty("Provider").getValue());
        	downloadInput.setValue(((Link)(item.getItemProperty("Download").getValue())).getCaption());
        	documentationInput.setValue(((Link)(item.getItemProperty("Documentation").getValue())).getCaption());
        	screencastInput.setValue(((Link)(item.getItemProperty("Screencast").getValue())).getCaption());
        	if(!(item.getItemProperty("Availability").getValue().toString().equals(""))){
        		String[] availability = ((String) item.getItemProperty("Availability").getValue()).split(",");
        		for(int i = 0; i < availability.length; i++) availabilitiesTagComponent.addChosenTag(availability[i]);
        	}
        	if(!(item.getItemProperty("Model type").getValue().toString().equals(""))){
        		String[] model_type = ((String) item.getItemProperty("Model type").getValue()).split(",");
        		for(int i = 0; i < model_type.length; i++) modelTagComponent.addChosenTag(model_type[i]);
        	}
        	if(!(item.getItemProperty("Platform").getValue().toString().equals(""))){
        		String[] platform = ((String) item.getItemProperty("Platform").getValue()).split(",");
        		for(int i = 0; i < platform.length; i++) platformTagComponent.addChosenTag(platform[i]);
        	}
        	if(!(item.getItemProperty("Supported functionality").getValue().toString().equals(""))){
        		String[] supported_functionality = ((String) item.getItemProperty("Supported functionality").getValue()).split(",");
        		for(int i = 0; i < supported_functionality.length; i++) functionalityTagComponent.addChosenTag(supported_functionality[i]);
        	}
        	contactNameInput.setValue(application.getName());
        	contactMailInput.setValue(application.getMailAddress());
        	BPTToolRepository toolRepository = application.getToolRepository();
        	Embedded image = (Embedded) BPTVaadinResources.generateComponent(toolRepository, toolRepository.readDocument(documentId), "_attachments", BPTPropertyValueType.IMAGE, "logo");
			image.setWidth("");
			image.setHeight("");
			addImageToPanel(image);
        }

		finishUploadButton = new Button("Submit");
		layout.addComponent(finishUploadButton);
		finishUploadButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				
				BPTToolRepository toolRepository = application.getToolRepository();
				
				if (toolRepository.containsName((String)nameInput.getValue()) && documentId == null) {
					addWarningWindow(getWindow());
				} else if (!BPTValidator.isValidURL((String)downloadInput.getValue())) {
					getWindow().showNotification("Invalid URL", "in field 'Download': " + (String)downloadInput.getValue(), Notification.TYPE_ERROR_MESSAGE);
				} else if (!((String)documentationInput.getValue()).isEmpty() && !BPTValidator.isValidURL((String)documentationInput.getValue())) {
					getWindow().showNotification("Invalid URL", "in field 'Documentation': " + (String)downloadInput.getValue(), Notification.TYPE_ERROR_MESSAGE);
				} else if (!((String)screencastInput.getValue()).isEmpty() && !BPTValidator.isValidURL((String)screencastInput.getValue())) {
					getWindow().showNotification("Invalid URL", "in field 'Screencast': " + (String)downloadInput.getValue(), Notification.TYPE_ERROR_MESSAGE);
				}
				else {
					finishUpload();
				}
				
			}

			private void finishUpload() {
				BPTToolRepository toolRepository = ((BPTApplication)getApplication()).getToolRepository();
				//TODO: if(!(item == null)) { updaten statt neuer eintrag
				if (documentId == null) { 
				
					documentId = toolRepository.createDocument(generateDocument(new Object[] {
							(String)nameInput.getValue(),
							(String)descriptionInput.getValue(),
							(String)providerInput.getValue(),
							(String)downloadInput.getValue(),
							(String)documentationInput.getValue(),
							(String)screencastInput.getValue(),
							new ArrayList<String>(availabilitiesTagComponent.getTagValues()),
							new ArrayList<String>(modelTagComponent.getTagValues()),
							new ArrayList<String>(platformTagComponent.getTagValues()),
							new ArrayList<String>(functionalityTagComponent.getTagValues()),
							(String)contactNameInput.getValue(),
							(String)contactMailInput.getValue(),
							(String)application.getUser(), 
							new Date(),
							new Date()
						}));
						
						if (logo != null) { // logo.exists()
							Map<String, Object> document = toolRepository.readDocument(documentId);
							String documentRevision = (String)document.get("_rev");
							
							toolRepository.createAttachment(documentId, documentRevision, "logo", logo, imageType);
							
							logo.delete();
						}
						
						getWindow().showNotification("New entry submitted: " + (String)nameInput.getValue());
						
					
				} else {
//					System.out.println(descriptionInput.getValue().getClass());
					Map<String, Object> newValues = new HashMap<String, Object>();
					newValues.put("name", nameInput.getValue().toString());
					newValues.put("description", descriptionInput.getValue().toString());
					newValues.put("provider", providerInput.getValue().toString());
					newValues.put("download_url", downloadInput.getValue().toString());
					newValues.put("documentation_url", documentationInput.getValue().toString());
					newValues.put("screencast_url", screencastInput.getValue().toString());
					newValues.put("availabilities", new ArrayList<String>(availabilitiesTagComponent.getTagValues()));
					newValues.put("model_types", new ArrayList<String>(modelTagComponent.getTagValues()));
					newValues.put("platforms", new ArrayList<String>(platformTagComponent.getTagValues()));
					newValues.put("supported_functionalities", new ArrayList<String>(functionalityTagComponent.getTagValues()));
					if (BPTToolStatus.Rejected == BPTToolStatus.valueOf((String) toolRepository.readDocument(documentId).get("status"))) {
						newValues.put("status", BPTToolStatus.Unpublished);
					}
					newValues.put("contact_name", contactNameInput.getValue().toString());
					newValues.put("contact_mail", contactMailInput.getValue().toString());
					newValues.put("last_update", new Date());
					toolRepository.updateDocument(newValues);
					
					getWindow().showNotification("Updated Entry: " + (String)nameInput.getValue());
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
		upload = new Upload("Upload a logo (*.jpg, *.gif, *.png supported):", this);
		upload.setImmediate(false);
		upload.setWidth("-1px");
		upload.setHeight("-1px");
		upload.addListener((Upload.SucceededListener)this);
        upload.addListener((Upload.FailedListener)this);
		parent.addComponent(upload);
	}
	
	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		imageName = filename;
		imageType = mimeType;
		
        if(System.getProperty("os.name").contains("Windows")) {
			logo = new File("C:\\temp\\" + filename);
		}
		else {
			logo = new File("/tmp/" + filename);
		}
        
        try {
        	if (Arrays.asList(supportedImageTypes).contains(imageType)) {
        		outputStream = new FileOutputStream(logo);
        	}
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        return outputStream;
	}
	
	@Override
	public void uploadSucceeded(final SucceededEvent event) {
		final FileResource imageResource = new FileResource(logo, getApplication());
		Embedded image = new Embedded(event.getFilename(), imageResource);
		
        addImageToPanel(image);
	}

	private void addImageToPanel(Embedded image) {
		imagePanel.removeAllComponents();
        imagePanel.addComponent(image);
        removeImageButton = new Button("Remove image");
		imagePanel.addComponent(removeImageButton);
		removeImageButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent clickEvent) {
				outputStream = null;
				imagePanel.removeAllComponents();
				createUploadComponent(imagePanel);
				imagePanel.addComponent(new Label("No image uploaded yet"));
				boolean deletionSuccessful = logo.delete();
				if (!deletionSuccessful) {
					throw new IllegalArgumentException("Deletion of picture failed.");
				}
				logo = null;
			}
		});
	}
	
	@Override
	public void uploadFailed(FailedEvent event) {
		getWindow().showNotification(
                "Upload failed :(",
                "The type of the file you have submitted is not supported or the file was not found.",
                Notification.TYPE_ERROR_MESSAGE);
	}
}
