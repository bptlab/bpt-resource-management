package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Collection;
import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.ui.*;

public class BPTApplication extends Application {
	private BPTTable table;
	private BPTSidebar sidebar;
	private Boolean loggedIn;
	private String username;
	private BPTMainFrame mainFrame;
	private BPTUploader uploader;
	
	@Override
	public void init() {
		Window mainWindow = new Window("BPTApplication");
		HorizontalLayout layout =  new HorizontalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		setLoggedIn(false);
		setUsername("Guest");
		table = new BPTTable();
		mainFrame = new BPTMainFrame(table);
		sidebar = new BPTSidebar(this);
		layout.addComponent(mainFrame);
		layout.addComponent(sidebar);
		mainFrame.add(table);
		layout.setExpandRatio(mainFrame, 7);
		layout.setExpandRatio(sidebar, 3);
		mainWindow.addComponent(layout);
		setMainWindow(mainWindow);
		
		
	
		
	}
	public void refresh(ArrayList<String> tagValues) {
		
		table.filterBy(tagValues);
	}
	public Boolean isLoggedIn() {
		return loggedIn;
	}
	public void setLoggedIn(Boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void uploader() {
		uploader = new BPTUploader();
		mainFrame.add(uploader);
	}
	public void finder() {
		table = new BPTTable();
		mainFrame.add(table);
	}

}
