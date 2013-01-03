package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class BPTTool {
	
	@JsonProperty("_id")
	private String name;
	@JsonProperty("_rev")
	private String revision; // CouchDB-specific, never shown on website
	private final String type = "BPTTool";

	private String description; // shall be rich text
	private String provider;
	@JsonProperty("download_url")
	private String downloadURL; // URL to be checked if valid
	@JsonProperty("documentation_url")
	private String documentationURL; // URL to be checked if valid
	@JsonProperty("screencast_url")
	private String screencastURL; // URL to be checked if valid
	private List<String> availabilities; 
		/*
		 * open source, freeware, shareware, free for academics, commercial, ...
		 */
	@JsonProperty("model_types")
	private List<String> modelTypes; 
		/*
		 * BPMN, EPC, Petri net, UML Activity Diagram, Workflow Net, YAWL, BPEL, ...
		 */
	private List<String> platforms;
		/*
		 * Windows, Linux, Mac OSX, Android, iOS, ...
		 */
	@JsonProperty("supported_functionalities")
	private List<String> supportedFunctionalities;
		/* 
		 * graphical model editor, model repository, verification of model properties, 
		 * enactment, runtime adaptation, process discovery based on event data, 
		 * conformance checking based on event data
		 */
	@JsonProperty("contact_name")
	private String contactName;
	@JsonProperty("contact_mail")
	private String contactMail; // email address
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
	
	public List<String> getAvailabilities() {
		return availabilities;
	}
	
	@JsonIgnore
	public String getAvailabilitiesAsString() {
		return stringListToString(availabilities);
	}
	
	public void setAvailabilities(List<String> availabilities) {
		this.availabilities = availabilities;
	}
	
	public List<String> getModelTypes() {
		return modelTypes;
	}
	
	@JsonIgnore
	public String getModelTypesAsString() {
		return stringListToString(modelTypes);
	}
	
	public void setModelTypes(List<String> modelTypes) {
		this.modelTypes = modelTypes;
	}
	
	public List<String> getPlatforms() {
		return platforms;
	}
	
	@JsonIgnore
	public String getPlatformsAsString() {
		return stringListToString(platforms);
	}
	
	public void setPlatforms(List<String> platforms) {
		this.platforms = platforms;
	}
	
	public List<String> getSupportedFunctionalities() {
		return supportedFunctionalities;
	}
	
	@JsonIgnore
	public String getSupportedFunctionalitiesAsString() {
		return stringListToString(supportedFunctionalities);
	}
	
	public void setSupportedFunctionalities(List<String> supportedFunctionalities) {
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
	
	private String stringListToString(List<String> list) {
		return Arrays.toString(list.toArray());
	}
}
