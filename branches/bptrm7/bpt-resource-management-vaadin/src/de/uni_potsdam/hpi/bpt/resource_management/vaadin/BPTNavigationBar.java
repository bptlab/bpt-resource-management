package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings({"serial"})
public class BPTNavigationBar extends HorizontalLayout {
	
	public BPTNavigationBar(final BPTApplicationUI applicationUI) {
		super();
		
		Button findButton = new Button("Find");
		findButton.addClickListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				applicationUI.showAll(true);
			}
		});
		addComponent(findButton);
		
		Button uploadButton = new Button("Upload");
		uploadButton.addClickListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				applicationUI.renderUploader();
			}
		});
		addComponent(uploadButton);
	}

}
