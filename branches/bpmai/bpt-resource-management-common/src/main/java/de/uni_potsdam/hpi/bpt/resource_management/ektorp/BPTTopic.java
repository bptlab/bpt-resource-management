package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import java.util.ArrayList;
import java.util.List;

public enum BPTTopic {
	FoundBPM ("Foundations of Business Process Management", "Grundlagen des Business Process Management"),
	FoundPM ("Foundations of Process Modeling", "Grundlagen der Prozessmodellierung"),
	PModLang ("Process Modeling Languages", "Prozessmodellierungssprachen"),
	PChor ("Process Choreographies", "Prozesschoreographien"),
	DataProc ("Data and Processes", "Daten und Prozesse"),
	PAnalys ("Process Analysis", "Prozessanalyse"),
	PMining ("Process Mining", "Process Mining"),
	PAbstr ("Process Abstraction", "Prozessabstraktion"),
	PFlex ("Process Flexibility", "Prozessflexibilität"),
	BPMArch ("BPM Architecture and Implementation", "Architektur und Implementation von BPM"),
	BPMMethd ("BPM Methodology", "Methodologien in BPM");
	
	private final String english;
	private final String german;
	
	BPTTopic(String english, String german) {
        this.english = english;
        this.german = german;
    }
	
	public static String getName(BPTTopic topicName, String language) {
		if (language == "Deutsch") {
			return topicName.german;
		} else {
			return topicName.english;
		}
	}
	
	public static List<String> getValues(String language) {
		ArrayList<String> values = new ArrayList<String>();
		if (language == "Deutsch") {
			for (BPTTopic topic : values()) {
				values.add(topic.german);
			}
			return values;
		} else {
			for (BPTTopic topic : values()) {
				values.add(topic.english);
			}
			return values;
		}
	}
	
	public static BPTTopic getValueOf(String topicName, String language) {
		if (language == "Deutsch") {
			for (BPTTopic topic : values()) {
				if (topic.german.equals(topicName)) {
					return topic;
				}
			}
		} else {
			for (BPTTopic topic : values()) {
				if (topic.english.equals(topicName)) {
					return topic;
				}
			}
		}
		return null;
	}
}
