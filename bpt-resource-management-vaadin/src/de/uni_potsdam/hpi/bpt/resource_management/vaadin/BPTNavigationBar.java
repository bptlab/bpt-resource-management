package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class BPTNavigationBar extends CustomComponent{
	
	private HorizontalLayout layout;
	
	public BPTNavigationBar() {
		
		layout = new HorizontalLayout();
		setCompositionRoot(layout);
		
		Button findButton = new Button("Find");
//        findButton.setStyleName(BaseTheme.BUTTON_LINK);
		findButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				((BPTApplication)getApplication()).renderEntries();
			}
		});
		layout.addComponent(findButton);
		
//		Label separatorLabel = new Label("&nbsp;|&nbsp;", Label.CONTENT_XHTML);
//		layout.addComponent(separatorLabel);
		
		Button uploadButton = new Button("Upload");
//	    uploadButton.setStyleName(BaseTheme.BUTTON_LINK);
		layout.addComponent(uploadButton);
		
		uploadButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				((BPTApplication)getApplication()).renderUploader();
			}
		});
	}

}
