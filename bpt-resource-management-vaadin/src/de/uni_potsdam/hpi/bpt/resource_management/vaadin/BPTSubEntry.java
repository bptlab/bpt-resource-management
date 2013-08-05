package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.Map;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;

public class BPTSubEntry extends CustomLayout{
	
	private String language, subtitle, description, exercise_url;
	
	public BPTSubEntry(Map entry){
		super("subEntry");
		language = (String) entry.get("language");
		subtitle = (String) entry.get("subtitle");
		Label subtitleLabel = new Label(subtitle);
		this.addComponent(subtitleLabel, "Subtitle");
		description = (String) entry.get("description");
		Label descriptionLabel = new Label(description);
		descriptionLabel.setContentMode(Label.CONTENT_XHTML);
		this.addComponent(descriptionLabel, "Description");
		exercise_url = (String) entry.get("exercise_url");
		Label exerciseURLLabel = new Label(exercise_url);
		this.addComponent(exerciseURLLabel, "Exercise URL");
		
	}

}
