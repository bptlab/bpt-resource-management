package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.IOException;
import java.util.ResourceBundle;

import org.expressme.openid.Association;
import org.expressme.openid.Endpoint;
import org.expressme.openid.OpenIdManager;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings({"serial"})
public class BPTLoginComponent extends VerticalLayout {
	
	private Label welcomeLabel;
	private BPTNavigationBar navigationBar;
	private BPTApplicationUI applicationUI;
	private String openIdReturnTo;
	private String openIdRealm;
	private Button administrationButton;
	
	public BPTLoginComponent(BPTApplicationUI applicationUI, BPTSidebar sidebar) {
		
		setProperties();
		
		this.applicationUI = applicationUI;
		navigationBar = new BPTNavigationBar(applicationUI);
		if (applicationUI.isLoggedIn()) {
			addComponent(navigationBar);
			addComponentsForLogout();
		} else {
			addComponentsForLogin();
		}
		
	}

	public void addComponentsForLogin() {
        HorizontalLayout openIdLayout = new HorizontalLayout();
        Label gsLabel = new Label("<iframe src=\"./VAADIN/themes/bpt/layouts/googleSignIn.html\" style=\"width:160px; height:55px; border: none\"></iframe>", ContentMode.HTML);
        openIdLayout.addComponent(gsLabel);
        
		Button loginButton = new Button("Sign in with Yahoo");
        loginButton.setStyleName(BaseTheme.BUTTON_LINK);
        
        loginButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
			        try {
						redirectToYahooOpenIDProvider();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		});
        

        addComponent(openIdLayout);
        addComponent(loginButton);
		setExpandRatio(openIdLayout, 50);
		setExpandRatio(loginButton, 50);
        setComponentAlignment(loginButton, Alignment.MIDDLE_CENTER);
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
		if (applicationUI.getOpenIdProvider().equals("Google")) {
	        Label gsLabel = new Label("<iframe src=\"./VAADIN/themes/bpt/layouts/googleSignOut.html\" style=\"width:160px; height:40px; border: none\"></iframe>", ContentMode.HTML);
	        addComponent(gsLabel);
		} else {
			Button logoutButton = new Button("Logout");
	        logoutButton.setStyleName(BaseTheme.BUTTON_LINK);
	        addComponent(logoutButton);
	        
	        logoutButton.addClickListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					applicationUI.logout();
				}
			});
		}
	}
	
	public void login(boolean moderated) {
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
	
	private void redirectToYahooOpenIDProvider() throws IOException {
		OpenIdManager manager = new OpenIdManager();
		manager.setReturnTo(openIdReturnTo);
        manager.setRealm(openIdRealm);
		manager.setTimeOut(10000);
        Endpoint endpoint = manager.lookupEndpoint("Yahoo");
//        System.out.println(endpoint);
        Association association = manager.lookupAssociation(endpoint);
//        System.out.println(association);
        String url = manager.getAuthenticationUrl(endpoint, association);
//        System.out.println("Copy the authentication URL in browser:\n" + url);
        
        applicationUI.setLoggingIn(true);
        
        applicationUI.getPage().open(url, "_self", false);

	}
	
	private void setProperties() {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("de.uni_potsdam.hpi.bpt.resource_management.bptrm");
		openIdReturnTo = resourceBundle.getString("OPENID_RETURN_TO");
		openIdRealm = resourceBundle.getString("OPENID_REALM");
	}
}
