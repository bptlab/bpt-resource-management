package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings({"serial", "deprecation"})
public class BPTNavigationBar extends HorizontalLayout {
	
	public BPTNavigationBar(final BPTApplicationUI applicationUI) {
		super();
		
		Button findButton = new Button("Find");
		findButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				applicationUI.renderEntries();
			}
		});
		addComponent(findButton);
		
		Button uploadButton = new Button("Upload");
		uploadButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				applicationUI.renderUploader();
			}
		});
		addComponent(uploadButton);
	}

}
