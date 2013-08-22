package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseSetRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseStatus;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTUserRepository;
import de.uni_potsdam.hpi.bpt.resource_management.search.BPTSearchComponent;
import de.uni_potsdam.hpi.bpt.resource_management.search.BPTTagSearchComponent;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

@SuppressWarnings({ "unchecked", "serial" })
public class BPTApplication extends Application implements HttpServletRequestListener {
	
	// change theme name for different platform
	private final String themeName = "bpmai";
	
	private BPTShowEntryComponent entryComponent;
	private BPTSidebar sidebar;
	private boolean loggedIn, loggingIn, moderated;
	private String name, mailAddress;
	private String applicationURL, openIdProvider;
	private BPTMainFrame mainFrame;
	private BPTUploader uploader;

	private BPTExerciseSetRepository exerciseSetRepository;
	private BPTExerciseRepository exerciseRepository;
	private BPTUserRepository userRepository;
	private BPTContainerProvider containerProvider;
	private int numberOfEntries;
	private Button administrationButton;
	private BPTAdministrator administrator;	@Override
	public void init() {
		exerciseSetRepository = BPTExerciseSetRepository.getInstance();
		exerciseRepository = BPTExerciseRepository.getInstance();
		userRepository = BPTUserRepository.getInstance();
		containerProvider = new BPTContainerProvider(this);
		
		setProperties();
		
		final Window mainWindow = new Window("BPM Academic Initiative");
		mainWindow.setScrollable(true);
		setMainWindow(mainWindow);
		setTheme(themeName);
		final CustomLayout custom = new CustomLayout(themeName + "_mainlayout");
		custom.setHeight("100%");
		VerticalLayout layout =  new VerticalLayout();
		layout.setWidth("732px");
		
		setSidebar(new BPTSidebar(this));
		entryComponent = new BPTEntryCards(this);
//		entryComponent = new BPTTable();
		mainFrame = new BPTMainFrame(entryComponent);
		layout.addComponent(getSidebar());
		layout.addComponent(mainFrame);
		mainFrame.add(entryComponent);
		custom.addComponent(layout, "application");
		custom.addStyleName("scroll");
		mainWindow.setContent(custom);
		
		addAdministrationButton(custom);
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

	public void renderUploader() {
		uploader = new BPTUploader(null, this);
		mainFrame.add(uploader);
		getSidebar().renderUploader();
	}
	
	public void renderAdministrator() {
		administrator = new BPTAdministrator(this);
		mainFrame.add(administrator);
		getSidebar().renderAdministrator();
	}
	
	public void renderEntries() {
		getSidebar().renderEntries();		refreshAndClean();
		mainFrame.add(entryComponent);
	}

	private void addAdministrationButton(final CustomLayout custom) {
		administrationButton = new Button("Administration");
		administrationButton.setStyleName(BaseTheme.BUTTON_LINK);
		administrationButton.addStyleName("redButton");
//        administrationButton.addStyleName("greyButton");
        custom.addComponent(administrationButton, "loginLink");
		
        administrationButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				final Window administrationWindow = new Window("Administration");
				administrationWindow.setClosable(true);
				administrationWindow.setDraggable(false);
				administrationWindow.setImmediate(true);
				administrationWindow.setModal(true);
				administrationWindow.setResizable(false);
				
				final CustomLayout administrationLayout = new CustomLayout("popup_administration");
				administrationLayout.setWidth("350px");
//				administrationLayout.setMargin(true);
				administrationWindow.setContent(administrationLayout);

				final Label failureLabel = new Label("<font color=\"#FF0000\">Invalid password.</font>", Label.CONTENT_XHTML);
				failureLabel.setVisible(false);
				administrationLayout.addComponent(failureLabel, "failureLabel");
				final PasswordField passwordInput = new PasswordField();
				passwordInput.setInputPrompt("Password");
				passwordInput.setWidth("230px");
				administrationLayout.addComponent(passwordInput, "passwordInput");
				Button loginButton = new Button("Login");
				loginButton.setClickShortcut(KeyCode.ENTER);
				loginButton.setWidth("70px");
				loginButton.addListener(new Button.ClickListener(){
						public void buttonClick(ClickEvent event) {
							if (((String) passwordInput.getValue()).equals("petrinet")) {
								failureLabel.setVisible(false);
								sidebar.getLoginComponent().addLoginButton();
								getMainWindow().removeWindow(administrationWindow);
								custom.removeComponent(administrationButton);
							} else {
								failureLabel.setVisible(true);
	}
						}
					});
				administrationLayout.addComponent(loginButton, "loginButton");
				getMainWindow().addWindow(administrationWindow);
			}
		});
	}
	
	public BPTExerciseRepository getExerciseRepository() {
		return exerciseRepository;
	}
	
	public BPTExerciseSetRepository getExerciseSetRepository() {
		return exerciseSetRepository;
	}
	
	public BPTUserRepository getUserRepository() {
		return userRepository;
	}
	
	public BPTContainerProvider getContainerProvider() {
		return containerProvider;
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
			getSidebar().login(name, moderated);
			renderEntries();
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
		getSidebar().renderUploader();	}
	
	public void refreshAndClean() {
		refreshAndClean(0);
		((BPTEntryCards) entryComponent).getBPTPageSelector().showNumberOfEntries(numberOfEntries);
	}
	
	public void refreshAndClean(int skip) {
		refresh(skip);
		Runtime.getRuntime().gc();
		((BPTEntryCards) entryComponent).getBPTPageSelector().switchToPage(skip);
	}

	private void refresh(int skip) {
		IndexedContainer sets;
		int limit = skip + 10;
		BPTTagSearchComponent tagSearchComponent = getSidebar().getSearchComponent().getTagSearchComponent();
		String query = getSidebar().getSearchComponent().getFullSearchComponent().getQuery();
		if (loggedIn) {
			if (!moderated) {
				if (getSidebar().getSearchComponent().isOwnEntriesOptionSelected()) {
					sets = containerProvider.getVisibleEntriesSetsByUser(tagSearchComponent.getLanguageTags(), (String)getUser(), tagSearchComponent.getTopicTags(), tagSearchComponent.getModelingLanguagesTags(), tagSearchComponent.getTaskTypesTags(), tagSearchComponent.getOtherTags(), query, ((BPTEntryCards) entryComponent).getSortValue(), skip, limit);
					numberOfEntries = containerProvider.getNumberOfEntriesByUser(tagSearchComponent.getLanguageTags(), (String)getUser(), tagSearchComponent.getTopicTags(), tagSearchComponent.getModelingLanguagesTags(), tagSearchComponent.getTaskTypesTags(), tagSearchComponent.getOtherTags(), query);
				} else {
					ArrayList<BPTExerciseStatus> statusList = new ArrayList<BPTExerciseStatus>();
					statusList.add(BPTExerciseStatus.Published);
					sets = containerProvider.getVisibleEntrieSets(tagSearchComponent.getLanguageTags(), statusList, tagSearchComponent.getTopicTags(), tagSearchComponent.getModelingLanguagesTags(), tagSearchComponent.getTaskTypesTags(), tagSearchComponent.getOtherTags(), query, ((BPTEntryCards) entryComponent).getSortValue(), skip, limit);
					numberOfEntries = containerProvider.getNumberOfEntries(tagSearchComponent.getLanguageTags(), statusList, tagSearchComponent.getTopicTags(), tagSearchComponent.getModelingLanguagesTags(), tagSearchComponent.getTaskTypesTags(), tagSearchComponent.getOtherTags(), query);
				}
			} else {
				ArrayList<BPTExerciseStatus> statusList = getSidebar().getSearchComponent().getSelectedStates();
				sets = containerProvider.getVisibleEntrieSets(tagSearchComponent.getLanguageTags(), statusList, tagSearchComponent.getTopicTags(), tagSearchComponent.getModelingLanguagesTags(), tagSearchComponent.getTaskTypesTags(), tagSearchComponent.getOtherTags(), query, ((BPTEntryCards) entryComponent).getSortValue(), skip, limit);
				numberOfEntries = containerProvider.getNumberOfEntries(tagSearchComponent.getLanguageTags(), statusList, tagSearchComponent.getTopicTags(), tagSearchComponent.getModelingLanguagesTags(), tagSearchComponent.getTaskTypesTags(), tagSearchComponent.getOtherTags(), query);
			}
		} else {
			ArrayList<BPTExerciseStatus> statusList = new ArrayList<BPTExerciseStatus>();
			statusList.add(BPTExerciseStatus.Published);
			sets = containerProvider.getVisibleEntrieSets(tagSearchComponent.getLanguageTags(), statusList, tagSearchComponent.getTopicTags(), tagSearchComponent.getModelingLanguagesTags(), tagSearchComponent.getTaskTypesTags(), tagSearchComponent.getOtherTags(), query, ((BPTEntryCards) entryComponent).getSortValue(), skip, limit);
			numberOfEntries = containerProvider.getNumberOfEntries(tagSearchComponent.getLanguageTags(), statusList, tagSearchComponent.getTopicTags(), tagSearchComponent.getModelingLanguagesTags(), tagSearchComponent.getTaskTypesTags(), tagSearchComponent.getOtherTags(), query);
		}
		entryComponent.show(sets);
	}
	
		public String getSelectedLanguage(){
			ArrayList<String> languageTags = getSidebar().getSearchComponent().getTagSearchComponent().getLanguageTags();
			if(!languageTags.isEmpty()){
				return languageTags.get(0);
			}
			else{
				return "Deutsch";
			}
	}

		public BPTSidebar getSidebar() {
			return sidebar;
		}

		public void setSidebar(BPTSidebar sidebar) {
			this.sidebar = sidebar;
		}
}
