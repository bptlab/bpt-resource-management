package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.ektorp.CouchDbConnector;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Window;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDatabase;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTTool;

public class BPTUploader extends CustomComponent implements Upload.Receiver{
	
	private VerticalLayout layout;
	private Upload upload;
	private TextField nameInput, providerInput, downloadInput, documentationInput, screencastInput;
	private TextArea descriptionInput;
	private Button finishUploadButton;
	private BPTSearchComponent availabilitiesTagComponent, modelTagComponent, platformTagComponent, functionalityTagComponent;
	
	
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
		
		
		
//		upload = new Upload("Please upload a screencast", this);
//		upload.setImmediate(false);
//		upload.setWidth("-1px");
//		upload.setHeight("-1px");
//		layout.addComponent(upload);
//		
//		upload.addListener(new Upload.FinishedListener() {
//			public void uploadFinished(FinishedEvent event) {
//				
//				System.out.println("upload finished");
//			}
//		});
		finishUploadButton = new Button("finish Upload");
		layout.addComponent(finishUploadButton);
		finishUploadButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				
				CouchDbConnector database = BPTDatabase.connect();
				String name = (String) nameInput.getValue();
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
				newTool.setAvailabilities(new HashSet<String>(availabilitiesTagComponent.getTagValues()));
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
				
			}});
		
	}

	public OutputStream receiveUpload(String filename, String mimeType) {
		
		System.out.println("output stream");
		return null;

		
	}

}
