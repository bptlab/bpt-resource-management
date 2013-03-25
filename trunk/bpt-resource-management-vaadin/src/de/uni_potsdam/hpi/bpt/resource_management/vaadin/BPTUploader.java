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
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.uni_potsdam.hpi.bpt.resource_management.BPTValidator;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTPropertyValueType;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

@SuppressWarnings("serial")
public class BPTUploader extends CustomComponent implements Upload.SucceededListener, Upload.FailedListener, Upload.Receiver {
	
	private VerticalLayout layout;
	private Upload upload;
	private TextField toolNameInput, descriptionURLInput, providerInput, providerURLInput, downloadURLInput, documentationURLInput, screencastURLInput, tutorialURLInput, contactNameInput, contactMailInput;
	private TextArea descriptionInput;
	private Button finishUploadButton, removeImageButton;
	private BPTTagComponent availabilitiesTagComponent, modelTagComponent, platformTagComponent, functionalityTagComponent;
	private Panel imagePanel;
	private File logo;
	private FileOutputStream outputStream;
	private final String[] supportedImageTypes = new String[] {"image/jpeg", "image/gif", "image/png"};
	private String documentId, imageType;
	private boolean logoDeleted = true;
	private BPTApplication application;
	
	public BPTUploader(Item item, final BPTApplication application) {
		this.application = application;
		layout = new VerticalLayout();
		setCompositionRoot(layout);
		
		documentId = null;
		
        Label label = new Label("<br/> <hr/> <br/>", Label.CONTENT_XHTML);
        layout.addComponent(label);
		
		layout.addComponent(new Label("Tool name *"));
		toolNameInput = new TextField();
		toolNameInput.setWidth("100%");
		layout.addComponent(toolNameInput);
		
		layout.addComponent(new Label("Description text (*) <font color=\"#BBBBBB\">and/or description URL</font>", Label.CONTENT_XHTML));
		descriptionInput = new TextArea();
		descriptionInput.setWidth("100%");
		layout.addComponent(descriptionInput);
		
		layout.addComponent(new Label("Description URL (*) <font color=\"#BBBBBB\">and/or description text</font>", Label.CONTENT_XHTML));
		descriptionURLInput = new TextField();
		descriptionURLInput.setInputPrompt("http://");
		descriptionURLInput.setWidth("100%");
		layout.addComponent(descriptionURLInput);
		
		layout.addComponent(new Label("Provider *"));
		providerInput = new TextField();
		providerInput.setWidth("100%");
		layout.addComponent(providerInput);
		
		layout.addComponent(new Label("Provider URL"));
		providerURLInput = new TextField();
		providerURLInput.setInputPrompt("http://");
		providerURLInput.setWidth("100%");
		layout.addComponent(providerURLInput);
		
		layout.addComponent(new Label("Download URL"));
		downloadURLInput = new TextField();
		downloadURLInput.setInputPrompt("http://");
		downloadURLInput.setWidth("100%");
		layout.addComponent(downloadURLInput);
		
		layout.addComponent(new Label("Documentation URL"));
		documentationURLInput = new TextField();
		documentationURLInput.setInputPrompt("http://");
		documentationURLInput.setWidth("100%");
		layout.addComponent(documentationURLInput);
		
		layout.addComponent(new Label("Screencast URL"));
		screencastURLInput = new TextField();
		screencastURLInput.setInputPrompt("http://");
		screencastURLInput.setWidth("100%");
		layout.addComponent(screencastURLInput);
		
		layout.addComponent(new Label("Tutorial URL"));
		tutorialURLInput = new TextField();
		tutorialURLInput.setInputPrompt("http://");
		tutorialURLInput.setWidth("100%");
		layout.addComponent(tutorialURLInput);
		
		layout.addComponent(new Label("Availability"));
		availabilitiesTagComponent = new BPTTagComponent(application, "availabilities", true);
		availabilitiesTagComponent.setWidth("100%");
		layout.addComponent(availabilitiesTagComponent);
		
		layout.addComponent(new Label("Model type"));
		modelTagComponent = new BPTTagComponent(application, "modelTypes", true);
		modelTagComponent.setWidth("100%");
		layout.addComponent(modelTagComponent);
		
		layout.addComponent(new Label("Platform"));
		platformTagComponent = new BPTTagComponent(application, "platforms", true);
		platformTagComponent.setWidth("100%");
		layout.addComponent(platformTagComponent);
		
		layout.addComponent(new Label("Supported functionality"));
		functionalityTagComponent = new BPTTagComponent(application, "supportedFunctionalities", true);
		functionalityTagComponent.setWidth("100%");
		layout.addComponent(functionalityTagComponent);
		
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
		
		imagePanel = new Panel("Logo");
		createUploadComponent(imagePanel);
        layout.addComponent(imagePanel);
        
        if (item != null) {
        	documentId = item.getItemProperty("ID").toString();
        	toolNameInput.setValue((item.getItemProperty("Name").getValue()));
        	descriptionInput.setValue((item.getItemProperty("Description").getValue().toString()));
        	descriptionURLInput.setValue(((Link)(item.getItemProperty("Description URL").getValue())).getCaption());
        	providerInput.setValue(item.getItemProperty("Provider").getValue().toString());
        	providerURLInput.setValue(((Link)(item.getItemProperty("Provider URL").getValue())).getCaption());
        	downloadURLInput.setValue(((Link)(item.getItemProperty("Download URL").getValue())).getCaption());
        	documentationURLInput.setValue(((Link)(item.getItemProperty("Documentation URL").getValue())).getCaption());
        	screencastURLInput.setValue(((Link)(item.getItemProperty("Screencast URL").getValue())).getCaption());
        	tutorialURLInput.setValue(((Link)(item.getItemProperty("Tutorial URL").getValue())).getCaption());
        	if(!(item.getItemProperty("Availability").getValue().toString().equals(""))){
        		String[] availability = ((String) item.getItemProperty("Availability").getValue()).split(",");
        		for(int i = 0; i < availability.length; i++) availabilitiesTagComponent.addChosenTag(availability[i].trim().replaceAll(" +", " "));
        	}
        	if(!(item.getItemProperty("Model type").getValue().toString().equals(""))){
        		String[] model_type = ((String) item.getItemProperty("Model type").getValue()).split(",");
        		for(int i = 0; i < model_type.length; i++) modelTagComponent.addChosenTag(model_type[i].trim().replaceAll(" +", " "));
        	}
        	if(!(item.getItemProperty("Platform").getValue().toString().equals(""))){
        		String[] platform = ((String) item.getItemProperty("Platform").getValue()).split(",");
        		for(int i = 0; i < platform.length; i++) platformTagComponent.addChosenTag(platform[i].trim().replaceAll(" +", " "));
        	}
        	if(!(item.getItemProperty("Supported functionality").getValue().toString().equals(""))){
        		String[] supported_functionality = ((String) item.getItemProperty("Supported functionality").getValue()).split(",");
        		for(int i = 0; i < supported_functionality.length; i++) functionalityTagComponent.addChosenTag(supported_functionality[i].trim().replaceAll(" +", " "));
        	}
        	contactNameInput.setValue(item.getItemProperty("Contact name").getValue().toString());
        	contactMailInput.setValue(((Link)item.getItemProperty("Contact mail").getValue()).getCaption());
        	BPTToolRepository toolRepository = application.getToolRepository();
        	Embedded image = (Embedded) BPTVaadinResources.generateComponent(toolRepository, toolRepository.readDocument(documentId), "_attachments", BPTPropertyValueType.IMAGE, "logo");
			image.setWidth("");
			image.setHeight("");
			if (image.getMimeType() != null) { // only if picture exists
				addImageToPanel(image);
			}
        }

		finishUploadButton = new Button("Submit");
		layout.addComponent(finishUploadButton);
		finishUploadButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				
				BPTToolRepository toolRepository = application.getToolRepository();
				
				if (((String)toolNameInput.getValue()).isEmpty()) {
					getWindow().showNotification("'Tool name' field is empty", Notification.TYPE_ERROR_MESSAGE);
				} else if (toolRepository.containsName((String)toolNameInput.getValue()) && documentId == null) {
					addWarningWindow(getWindow());
				} else if (((String)descriptionInput.getValue()).isEmpty() && (((String)descriptionURLInput.getValue()).isEmpty())) {
					getWindow().showNotification("One of the fields 'Description' and 'Description URL' must be filled", Notification.TYPE_ERROR_MESSAGE);
				} else if (!((String)descriptionURLInput.getValue()).isEmpty() && !BPTValidator.isValidURL((String)descriptionURLInput.getValue())) {
					getWindow().showNotification("Invalid URL", "in field 'Description URL': " + (String)descriptionURLInput.getValue(), Notification.TYPE_ERROR_MESSAGE);
				} else if (((String)providerInput.getValue()).isEmpty()) {
					getWindow().showNotification("'Provider' field is empty", Notification.TYPE_ERROR_MESSAGE);
				} else if (!((String)providerURLInput.getValue()).isEmpty() && !BPTValidator.isValidURL((String)providerURLInput.getValue())) {
					getWindow().showNotification("Invalid URL", "in field 'Provider URL': " + (String)providerURLInput.getValue(), Notification.TYPE_ERROR_MESSAGE);
				} else if (!((String)documentationURLInput.getValue()).isEmpty() && !BPTValidator.isValidURL((String)documentationURLInput.getValue())) {
					getWindow().showNotification("Invalid URL", "in field 'Documentation URL': " + (String)documentationURLInput.getValue(), Notification.TYPE_ERROR_MESSAGE);
				} else if (!((String)screencastURLInput.getValue()).isEmpty() && !BPTValidator.isValidURL((String)screencastURLInput.getValue())) {
					getWindow().showNotification("Invalid URL", "in field 'Screencast URL': " + (String)screencastURLInput.getValue(), Notification.TYPE_ERROR_MESSAGE);
				} else if (!((String)tutorialURLInput.getValue()).isEmpty() && !BPTValidator.isValidURL((String)tutorialURLInput.getValue())) {
					getWindow().showNotification("Invalid URL", "in field 'Tutorial URL': " + (String)tutorialURLInput.getValue(), Notification.TYPE_ERROR_MESSAGE);
				} else if (((String)contactNameInput.getValue()).isEmpty()) {
					getWindow().showNotification("'Contact name' field is empty", Notification.TYPE_ERROR_MESSAGE);
				} else if (!BPTValidator.isValidEmail((String)contactMailInput.getValue())) {
					getWindow().showNotification("Invalid e-mail address", "in field 'Contact mail': " + (String)contactMailInput.getValue(), Notification.TYPE_ERROR_MESSAGE);
				} else {
					finishUpload();
				}
			}

			private void finishUpload() {
				BPTToolRepository toolRepository = ((BPTApplication)getApplication()).getToolRepository();
				
				if (documentId == null) { 
				
					documentId = toolRepository.createDocument(generateDocument(new Object[] {
						// order of parameters MUST accord to the one given in BPTDocumentTypes.java
						(String)toolNameInput.getValue(),
						(String)descriptionInput.getValue(),
						(String)descriptionURLInput.getValue(),
						(String)providerInput.getValue(),
						(String)providerURLInput.getValue(),
						(String)downloadURLInput.getValue(),
						(String)documentationURLInput.getValue(),
						(String)screencastURLInput.getValue(),
						(String)tutorialURLInput.getValue(),
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
						
					if (!logoDeleted) { // logo.exists()
						Map<String, Object> document = toolRepository.readDocument(documentId);
						String documentRevision = (String)document.get("_rev");
						
						toolRepository.createAttachment(documentId, documentRevision, "logo", logo, imageType);
						
						logo.delete();
					}
					
					getWindow().showNotification("New entry submitted: " + (String)toolNameInput.getValue());

				} else {
					Map<String, Object> newValues = new HashMap<String, Object>();
					newValues.put("_id", documentId);
					newValues.put("name", toolNameInput.getValue().toString());
					newValues.put("description", descriptionInput.getValue().toString());
					newValues.put("description_url", descriptionURLInput.getValue().toString());
					newValues.put("provider", providerInput.getValue().toString());
					newValues.put("provider_url", providerURLInput.getValue().toString());
					newValues.put("download_url", downloadURLInput.getValue().toString());
					newValues.put("documentation_url", documentationURLInput.getValue().toString());
					newValues.put("screencast_url", screencastURLInput.getValue().toString());
					newValues.put("tutorial_url", tutorialURLInput.getValue().toString());
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
					
					Map<String, Object> document = toolRepository.updateDocument(newValues);
					String documentRevision = (String)document.get("_rev");
					
					if (logoDeleted) {						
						toolRepository.deleteAttachment(documentId, documentRevision, "logo");
					} else if (logo != null) {
						toolRepository.createAttachment(documentId, documentRevision, "logo", logo, imageType);		
					}
					
					getWindow().showNotification("Updated entry: " + (String)toolNameInput.getValue());
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
		upload = new Upload("Upload a logo (*.jpg, *.gif, *.png)", this);
		upload.setImmediate(false);
		upload.setWidth("-1px");
		upload.setHeight("-1px");
		upload.addListener((Upload.SucceededListener)this);
        upload.addListener((Upload.FailedListener)this);
		parent.addComponent(upload);
	}
	
	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		imageType = mimeType;
		logo = new File(filename);
		
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
        logoDeleted = false;
        application.refresh();
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
				if (logo != null) {
					boolean deletionSuccessful = logo.delete();
					if (!deletionSuccessful) {
						throw new IllegalArgumentException("Deletion of picture failed.");
					}
					logo = null;
				} 
				logoDeleted = true;
			}
		});
	}
	
	@Override
	public void uploadFailed(FailedEvent event) {
		getWindow().showNotification(
                "Upload failed",
                "The type of the file you have submitted is not supported or the file was not found.",
                Notification.TYPE_ERROR_MESSAGE);
	}
}
