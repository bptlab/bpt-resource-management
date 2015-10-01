package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONException;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
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

@SuppressWarnings({ "unchecked", "serial" })
@Title("Tools for BPM")
@Theme("bpt")
@Push(transport = Transport.STREAMING)
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
		addUriListener();
		enter(getPage().getUriFragment());
	}
	
	private void addJavaScriptFunctions() {
		JavaScript.getCurrent().addFunction("de.hpi.showAll", 
				new JavaScriptFunction() {
					
					@Override
					public void call(final JSONArray arguments) throws JSONException {
						String message = arguments.getString(0);
						if(message.equals("Others")){
							showAllAndRefreshSidebar(true);
						}
						else{
							selectTag(message);
						}
					}
				});
		JavaScript.getCurrent().addFunction("de.hpi.logon", 
				new JavaScriptFunction() {
					
					@Override
					public void call(final JSONArray arguments) throws JSONException {
						loginWithGoogle(arguments.getString(0), arguments.getString(1), arguments.getString(2));
					}
				});

		JavaScript.getCurrent().addFunction("de.hpi.logout", 
				new JavaScriptFunction() {
					
					@Override
					public void call(final JSONArray arguments) throws JSONException {
						logout();
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
		getPage().setUriFragment("!uploader", false);
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
		getSidebar().showAll();
		showAll(loadEntries);
		getPage().setUriFragment("!showAll", false);
		JavaScript.getCurrent().execute("window.scrollTo(0, 0);");
		removeStyleName("waitCursor");
	}
	
	public void showAll(boolean loadEntries) {
//		if (!(entryComponent instanceof BPTEntryCards)) {
			entryComponent = new BPTEntryCards(this, loadEntries);
			mainFrame.add(entryComponent);
			getSidebar().showAll();
			getPage().setUriFragment("#!showAll", false);
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
		getPage().setUriFragment(fragmentForEntry, false);
		entryComponent = new BPTShareableEntryContainer(this, entryId);
		mainFrame.add(entryComponent);
		getSidebar().showSpecificEntry(applicationURL + "#" + fragmentForEntry);
		JavaScript.getCurrent().execute("('html,body').scrollTop(0);");
	}
	
	private void addUriListener() {
		getPage().addUriFragmentChangedListener(new UriFragmentChangedListener(){
			public void uriFragmentChanged(UriFragmentChangedEvent source) {
				enter(source.getUriFragment());
			}
		});
	}
	
	protected void enter(String uriFragment) {
		if (uriFragment != null) {
			if (uriFragment.equals("")) {
				showStartPage();
			} else if(uriFragment.equals("!showAll")) {
				showAllAndRefreshSidebar(true);
			} else if(uriFragment.equals("!uploader")) {
				renderUploader();
			} else if (uriFragment.startsWith("!")) {
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

	public void loginWithGoogle(String id, String email, String name) {
		
		openIdProvider = "Google";
		System.out.println("The Google identifier: " + id);
		System.out.println("The name: " + name);
		System.out.println("The mail address: " + email);
		moderated = userRepository.isModerator(id, name, email);
		this.user = id;
		this.name = name;
		this.mailAddress = email;
		loggedIn = true;
//		getPage().open(applicationURL, "_self");
		sidebar.login(moderated);
		enter(getPage().getUriFragment());
	}

	public void loginWithYahoo(Map<String, String[]> map) {
		openIdProvider = "Yahoo";
		user = map.get("openid.identity")[0];
			mailAddress = map.get("openid.ax.value.email")[0];
			if (map.containsKey("openid.ax.value.fullname")) {
				name = map.get("openid.ax.value.fullname")[0]; 
			} else {
				name = mailAddress;
			}
		System.out.println("The OpenID identifier: " + user);
		System.out.println("The name: " + name);
		System.out.println("The mail address: " + mailAddress);
		moderated = userRepository.isModerator(user, name, mailAddress);
		loggedIn = true;
//		getPage().open(applicationURL, "_self");
		sidebar.login(moderated);
		enter(getPage().getUriFragment());
	}

	public void edit(Item item) {
		uploader = new BPTUploader(item, this);
		mainFrame.add(uploader);
		getSidebar().renderUploader();
		getPage().setUriFragment("!uploader", false);
	}
	
	public void refreshAndClean() {
		if (entryComponent instanceof BPTSmallRandomEntries) {
//			showAllAndRefreshSidebar(false);
			showAll(false);
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
//		int limit = skip + 10;
		// the limit is 10 entries per page
		int limit = 10;
		BPTTagSearchComponent tagSearchComponent = getSidebar().getSearchComponent().getTagSearchComponent();
		String query = getSidebar().getSearchComponent().getFullSearchComponent().getQuery();
//		if (!tagSearchComponent.isNoTagSelected() || (query != null && !query.isEmpty())) {
//			showAll(true);
//		}
		if (loggedIn) {
			if (!moderated) {
				if (getSidebar().getSearchComponent().isOwnEntriesOptionSelected()) {
//					showAll(true);
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
//				if (statusList.size() != 1 || !statusList.contains(BPTToolStatus.Published)) {
//					showAll(true);
//				}
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
		boolean wasLoggingIn = false;
		if (loggingIn) {
			Map<String, String[]> map = request.getParameterMap();
			if (map.get("openid.identity") != null) {
				loginWithYahoo(map);
				wasLoggingIn = true;
			} else {
				loggingIn = false;
			}
		}
		UI.setCurrent(this);
		
		if(entryComponent instanceof BPTSmallRandomEntries) {
			((BPTSmallRandomEntries) entryComponent).showNewEntries();
		}
		
		else if(entryComponent instanceof BPTShareableEntryContainer) {
			((BPTShareableEntryContainer) entryComponent).showButtons();
		}
		if (wasLoggingIn) {
			if (getPage().getUriFragment() != null) {
				this.getPage().open(applicationURL + "#" + getPage().getUriFragment(), "_self");
				System.out.println("Going to " + applicationURL + "#" + getPage().getUriFragment());
			} else {
				this.getPage().open(applicationURL + "#", "_self");
				System.out.println("Going to " + applicationURL + "#");
			}
		}
	}

	public void logout() {
		setName("");
		setMailAddress("");
		setLoggedIn(false);
		setModerated(false);
		setOpenIdProvider(null);
		renderEntries();
		
		if (entryComponent instanceof BPTShareableEntryContainer) {
			((BPTShareableEntryContainer) entryComponent).removeButtons();
		}
		sidebar.logout();
	}
}
