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
import java.util.Map;

import com.vaadin.terminal.FileResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentTypes;

public class BPTUploader extends CustomComponent implements Upload.SucceededListener, Upload.FailedListener, Upload.Receiver {
	
	private VerticalLayout layout;
	private Upload upload;
	private TextField nameInput, providerInput, downloadInput, documentationInput, screencastInput;
	private TextArea descriptionInput;
	private Button finishUploadButton, removeImageButton;
	private BPTSearchComponent availabilitiesTagComponent, modelTagComponent, platformTagComponent, functionalityTagComponent;
	private Panel imagePanel;
	private File logo;
	private FileOutputStream outputStream;
	private final String[] supportedImageTypes = new String[] {"image/jpeg", "image/gif", "image/png"};
	private String documentId, imageName, imageType;
	
	public BPTUploader(){
		layout = new VerticalLayout();
		setCompositionRoot(layout);
		
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
		availabilitiesTagComponent = new BPTSearchComponent("availabilities", true);
		layout.addComponent(availabilitiesTagComponent);
		
		layout.addComponent(new Label("Model Type:"));
		modelTagComponent = new BPTSearchComponent("modelTypes", true);
		layout.addComponent(modelTagComponent);
		
		layout.addComponent(new Label("Platform:"));
		platformTagComponent = new BPTSearchComponent("platforms", true);
		layout.addComponent(platformTagComponent);
		
		layout.addComponent(new Label("Supported functionality:"));
		functionalityTagComponent = new BPTSearchComponent("supportedFunctionalities", true);
		layout.addComponent(functionalityTagComponent);
		
		imagePanel = new Panel("Logo");
		
		createUploadComponent(imagePanel);
		
        imagePanel.addComponent(new Label("No image uploaded yet"));
        layout.addComponent(imagePanel);

		finishUploadButton = new Button("Submit");
		layout.addComponent(finishUploadButton);
		finishUploadButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				
				BPTDocumentRepository toolRepository = ((BPTApplication)getApplication()).getToolRepository();
				
				documentId = toolRepository.createDocument("BPTTool", generateDocument(new Object[] {
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
					"Random name",
					"random_address@example.org",
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
				((BPTApplication)getApplication()).finder();
				/*String name = (String)nameInput.getValue();
				
				if (database.contains(name)){
					final Window subWindow = new Window("Name taken");
					subWindow.setModal(true);
					subWindow.addComponent(new Label("There is already a Tool with the chosen name!"));
					getWindow().addWindow(subWindow);
					Button okButton = new Button("OK");
					subWindow.addComponent(okButton);
					okButton.addListener(new Button.ClickListener(){
						public void buttonClick(ClickEvent event) {
							getWindow().removeWindow(subWindow);
							
						}
				});
				}
				else{
					
				BPTTool newTool = new BPTTool();
				newTool.setName(name);
				newTool.setDescription((String) descriptionInput.getValue());
				newTool.setProvider((String) providerInput.getValue());
				newTool.setDocumentationURL((String) documentationInput.getValue());
				newTool.setAvailabilities();
				newTool.setModelTypes(new HashSet<String>(modelTagComponent.getTagValues()));
				newTool.setPlatforms(new HashSet<String>(platformTagComponent.getTagValues()));
				newTool.setSupportedFunctionalities(new HashSet<String>(functionalityTagComponent.getTagValues()));
				newTool.setContactName("Eric Verbeek");
				newTool.setContactMail("h.m.w.verbeek@tunnel"); // invalid -> must not be included in the document later
				newTool.setDateCreated(new Date());
				newTool.setLastUpdate(new Date());
				database.create(newTool);
				getWindow().showNotification("Upload Sucessful: " + name);
				}
				*/
				
			}
		});
	}

	private Map<String, Object> generateDocument(Object[] values) {
		Map<String, Object> document = new HashMap<String, Object>();
		String[] keys = BPTDocumentTypes.getDocumentKeys("BPTTool");
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
        imagePanel.removeAllComponents();
        imagePanel.addComponent(new Embedded(event.getFilename(), imageResource));
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
					throw new IllegalArgumentException("Deletion of " + event.getFilename() + " failed.");
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
