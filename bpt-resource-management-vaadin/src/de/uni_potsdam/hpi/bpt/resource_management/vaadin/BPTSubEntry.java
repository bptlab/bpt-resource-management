package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.Map;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

public class BPTSubEntry extends CustomLayout{
	
	private String title, description, exercise_url, topics;
	
	public BPTSubEntry(Map entry) {
		
		// TODO: Refactor this class - see BPTEntry.
		// TODO: .setWidth("90%");
		super("subEntry");
		title = (String) entry.get("title");
		Label titleLabel = new Label(title);
		this.addComponent(titleLabel, "Title");
		description = (String) entry.get("description");
		Label descriptionLabel = new Label(description, Label.CONTENT_XHTML);
		descriptionLabel.setContentMode(Label.CONTENT_XHTML);
		this.addComponent(descriptionLabel, "Description");
		exercise_url = (String) entry.get("exercise_url");
		if (exercise_url == null || exercise_url.isEmpty()) {
			exercise_url = "(none)";
		} else {
			exercise_url = "<a href=\"" + exercise_url + "\" target=\"_blank\">" + exercise_url + "</a>";
		}
		Label exerciseURLLabel = new Label(exercise_url, Label.CONTENT_XHTML);
		this.addComponent(exerciseURLLabel, "Exercise URL");
		topics = entry.get("topics").toString();
		Label topicsLabel = new Label(topics);
		this.addComponent(topicsLabel, "Topics");
	}

}
