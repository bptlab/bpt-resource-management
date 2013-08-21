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
		Label descriptionLabel = (Label) entry.getItemProperty("Description").getValue();
		this.addComponent(descriptionLabel, "Description");
		Link exerciseURLLink = (Link) entry.getItemProperty("Exercise URL").getValue();
		this.addComponent(exerciseURLLink, "Exercise URL");
//		String topics = (String) entry.getItemProperty("Topics").getValue();
//		Label topicsLabel = new Label(topics);
//		this.addComponent(topicsLabel, "Topics");
		
		VerticalLayout attachmentLayout = new VerticalLayout();
		this.addComponent(attachmentLayout, "Attachments");
		for (int i = 1; entry.getItemProperty("Attachment" + i).getValue() != null; i++) {
			attachmentLayout.addComponent((Link) entry.getItemProperty("Attachment" + i).getValue());
		}
	}

}
