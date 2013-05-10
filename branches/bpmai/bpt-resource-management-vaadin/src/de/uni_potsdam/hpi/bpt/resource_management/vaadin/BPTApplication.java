package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseStatus;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTUserRepository;
import de.uni_potsdam.hpi.bpt.resource_management.search.BPTTagSearchComponent;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

@SuppressWarnings({ "unchecked", "serial" })
public class BPTApplication extends Application implements HttpServletRequestListener {
	
	private BPTShowEntryComponent entryComponent;
	private BPTSidebar sidebar;
	private boolean loggedIn, loggingIn, moderated;
	private String name, mailAddress;
	private String applicationURL, openIdProvider;
	private BPTMainFrame mainFrame;
	private BPTUploader uploader;
	private BPTExerciseRepository exerciseRepository;
	private BPTUserRepository userRepository;
	
	@Override
	public void init() {
		exerciseRepository = BPTExerciseRepository.getInstance();
		userRepository = BPTUserRepository.getInstance();
		
		setProperties();
		
		Window mainWindow = new Window("BPM Academic Initiative");
//		mainWindow.setScrollable(true);
		setMainWindow(mainWindow);
		setTheme("bpmai");
		CustomLayout custom = new CustomLayout("mainlayout");
		custom.setHeight("100%");
		VerticalLayout layout =  new VerticalLayout();
		layout.setWidth("732px");
		
		sidebar = new BPTSidebar(this);
		entryComponent = new BPTEntryCards(this);
//		entryComponent = new BPTTable();
		mainFrame = new BPTMainFrame(entryComponent);
		layout.addComponent(sidebar);
		layout.addComponent(mainFrame);
//		layout.addStyleName("scroll");
		mainFrame.add(entryComponent);
		custom.addComponent(layout, "application");
//		custom.addStyleName("scroll");
		mainWindow.setContent(custom);
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}
	
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	
	public boolean isLoggingIn() {
		return loggingIn;
	}

	public void setLoggingIn(boolean loggingIn) {
		this.loggingIn = loggingIn;
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
		applicationURL = resourceBundle.getString("OPENID_RETURN_TO");
		openIdProvider = resourceBundle.getString("DEFAULT_OPEN_ID_PROVIDER");
		setLogoutURL(applicationURL);
		setLoggedIn(false);
		setLoggingIn(false);
		setModerated(false);
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
	
	public BPTExerciseRepository getExerciseRepository() {
		return exerciseRepository;
	}
	
	public BPTUserRepository getUserRepository() {
		return userRepository;
	}
	
	public BPTShowEntryComponent getTable(){
		return entryComponent;
	}

	public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String[]> map = request.getParameterMap();
		
		if (loggingIn && map.containsKey("openid.identity")) {
			loggingIn = false;
			// TODO: check nonce for security reasons
//			checkNonce(request.getParameter("openid.response_nonce"));
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
			finder();
			try {
				response.sendRedirect(getLogoutURL());
			} catch (IOException e) {
				e.printStackTrace();
			}
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

//	private void checkNonce(String nonce) {
//		// TODO Auto-generated method stub
//		if (nonce == null || nonce.length() < 20) {
//            throw new OpenIdException("Verify failed.");
//		}
//        long nonceTime = getNonceTime(nonce);
//        long diff = System.currentTimeMillis() - nonceTime;
//        if (diff < 0) {
//            diff = (-diff);
//        }
//        if (diff > 3600000L) {// ONE_HOUR
//            throw new OpenIdException("Bad nonce time.");
//        }
//        if (nonceExists(nonce)) {
//            throw new OpenIdException("Verify nonce failed.");
//        }
//        storeNonce(nonce, nonceTime + 7200000L);
//
//	}
//
//	private void storeNonce(String nonce, long nonceExpiryTime) {
//		// TODO to store in database
//		
//	}
//
//	private boolean nonceExists(String nonce) {
//		// TODO to check from database
//		return false;
//	}
//
//	private long getNonceTime(String nonce) {
//		try {
//            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
//                    .parse(nonce.substring(0, 19) + "+0000")
//                    .getTime();
//        } catch (ParseException e) {
//            throw new OpenIdException("Bad nonce time.");
//        }
//	}

	public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
	}

	public void edit(Item item) {
		uploader = new BPTUploader(item, this);
		mainFrame.add(uploader);
		sidebar.upload();
	}
	
	public void refresh() {
		IndexedContainer dataSource;
		BPTTagSearchComponent tagSearchComponent = sidebar.getSearchComponent().getTagSearchComponent();
		String query = sidebar.getSearchComponent().getFullSearchComponent().getQuery();
		String language = getSelectedLanguage();
		if (loggedIn) {
			if (!moderated) {
				if (sidebar.getSearchComponent().isOwnEntriesOptionSelected()) {
					ArrayList<String> selectedTags = tagSearchComponent.getSelectedTags();
					dataSource = BPTContainerProvider.getVisibleEntriesByUser((String)getUser(), selectedTags, query);
				} else {
					ArrayList<BPTExerciseStatus> states = new ArrayList<BPTExerciseStatus>();
					states.add(BPTExerciseStatus.Published);
					ArrayList<String> selectedTags = tagSearchComponent.getSelectedTags();
					dataSource = BPTContainerProvider.getVisibleEntries(language, states, selectedTags, query);
				}
			} else {
				ArrayList<BPTExerciseStatus> states = sidebar.getSearchComponent().getSelectedStates();
				ArrayList<String> selectedTags = tagSearchComponent.getSelectedTags();
				dataSource = BPTContainerProvider.getVisibleEntries(language, states, selectedTags, query);
			}
		} else {
			ArrayList<BPTExerciseStatus> states = new ArrayList<BPTExerciseStatus>();
			states.add(BPTExerciseStatus.Published);
			ArrayList<String> selectedTags = tagSearchComponent.getSelectedTags();
			dataSource = BPTContainerProvider.getVisibleEntries(language, states, selectedTags, query);
		}
		
		entryComponent.showEntries(dataSource);
	}
	
	public String getSelectedLanguage(){
		return sidebar.getSearchComponent().getLanguageSelector().getLanguage();
	}
}
