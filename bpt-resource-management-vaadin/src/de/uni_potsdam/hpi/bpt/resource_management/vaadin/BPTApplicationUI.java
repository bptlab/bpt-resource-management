package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.tools.ant.taskdefs.Java;
import org.json.JSONArray;
import org.json.JSONException;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.Page;
import com.vaadin.server.ClientConnector.AttachEvent;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTUserRepository;
import de.uni_potsdam.hpi.bpt.resource_management.search.BPTTagSearchComponent;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.utils.PageRefreshListener;
//import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
//import com.vaadin.ui.UriFragmentUtility;
//import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
//import com.vaadin.ui.UriFragmentUtility.FragmentChangedListener;

@SuppressWarnings({ "unchecked", "serial" })
@Title("Tools for BPM")
@Theme("bpt")
@Push
//@Theme("bpmai") change theme name for different platform
@PreserveOnRefresh // keeps state like in Vaadin 6
public class BPTApplicationUI extends UI implements PageRefreshListener {
	
	private BPTShowEntryComponent entryComponent;
	private BPTSidebar sidebar;
	private boolean loggedIn, loggingIn, moderated;
	private String user, name, mailAddress;
	private String applicationURL, openIdProvider;
	private BPTMainFrame mainFrame;
	private BPTUploader uploader;
	private BPTToolRepository toolRepository;
	private BPTUserRepository userRepository;
	private BPTContainerProvider containerProvider;
	private int numberOfEntries;

//	private final UriFragmentUtility uriFu = new UriFragmentUtility();

//	private BPTShareableEntry entry;
	private BPTAdministrator administrator;

	@Override
	public void init(VaadinRequest request) {

		setProperties();
		
		addJavaScriptFunctions();
		toolRepository = BPTToolRepository.getInstance();
		userRepository = BPTUserRepository.getInstance();
		containerProvider = new BPTContainerProvider(this);
		
		CustomLayout custom = new CustomLayout("bptMainLayout");
		custom.setHeight("100%");
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth("732px");
	
		setSidebar(new BPTSidebar(this));
		layout.addComponent(getSidebar());

		entryComponent = new BPTSmallRandomEntries(this);
//		entryComponent = new BPTEntryCards(this);
		mainFrame = new BPTMainFrame(entryComponent);
		layout.addComponent(mainFrame);
		custom.addComponent(layout, "application");
		setContent(custom);
//		mainWindow.addComponent(uriFu);
//		custom.addComponent(uriFu, "uriFragmentUtility");
		addUriListener();
		enter(getPage().getUriFragment());
	}
	
	private void addJavaScriptFunctions() {
		JavaScript.getCurrent().addFunction("de.hpi.showAll", 
				new JavaScriptFunction() {
					
					@Override
					public void call(final JSONArray arguments) throws JSONException {
//						Notification.show("Received call");
						String message = arguments.getString(0);
						if(message.equals("Others")){
							showAllAndRefreshSidebar(true);
						}
						else{
							selectTag(message);
						}
					}
				});
	}

	protected void selectTag(String message) {
		getSidebar().getSearchComponent().getTagSearchComponent().selectTag(message);
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
//		setLogoutURL(applicationURL);
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
		mainFrame.add(entryComponent);
	}
	
	public void showAllAndRefreshSidebar(boolean loadEntries) {
		addStyleName("waitCursor");
		JavaScript.getCurrent().execute("document.getElementById('barchart').firstChild.firstChild.contentWindow.showWaitCursor()");
		JavaScript.getCurrent().execute("document.getElementById('piechart').firstChild.firstChild.contentWindow.showWaitCursor()");
		JavaScript.getCurrent().execute("document.getElementById('tagcloud').firstChild.firstChild.contentWindow.showWaitCursor()");
		push();
		if(entryComponent instanceof BPTShareableEntryContainer){
			getSidebar().showAll();			
		}
		showAll(loadEntries);
		getPage().setUriFragment("!showAll", false);
		JavaScript.getCurrent().execute("window.scrollTo(0, 0);");
		removeStyleName("waitCursor");
	}
	
	public void showAll(boolean loadEntries) {
//		if (!(entryComponent instanceof BPTEntryCards)) {
			entryComponent = new BPTEntryCards(this, loadEntries);
			mainFrame.add(entryComponent);
//		}
	}
	
	public void showStartPage() {
		entryComponent = new BPTSmallRandomEntries(this);
		mainFrame.add(entryComponent);
		getSidebar().showAll();
		getPage().setUriFragment("", false);
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
//		IndexedContainer container = containerProvider.generateContainer(new ArrayList<Map>(Arrays.asList(tool)), BPTDocumentType.BPT_RESOURCES_TOOLS);
//		Item item = container.getItem(container.getItemIds().iterator().next());
		getPage().setUriFragment(fragmentForEntry, false);
//		entry = new BPTShareableEntry(item, this);
//		mainFrame.add(entry);
		entryComponent = new BPTShareableEntryContainer(this, entryId);
		mainFrame.add(entryComponent);
		getSidebar().showSpecificEntry(applicationURL + "#" + fragmentForEntry);
		JavaScript.getCurrent().execute("('html,body').scrollTop(0);");
	}
	
	private void addUriListener() {
		getPage().addUriFragmentChangedListener(new UriFragmentChangedListener(){
			public void uriFragmentChanged(
	                   UriFragmentChangedEvent source) {
	               enter(source.getUriFragment());
	            }
		});
		
	}
	
	protected void enter(String uriFragment) {
		if (uriFragment != null) {
			if(uriFragment.equals("")){
				showStartPage();
			}
			else if(uriFragment.equals("!showAll")){
				showAllAndRefreshSidebar(true);
			}
			else if (uriFragment.startsWith("!")) {
            	try {
            		uriFragment = uriFragment.substring(1);
                    int separatorIndex = uriFragment.indexOf("-");
                    String entryId = uriFragment.substring(0, separatorIndex);
                    String formattedNameOfTool = uriFragment.substring(separatorIndex + 1, uriFragment.length());
                    String nameOfTool = (String) toolRepository.get(entryId).get("name");
            		if (formattedNameOfTool.equals(nameOfTool.replaceAll("[^\\w]", "-").toLowerCase())) {
            			showSpecificEntry(entryId);
            		}
            	} catch (IndexOutOfBoundsException e) {
//            		e.printStackTrace();
            	} catch (NullPointerException e) {
//            		e.printStackTrace();
            	}
            }
        } else {
//        	XXX
//        	if (entryComponent instanceof BPTShareableEntryContainer) {
//        		showStartPage();
//        	}
        }
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

	public BPTShowEntryComponent getTable() {
		return entryComponent;
	}

	public void login(Map <String, String[]> map) {
		System.out.println("----- LOGIN STARTED -----");
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
		// TODO: check nonce for security reasons
//			checkNonce(request.getParameter("openid.response_nonce"));
		user = map.get("openid.identity")[0];
		System.out.println("The OpenID identifier: " + user);
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
		moderated = userRepository.isModerator(user, name, mailAddress);
		loggedIn = true;
		System.out.println("----- LOGIN FINISHED -----");
		getSidebar().login(name, moderated);
//		renderEntries();
//			try {
//				response.sendRedirect(getLogoutURL());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		} else {
//			System.out.println("----- LOGIN FINISHED -----");
//			return;
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

	public void edit(Item item) {
		uploader = new BPTUploader(item, this);
		mainFrame.add(uploader);
		getSidebar().renderUploader();
	}
	
	public void refreshAndClean() {
		if(entryComponent instanceof BPTSmallRandomEntries){
			showAllAndRefreshSidebar(false);
		}
		refreshAndClean(0);
		((BPTEntryCards) entryComponent).showNumberOfEntries(numberOfEntries);
	}
	
	public void refreshAndClean(int skip) {
		refresh(skip);
		Runtime.getRuntime().gc();
		if(numberOfEntries != 0){
			((BPTEntryCards) entryComponent).switchToPage(skip);
		}
		JavaScript.getCurrent().execute("window.scrollTo(0, 0);");
	}

	private void refresh(int skip) {
		IndexedContainer dataSource;
		int limit = skip + 10;
		BPTTagSearchComponent tagSearchComponent = getSidebar().getSearchComponent().getTagSearchComponent();
		String query = getSidebar().getSearchComponent().getFullSearchComponent().getQuery();
		if (!tagSearchComponent.isNoTagSelected() || (query != null && !query.isEmpty())) {
			showAll(true);
		}
		if (loggedIn) {
			if (!moderated) {
				if (getSidebar().getSearchComponent().isOwnEntriesOptionSelected()) {
					showAll(true);
					dataSource = containerProvider.getVisibleEntriesByUser(user, tagSearchComponent.getAvailabiltyTags(), tagSearchComponent.getModelTypeTags(), tagSearchComponent.getPlatformsTags(), tagSearchComponent.getSupportedFunctionalityTags(), query, ((BPTEntryCards) entryComponent).getSortValue(), skip, limit);
					numberOfEntries = containerProvider.getNumberOfEntriesByUser(user, tagSearchComponent.getAvailabiltyTags(), tagSearchComponent.getModelTypeTags(), tagSearchComponent.getPlatformsTags(), tagSearchComponent.getSupportedFunctionalityTags(), query);
				} else {
					ArrayList<BPTToolStatus> statusList = new ArrayList<BPTToolStatus>();
					statusList.add(BPTToolStatus.Published);
					dataSource = containerProvider.getVisibleEntries(statusList, tagSearchComponent.getAvailabiltyTags(), tagSearchComponent.getModelTypeTags(), tagSearchComponent.getPlatformsTags(), tagSearchComponent.getSupportedFunctionalityTags(), query, ((BPTEntryCards) entryComponent).getSortValue(), skip, limit);
					numberOfEntries = containerProvider.getNumberOfEntries(statusList, tagSearchComponent.getAvailabiltyTags(), tagSearchComponent.getModelTypeTags(), tagSearchComponent.getPlatformsTags(), tagSearchComponent.getSupportedFunctionalityTags(), query);
				}
			} else {
				ArrayList<BPTToolStatus> statusList = getSidebar().getSearchComponent().getSelectedStates();
				if (statusList.size() != 1 || !statusList.contains(BPTToolStatus.Published)) {
					showAll(true);
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
		UI.setCurrent(this);
		
		if(entryComponent instanceof BPTSmallRandomEntries){
			((BPTSmallRandomEntries) entryComponent).showNewEntries();
		}
		
		else if(entryComponent instanceof BPTShareableEntryContainer){
			((BPTShareableEntryContainer) entryComponent).showButtons();
		}
	}
}
