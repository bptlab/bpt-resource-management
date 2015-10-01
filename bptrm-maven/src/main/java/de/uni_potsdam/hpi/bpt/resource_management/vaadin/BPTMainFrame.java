package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class BPTMainFrame extends VerticalLayout {
	
	public BPTMainFrame(Component component) {
		setWidth("100%");
		addComponent(component);
	}
	
	public void add(Component component) {
		removeAllComponents();
		addComponent(component);
	}
}
