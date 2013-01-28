package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.Application;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.terminal.gwt.server.WebApplicationContext;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTLoginManager;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTUserRepository;

public class BPTApplication extends Application implements HttpServletRequestListener{
	private BPTShowEntryComponent entryComponent;
	private BPTSidebar sidebar;
	private boolean loggedIn, moderator;
	private String username, mailAddress;
	private BPTMainFrame mainFrame;
	private BPTUploader uploader;
	private BPTToolRepository toolRepository = new BPTToolRepository();
	private BPTUserRepository userRepository = new BPTUserRepository();
	private BPTLoginManager loginManager;
	private HttpServletRequest request;
	private HttpServletResponse response;
	
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
	
	public boolean isModerator() {
		return moderator;
	}
	
	public void setModerator(boolean moderator) {
		this.moderator = moderator;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
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
	
	public BPTToolRepository getToolRepository() {
		return toolRepository;
	}
	
	public BPTUserRepository getUserRepository() {
		return userRepository;
	}
	
	public BPTShowEntryComponent getTable(){
		return entryComponent;
	}
	
	public void loginRequest(String userSupportedString){
		// userSupportedString = https://www.google.com/accounts/o8/id
		ServletContext context = ((WebApplicationContext) getContext()).getHttpSession().getServletContext();
		loginManager.loginRequest(userSupportedString, context, request, response);
	}

	@Override
	public void onRequestStart(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		System.out.println("-------------------------------START---------------------------------");
		this.request = request;
		this.response = response;
		System.out.println(request.getPathInfo());
		System.out.println(request.getRequestURL());
		System.out.println(request.getRequestURI());
		
	}

	@Override
	public void onRequestEnd(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		
		System.out.println("-------------------------------ENDE---------------------------------");
		this.request = request;
		this.response = response;
	}

}
