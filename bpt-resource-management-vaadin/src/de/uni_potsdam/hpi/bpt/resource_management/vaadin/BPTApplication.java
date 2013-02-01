package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.Application;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import com.vaadin.data.Item;
import com.vaadin.service.ApplicationContext;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.terminal.gwt.server.WebApplicationContext;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTLoginManager;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTUserRepository;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

public class BPTApplication extends Application implements HttpServletRequestListener {
	private BPTShowEntryComponent entryComponent;
	private BPTSidebar sidebar;
	private boolean loggedIn, moderated;
	private String _id, name, mailAddress;
	private String openIdProvider = "Google"; // TODO: hard coded
	private BPTMainFrame mainFrame;
	private BPTUploader uploader;
	private BPTToolRepository toolRepository = new BPTToolRepository();
	private BPTUserRepository userRepository = new BPTUserRepository();
//	private BPTLoginManager loginManager;
//	private HttpServletRequest request;
//	private HttpServletResponse response;
//	private WebApplicationContext webAppCtx;
	
	@Override
	public void init() {
		Window mainWindow = new Window("BPTApplication");
		HorizontalLayout layout =  new HorizontalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		setLoggedIn(false);
		entryComponent = new BPTEntryCards(this);
		mainFrame = new BPTMainFrame(entryComponent);
		sidebar = new BPTSidebar(this);
		layout.addComponent(mainFrame);
		layout.addComponent(sidebar);
		mainFrame.add(entryComponent);
		layout.setExpandRatio(mainFrame, 7);
		layout.setExpandRatio(sidebar, 3);
		mainWindow.addComponent(layout);
		setMainWindow(mainWindow);
		setTheme("bpt_theme");
//		loginManager = new BPTLoginManager();
		
//		ApplicationContext ctx = this.getContext();
//		this.webAppCtx = (WebApplicationContext) ctx;
//		ctx.addTransactionListener(this);
					
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}
	
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isModerated() {
		return moderated;
	}
	
	public void setModerated(boolean moderated) {
		this.moderated = moderated;
	}
	
	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public String getOpenIdProvider() {
		return openIdProvider;
	}

	public void setOpenIdProvider(String openIdProvider) {
		this.openIdProvider = openIdProvider;
	}

	public void uploader() {
		uploader = new BPTUploader(null, this);
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
		// http://novell.com/openid ?
		// https://me.yahoo.com
//		ServletContext context = ((WebApplicationContext) getContext()).getHttpSession().getServletContext();
//		String x = loginManager.loginRequest(userSupportedString, context, request, response);
//		System.out.println(x);
	}

	@Override
	public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String[]> map = request.getParameterMap();
		
		try {
			_id = map.get("openid.identity")[0];
			System.out.println("The OpenID identifier: " + _id);
			if (openIdProvider.equals("Google")) {
				name = map.get("openid.ext1.value.firstname")[0] + " " + map.get("openid.ext1.value.lastname")[0]; 
				mailAddress = map.get("openid.ext1.value.email")[0];
			} else { // openIdProvider.equals("Yahoo")
				name = map.get("openid.ax.value.fullname")[0]; 
				mailAddress = map.get("openid.ax.value.email")[0];
			}
			moderated = userRepository.isModerator(_id, name, mailAddress);
			loggedIn = true;
			sidebar.login(name);
			
		} catch (NullPointerException e) {
			return;
		}
		
		System.out.println("-------------------------------START---------------------------------");
		
		for (Map.Entry<String, String[]> entry : map.entrySet()) {
		    System.out.println("Key = " + entry.getKey());
		    System.out.println("Values:");
		    for(int i = 0; i < entry.getValue().length; i++){
		    	System.out.println(entry.getValue()[i].toString());
		    }
		}
		
		System.out.println("-------------------------------END---------------------------------");
		System.out.println();
		
	}

	@Override
	public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
		
	}

//	@Override
//	public void transactionStart(Application application, Object transactionData) {
//		// TODO Auto-generated method stub
//		System.out.println("Transaction start");
//		System.out.println(transactionData);
//		HttpServletRequest request = (HttpServletRequest)transactionData;
//		Map<String, String[]> map = request.getParameterMap();
//		for (Map.Entry<String, String[]> entry : map.entrySet()) {
//		    System.out.println("Key = " + entry.getKey());
//		    System.out.println("Values:");
//		    for(int i = 0; i < entry.getValue().length; i++){
//		    	System.out.println(entry.getValue()[i].toString());
//		    }
//		}
//		
//	}
//
//	@Override
//	public void transactionEnd(Application application, Object transactionData) {
//		// TODO Auto-generated method stub
//		
//	}

	public void edit(Item item) {
		uploader = new BPTUploader(item, this);
		mainFrame.add(uploader);
		sidebar.upload();
	}
	
	public void refresh(){
		ArrayList<BPTToolStatus> states = sidebar.getSearchComponent().getSelectedStates();
		ArrayList<String> selectedTags = sidebar.getSearchComponent().getSelectedTags();
		entryComponent.showEntries(BPTContainerProvider.getVisibleEntries(states, selectedTags));
	}
}
