package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class BPTNavigationBar extends CustomComponent{
	
	private HorizontalLayout layout;
	
	public BPTNavigationBar(){
		
		layout = new HorizontalLayout();
		setCompositionRoot(layout);
		Button findButton = new Button("Find");
		layout.addComponent(findButton);
		Button uploadButton = new Button("Upload");
		layout.addComponent(uploadButton);
		
		findButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				((BPTApplication)getApplication()).finder();
			}});
		
		uploadButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				((BPTApplication)getApplication()).uploader();
			}});
	}

}
