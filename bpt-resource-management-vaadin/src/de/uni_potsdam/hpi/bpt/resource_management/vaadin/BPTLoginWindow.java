package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class BPTLoginWindow extends Window {

	private BPTLoginWindow loginWindow;
	
	public BPTLoginWindow(String title, final BPTLoginComponent component){
		super(title);
		loginWindow = this;
		setWidth("400px");
		setModal(true);
		final TextField usernameField = new TextField("Name");
		addComponent(usernameField);
		final TextField mailAddressField = new TextField("Mail");
		addComponent(mailAddressField);
//		final PasswordField password = new PasswordField("Password");
//		addComponent(password);
		Button loginButton = new Button("Login");
		addComponent(loginButton);
//		Button cancelButton = new Button("Cancel");
//		addComponent(cancelButton);
//		Button registerButton = new Button("Not registered yet?");
//		registerButton.setStyleName(BaseTheme.BUTTON_LINK);
// 		addComponent(registerButton);
        
		loginButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				BPTApplication application = (BPTApplication) getApplication();
				String username = ((String) usernameField.getValue());
				String mailAddress = ((String) mailAddressField.getValue());
				application.setUsername(username);
				application.setMailAddress(mailAddress);
				application.setLoggedIn(true);
				usernameField.setValue("");
				mailAddressField.setValue("");
				component.getWindow().removeWindow(loginWindow);
				component.login(username);
				application.loginRequest(username);
			}});
		
//		cancelButton.addListener(new Button.ClickListener(){
//			public void buttonClick(ClickEvent event) {
//				component.getWindow().removeWindow(loginWindow);
//				usernameField.setValue("");
//				mailAddressField.setValue("");
//			}});
	}
}
