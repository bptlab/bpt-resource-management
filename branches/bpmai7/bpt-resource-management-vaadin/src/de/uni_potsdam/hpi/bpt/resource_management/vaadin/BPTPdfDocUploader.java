package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.File;
import java.util.Arrays;

import com.vaadin.server.FileResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.themes.BaseTheme;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTMimeType;

@SuppressWarnings("serial")
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

	public BPTPdfDocUploader(BPTApplicationUI applicationUI, String captionOfPanel,	String captionOfUploadComponent, BPTUploadPanel uploadPanel) {
		super(applicationUI, captionOfPanel, captionOfUploadComponent, uploadPanel);
		this.supportedDocumentTypes = BPTMimeType.getMimeTypes();
		pdfLayout = new HorizontalLayout();
		pdfLayout.addComponent(new Label("PDF file:&nbsp;", ContentMode.HTML));
		pdfInnerLayout = new HorizontalLayout();
		pdfInnerLayout.addComponent(new Label("(none)"));
		pdfLayout.addComponent(pdfInnerLayout);
		mainLayout.addComponent(pdfLayout);
		docLayout = new HorizontalLayout();
		docLayout.addComponent(new Label("DOC file:&nbsp;", ContentMode.HTML));
		docInnerLayout = new HorizontalLayout();
		docInnerLayout.addComponent(new Label("(none)"));
		docLayout.addComponent(docInnerLayout);
		mainLayout.addComponent(docLayout);
	}
	
	public void addPdfLinkToPanel(Link link) {
		File attachmentFile = convertToFile(link);
		pdfFile = new FileResource(attachmentFile);
		nameOfPdfFile = link.getCaption();
		addPdfLink(link);
	}
	
	private void addPdfLink(Link link){
		pdfInnerLayout.removeAllComponents();
		pdfInnerLayout.addComponent(link);
		pdfInnerLayout.addComponent(new Label("&nbsp;&nbsp;&nbsp;", ContentMode.HTML));
		Button deletePdfButton = new Button("x");
		deletePdfButton.setStyleName(BaseTheme.BUTTON_LINK);
		deletePdfButton.addClickListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				if (pdfFile != null) {
					pdfFile.getSourceFile().delete();
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
		docFile = new FileResource(attachmentFile);
		nameOfDocFile = link.getCaption();
		addDocLink(link);
	}

	private void addDocLink(Link link) {
		docInnerLayout.removeAllComponents();
		docInnerLayout.addComponent(link);
		docInnerLayout.addComponent(new Label("&nbsp;&nbsp;&nbsp;", ContentMode.HTML));
		Button deleteDocButton = new Button("x");
		deleteDocButton.setStyleName(BaseTheme.BUTTON_LINK);
		deleteDocButton.addClickListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				if (docFile != null) {
					docFile.getSourceFile().delete();
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
		final FileResource documentResource = new FileResource(tempAttachment);
		if (documentResource.getMIMEType().equals(BPTMimeType.PDF.toString())) {
			nameOfPdfFile = documentResource.getFilename();
//			if (pdfLink != null) {
//				removeComponent(pdfLink);
//			}
			pdfLink = getLinkToAttachment(documentResource);
			addPdfLink(pdfLink);
			pdfFile = documentResource;
	        applicationUI.refreshAndClean();
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
	        applicationUI.refreshAndClean();
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
			pdfFile.getSourceFile().delete();
		}
		if (docFile != null) {
			file = docFile.getSourceFile();
			docFile.getSourceFile().delete();
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
