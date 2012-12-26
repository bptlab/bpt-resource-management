package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class BPTLoginComponent extends CustomComponent{
		
	private Button loginButton;
	private Window subWindow;
	
	public BPTLoginComponent(){
		
		HorizontalLayout layout = new HorizontalLayout();
		setCompositionRoot(layout);
		layout.addComponent(new Label("Willkommen"));
		loginButton = new Button("Login");
        loginButton.setStyleName(BaseTheme.BUTTON_LINK);
        layout.addComponent(loginButton);
        createSubWindow();
        
        loginButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				getWindow().addWindow(subWindow);
			}});
	}

	private void createSubWindow(){
		subWindow = new Window("Login");
		subWindow.setModal(true);
		final TextField username = new TextField("Username");
		subWindow.addComponent(username);
		final PasswordField password = new PasswordField("Password");
		subWindow.addComponent(password);
		Button loginButton = new Button("Login");
		subWindow.addComponent(loginButton);
		Button cancelButton = new Button("Cancel");
		subWindow.addComponent(cancelButton);
		Button registerButton = new Button("Not registered yet?");
        registerButton.setStyleName(BaseTheme.BUTTON_LINK);
		subWindow.addComponent(registerButton);
        
		loginButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				
			}});
		
		cancelButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				getWindow().removeWindow(subWindow);
				username.setValue("");
				password.setValue("");
			}});
	}
}
