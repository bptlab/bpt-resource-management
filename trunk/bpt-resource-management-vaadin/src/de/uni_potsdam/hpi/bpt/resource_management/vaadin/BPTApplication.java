package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import com.vaadin.Application;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentRepository;

public class BPTApplication extends Application {
	private BPTTable table;
	private BPTSidebar sidebar;
	private boolean loggedIn;
	private String username;
	private BPTMainFrame mainFrame;
	private BPTUploader uploader;
	private BPTDocumentRepository toolRepository = new BPTDocumentRepository("bpt_resources");
	
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
	
	public boolean isLoggedIn() {
		return loggedIn;
	}
	
	public void setLoggedIn(boolean loggedIn) {
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
		sidebar.upload();
	}
	
	public void finder() {
		table = new BPTTable();
		mainFrame.add(table);
		sidebar.finder();
	}
	
	public BPTDocumentRepository getToolRepository() {
		return toolRepository;
	}
	
	public BPTTable getTable(){
		return table;
	}

}
