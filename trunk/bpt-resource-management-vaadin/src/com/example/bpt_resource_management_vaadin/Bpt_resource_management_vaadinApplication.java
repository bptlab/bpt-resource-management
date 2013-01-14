package com.example.bpt_resource_management_vaadin;

import com.vaadin.Application;
import com.vaadin.ui.*;

public class Bpt_resource_management_vaadinApplication extends Application {
	@Override
	public void init() {
		Window mainWindow = new Window("Bpt_resource_management_vaadin Application");
		Label label = new Label("Hello Vaadin user");
		mainWindow.addComponent(label);
		setMainWindow(mainWindow);
	}

}
