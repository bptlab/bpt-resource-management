package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;

@SuppressWarnings({"serial"})
public class BPTBoxContainer extends HorizontalLayout {
	
	private BPTApplicationUI applicationUI;
	private CheckBox unpublishedCheckBox, publishedCheckBox, rejectedCheckBox;
	private boolean loggedIn, moderated;
	private OptionGroup resourceProviderOptionGroup;
	
	public BPTBoxContainer(BPTApplicationUI applicationUI) {
		
		// TODO: this component is currently initialized at start of application and not at login
		
		this.applicationUI = applicationUI;
		this.loggedIn = applicationUI.isLoggedIn();
		this.moderated = applicationUI.isModerated();
		
		if (loggedIn) {
			if (moderated) {
				publishedCheckBox = new CheckBox("published");
				publishedCheckBox.setValue(true);
				publishedCheckBox.setImmediate(true);
				addComponent(publishedCheckBox);
				
				unpublishedCheckBox = new CheckBox("unpublished");
				addComponent(unpublishedCheckBox);
				unpublishedCheckBox.setImmediate(true);
				
				rejectedCheckBox = new CheckBox("rejected");
				addComponent(rejectedCheckBox);
				rejectedCheckBox.setImmediate(true);
				
				unpublishedCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
				    	refresh();
				    }
				});
				rejectedCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
				    	refresh();
				    }
				});

				publishedCheckBox.addValueChangeListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
				    	refresh();
				    }
				});
			} else {
				resourceProviderOptionGroup = new OptionGroup();
				addComponent(resourceProviderOptionGroup);
				resourceProviderOptionGroup.setImmediate(true);
				resourceProviderOptionGroup.addItem("published entries");
				resourceProviderOptionGroup.addItem("own entries");
				resourceProviderOptionGroup.select("published entries");
				resourceProviderOptionGroup.addStyleName("horizontal");
				resourceProviderOptionGroup.setSizeUndefined();
				resourceProviderOptionGroup.addValueChangeListener(new Property.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
				    	refresh();
				    }
				});
			}
		}
	}
	
	private void refresh() {
		applicationUI.refreshAndClean();
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
