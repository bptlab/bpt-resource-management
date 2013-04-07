package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseStatus;

@SuppressWarnings("serial")
public class BPTBoxContainer extends CustomComponent {
	
	private HorizontalLayout layout;
	private CheckBox unpublishedCheckBox, publishedCheckBox, rejectedCheckBox;
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
				resourceProviderOptionGroup.addItem("published entries");
				resourceProviderOptionGroup.addItem("own entries");
				resourceProviderOptionGroup.select("published entries");
				
				resourceProviderOptionGroup.addStyleName("horizontal");
				resourceProviderOptionGroup.setSizeUndefined();
				
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

	public ArrayList<BPTExerciseStatus> getSelectedStates() {
		ArrayList<BPTExerciseStatus> statusList = new ArrayList<BPTExerciseStatus>();
		
		if ((Boolean) publishedCheckBox.getValue()) {
			statusList.add(BPTExerciseStatus.Published);
		}
		if ((Boolean) unpublishedCheckBox.getValue()) {
			statusList.add(BPTExerciseStatus.Unpublished);
		}
		if ((Boolean) rejectedCheckBox.getValue()) {
			statusList.add(BPTExerciseStatus.Rejected);
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
