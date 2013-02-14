package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.Application;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.service.ApplicationContext;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.terminal.gwt.server.WebApplicationContext;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTUserRepository;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

public class BPTApplication extends Application implements HttpServletRequestListener {
	private BPTShowEntryComponent entryComponent;
	private BPTSidebar sidebar;
	private boolean loggedIn;
	private boolean moderated;
	private String name, mailAddress;
	private String openIdProvider;
	private BPTMainFrame mainFrame;
	private BPTUploader uploader;
	private BPTToolRepository toolRepository = new BPTToolRepository();
	private BPTUserRepository userRepository = new BPTUserRepository();
	
	@Override
	public void init() {
		
		setProperties();
		
		setLoggedIn(false);
		setModerated(false);
		
		Window mainWindow = new Window("BPTApplication");
		setTheme("bpt");
		CustomLayout custom = new CustomLayout("mainlayout");
		custom.setWidth("100%");
		custom.setHeight("100%");
//		HorizontalLayout layout = new HorizontalLayout();
		VerticalLayout layout =  new VerticalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		
		entryComponent = new BPTEntryCards(this);
//		entryComponent = new BPTTable();
		mainFrame = new BPTMainFrame(entryComponent);
		sidebar = new BPTSidebar(this);
		layout.addComponent(sidebar);
		layout.addComponent(mainFrame);
		mainFrame.add(entryComponent);
//		layout.setExpandRatio(mainFrame, 7);
//		layout.setExpandRatio(sidebar, 3);
		
		custom.addComponent(layout, "application");
		mainWindow.addComponent(custom);
		setMainWindow(mainWindow);
//		mainWindow.executeJavaScript(getScript());
		
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}
	
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	
	public boolean isModerated() {
		return moderated;
	}
	
	public void setModerated(boolean moderated) {
		this.moderated = moderated;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	

	private void setProperties() {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("de.uni_potsdam.hpi.bpt.resource_management.bptrm");
		openIdProvider = resourceBundle.getString("DEFAULT_OPEN_ID_PROVIDER");
	}

	public void uploader() {
		uploader = new BPTUploader(null, this);
		mainFrame.add(uploader);
		sidebar.upload();
	}
	
	public void finder() {
		sidebar.finder();
		refresh();
		mainFrame.add(entryComponent);
		
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

	@Override
	public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String[]> map = request.getParameterMap();
		
		if (map.containsKey("openid.identity")) {
			setUser(map.get("openid.identity")[0]);
			System.out.println("The OpenID identifier: " + (String)getUser());
			if (openIdProvider.equals("Google")) {
				name = map.get("openid.ext1.value.firstname")[0] + " " + map.get("openid.ext1.value.lastname")[0]; 
				mailAddress = map.get("openid.ext1.value.email")[0];
			} else { // openIdProvider.equals("Yahoo")
				name = map.get("openid.ax.value.fullname")[0]; 
				mailAddress = map.get("openid.ax.value.email")[0];
			}
			moderated = userRepository.isModerator((String)getUser(), name, mailAddress);
			loggedIn = true;
			sidebar.login(name);
		} else {
			return;
		}
		
//		System.out.println("-------------------------------START---------------------------------");
//		
//		for (Map.Entry<String, String[]> entry : map.entrySet()) {
//		    System.out.println("Key = " + entry.getKey());
//		    System.out.println("Values:");
//		    for(int i = 0; i < entry.getValue().length; i++){
//		    	System.out.println(entry.getValue()[i].toString());
//		    }
//		}
//		
//		System.out.println("-------------------------------END---------------------------------");
//		System.out.println();

	}

	@Override
	public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
		
	}

	public void edit(Item item) {
		uploader = new BPTUploader(item, this);
		mainFrame.add(uploader);
		sidebar.upload();
	}
	
	public void refresh() {
		IndexedContainer dataSource;
		if (loggedIn) {
			if (!moderated) {
				if (sidebar.getSearchComponent().isOwnEntriesOptionSelected()) {
					dataSource = BPTContainerProvider.getVisibleEntriesByUser((String)getUser());
				} else {
					ArrayList<BPTToolStatus> states = new ArrayList<BPTToolStatus>();
					states.add(BPTToolStatus.Published);
					ArrayList<String> selectedTags = sidebar.getSearchComponent().getSelectedTags();
					dataSource = BPTContainerProvider.getVisibleEntries(states, selectedTags);
				}
			} else {
				ArrayList<BPTToolStatus> states = sidebar.getSearchComponent().getSelectedStates();
				ArrayList<String> selectedTags = sidebar.getSearchComponent().getSelectedTags();
				dataSource = BPTContainerProvider.getVisibleEntries(states, selectedTags);
			}
		} else {
			ArrayList<BPTToolStatus> states = new ArrayList<BPTToolStatus>();
			states.add(BPTToolStatus.Published);
			ArrayList<String> selectedTags = sidebar.getSearchComponent().getSelectedTags();
			dataSource = BPTContainerProvider.getVisibleEntries(states, selectedTags);
		}
		
		entryComponent.showEntries(dataSource);
	}
	
//	private String getScript(){
//		
//		return null;
//	}
}
