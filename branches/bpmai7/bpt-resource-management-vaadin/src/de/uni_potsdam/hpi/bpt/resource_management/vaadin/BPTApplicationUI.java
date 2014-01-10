package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseSetRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseStatus;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTUserRepository;
import de.uni_potsdam.hpi.bpt.resource_management.search.BPTTagSearchComponent;
import de.uni_potsdam.hpi.bpt.resource_management.upload.BPTMultiUploader;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.utils.PageRefreshListener;

@SuppressWarnings({ "serial" })
@Title("BPM Academic Initiative")
//@Theme("bpt") change theme name for different platform
@Theme("bpmai") 
@PreserveOnRefresh // keeps state like in Vaadin 6
public class BPTApplicationUI extends UI implements PageRefreshListener {
	
	private BPTShowEntryComponent entryComponent;
	private BPTSidebar sidebar;
	private boolean loggedIn, loggingIn, moderated;
	private String user, name, mailAddress;
	private String applicationURL, openIdProvider;
	private BPTMainFrame mainFrame;
	private BPTUploader uploader;
	private BPTMultiUploader multiUploader;
	private BPTExerciseSetRepository exerciseSetRepository;
	private BPTExerciseRepository exerciseRepository;
	private BPTUserRepository userRepository;
	private BPTContainerProvider containerProvider;
	private int numberOfEntries;
	private Button administrationButton;
	private BPTAdministrator administrator;
	
	@Override
	public void init(VaadinRequest request) {
		
		setProperties();
		
		exerciseRepository = BPTExerciseRepository.getInstance();
		exerciseSetRepository = BPTExerciseSetRepository.getInstance();
		userRepository = BPTUserRepository.getInstance();
		containerProvider = new BPTContainerProvider(this);
		
		final CustomLayout custom = new CustomLayout("bpmai_mainlayout");
		custom.setHeight("100%");
		VerticalLayout layout =  new VerticalLayout();
		layout.setWidth("732px");
		
		setSidebar(new BPTSidebar(this));
		layout.addComponent(getSidebar());
		
		entryComponent = new BPTEntryCards(this);
		mainFrame = new BPTMainFrame(entryComponent);
		layout.addComponent(mainFrame);
		custom.addComponent(layout, "application");
		setContent(custom);
		
		addAdministrationButton(custom);
		
		addUriListener();
		enter(getPage().getUriFragment());
//		renderMultiUploader();
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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
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
		administrator = new BPTAdministrator();
		mainFrame.add(administrator);
		getSidebar().renderAdministrator();
	}
	
	public void renderEntries() {
		getSidebar().renderEntries();
		refreshAndClean();
		mainFrame.add(entryComponent);
	}
	
	public void showAllAndRefreshSidebar() {
		getSidebar().renderEntries();
		showAll();
	}
	
	public void showAll() {
		if (!(entryComponent instanceof BPTEntryCards)) {
			entryComponent = new BPTEntryCards(this);
			mainFrame.add(entryComponent);
		}
	}
	
	private void addAdministrationButton(final CustomLayout custom) {
		administrationButton = new Button("Administration");
		administrationButton.setStyleName(BaseTheme.BUTTON_LINK);
		administrationButton.addStyleName("redButton");
//        administrationButton.addStyleName("greyButton");
        custom.addComponent(administrationButton, "loginLink");
		
        administrationButton.addClickListener(new Button.ClickListener(){
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

				final Label failureLabel = new Label("<font color=\"#FF0000\">Invalid password.</font>", ContentMode.HTML);
				failureLabel.setVisible(false);
				administrationLayout.addComponent(failureLabel, "failureLabel");
				final PasswordField passwordInput = new PasswordField();
				passwordInput.setInputPrompt("Password");
				passwordInput.setWidth("230px");
				administrationLayout.addComponent(passwordInput, "passwordInput");
				Button loginButton = new Button("Login");
				loginButton.setClickShortcut(KeyCode.ENTER);
				loginButton.setWidth("70px");
				loginButton.addClickListener(new Button.ClickListener(){
						public void buttonClick(ClickEvent event) {
							if (((String) passwordInput.getValue()).equals("petrinet")) {
								failureLabel.setVisible(false);
								sidebar.getLoginComponent().addComponentsForLogin();
								removeWindow(administrationWindow);
								custom.removeComponent(administrationButton);
							} else {
								failureLabel.setVisible(true);
							}
						}
					});
				administrationLayout.addComponent(loginButton, "loginButton");
				addWindow(administrationWindow);
			}
		});
	}
	
	private void addUriListener() {
		getPage().addUriFragmentChangedListener(new UriFragmentChangedListener(){
			public void uriFragmentChanged(UriFragmentChangedEvent source) {
	               enter(source.getUriFragment());
	            }
		});
	}
	
	protected void enter(String uriFragment) {
//		if (uriFragment != null) {
//            if (uriFragment.startsWith("!")) {
//            	try {
//            		uriFragment = uriFragment.substring(1);
//                    int separatorIndex = uriFragment.indexOf("-");
//                    String entryId = uriFragment.substring(0, separatorIndex);
//                    String formattedNameOfTool = uriFragment.substring(separatorIndex + 1, uriFragment.length());
//                    String nameOfTool = (String) toolRepository.get(entryId).get("name");
//            		if (formattedNameOfTool.equals(nameOfTool.replaceAll("[^\\w]", "-").toLowerCase())) {
//            			showSpecificEntry(entryId);
//            		}
//            	} catch (IndexOutOfBoundsException e) {
////            		e.printStackTrace();
//            	} catch (NullPointerException e) {
////            		e.printStackTrace();
//            	}
//            }
//        } else {
//        	if (entryComponent instanceof BPTShareableEntryContainer) {
//        		showStartPage();
//        	}
//        }
		// TODO: implement shareable entry page
		showAll();
	}
	
	// TODO: implement shareable entry page
	public void showSpecificEntry(String entryId) {
//		Map<String, Object> tool = toolRepository.get(entryId);
//		StringBuffer sbUriFragment = new StringBuffer();
//		sbUriFragment.append(entryId + "-");
//		String nameOfTool = (String) tool.get("name");
//		String formattedNameOfTool = nameOfTool.replaceAll("[^\\w]", "-").toLowerCase();
//		String fragmentForEntry = "!" + entryId + "-" + formattedNameOfTool;
//		String applicationString = applicationURL;
//		if (applicationURL.charAt(applicationURL.length() - 1) == '/') {
//			applicationString = applicationString.substring(0, applicationURL.length() - 1);
//		}
////		IndexedContainer container = containerProvider.generateContainer(new ArrayList<Map>(Arrays.asList(tool)), BPTDocumentType.BPT_RESOURCES_TOOLS);
////		Item item = container.getItem(container.getItemIds().iterator().next());
//		getPage().setUriFragment(fragmentForEntry, false);
////		entry = new BPTShareableEntry(item, this);
////		mainFrame.add(entry);
//		entryComponent = new BPTShareableEntryContainer(this, entryId);
//		mainFrame.add(entryComponent);
//		getSidebar().showSpecificEntry(applicationURL + "#" + fragmentForEntry);
//		JavaScript.getCurrent().execute("('html,body').scrollTop(0);");
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
	
	public void login(Map <String, String[]> map) {
		if (map.containsKey("openid.identity")) {
//			System.out.println("----- LOGIN STARTED -----");
			loggingIn = false;
			// TODO: check nonce for security reasons
//			checkNonce(request.getParameter("openid.response_nonce"));
			setUser(map.get("openid.identity")[0]);
//			System.out.println("The OpenID identifier: " + (String)getUser());
			if (openIdProvider.equals("Google")) {
				mailAddress = map.get("openid.ext1.value.email")[0];
				if (map.containsKey("openid.ext1.value.firstname") && map.containsKey("openid.ext1.value.lastname")) {
					name = map.get("openid.ext1.value.firstname")[0] + " " + map.get("openid.ext1.value.lastname")[0];
				} else {
					name = mailAddress;
				}
			} else { // openIdProvider.equals("Yahoo")
				mailAddress = map.get("openid.ax.value.email")[0];
				if (map.containsKey("openid.ax.value.fullname")) {
					name = map.get("openid.ax.value.fullname")[0]; 
				} else {
					name = mailAddress;
				}
			}
//			System.out.println("The name: " + name);
//			System.out.println("The mail address: " + mailAddress);
			moderated = userRepository.isModerator((String)getUser(), name, mailAddress);
			loggedIn = true;
//			System.out.println("----- LOGIN FINISHED -----");
			getPage().open(applicationURL, "_self");
			getSidebar().login(name, moderated);
//			renderEntries();
		}
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

	public void edit(Item item) {
		uploader = new BPTUploader(item, this);
		mainFrame.add(uploader);
		getSidebar().renderUploader();
	}
	
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
		if (!tagSearchComponent.isNoTagSelected() || (query != null && !query.isEmpty())) {
			showAll();
		}
		if (loggedIn) {
			if (!moderated) {
				if (getSidebar().getSearchComponent().isOwnEntriesOptionSelected()) {
					sets = containerProvider.getVisibleEntriesSetsByUser(tagSearchComponent.getLanguageTags(), user, tagSearchComponent.getTopicTags(), tagSearchComponent.getModelingLanguagesTags(), tagSearchComponent.getTaskTypesTags(), tagSearchComponent.getOtherTags(), query, ((BPTEntryCards) entryComponent).getSortValue(), skip, limit);
					numberOfEntries = containerProvider.getNumberOfEntriesByUser(tagSearchComponent.getLanguageTags(), user, tagSearchComponent.getTopicTags(), tagSearchComponent.getModelingLanguagesTags(), tagSearchComponent.getTaskTypesTags(), tagSearchComponent.getOtherTags(), query);
				} else {
					ArrayList<BPTExerciseStatus> statusList = new ArrayList<BPTExerciseStatus>();
					statusList.add(BPTExerciseStatus.Published);
					sets = containerProvider.getVisibleEntrySets(tagSearchComponent.getLanguageTags(), statusList, tagSearchComponent.getTopicTags(), tagSearchComponent.getModelingLanguagesTags(), tagSearchComponent.getTaskTypesTags(), tagSearchComponent.getOtherTags(), query, ((BPTEntryCards) entryComponent).getSortValue(), skip, limit);
					numberOfEntries = containerProvider.getNumberOfEntries(tagSearchComponent.getLanguageTags(), statusList, tagSearchComponent.getTopicTags(), tagSearchComponent.getModelingLanguagesTags(), tagSearchComponent.getTaskTypesTags(), tagSearchComponent.getOtherTags(), query);
				}
			} else {
				ArrayList<BPTExerciseStatus> statusList = getSidebar().getSearchComponent().getSelectedStates();
				sets = containerProvider.getVisibleEntrySets(tagSearchComponent.getLanguageTags(), statusList, tagSearchComponent.getTopicTags(), tagSearchComponent.getModelingLanguagesTags(), tagSearchComponent.getTaskTypesTags(), tagSearchComponent.getOtherTags(), query, ((BPTEntryCards) entryComponent).getSortValue(), skip, limit);
				numberOfEntries = containerProvider.getNumberOfEntries(tagSearchComponent.getLanguageTags(), statusList, tagSearchComponent.getTopicTags(), tagSearchComponent.getModelingLanguagesTags(), tagSearchComponent.getTaskTypesTags(), tagSearchComponent.getOtherTags(), query);
			}
		} else {
			ArrayList<BPTExerciseStatus> statusList = new ArrayList<BPTExerciseStatus>();
			statusList.add(BPTExerciseStatus.Published);
			sets = containerProvider.getVisibleEntrySets(tagSearchComponent.getLanguageTags(), statusList, tagSearchComponent.getTopicTags(), tagSearchComponent.getModelingLanguagesTags(), tagSearchComponent.getTaskTypesTags(), tagSearchComponent.getOtherTags(), query, ((BPTEntryCards) entryComponent).getSortValue(), skip, limit);
			numberOfEntries = containerProvider.getNumberOfEntries(tagSearchComponent.getLanguageTags(), statusList, tagSearchComponent.getTopicTags(), tagSearchComponent.getModelingLanguagesTags(), tagSearchComponent.getTaskTypesTags(), tagSearchComponent.getOtherTags(), query);
		}
		entryComponent.show(sets);
	}
	
	public String getSelectedLanguage() {
		ArrayList<String> languageTags = getSidebar().getSearchComponent().getTagSearchComponent().getLanguageTags();
		if (!languageTags.isEmpty()) {
			return languageTags.get(0);
		} else {
			return "Deutsch";
		}
	}

	public BPTSidebar getSidebar() {
		return sidebar;
	}

	public void setSidebar(BPTSidebar sidebar) {
		this.sidebar = sidebar;
	}
	
	@Override
	public void pageRefreshed(VaadinRequest request) {
		if (loggingIn) {
			Map<String, String[]> map = request.getParameterMap();
//			System.out.println("The parameter map: ");
//			for (String key: map.keySet()) {
//				StringBuffer sb = new StringBuffer();
//				sb.append("\t" + key + ": ");
//				String[] array = map.get(key);
//				for (int i = 0; i < array.length; i++) {
//					sb.append(array[i]);
//					if (i < array.length - 1) {
//						sb.append(", ");
//					} else {
//						sb.append(";");
//					}
//				}
//				System.out.println(sb.toString());
//			}
			login(map);
		}
	}
}
