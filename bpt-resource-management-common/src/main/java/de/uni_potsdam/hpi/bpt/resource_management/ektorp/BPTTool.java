package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class BPTTool {
	
	@JsonProperty("_id")
	private String name = new String();
	@JsonProperty("_rev")
	private String revision; // CouchDB-specific, never shown on website
	private final String type = "BPTTool";

	private String description = new String(); // shall be rich text
	private String provider = new String();
	@JsonProperty("download_url")
	private String downloadURL = "http://www.example.org"; // URL to be checked if valid
	@JsonProperty("documentation_url")
	private String documentationURL = "http://www.example.org"; // URL to be checked if valid
	@JsonProperty("screencast_url")
	private String screencastURL = "http://www.example.org"; // URL to be checked if valid
	private Set<String> availabilities = new HashSet<String>(); 
		/*
		 * open source, freeware, shareware, free for academics, commercial, ...
		 */
	@JsonProperty("model_types")
	private Set<String> modelTypes = new HashSet<String>(); 
		/*
		 * BPMN, EPC, Petri net, UML Activity Diagram, Workflow Net, YAWL, BPEL, ...
		 */
	private Set<String> platforms = new HashSet<String>();
		/*
		 * Windows, Linux, Mac OSX, Android, iOS, ...
		 */
	@JsonProperty("supported_functionalities")
	private Set<String> supportedFunctionalities = new HashSet<String>();
		/* 
		 * graphical model editor, model repository, verification of model properties, 
		 * enactment, runtime adaptation, process discovery based on event data, 
		 * conformance checking based on event data
		 */
	@JsonProperty("contact_name")
	private String contactName = new String();
	@JsonProperty("contact_mail")
	private String contactMail = "example@test.org"; // email address
	@JsonProperty("date_created")
	private Date dateCreated = new Date();
	@JsonProperty("last_update")
	private Date lastUpdate = new Date();
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getType() {
		return type;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getProvider() {
		return provider;
	}
	
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	public String getDownloadURL() {
		return downloadURL;
	}
	
	public void setDownloadURL(String downloadURL) {
		if (BPTValidator.isValidURL(downloadURL))
			this.downloadURL = downloadURL;
	}
	
	public String getDocumentationURL() {
		return documentationURL;
	}
	
	public void setDocumentationURL(String documentationURL) {
		if (BPTValidator.isValidURL(documentationURL))
			this.documentationURL = documentationURL;
	}
	
	public String getScreencastURL() {
		return screencastURL;
	}
	
	public void setScreencastURL(String screencastURL) {
		if (BPTValidator.isValidURL(screencastURL))
			this.screencastURL = screencastURL;
	}
	
	public Set<String> getAvailabilities() {
		return availabilities;
	}
	
	public void setAvailabilities(Set<String> availabilities) {
		this.availabilities = availabilities;
	}
	
	public Set<String> getModelTypes() {
		return modelTypes;
	}
	
	public void setModelTypes(Set<String> modelTypes) {
		this.modelTypes = modelTypes;
	}
	
	public Set<String> getPlatforms() {
		return platforms;
	}
	
	public void setPlatforms(Set<String> platforms) {
		this.platforms = platforms;
	}
	
	public Set<String> getSupportedFunctionalities() {
		return supportedFunctionalities;
	}
	
	public void setSupportedFunctionalities(Set<String> supportedFunctionalities) {
		this.supportedFunctionalities = supportedFunctionalities;
	}
	
	public String getContactName() {
		return contactName;
	}
	
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	
	public String getContactMail() {
		return contactMail;
	}
	
	public void setContactMail(String contactMail) {
		if (BPTValidator.isValidEmail(contactMail))
			this.contactMail = contactMail;
	}
	
	public Date getDateCreated() {
		return dateCreated;
	}
	
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public Date getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
