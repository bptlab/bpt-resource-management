package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.IOException;
import java.util.ResourceBundle;

import org.expressme.openid.Association;
import org.expressme.openid.Endpoint;
import org.expressme.openid.OpenIdManager;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class BPTLoginComponent extends CustomComponent implements Property.ValueChangeListener {
		
	private VerticalLayout layout;
	private Button loginButton;
	private Button logoutButton;
	private Button registerButton;
	private Label welcomeLabel;
	private Window subWindow;
	private BPTNavigationBar navigationBar;
	private BPTSidebar sidebar;
	private static final String[] openIdProviders = new String[] { "Google", "Yahoo" };
	private String openIdReturnTo;
	private String openIdRealm;
	private String openIdProvider = openIdProviders[0];
	
	public BPTLoginComponent(boolean isLoggedIn, BPTSidebar sidebar){
		
		setProperties();
		
		this.sidebar = sidebar;
		layout = new VerticalLayout();
		setCompositionRoot(layout);
		navigationBar = new BPTNavigationBar();
		
		if (isLoggedIn) {
			layout.addComponent(navigationBar);
			addLogoutButton();
		}
		else {
			addLoginButton();
		}
		
	}

	private void addLoginButton() {
		loginButton = new Button("Login");
        loginButton.setStyleName(BaseTheme.BUTTON_LINK);
        layout.addComponent(loginButton);
        
        loginButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
			        try {
						redirectToOpenIDProvider();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		});
        
        NativeSelect openIdProviderNativeSelect = new NativeSelect("OpenID provider");
        for (String openIdProvider : openIdProviders) {
        	openIdProviderNativeSelect.addItem(openIdProvider);
        }
        openIdProviderNativeSelect.setNullSelectionAllowed(false);
        openIdProviderNativeSelect.setValue(openIdProviders[0]);
        openIdProviderNativeSelect.setImmediate(true);
        openIdProviderNativeSelect.addListener(this);
        
        layout.addComponent(openIdProviderNativeSelect);

	}

	private void addLogoutButton() {
		logoutButton = new Button("Logout");
        logoutButton.setStyleName(BaseTheme.BUTTON_LINK);
        layout.addComponent(logoutButton);
        
        logoutButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				BPTApplication application = (BPTApplication) getApplication();
				application.setName("");
				application.setMailAddress("");
				application.setLoggedIn(false);
				application.setModerated(false);
				application.setOpenIdProvider(openIdProviders[0]);
				application.finder();
				layout.removeAllComponents();
				addLoginButton();
				sidebar.logout();
	
			}});
		
	}
	
	public void login(String name){
		layout.removeAllComponents();
//		System.out.println(name);
		layout.addComponent(navigationBar);
		welcomeLabel = new Label("Hello " + ((BPTApplication) getApplication()).getName() + "!");
		layout.addComponent(welcomeLabel);
		addLogoutButton();
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
        ((BPTApplication) getApplication()).getMainWindow().open(new ExternalResource(url), "_self");
        /*
         *  TODO: this is not a clean solution
         *  if user clicks on login and then goes back to the application
         *  the user can paste the OpenID return URL with parameters
         *  and may login as another user
         */
        ((BPTApplication) getApplication()).setLoggingIn(true);
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

	@Override
	public void valueChange(ValueChangeEvent event) {
		openIdProvider = event.getProperty().toString();
		((BPTApplication) getApplication()).setOpenIdProvider(openIdProvider);
	}	
}
