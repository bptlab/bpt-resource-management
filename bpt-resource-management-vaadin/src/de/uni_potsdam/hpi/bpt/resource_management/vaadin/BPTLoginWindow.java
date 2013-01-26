package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class BPTLoginWindow extends Window {

	private BPTLoginWindow window;
	
	public BPTLoginWindow(String title, final BPTLoginComponent component){
		super(title);
		window = this;
		setModal(true);
		final TextField usernameField = new TextField("Username");
		addComponent(usernameField);
		final PasswordField password = new PasswordField("Password");
		addComponent(password);
		Button loginButton = new Button("Login");
		addComponent(loginButton);
		Button cancelButton = new Button("Cancel");
		addComponent(cancelButton);
		Button registerButton = new Button("Not registered yet?");
        registerButton.setStyleName(BaseTheme.BUTTON_LINK);
		addComponent(registerButton);
        
		loginButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				BPTApplication application = (BPTApplication) getApplication();
				String username = ((String) usernameField.getValue());
				application.setUsername(username);
				application.setLoggedIn(true);
				usernameField.setValue("");
				password.setValue("");
				component.getWindow().removeWindow(window);
				component.login(username);
				((BPTApplication) getApplication()).loginRequest(username);
			}});
		
		cancelButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				component.getWindow().removeWindow(window);
				usernameField.setValue("");
				password.setValue("");
			}});
	}
}
