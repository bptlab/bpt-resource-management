package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class BPTLoginComponent extends CustomComponent{
		
	private VerticalLayout layout;
	private Button loginWindowButton;
	private Button logoutButton;
	private Button registerButton;
	private Window subWindow;
	private Label welcomeLabel;
	private BPTNavigationBar navigationBar;
	private BPTSidebar sidebar;
	
	public BPTLoginComponent(String username, boolean isLoggedIn, BPTSidebar sidebar){
		
		this.sidebar = sidebar;
		layout = new VerticalLayout();
		setCompositionRoot(layout);
		navigationBar = new BPTNavigationBar();
		
		if (isLoggedIn){
			layout.addComponent(navigationBar);
			addLogoutButton();
		}
		else {
			welcomeLabel = new Label("Willkommen Gast");
			layout.addComponent(welcomeLabel);
			addLoginButton();
			addRegisterButton();
		}
		
		
	}

	private void addRegisterButton() {
		registerButton = new Button("Register");
		registerButton.setStyleName(BaseTheme.BUTTON_LINK);
		layout.addComponent(registerButton);
		
	}

	private void addLoginButton() {
		loginWindowButton = new Button("Login");
        loginWindowButton.setStyleName(BaseTheme.BUTTON_LINK);
        layout.addComponent(loginWindowButton);
        createSubWindow();
        
        loginWindowButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				getWindow().addWindow(subWindow);
			}});
		
	}

	private void addLogoutButton() {
		logoutButton = new Button("Logout");
        logoutButton.setStyleName(BaseTheme.BUTTON_LINK);
        layout.addComponent(logoutButton);
        
        logoutButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				BPTApplication application = (BPTApplication) getApplication();
				application.setUsername("Guest");
				application.setLoggedIn(false);
				application.finder();
				layout.removeAllComponents();
				layout.addComponent(welcomeLabel);
				addLoginButton();
				addRegisterButton();
				sidebar.logout();
			}});
		
	}

	private void createSubWindow(){
		subWindow = new BPTLoginWindow("Login", this);
	}
	
	public void login(String username){
		layout.removeAllComponents();
		System.out.println(username);
		layout.addComponent(navigationBar);
		layout.addComponent(new Label(username));
		addLogoutButton();
		sidebar.login();
	}
	
}
