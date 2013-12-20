package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.jsoup.nodes.Entities;

import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import de.uni_potsdam.hpi.bpt.resource_management.BPTExcelImporter;

public class BPTMultiUploadReceiver implements Upload.Receiver, SucceededListener {
	
	private File file;
	private BPTApplicationUI applicationUI;

	public BPTMultiUploadReceiver(BPTApplicationUI applicationUI) {
		this.applicationUI = applicationUI;
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		FileOutputStream fos = null;

        try {
            // Open the file for writing.
            file = new File("C:/Windows/Temp/" + filename);
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            new Notification("Could not open file",
                             e.getMessage(),
                             Notification.Type.ERROR_MESSAGE)
                .show(Page.getCurrent());
            return null;
        }
		if(!filename.endsWith("zip")){
			new Notification("Please upload a Zip-archive",
                    Notification.Type.ERROR_MESSAGE)
			.show(Page.getCurrent());
		}
        return fos;
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		try {
			ZipFile zipFile = new ZipFile(file);
			Enumeration entries = zipFile.entries();
			System.out.println(zipFile.getEntry("ue1.pdf"));
			 while(entries.hasMoreElements()) {
				 ZipEntry zipEntry = (ZipEntry) entries.nextElement();
				 if(zipEntry.toString().endsWith("xls")){
					 InputStream inputStream = zipFile.getInputStream(zipEntry);
					 BPTExcelImporter.createUploadsFromExcelFile(inputStream, applicationUI.getUser(), zipFile);
					 return;
				 }
			 }
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
