package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

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
		if (language == "de") {
			return topicName.german;
		} else {
			return topicName.english;
		}
	}
}
