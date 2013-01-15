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
//		subWindow.setModal(true);
//		final TextField usernameField = new TextField("Username");
//		subWindow.addComponent(usernameField);
//		final PasswordField password = new PasswordField("Password");
//		subWindow.addComponent(password);
//		Button loginButton = new Button("Login");
//		subWindow.addComponent(loginButton);
//		Button cancelButton = new Button("Cancel");
//		subWindow.addComponent(cancelButton);
//		Button registerButton = new Button("Not registered yet?");
//        registerButton.setStyleName(BaseTheme.BUTTON_LINK);
//		subWindow.addComponent(registerButton);
//        
//		loginButton.addListener(new Button.ClickListener(){
//			public void buttonClick(ClickEvent event) {
//				BPTApplication application = (BPTApplication) getApplication();
//				String username = ((String) usernameField.getValue());
//				application.setUsername(username);
//				application.setLoggedIn(true);
//				usernameField.setValue("");
//				password.setValue("");
//				layout.removeAllComponents();
//				System.out.println(username);
//				layout.addComponent(navigationBar);
//				layout.addComponent(new Label(username));
//				addLogoutButton();
//				sidebar.addBoxContainer();
//				getWindow().removeWindow(subWindow);
//				
//			}});
//		
//		cancelButton.addListener(new Button.ClickListener(){
//			public void buttonClick(ClickEvent event) {
//				getWindow().removeWindow(subWindow);
//				usernameField.setValue("");
//				password.setValue("");
//			}});
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
