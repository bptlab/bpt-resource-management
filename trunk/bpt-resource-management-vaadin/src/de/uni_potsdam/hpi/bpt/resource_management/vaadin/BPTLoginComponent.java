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
	private Label welcomeLabel;
	private Window subWindow;
	private BPTNavigationBar navigationBar;
	private BPTSidebar sidebar;
	
	public BPTLoginComponent(boolean isLoggedIn, BPTSidebar sidebar){
		
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
				application.setUsername("");
				application.setMailAddress("");
				application.setLoggedIn(false);
				application.finder();
				layout.removeAllComponents();
				addLoginButton();
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
		welcomeLabel = new Label("Hello " + ((BPTApplication) getApplication()).getUsername() + "!");
		layout.addComponent(welcomeLabel);
		addLogoutButton();
		sidebar.login();
	}
	
}
