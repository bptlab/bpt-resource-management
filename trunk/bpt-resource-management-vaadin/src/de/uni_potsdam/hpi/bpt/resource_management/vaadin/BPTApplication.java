package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Collection;
import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.ui.*;

public class BPTApplication extends Application {
	private BPTTable table;
	private BPTSidebar sidebar;
	@Override
	public void init() {
		Window mainWindow = new Window("BPTApplication");
		HorizontalLayout layout =  new HorizontalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		table = new BPTTable();
		sidebar = new BPTSidebar(this);
		layout.addComponent(table);
		layout.addComponent(sidebar);
		layout.setExpandRatio(table, 7);
		layout.setExpandRatio(sidebar, 3);
		mainWindow.addComponent(layout);
		setMainWindow(mainWindow);
		
	
		
	}
	public void refresh(ArrayList<String> tagValues) {
		
		table.filterBy(tagValues);
	}

}
