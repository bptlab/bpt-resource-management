package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.Map;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;

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
		Label descriptionLabel = new Label(description);
		descriptionLabel.setContentMode(Label.CONTENT_XHTML);
		this.addComponent(descriptionLabel, "Description");
		exercise_url = (String) entry.get("exercise_url");
		if (exercise_url == null || exercise_url.isEmpty()) {
			exercise_url = "(none)";
		}
		Label exerciseURLLabel = new Label(exercise_url);
		this.addComponent(exerciseURLLabel, "Exercise URL");
		topics = entry.get("topics").toString();
		Label topicsLabel = new Label(topics);
		this.addComponent(topicsLabel, "Topics");
	}

}
