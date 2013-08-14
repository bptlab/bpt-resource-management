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
	
	private String title, description, exercise_url, topics;
	
	public BPTSubEntry(Item entry) {
		
		// TODO: Refactor this class - see BPTEntry.
		// TODO: .setWidth("90%");
		super("subEntry");
		title = (String) entry.getItemProperty("Title").getValue();
		Label titleLabel = new Label(title);
		this.addComponent(titleLabel, "Title");
		description = (String) entry.getItemProperty("Description").getValue();
		Label descriptionLabel = new Label(description, Label.CONTENT_XHTML);
		descriptionLabel.setContentMode(Label.CONTENT_XHTML);
		this.addComponent(descriptionLabel, "Description");
		exercise_url = (String) entry.getItemProperty("Exercise URL").getValue();
		if (exercise_url == null || exercise_url.isEmpty()) {
			exercise_url = "(none)";
		} else {
			exercise_url = "<a href=\"" + exercise_url + "\" target=\"_blank\">" + exercise_url + "</a>";
		}
		Label exerciseURLLabel = new Label(exercise_url, Label.CONTENT_XHTML);
		this.addComponent(exerciseURLLabel, "Exercise URL");
		topics = (String) entry.getItemProperty("Topics").getValue();
		Label topicsLabel = new Label(topics);
		this.addComponent(topicsLabel, "Topics");
		
		ArrayList<Link> attachements = (ArrayList<Link>) entry.getItemProperty("Attachements").getValue();
		VerticalLayout attachementLayout = new VerticalLayout();
		for(Link link : attachements){
			attachementLayout.addComponent(link);
		}
		this.addComponent(attachementLayout, "Attachements");
	}

}
