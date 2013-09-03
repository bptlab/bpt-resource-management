package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTMimeTypes;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTPropertyValueType;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

@SuppressWarnings("serial")
public class BPTUploadPanel extends VerticalLayout {
	
	private VerticalLayout layout;
	private TextField titleInput, exerciseURLInput;
	private ComboBox languageInput;
	private RichTextArea descriptionInput;
	private BPTUploader uploader;
	
	private String documentId;
	private BPTExerciseRepository exerciseRepository = BPTExerciseRepository.getInstance();
	private BPTAttachmentUploader attachmentPanel;
	private Label titleLabel, languageLabel, descriptionLabel;
	private ArrayList<Link> linksToExistingAttachments;
	
	public BPTUploadPanel(Item item, final BPTApplication application, BPTUploader uploader) {
		super();
		this.layout = this;
		this.uploader = uploader;
		
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
		
		attachmentPanel = new BPTAttachmentUploader(application, "Documents", "Upload at least one document (*.pdf, *.doc, *.docx)", BPTMimeTypes.getMimeTypes());
        layout.addComponent(attachmentPanel);
        
        if (item != null) {
        	documentId = item.getItemProperty("ID").toString();
        	titleInput.setValue(item.getItemProperty("Title").getValue());
        	descriptionInput.setValue(item.getItemProperty("Description").getValue().toString());
        	if (!item.getItemProperty("Exercise URL").getValue().toString().equals("")) {
            	exerciseURLInput.setValue(((Link)item.getItemProperty("Exercise URL").getValue()).getCaption().toString());
        	}
        	linksToExistingAttachments = (ArrayList<Link>) BPTVaadinResources.generateComponent(exerciseRepository, exerciseRepository.readDocument(documentId), "names_of_attachments", BPTPropertyValueType.LINK_ATTACHMENT, null, application);
        	attachmentPanel.addLinksToExistingAttachments(linksToExistingAttachments);      	
        }
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
		return (String) descriptionInput.getValue();
	}

	public String getExerciseURLFromInput() {
		return (String) exerciseURLInput.getValue();
	}

	public List<String> getNamesOfAttachments() {
		return attachmentPanel.getNamesOfAttachments();
	}

	public List<FileResource> getAttachments() {
		return attachmentPanel.getAttachments();
	}
	
	public void clearAttachments() {
		attachmentPanel.clearAttachments();
	}

	public ArrayList<Link> getLinksToExistingAttachments() {
		return linksToExistingAttachments;
	}

}
