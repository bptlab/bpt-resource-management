package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentType;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTUserRepository;
import de.uni_potsdam.hpi.bpt.resource_management.search.BPTTagSearchComponent;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class BPTApplication extends Application implements HttpServletRequestListener {
	
	// change theme name for different platform
	private final String themeName = "bpt";
//	private final String themeName = "bpmai";
	
	private BPTShowEntryComponent entryComponent;
	private BPTSidebar sidebar;
	private boolean loggedIn, loggingIn, moderated;
	private String name, mailAddress;
	private String applicationURL, openIdProvider;
	private BPTMainFrame mainFrame;
	private BPTUploader uploader;
	private BPTToolRepository toolRepository;
	private BPTUserRepository userRepository;
	private BPTContainerProvider containerProvider;
	private int numberOfEntries;
	private final UriFragmentUtility uriFu = new UriFragmentUtility();

	private BPTShareableEntry entry;
	private BPTAdministrator administrator;

	@Override
	public void init() {
		
		toolRepository = BPTToolRepository.getInstance();
		userRepository = BPTUserRepository.getInstance();
		containerProvider = new BPTContainerProvider(this);
		setProperties();
		
		final Window mainWindow = new Window("Tools for BPM");
		mainWindow.setScrollable(true);
		setMainWindow(mainWindow);
		setTheme(themeName);
		CustomLayout custom = new CustomLayout(themeName + "_mainlayout");
		custom.setHeight("100%");
		VerticalLayout layout =  new VerticalLayout();
		layout.setWidth("732px");
	
		entryComponent = new BPTSmallRandomEntries(this);
//		entryComponent = new BPTEntryCards(this);
		mainFrame = new BPTMainFrame(entryComponent);
		setSidebar(new BPTSidebar(this));
		layout.addComponent(getSidebar());
		layout.addComponent(mainFrame);
		custom.addComponent(layout, "application");
		custom.addStyleName("scroll");
		mainWindow.setContent(custom);
//		mainWindow.addComponent(uriFu);
		custom.addComponent(uriFu, "uriFragmentUtility");
		addListenerToUriFragmentUtility();
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
		if (entryComponent instanceof BPTEntryCards) {
			refreshAndClean();
		}
	}
	
	public void showAllAndRefreshSidebar() {
		getSidebar().showAll();
		showAll();
	}
	
	public void showAll() {
//		if (entryComponent instanceof BPTSmallRandomEntries) {
			entryComponent = new BPTEntryCards(this);
			mainFrame.add(entryComponent);
//		}
	}
	
	public void showStartPage() {
		entryComponent = new BPTSmallRandomEntries(this);
		mainFrame.add(entryComponent);
	}
	
	public void showSpecificEntry(String entryId) {
		Map<String, Object> tool = toolRepository.get(entryId);
		StringBuffer sbUriFragment = new StringBuffer();
		sbUriFragment.append(entryId + "-");
		String nameOfTool = (String) tool.get("name");
		String formattedNameOfTool = nameOfTool.replaceAll("[^\\w]", "-").toLowerCase();
		String fragmentForEntry = "!" + entryId + "-" + formattedNameOfTool;
		String applicationString = applicationURL;
		if (applicationURL.charAt(applicationURL.length() - 1) == '/') {
			applicationString = applicationString.substring(0, applicationURL.length() - 1);
		}
		IndexedContainer container = containerProvider.generateContainer(new ArrayList<Map>(Arrays.asList(tool)), BPTDocumentType.BPT_RESOURCES_TOOLS);
		Item item = container.getItem(container.getItemIds().iterator().next());
		uriFu.setFragment(fragmentForEntry, false);
//		entry = new BPTShareableEntry(item, this);
//		mainFrame.add(entry);
		entryComponent = new BPTShareableEntryContainer(this, entryId);
		mainFrame.add(entryComponent);
		getSidebar().showSpecificEntry(applicationURL + "#" + fragmentForEntry);
		getMainWindow().executeJavaScript("('html,body').scrollTop(0);");
	}
	
	private void addListenerToUriFragmentUtility() {
		uriFu.addListener(new FragmentChangedListener() {
            public void fragmentChanged(FragmentChangedEvent source) {
                String fragment = source.getUriFragmentUtility().getFragment();
                if (fragment != null) {
                    if (fragment.startsWith("!")) {
                    	try {
                    		fragment = fragment.substring(1);
	                        int separatorIndex = fragment.indexOf("-");
	                        String entryId = fragment.substring(0, separatorIndex);
	                        String formattedNameOfTool = fragment.substring(separatorIndex + 1, fragment.length());
	                        String nameOfTool = (String) toolRepository.get(entryId).get("name");
	                		if (formattedNameOfTool.equals(nameOfTool.replaceAll("[^\\w]", "-").toLowerCase())) {
	                			showSpecificEntry(entryId);
	                		}
                    	} catch (IndexOutOfBoundsException e) {
//                    		e.printStackTrace();
                    	} catch (NullPointerException e) {
//                    		e.printStackTrace();
                    	}
                        

                    }
                }
            }
        });
	}
	
	public BPTToolRepository getToolRepository() {
		return toolRepository;
	}
	
	public BPTUserRepository getUserRepository() {
		return userRepository;
	}
	
	public BPTContainerProvider getContainerProvider() {
		return containerProvider;
	}

	public UriFragmentUtility getUriFragmentUtility() {
		return uriFu;
	}

	public BPTShowEntryComponent getTable(){
		return entryComponent;
	}

	public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
		if (loggingIn) {
			System.out.println("----- LOGIN STARTED -----");
			Map<String, String[]> map = request.getParameterMap();
			System.out.println("The parameter map: ");
			for (String key: map.keySet()) {
				StringBuffer sb = new StringBuffer();
				sb.append("\t" + key + ": ");
				String[] array = map.get(key);
				for (int i = 0; i < array.length; i++) {
					sb.append(array[i]);
					if (i < array.length - 1) {
						sb.append(", ");
					} else {
						sb.append(";");
					}
				}
				System.out.println(sb.toString());
			}
			if (map.containsKey("openid.identity")) {
				loggingIn = false;
				// TODO: check nonce for security reasons
//				checkNonce(request.getParameter("openid.response_nonce"));
				setUser(map.get("openid.identity")[0]);
				System.out.println("The OpenID identifier: " + (String)getUser());
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
				System.out.println("The name: " + name);
				System.out.println("The mail address: " + mailAddress);
				moderated = userRepository.isModerator((String)getUser(), name, mailAddress);
				loggedIn = true;
				System.out.println("----- LOGIN FINISHED -----");
				getSidebar().login(name, moderated);
				renderEntries();
				try {
					response.sendRedirect(getLogoutURL());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("----- LOGIN FINISHED -----");
				return;
			}
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
		IndexedContainer dataSource;
		int limit = skip + 10;
		BPTTagSearchComponent tagSearchComponent = getSidebar().getSearchComponent().getTagSearchComponent();
		String query = getSidebar().getSearchComponent().getFullSearchComponent().getQuery();
		if (!tagSearchComponent.isNoTagSelected() || (query != null && !query.isEmpty())) {
			showAll();
		}
		if (loggedIn) {
			if (!moderated) {
				if (getSidebar().getSearchComponent().isOwnEntriesOptionSelected()) {
					showAll();
					dataSource = containerProvider.getVisibleEntriesByUser((String)getUser(), tagSearchComponent.getAvailabiltyTags(), tagSearchComponent.getModelTypeTags(), tagSearchComponent.getPlatformsTags(), tagSearchComponent.getSupportedFunctionalityTags(), query, ((BPTEntryCards) entryComponent).getSortValue(), skip, limit);
					numberOfEntries = containerProvider.getNumberOfEntriesByUser((String)getUser(), tagSearchComponent.getAvailabiltyTags(), tagSearchComponent.getModelTypeTags(), tagSearchComponent.getPlatformsTags(), tagSearchComponent.getSupportedFunctionalityTags(), query);
				} else {
					ArrayList<BPTToolStatus> statusList = new ArrayList<BPTToolStatus>();
					statusList.add(BPTToolStatus.Published);
					dataSource = containerProvider.getVisibleEntries(statusList, tagSearchComponent.getAvailabiltyTags(), tagSearchComponent.getModelTypeTags(), tagSearchComponent.getPlatformsTags(), tagSearchComponent.getSupportedFunctionalityTags(), query, ((BPTEntryCards) entryComponent).getSortValue(), skip, limit);
					numberOfEntries = containerProvider.getNumberOfEntries(statusList, tagSearchComponent.getAvailabiltyTags(), tagSearchComponent.getModelTypeTags(), tagSearchComponent.getPlatformsTags(), tagSearchComponent.getSupportedFunctionalityTags(), query);
				}
			} else {
				ArrayList<BPTToolStatus> statusList = getSidebar().getSearchComponent().getSelectedStates();
				if (statusList.size() != 1 || !statusList.contains(BPTToolStatus.Published)) {
					showAll();
				}
				dataSource = containerProvider.getVisibleEntries(statusList, tagSearchComponent.getAvailabiltyTags(), tagSearchComponent.getModelTypeTags(), tagSearchComponent.getPlatformsTags(), tagSearchComponent.getSupportedFunctionalityTags(), query, ((BPTEntryCards) entryComponent).getSortValue(), skip, limit);
				numberOfEntries = containerProvider.getNumberOfEntries(statusList, tagSearchComponent.getAvailabiltyTags(), tagSearchComponent.getModelTypeTags(), tagSearchComponent.getPlatformsTags(), tagSearchComponent.getSupportedFunctionalityTags(), query);
			}
		} else {
			ArrayList<BPTToolStatus> statusList = new ArrayList<BPTToolStatus>();
			statusList.add(BPTToolStatus.Published);
			dataSource = containerProvider.getVisibleEntries(statusList, tagSearchComponent.getAvailabiltyTags(), tagSearchComponent.getModelTypeTags(), tagSearchComponent.getPlatformsTags(), tagSearchComponent.getSupportedFunctionalityTags(), query, ((BPTEntryCards) entryComponent).getSortValue(), skip, limit);
			numberOfEntries = containerProvider.getNumberOfEntries(statusList, tagSearchComponent.getAvailabiltyTags(), tagSearchComponent.getModelTypeTags(), tagSearchComponent.getPlatformsTags(), tagSearchComponent.getSupportedFunctionalityTags(), query);
		}
		entryComponent.show(dataSource);
	}

	public BPTSidebar getSidebar() {
		return sidebar;
	}

	private void setSidebar(BPTSidebar sidebar) {
		this.sidebar = sidebar;
	}
}
