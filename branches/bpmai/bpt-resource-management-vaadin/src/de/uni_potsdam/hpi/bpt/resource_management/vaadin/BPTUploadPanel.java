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
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
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
public class BPTUploadPanel extends VerticalLayout implements Upload.SucceededListener, Upload.FailedListener, Upload.Receiver {
	
	private VerticalLayout layout;
	private Upload upload;
	private TextField subTitleInput, contactNameInput, contactMailInput, exerciseURLInput;
	private ComboBox languageInput;
	private RichTextArea descriptionInput;
	private Button finishUploadButton;
	private BPTTagComponent topic, modellingLanguage, taskType, other;
	private BPTUploader uploader;
	
	private File document;
	private FileOutputStream outputStream;
	private final String[] supportedDocumentTypes = new String[] {"application/pdf"};
	private String documentId, documentType, language, set_id;
	private BPTApplication application;
	private BPTExerciseRepository exerciseRepository = BPTExerciseRepository.getInstance();
	private Panel documentPanel;
	private Label subTitleLabel, languageLabel, descriptionLabel, exerciseURLLabel;
	
	public BPTUploadPanel(Item item, final BPTApplication application, BPTUploader uploader) {
		super();
		this.application = application;
		layout = this;
		this.uploader = uploader;
		set_id = uploader.getSetId();
		
		documentId = null;
		
        Label label = new Label("<br/> <hr/> <br/>", Label.CONTENT_XHTML);
        layout.addComponent(label);
		
        subTitleLabel = new Label("Subtitle");
		layout.addComponent(subTitleLabel);
		subTitleInput = new TextField();
		subTitleInput.setWidth("100%");
		layout.addComponent(subTitleInput);
		
		languageLabel = new Label("Language");
		layout.addComponent(languageLabel);
		languageInput = new ComboBox();
		languageInput.setNewItemsAllowed(true);
		languageInput.setImmediate(true);
		languageInput.setWidth("100%");
		languageInput.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				setLanguageTo(languageInput.getValue().toString());
			}
		});
		layout.addComponent(languageInput);
		
		descriptionLabel = new Label("Description");
		layout.addComponent(descriptionLabel);
		descriptionInput = new RichTextArea();
		descriptionInput.setWidth("100%");
		layout.addComponent(descriptionInput);
		
		layout.addComponent(new Label("Exercise URL"));
		exerciseURLInput = new TextField();
		exerciseURLInput.setInputPrompt("http://");
		exerciseURLInput.setWidth("100%");
		layout.addComponent(exerciseURLInput);
		
		documentPanel = new Panel("Documents");
		createUploadComponent(documentPanel);
        layout.addComponent(documentPanel);
        
        if (item != null) {
        	documentId = item.getItemProperty("ID").toString();
        	subTitleInput.setValue((item.getItemProperty("Subtitle").getValue()));
        	descriptionInput.setValue((item.getItemProperty("Description").getValue().toString()));
        }

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
		document = new File(filename + "\\");
		System.out.println(filename + ":" + document.canExecute() + document.canRead() + document.canWrite());
		System.out.println(filename + ":" + document.canExecute() + document.canRead() + document.canWrite());

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
	
	private void setLanguageTo(String value) {
		getBptUploader().getTabSheet().getTab(this).setCaption(value);
		if(value.equals("Deutsch")){
			subTitleLabel.setValue("Untertitel");
			languageLabel.setValue("Sprache");
			descriptionLabel.setValue("Beschreibung");
		}
		else{
			subTitleLabel.setValue("Subtitle");
			languageLabel.setValue("Language");
			descriptionLabel.setValue("Description");
		}
	}

	private BPTUploader getBptUploader() {
		return uploader;
	}

	private void setBptUploader(BPTUploader bptUploader) {
		this.uploader = bptUploader;
	}
	
	public void putLanguageInput(String language){
    	languageInput.addItem(language);
    	languageInput.setValue(language);
	}
	
	public String getDocumentId(){
		return documentId;
	}

	public String getSubtitleFromInput() {
		return (String)subTitleInput.getValue();
	}

	public String getLanguageFromInput() {
		return (String)languageInput.getValue();
	}

	public String getDescriptionFromInput() {
		// XXX vorher: toString() ..?
		return (String) descriptionInput.getValue();
	}

	public String getExerciseURLFromInput() {
		return (String) exerciseURLInput.getValue();
	}

}
