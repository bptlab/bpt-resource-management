package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ektorp.impl.DocIdResponseHandler;

import com.vaadin.terminal.FileResource;
import com.vaadin.ui.Link;
import com.vaadin.ui.Upload.SucceededEvent;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTMimeTypes;

public class BPTPdfDocUploader extends BPTAttachmentUploader {
	
	private static final long serialVersionUID = -4515804890418012196L;
	protected String nameOfPdfFile;
	protected FileResource pdfFile;
	protected Link pdfLink;
	protected String nameOfDocFile;
	protected FileResource docFile;
	protected Link docLink;

	public BPTPdfDocUploader(BPTApplication application, String captionOfPanel,	String captionOfUploadComponent, String[] supportedDocumentTypes) {
		super(application, captionOfPanel, captionOfUploadComponent, supportedDocumentTypes);
	}
	
	@Override
	public void uploadSucceeded(final SucceededEvent event) {
		final FileResource documentResource = new FileResource(tempAttachment, getApplication());
		if (documentResource.getMIMEType().equals(BPTMimeTypes.PDF.toString())) {
			nameOfPdfFile = documentResource.getFilename();
			if (pdfLink != null) {
				removeComponent(pdfLink);
			}
			pdfLink = addLinkToAttachmentPanel(documentResource);
			pdfFile = documentResource;
	        application.refreshAndClean();
		} else if (documentResource.getMIMEType().equals(BPTMimeTypes.DOC.toString()) 
				|| documentResource.getMIMEType().equals(BPTMimeTypes.DOCX.toString())
				/* MIME type of docx files is often application/octet-stream which is not used in BPTMimeTypes */
				|| documentResource.getFilename().toLowerCase().endsWith(".docx")) {
			nameOfDocFile = documentResource.getFilename();
			if (docLink != null) {
				removeComponent(docLink);
			}
			docLink = addLinkToAttachmentPanel(documentResource);
			docFile = documentResource;
	        application.refreshAndClean();
		} else {
			// TODO: do something if uploaded file is neither PDF nor DOC/DOCX
		}
	}

	public String getNameOfPdfFile() {
		return nameOfPdfFile;
	}

	public FileResource getPdfFile() {
		return pdfFile;
	}

	public String getNameOfDocFile() {
		return nameOfDocFile;
	}

	public FileResource getDocFiles() {
		return docFile;
	}
	
	@Override
	public void clearFile() {
		File file;
		file = pdfFile.getSourceFile();
		pdfFile.setSourceFile(null);
		file.delete();
		pdfFile = null;
		file = docFile.getSourceFile();
		docFile.setSourceFile(null);
		file.delete();
		docFile = null;
	}

}
