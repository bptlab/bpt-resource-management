package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class BPTLoginComponent extends CustomComponent{
	
	public BPTLoginComponent(){
		
		HorizontalLayout layout = new HorizontalLayout();
		setCompositionRoot(layout);
		layout.addComponent(new Label("Willkommen"));
	}

}
