package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

public class BPTSubEntry extends CustomLayout{
	
	public BPTSubEntry(Item entry) {
		
		// TODO: Refactor this class - see BPTEntry.
		// TODO: .setWidth("90%");
		super("subEntry");
		String title = (String) entry.getItemProperty("Title").getValue();
		Label titleLabel = new Label(title);
		this.addComponent(titleLabel, "Title");
		String setID = (String) entry.getItemProperty("Exercise Set ID").getValue();
		Label setIDLabel = new Label(setID);
		this.addComponent(setIDLabel, "Exercise Set ID");
		Label descriptionLabel = (Label) entry.getItemProperty("Description").getValue();
		descriptionLabel.setWidth("90%");
		this.addComponent(descriptionLabel, "Description");
		Link exerciseURLLink = (Link) entry.getItemProperty("Exercise URL").getValue();
		if (exerciseURLLink.getCaption().isEmpty()) {
			Label label = new Label("(none)");
			label.setWidth("90%");
			this.addComponent(label, "Exercise URL");
		} else {
			this.addComponent(exerciseURLLink, "Exercise URL");
		}
//		String topics = (String) entry.getItemProperty("Topics").getValue();
//		Label topicsLabel = new Label(topics);
//		this.addComponent(topicsLabel, "Topics");
		
		VerticalLayout attachmentLayout = new VerticalLayout();
		this.addComponent(attachmentLayout, "Supplementary files");
		for (int i = 1; entry.getItemProperty("Supplementary file" + i).getValue() != null; i++) {
			attachmentLayout.addComponent((Link) entry.getItemProperty("Supplementary file" + i).getValue());
		}
	}

}
