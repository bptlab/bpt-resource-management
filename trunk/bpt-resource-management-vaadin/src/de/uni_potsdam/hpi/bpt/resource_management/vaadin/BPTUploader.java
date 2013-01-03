package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.OutputStream;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Upload.FinishedEvent;

public class BPTUploader extends CustomComponent implements Upload.Receiver{
	
	private VerticalLayout layout;
	private Upload upload;
	
	public BPTUploader(){
		layout = new VerticalLayout();
		setCompositionRoot(layout);
		
		
		
		
		upload = new Upload("Please upload a doc/docx or pdf document", this);
		upload.setImmediate(false);
		upload.setWidth("-1px");
		upload.setHeight("-1px");
		layout.addComponent(upload);
		
		upload.addListener(new Upload.FinishedListener() {
			public void uploadFinished(FinishedEvent event) {
				
				System.out.println("upload finished");
			}
		});
	}

	public OutputStream receiveUpload(String filename, String mimeType) {
		
		System.out.println("output stream");
		return null;

		
	}

}
