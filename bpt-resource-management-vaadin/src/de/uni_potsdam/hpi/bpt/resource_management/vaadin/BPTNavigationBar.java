package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class BPTNavigationBar extends HorizontalLayout{
	
	public BPTNavigationBar() {
		super();
		
		Button findButton = new Button("Find");
		findButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				((BPTApplication)getApplication()).renderEntries();
			}
		});
		addComponent(findButton);
		
		Button uploadButton = new Button("Upload");
		uploadButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				((BPTApplication)getApplication()).renderUploader();
			}
		});
		addComponent(uploadButton);
	}

}
