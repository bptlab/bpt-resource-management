package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.File;
import java.util.Arrays;

import com.vaadin.terminal.FileResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.themes.BaseTheme;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTMimeType;

public class BPTPdfDocUploader extends BPTAttachmentUploader {
	
	private static final long serialVersionUID = -4515804890418012196L;
	protected String nameOfPdfFile;
	protected FileResource pdfFile;
	protected Link pdfLink;
	protected String nameOfDocFile;
	protected FileResource docFile;
	protected Link docLink;
	private String[] supportedDocumentTypes;
	protected HorizontalLayout pdfLayout, docLayout, pdfInnerLayout, docInnerLayout;

	public BPTPdfDocUploader(BPTApplication application, String captionOfPanel,	String captionOfUploadComponent, BPTUploadPanel uploadPanel) {
		super(application, captionOfPanel, captionOfUploadComponent, uploadPanel);
		this.supportedDocumentTypes = BPTMimeType.getMimeTypes();
		pdfLayout = new HorizontalLayout();
		pdfLayout.addComponent(new Label("PDF file:&nbsp;", Label.CONTENT_XHTML));
		pdfInnerLayout = new HorizontalLayout();
		pdfInnerLayout.addComponent(new Label("(none)"));
		pdfLayout.addComponent(pdfInnerLayout);
		addComponent(pdfLayout);
		docLayout = new HorizontalLayout();
		docLayout.addComponent(new Label("DOC file:&nbsp;", Label.CONTENT_XHTML));
		docInnerLayout = new HorizontalLayout();
		docInnerLayout.addComponent(new Label("(none)"));
		docLayout.addComponent(docInnerLayout);
		addComponent(docLayout);
	}
	
	public void addPdfLinkToPanel(Link link) {
		File attachmentFile = convertToFile(link);
		pdfFile = new FileResource(attachmentFile, application);
		nameOfPdfFile = link.getCaption();
		addPdfLink(link);
	}
	
	private void addPdfLink(Link link){
		pdfInnerLayout.removeAllComponents();
		pdfInnerLayout.addComponent(link);
		pdfInnerLayout.addComponent(new Label("&nbsp;&nbsp;&nbsp;", Label.CONTENT_XHTML));
		Button deletePdfButton = new Button("x");
		deletePdfButton.setStyleName(BaseTheme.BUTTON_LINK);
		deletePdfButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				if (pdfFile != null) {
					File file = pdfFile.getSourceFile();
					pdfFile.setSourceFile(null);
					file.delete();
					pdfFile = null;
					nameOfPdfFile = "";
					pdfInnerLayout.removeAllComponents();
					pdfInnerLayout.addComponent(new Label("(none)"));
					pdfLink = null;
				}
			}
		});
		pdfInnerLayout.addComponent(deletePdfButton);
	}
	
	public void addDocLinkToPanel(Link link) {
		File attachmentFile = convertToFile(link);
		docFile = new FileResource(attachmentFile, application);
		nameOfDocFile = link.getCaption();
		addDocLink(link);
	}

	private void addDocLink(Link link) {
		docInnerLayout.removeAllComponents();
		docInnerLayout.addComponent(link);
		docInnerLayout.addComponent(new Label("&nbsp;&nbsp;&nbsp;", Label.CONTENT_XHTML));
		Button deleteDocButton = new Button("x");
		deleteDocButton.setStyleName(BaseTheme.BUTTON_LINK);
		deleteDocButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				if (docFile != null) {
					File file = docFile.getSourceFile();
					docFile.setSourceFile(null);
					file.delete();
					docFile = null;
					nameOfDocFile = "";
					docInnerLayout.removeAllComponents();
					docInnerLayout.addComponent(new Label("(none)"));
					docLink = null;
				}
			}
		});
		docInnerLayout.addComponent(deleteDocButton);
	}
	
	
	
	@Override
	public void uploadSucceeded(final SucceededEvent event) {
		final FileResource documentResource = new FileResource(tempAttachment, getApplication());
		if (documentResource.getMIMEType().equals(BPTMimeType.PDF.toString())) {
			nameOfPdfFile = documentResource.getFilename();
//			if (pdfLink != null) {
//				removeComponent(pdfLink);
//			}
			pdfLink = getLinkToAttachment(documentResource);
			addPdfLink(pdfLink);
			pdfFile = documentResource;
	        application.refreshAndClean();
		} else if (documentResource.getMIMEType().equals(BPTMimeType.DOC.toString()) 
				|| documentResource.getMIMEType().equals(BPTMimeType.DOCX.toString())
				/* MIME type of docx files is often application/octet-stream which is not used in BPTMimeTypes */
				|| documentResource.getFilename().toLowerCase().endsWith(".docx")) {
			nameOfDocFile = documentResource.getFilename();
//			if (docLink != null) {
//				removeComponent(docLink);
//			}
			docLink = getLinkToAttachment(documentResource);
			addDocLink(docLink);
			docFile = documentResource;
	        application.refreshAndClean();
		} else {
			// TODO: do something if uploaded file is neither PDF nor DOC/DOCX
		}
	}

	public String getNameOfPdfFile() {
		if (nameOfPdfFile == null) {
			return new String();
		}
		return nameOfPdfFile;
	}

	public FileResource getPdfFile() {
		return pdfFile;
	}

	public String getNameOfDocFile() {
		if (nameOfDocFile == null) {
			return new String();
		}
		return nameOfDocFile;
	}

	public FileResource getDocFiles() {
		return docFile;
	}
	
	@Override
	public void clearFiles() {
		File file;
		if (pdfFile != null) {
			file = pdfFile.getSourceFile();
			pdfFile.setSourceFile(null);
			file.delete();
		}
		if (docFile != null) {
			file = docFile.getSourceFile();
			docFile.setSourceFile(null);
			file.delete();
		}
	}
	
	@Override
	public void uploadStarted(StartedEvent event) {
		String documentType = event.getMIMEType();
    	if (!Arrays.asList(supportedDocumentTypes).contains(documentType)) {
    		errorMessage = "The type of the file you have submitted is not supported.";
            uploadComponent.interruptUpload();
    	}
    	if(event.getFilename().endsWith("pdf")){
    		if(!(uploadPanel.isNameAvailableForDoc(event.getFilename()))){
        		errorMessage = "You can not upload two files with the same name in one excercise.";
                uploadComponent.interruptUpload();
    		}
    	}
    	else{
    		if(!(uploadPanel.isNameAvailableForPdf(event.getFilename()))){
        		errorMessage = "You can not upload two files with the same name in one excercise.";
                uploadComponent.interruptUpload();
    		}
    	}

	}
}
