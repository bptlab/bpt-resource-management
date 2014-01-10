package de.uni_potsdam.hpi.bpt.resource_management.upload;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;

import de.uni_potsdam.hpi.bpt.resource_management.BPTExcelImporter;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTApplicationUI;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTUploadPanel;

@SuppressWarnings("serial")
public class BPTMultiUploader extends BPTAttachmentUploader {

	public BPTMultiUploader(BPTApplicationUI applicationUI,	String captionOfPanel, String captionOfUploadComponent,	BPTUploadPanel uploadPanel) {
		super(applicationUI, captionOfPanel, captionOfUploadComponent, uploadPanel);
	}

	@Override
	public void uploadStarted(StartedEvent event) {
		if(!event.getMIMEType().equals("application/zip")){
			errorMessage = "The type of the file you have submitted is not supported.";
			uploadComponent.interruptUpload();
    	}
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		try {
			ZipFile zipFile = new ZipFile(tempAttachment);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) entries.nextElement();
//				System.out.println(zipEntry);
				if (zipEntry.toString().endsWith("xls")) {
					InputStream inputStream = zipFile.getInputStream(zipEntry);
					int numberOfExerciseSets = BPTExcelImporter.createExercisesFromExcelFile(inputStream, applicationUI.getUser(), zipFile);
					Notification.show("Upload successful", numberOfExerciseSets + " exercise sets have been uploaded.", Notification.Type.HUMANIZED_MESSAGE);
					zipFile.close();
					return;
				}
			}
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
