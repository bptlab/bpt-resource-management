package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.Application;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.*;

public class BPTApplication extends Application {
	@Override
	public void init() {
		Window mainWindow = new Window("BPTApplication");
		HorizontalLayout layout =  new HorizontalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		BPTTable table = new BPTTable();
		BPTSidebar sidebar = new BPTSidebar();
		layout.addComponent(table);
		layout.addComponent(sidebar);
		layout.setExpandRatio(table, 7);
		layout.setExpandRatio(sidebar, 3);
		mainWindow.addComponent(layout);
		setMainWindow(mainWindow);
		
	
		
	}

}
