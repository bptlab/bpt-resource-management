package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTMimeTypes;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTPropertyValueType;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

@SuppressWarnings("serial")
public class BPTUploadPanel extends VerticalLayout implements Upload.SucceededListener, Upload.FailedListener, Upload.Receiver {
	
	private VerticalLayout layout;
	private Upload upload;
	private TextField titleInput, exerciseURLInput;
	private ComboBox languageInput;
	private RichTextArea descriptionInput;
	private BPTUploader uploader;
	
	private File tempAttachment;
	private List<FileResource> attachments = new ArrayList<FileResource>();
	private List<String> namesOfAttachments = new ArrayList<String>();
	private FileOutputStream outputStream;
	private final String[] supportedDocumentTypes;
	private String documentId, language, set_id;
	private BPTApplication application;
	private BPTExerciseRepository exerciseRepository = BPTExerciseRepository.getInstance();
	private Panel attachmentPanel;
	private Label titleLabel, languageLabel, descriptionLabel;
	
	public BPTUploadPanel(Item item, final BPTApplication application, BPTUploader uploader) {
		super();
		this.application = application;
		this.supportedDocumentTypes = BPTMimeTypes.getMimeTypes();
		this.layout = this;
		this.uploader = uploader;
		this.set_id = uploader.getSetId();
		
		this.documentId = null;
		
        titleLabel = new Label("Title *");
		layout.addComponent(titleLabel);
		titleInput = new TextField();
		titleInput.setWidth("100%");
		layout.addComponent(titleInput);
		
		languageLabel = new Label("Language *");
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
		
		descriptionLabel = new Label("Description *");
		layout.addComponent(descriptionLabel);
		descriptionInput = new RichTextArea();
		descriptionInput.setWidth("100%");
		layout.addComponent(descriptionInput);
		
		layout.addComponent(new Label("Exercise URL"));
		exerciseURLInput = new TextField();
		exerciseURLInput.setInputPrompt("http://");
		exerciseURLInput.setWidth("100%");
		layout.addComponent(exerciseURLInput);
		
		attachmentPanel = new Panel("Documents");
		createUploadComponent(attachmentPanel);
        layout.addComponent(attachmentPanel);
        
        if (item != null) {
        	documentId = item.getItemProperty("ID").toString();
        	titleInput.setValue(item.getItemProperty("Title").getValue());
        	descriptionInput.setValue(item.getItemProperty("Description").getValue().toString());
        	if (!item.getItemProperty("Exercise URL").getValue().toString().equals("")) {
            	exerciseURLInput.setValue(item.getItemProperty("Exercise URL").getValue().toString());
        	}
        	ArrayList<Link> attachmentLinks = (ArrayList<Link>) BPTVaadinResources.generateComponent(exerciseRepository, exerciseRepository.readDocument(documentId), "names_of_attachments", BPTPropertyValueType.LINK_ATTACHMENT, null, application);
        	for (Link link : attachmentLinks) {
        		addLinkToAttachmentPanel(link);
        	}
        }
	}

	private void createUploadComponent(Panel parent) {
		upload = new Upload("Upload at least one document (*.pdf, *.doc, *.docx)", this);
		upload.setImmediate(false);
		upload.setWidth("-1px");
		upload.setHeight("-1px");
		upload.addListener((Upload.SucceededListener)this);
        upload.addListener((Upload.FailedListener)this);
		parent.addComponent(upload);
	}
	
	public OutputStream receiveUpload(String filename, String mimeType) {
		String documentType = mimeType;
		tempAttachment = new File(filename);
//		System.out.println(filename + ":" + document.canExecute() + document.canRead() + document.canWrite());
        try {
        	if (Arrays.asList(supportedDocumentTypes).contains(documentType)) {
        		outputStream = new FileOutputStream(tempAttachment);
        	}
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        return outputStream;
	}
	
	public void uploadSucceeded(final SucceededEvent event) {
		final FileResource documentResource = new FileResource(tempAttachment, getApplication());
		namesOfAttachments.add(documentResource.getFilename());
		addLinkToAttachmentPanel(documentResource);
		attachments.add(documentResource);
		System.out.println(documentResource);
        application.refreshAndClean();
	}
	
	private void addLinkToAttachmentPanel(FileResource documentRessource) {
		Link link = new Link(documentRessource.getFilename(), documentRessource);
		link.setTargetName("_blank");
		String mimeType = documentRessource.getMIMEType();
		if (mimeType.equals(BPTMimeTypes.PDF.toString())) {
			link.setIcon(new ThemeResource("images/logo-pdf-16px.png"));
		} else if (mimeType.equals(BPTMimeTypes.DOC.toString())) {
			link.setIcon(new ThemeResource("images/logo-doc-16px.png"));
		} else if (mimeType.equals(BPTMimeTypes.DOCX.toString())) {
			link.setIcon(new ThemeResource("images/logo-docx-16px.png"));
		}
		addLinkToAttachmentPanel(link);
	}

	private void addLinkToAttachmentPanel(Link link) {
		attachmentPanel.addComponent(link);
	}

	public void uploadFailed(FailedEvent event) {
		getWindow().showNotification(
                "Upload failed",
                "The type of the file you have submitted is not supported or the file was not found.",
                Notification.TYPE_ERROR_MESSAGE);
	}
	
	private void setLanguageTo(String value) {
		uploader.getTabSheet().getTab(this).setCaption(value);
		if (value.equals("Deutsch")) {
			titleLabel.setValue("Titel *");
			languageLabel.setValue("Sprache *");
			descriptionLabel.setValue("Beschreibung *");
		}
		else{
			titleLabel.setValue("Title *");
			languageLabel.setValue("Language *");
			descriptionLabel.setValue("Description *");
		}
	}
	
	public void putLanguageInput(String language) {
    	languageInput.addItem(language);
    	languageInput.setValue(language);
	}
	
	public String getDocumentId() {
		return documentId;
	}

	public String getTitleFromInput() {
		return (String) titleInput.getValue();
	}

	public String getLanguageFromInput() {
		return (String) languageInput.getValue();
	}

	public String getDescriptionFromInput() {
		// XXX vorher: toString() ..?
		return (String) descriptionInput.getValue();
	}

	public String getExerciseURLFromInput() {
		return (String) exerciseURLInput.getValue();
	}

	public List<String> getNamesOfAttachments() {
		return namesOfAttachments;
	}

	public List<FileResource> getAttachments() {
		return attachments;
	}
	
	public void clearAttachments() {
		attachments.clear();
	}

}
