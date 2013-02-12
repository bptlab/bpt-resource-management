package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;

public class BPTBoxContainer extends CustomComponent {

	private HorizontalLayout layout;
	private CheckBox unpublishedCheckBox, publishedCheckBox, rejectedCheckBox, ownEntriesCheckBox;
	private boolean loggedIn, moderated;
	private OptionGroup resourceProviderOptionGroup;
	
	public BPTBoxContainer(BPTApplication application) {
		
		// TODO: this component is currently initialized at start of application and not at login
		
		this.loggedIn = application.isLoggedIn();
		this.moderated = application.isModerated();
		
		layout = new HorizontalLayout();
		setCompositionRoot(layout);
		
		if (loggedIn) {
			if (moderated) {
				
				publishedCheckBox = new CheckBox("published");
				publishedCheckBox.setValue(true);
				publishedCheckBox.setImmediate(true);
				layout.addComponent(publishedCheckBox);
				
				unpublishedCheckBox = new CheckBox("unpublished");
				layout.addComponent(unpublishedCheckBox);
				unpublishedCheckBox.setImmediate(true);
				
				rejectedCheckBox = new CheckBox("rejected");
				layout.addComponent(rejectedCheckBox);
				rejectedCheckBox.setImmediate(true);
				
				unpublishedCheckBox.addListener(new Property.ValueChangeListener() {
				    public void valueChange(ValueChangeEvent event) {
				    	
				    	refresh();
				    }
				});
				rejectedCheckBox.addListener(new Property.ValueChangeListener() {
				    public void valueChange(ValueChangeEvent event) {
				    	refresh();
				    }
				});

				publishedCheckBox.addListener(new Property.ValueChangeListener() {
				    public void valueChange(ValueChangeEvent event) {
				    	
				    	refresh();
				    }
				});
				
			} else {
				
				resourceProviderOptionGroup = new OptionGroup();
				layout.addComponent(resourceProviderOptionGroup);
				resourceProviderOptionGroup.setImmediate(true);
				resourceProviderOptionGroup.addItem("all entries");
				resourceProviderOptionGroup.addItem("own entries");
				resourceProviderOptionGroup.select("all entries");
				
				resourceProviderOptionGroup.addListener(new Property.ValueChangeListener() {
				    public void valueChange(ValueChangeEvent event) {
				    	refresh();
				    }
				});
				
			}
		}
	}
	
	private void refresh() {
		((BPTApplication) getApplication()).refresh();
	}

	public ArrayList<BPTToolStatus> getSelectedStates() {
		ArrayList<BPTToolStatus> statusList = new ArrayList<BPTToolStatus>();
		
		if ((Boolean) publishedCheckBox.getValue()) {
			statusList.add(BPTToolStatus.Published);
		}
		if ((Boolean) unpublishedCheckBox.getValue()) {
			statusList.add(BPTToolStatus.Unpublished);
		}
		if ((Boolean) rejectedCheckBox.getValue()) {
			statusList.add(BPTToolStatus.Rejected);
		}
		return statusList;
	}
	
	public boolean isOwnEntriesOptionSelected() {
		if (loggedIn && !moderated) {
			if (((String)resourceProviderOptionGroup.getValue()).equals("own entries")) {
				return true;
			}
		}
		return false;
	}
}
