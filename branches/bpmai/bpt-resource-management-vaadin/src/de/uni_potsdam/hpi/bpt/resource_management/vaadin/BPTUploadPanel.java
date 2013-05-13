package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.vaadin.ui.themes.BaseTheme;

import de.uni_potsdam.hpi.bpt.resource_management.BPTValidator;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseStatus;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTPropertyValueType;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

@SuppressWarnings("serial")
public class BPTUploadPanel extends VerticalLayout implements Upload.SucceededListener, Upload.FailedListener, Upload.Receiver {
	
	private VerticalLayout layout;
	private Upload upload;
	private TextField subTitleInput, exerciseURLInput;
	private ComboBox languageInput;
	private RichTextArea descriptionInput;
	private BPTUploader uploader;
	
	private FileOutputStream outputStream;
	private final String[] supportedDocumentTypes = new String[] {"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
	private String documentId, language, set_id;
	private BPTApplication application;
	private BPTExerciseRepository exerciseRepository = BPTExerciseRepository.getInstance();
	private Panel documentPanel;
	private Label subTitleLabel, languageLabel, descriptionLabel, exerciseURLLabel;
	private Map<String, File> documents = new HashMap<String, File>();
	private Map<String, String> mimeTypes = new HashMap<String, String>();
	private List<String>  attachmentNames = new ArrayList<String>();
	private File document;
	
	public BPTUploadPanel(Item item, final BPTApplication application, BPTUploader uploader) {
		super();
		this.application = application;
		layout = this;
		this.uploader = uploader;
		set_id = uploader.getSetId();
		
		documentId = null;
		
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
		
//		link = new Link();
//		link.setVisible(false);
//		parent.addComponent(link);
	}
	
	public OutputStream receiveUpload(String filename, String mimeType) {
		document = new File(filename);
//		System.out.println(filename + ":" + document.canExecute() + document.canRead() + document.canWrite());
//		System.out.println(filename + ":" + document.canExecute() + document.canRead() + document.canWrite());

        try {
        	if (Arrays.asList(supportedDocumentTypes).contains(mimeType)) {
        		outputStream = new FileOutputStream(document);
        	}
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        documents.put(filename, document);
        mimeTypes.put(filename, mimeType);
        attachmentNames.add(filename);
        
        return outputStream;
	}
	
	public void uploadSucceeded(final SucceededEvent event) {
		final FileResource documentResource = new FileResource(document, getApplication());
		String filename = event.getFilename();
		addDocumentToPanel(documentResource, filename);
		System.out.println(documentResource);
        application.refresh();
	}
	
	private void addDocumentToPanel(FileResource documentResource, final String filename) {
		final Link link = new Link();
		link.setVisible(true);
		link.setCaption(filename);
		link.setResource(documentResource);	
		documentPanel.addComponent(link);
		final Button removeDocumentButton = new Button("Remove document");
		documentPanel.addComponent(removeDocumentButton);
		removeDocumentButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent clickEvent) {
				if (documents.get(filename) != null) {
					boolean deletionSuccessful = documents.get(filename).delete();
					documents.remove(filename);
					mimeTypes.remove(filename);
					attachmentNames.remove(filename);
					documentPanel.removeComponent(link);
					documentPanel.removeComponent(removeDocumentButton);
					if (!deletionSuccessful) {
						throw new IllegalArgumentException("Deletion of file " + filename + " failed.");
					}
				}
			}
		});
		
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

	public Map<String, File> getDocuments() {
		return documents;
	}
	
	public Map<String, String> getMimeTypes() {
		return mimeTypes;
	}
	
	public List<String> getAttachmentNames() {
		return attachmentNames;
	}
}
