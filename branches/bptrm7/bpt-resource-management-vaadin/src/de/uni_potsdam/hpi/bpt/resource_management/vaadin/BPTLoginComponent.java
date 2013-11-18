package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.IOException;
import java.util.ResourceBundle;

import org.expressme.openid.Association;
import org.expressme.openid.Endpoint;
import org.expressme.openid.OpenIdManager;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings({"serial"})
public class BPTLoginComponent extends VerticalLayout implements Property.ValueChangeListener {
	
	private Label welcomeLabel;
	private BPTNavigationBar navigationBar;
	private BPTApplicationUI applicationUI;
	private BPTSidebar sidebar;
	private static final String[] openIdProviders = new String[] { "Google", "Yahoo" };
	private String openIdReturnTo;
	private String openIdRealm;
	private String openIdProvider = openIdProviders[0];
	private Button administrationButton;
	
	public BPTLoginComponent(BPTApplicationUI applicationUI, BPTSidebar sidebar) {
		
		setProperties();
		
		this.applicationUI = applicationUI;
		this.sidebar = sidebar;
		navigationBar = new BPTNavigationBar(applicationUI);
		if (applicationUI.isLoggedIn()) {
			addComponent(navigationBar);
			addComponentsForLogout();
		} else {
			addComponentsForLogin();
		}
		
	}

	private void addComponentsForLogin() {
        HorizontalLayout openIdLayout = new HorizontalLayout();
        Label loginLabel = new Label("OpenID provider:&nbsp;", ContentMode.HTML);
        NativeSelect openIdProviderNativeSelect = new NativeSelect();
        for (String openIdProvider : openIdProviders) {
        	openIdProviderNativeSelect.addItem(openIdProvider);
        }
        openIdProviderNativeSelect.setNullSelectionAllowed(false);
        openIdProviderNativeSelect.setValue(openIdProviders[0]);
        openIdProviderNativeSelect.setImmediate(true);
        openIdProviderNativeSelect.addValueChangeListener(this);
        openIdLayout.addComponent(loginLabel);
        openIdLayout.addComponent(openIdProviderNativeSelect);
        
		Button loginButton = new Button("Login");
        loginButton.setStyleName(BaseTheme.BUTTON_LINK);
        
        loginButton.addClickListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
			        try {
						redirectToOpenIDProvider();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		});
        
        addComponent(loginButton);
        addComponent(openIdLayout);
		setExpandRatio(openIdLayout, 50);
		setExpandRatio(loginButton, 50);
	}
	
	private void addAdministrationButton() {
		administrationButton = new Button("Administration");
		administrationButton.setStyleName(BaseTheme.BUTTON_LINK);
		addComponent(administrationButton);
		
		administrationButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				applicationUI.renderAdministrator();
			}
		});
}


	private void addComponentsForLogout() {
		Button logoutButton = new Button("Logout");
        logoutButton.setStyleName(BaseTheme.BUTTON_LINK);
        addComponent(logoutButton);
        
        logoutButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				applicationUI.setName("");
				applicationUI.setMailAddress("");
				applicationUI.setLoggedIn(false);
				applicationUI.setModerated(false);
				applicationUI.setOpenIdProvider(openIdProviders[0]);
				applicationUI.renderEntries();
				removeAllComponents();
				addComponentsForLogin();
				sidebar.logout();
			}});
	}
	
	public void login(String name, boolean moderated) {
		removeAllComponents();
//		System.out.println(name);
//		navigationBar = new BPTNavigationBar(true);
		addComponent(navigationBar);
		welcomeLabel = new Label("Hello " + applicationUI.getName() + "!");
		addComponent(welcomeLabel);
		if (moderated) {
			addAdministrationButton();
		}
		addComponentsForLogout();
	}
	
	private void redirectToOpenIDProvider() throws IOException {
		OpenIdManager manager = new OpenIdManager();
		manager.setReturnTo(openIdReturnTo);
        manager.setRealm(openIdRealm);
		manager.setTimeOut(10000);
        Endpoint endpoint = manager.lookupEndpoint(openIdProvider);
//        System.out.println(endpoint);
        Association association = manager.lookupAssociation(endpoint);
//        System.out.println(association);
        String url = manager.getAuthenticationUrl(endpoint, association);
//        System.out.println("Copy the authentication URL in browser:\n" + url);
        
        applicationUI.setLoggingIn(true);
        
        applicationUI.getPage().open(url, "_self", false);
        /*
         *  TODO: this is not a clean solution
         *  if user clicks on login and then goes back to the application
         *  the user can paste the OpenID return URL with parameters
         *  and may login as another user
         */
//        System.out.println("After successfully sign on in browser, enter the URL of address bar in browser:");
//        String ret = readLine();
//        HttpServletRequest request = createRequest(ret);
//        Authentication authentication = manager.getAuthentication(request, association.getRawMacKey(), endpoint.getAlias());
//        System.out.println(authentication);

	}
	
	private void setProperties() {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("de.uni_potsdam.hpi.bpt.resource_management.bptrm");
		openIdReturnTo = resourceBundle.getString("OPENID_RETURN_TO");
		openIdRealm = resourceBundle.getString("OPENID_REALM");
	}

	public void valueChange(ValueChangeEvent event) {
		openIdProvider = event.getProperty().toString();
		applicationUI.setOpenIdProvider(openIdProvider);
	}	
}
