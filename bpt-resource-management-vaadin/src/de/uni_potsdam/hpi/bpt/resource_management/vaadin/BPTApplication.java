package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import javax.servlet.ServletContext;

import com.vaadin.Application;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import com.vaadin.terminal.gwt.server.WebApplicationContext;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTLoginManager;

public class BPTApplication extends Application {
	private BPTShowEntryComponent entryComponent;
	private BPTSidebar sidebar;
	private boolean loggedIn;
	private String username;
	private BPTMainFrame mainFrame;
	private BPTUploader uploader;
	private BPTDocumentRepository toolRepository = new BPTDocumentRepository("bpt_resources");
	private BPTLoginManager loginManager;
	
	@Override
	public void init() {
		Window mainWindow = new Window("BPTApplication");
		HorizontalLayout layout =  new HorizontalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		setLoggedIn(false);
		setUsername("Guest");
		entryComponent = new BPTTable();
		mainFrame = new BPTMainFrame(entryComponent);
		sidebar = new BPTSidebar(this);
		layout.addComponent(mainFrame);
		layout.addComponent(sidebar);
		mainFrame.add(entryComponent);
		layout.setExpandRatio(mainFrame, 7);
		layout.setExpandRatio(sidebar, 3);
		mainWindow.addComponent(layout);
		setMainWindow(mainWindow);
		loginManager = new BPTLoginManager();
					
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
		entryComponent = new BPTTable();
		mainFrame.add(entryComponent);
		sidebar.finder();
	}
	
	public BPTDocumentRepository getToolRepository() {
		return toolRepository;
	}
	
	public BPTShowEntryComponent getTable(){
		return entryComponent;
	}
	
	public void loginRequest(String userSupportedString){
		ServletContext context = ((WebApplicationContext) getContext()).getHttpSession().getServletContext();
		loginManager.loginRequest(userSupportedString, context);
	}

}
