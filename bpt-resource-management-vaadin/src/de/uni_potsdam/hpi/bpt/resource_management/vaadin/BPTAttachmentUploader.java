package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTMimeType;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

public class BPTAttachmentUploader extends Panel implements Upload.StartedListener, Upload.SucceededListener, Upload.FailedListener, Upload.Receiver {
	
	protected static final long serialVersionUID = 5970533396623869051L;
	protected Upload uploadComponent;
	protected File tempAttachment;
	protected List<String> namesOfAttachments = new ArrayList<String>();
	protected List<FileResource> attachments = new ArrayList<FileResource>();
	protected BPTApplication application;
	protected String errorMessage = new String();
	private VerticalLayout mainLayout;
	protected BPTUploadPanel uploadPanel;

	public BPTAttachmentUploader(BPTApplication application, String captionOfPanel, String captionOfUploadComponent, BPTUploadPanel uploadPanel) {
		this.application = application;
		setCaption(captionOfPanel);
		this.mainLayout = new VerticalLayout();
		mainLayout.setImmediate(true);
		this.addComponent(mainLayout);
		this.uploadPanel = uploadPanel;
		uploadComponent = new Upload(captionOfUploadComponent, this);
		uploadComponent.setImmediate(false);
		uploadComponent.setWidth("-1px");
		uploadComponent.setHeight("-1px");
		uploadComponent.addListener((Upload.StartedListener)this);
		uploadComponent.addListener((Upload.SucceededListener)this);
		uploadComponent.addListener((Upload.FailedListener)this);
		mainLayout.addComponent(uploadComponent);
	}

	public void uploadStarted(StartedEvent event) {
		if(!(uploadPanel.isNameAvailableForAttachement(event.getFilename()))){
    		errorMessage = "You can not upload two files with the same name in one excercise.";
            uploadComponent.interruptUpload();
		}
	}
	
	public OutputStream receiveUpload(String filename, String mimeType) {
		FileOutputStream outputStream = null;
		tempAttachment = new File(filename);
		//		System.out.println(filename + ":" + document.canExecute() + document.canRead() + document.canWrite());
        try {
        	outputStream = new FileOutputStream(tempAttachment);
        } catch (FileNotFoundException e) {
            errorMessage = "The file was not found.";
            uploadComponent.interruptUpload();
        }
        return outputStream;
	}
	
	public void uploadFailed(FailedEvent event) {
		getWindow().showNotification(
                "Upload failed",
                errorMessage,
                Notification.TYPE_ERROR_MESSAGE);
	}
	
	public void uploadSucceeded(final SucceededEvent event) {
		final FileResource documentResource = new FileResource(tempAttachment, getApplication());
		namesOfAttachments.add(documentResource.getFilename());
		final HorizontalLayout layout = new HorizontalLayout();
		layout.addComponent(getLinkToAttachment(documentResource));
		layout.addComponent(new Label("&nbsp;&nbsp;&nbsp;", Label.CONTENT_XHTML));
		Button deleteButton = new Button("x");
		deleteButton.setStyleName(BaseTheme.BUTTON_LINK);
		deleteButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				mainLayout.removeComponent(layout);
				namesOfAttachments.remove(documentResource.getFilename());
				attachments.remove(documentResource);
			}
		});
		layout.addComponent(deleteButton);
		mainLayout.addComponent(layout);
		attachments.add(documentResource);
//		System.out.println(documentResource);
//        application.refreshAndClean();
	}
	
	protected Link getLinkToAttachment(FileResource documentResource) {
		Link link = new Link(documentResource.getFilename(), documentResource);
		BPTVaadinResources.setTargetAndIcon(link);
		return link;
	}
	
	public List<String> getNamesOfAttachments() {
		return namesOfAttachments;
	}

	public List<FileResource> getAttachments() {
		return attachments;
	}
	
	public void clearFiles() {
		for (FileResource attachment : attachments) {
			File file = attachment.getSourceFile();
			attachment.setSourceFile(null);
			file.delete();
		}
		attachments.clear();
	}

	public void addLinksToExistingAttachments(ArrayList<Link> linksToExistingAttachments) {
		for (Link link : linksToExistingAttachments) {
    		File attachmentFile = convertToFile(link);
    		attachments.add(new FileResource(attachmentFile, application));
    		namesOfAttachments.add(link.getCaption());
    		addComponent(link);
    	}
	}

	protected File convertToFile(Link link) {
		InputStream inputStream = null;
		OutputStream outputStream = null;;
		File attachmentFile = new File(link.getCaption());
		try {
    		inputStream = ((StreamResource) link.getResource()).getStream().getStream();
    		outputStream = new FileOutputStream(attachmentFile);
    		int read = 0;
    		byte[] bytes = new byte[1024];
    		
    		while ((read = inputStream.read(bytes)) != -1) {
    			outputStream.write(bytes, 0, read);
    		}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	 
			}
		}
		return attachmentFile;
	}
}
