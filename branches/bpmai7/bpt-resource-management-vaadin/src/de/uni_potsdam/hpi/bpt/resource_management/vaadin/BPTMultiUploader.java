package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class BPTMultiUploader extends VerticalLayout{

	private BPTApplicationUI applicationUI;
	private Upload sample;
    private Window uploadInfoWindow;
	private BPTMultiUploadReceiver uploadReceiver;

	public BPTMultiUploader(BPTApplicationUI applicationUI) {
		this.applicationUI = applicationUI;
		this.uploadReceiver = new BPTMultiUploadReceiver(applicationUI);
		sample = new Upload("Zip-Archiv upload", uploadReceiver);
        sample.setImmediate(false);
        sample.setButtonCaption("Upload File");
//        sample.addStartedListener(new StartedListener() {
// 
//
//			@Override
//            public void uploadStarted(final StartedEvent event) {
//				
//            }
//        });
//        sample.addFinishedListener(new Upload.FinishedListener() {
//            @Override
//            public void uploadFinished(final FinishedEvent event) {
//            	
//            }
//        });
        
        //TODO: receiver implementieren
        sample.addSucceededListener(uploadReceiver);
        
        addComponent(sample);

	}
}
