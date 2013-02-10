package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTUserRepository;

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
		loginButton.setClickShortcut(KeyCode.ENTER);
		addComponent(loginButton);
//		Button registerButton = new Button("Not registered yet?");
//		registerButton.setStyleName(BaseTheme.BUTTON_LINK);
// 		addComponent(registerButton);
        
		loginButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				BPTApplication application = (BPTApplication) getApplication();
				BPTUserRepository userRepository = application.getUserRepository();
				
				String username = ((String) usernameField.getValue());
				String mailAddress = ((String) mailAddressField.getValue());
				application.setName(username);
				application.setMailAddress(mailAddress);
				application.setLoggedIn(true);
				
//				application.setModerator(userRepository.isModerator(username, mailAddress));
//				System.out.println("LoginWindow: " + application.isModerated());
				usernameField.setValue("");
				mailAddressField.setValue("");
				
				component.getWindow().removeWindow(loginWindow);
				component.login(username);
			}});

	}
}
