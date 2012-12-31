package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

public class BPTDatabase {

	public static void main(String[] args) {
		
		HttpClient httpClient = new StdHttpClient.Builder()
									.host("localhost")
									.port(5984)
								// 	.username("")
								// 	.password("")
									.build();
		
		CouchDbInstance databaseInstance = new StdCouchDbInstance(httpClient);
		CouchDbConnector database = new StdCouchDbConnector("bpt_resources", databaseInstance);
		
		database.createDatabaseIfNotExists();
		
		/* example follows */
		
		try {
			BPTTool exampleToolFromDatabase = database.get(BPTTool.class, "ProM");
			exampleToolFromDatabase.setDownloadURL("http://www.promtools.org");
			database.update(exampleToolFromDatabase);
		} catch (DocumentNotFoundException e) {
			BPTTool exampleTool = generateExampleTool();
			database.create(exampleTool);
		}
		
	}

	private static BPTTool generateExampleTool() {
		
		BPTTool exampleTool = new BPTTool();
		exampleTool.setName("ProM");
		exampleTool.setDescription("<b>ProM</b> is an <b>extensible</b> framework that supports a wide variety of process mining techniques in the form of plug-ins. It is <u>platform independent</u> as it is implemented in Java, and can be downloaded <u>free of charge</u>.");
		exampleTool.setProvider("Eindhoven University of Technology");
		exampleTool.setDocumentationURL("http://www.promtools.org/prom6/pack-docs.html");
		exampleTool.setAvailability(new ArrayList<String>(Arrays.asList("open source", "freeware")));
		exampleTool.setModelTypes(new ArrayList<String>(Arrays.asList("BPMN", "EPC", "Petri net")));
		exampleTool.setPlatforms(new ArrayList<String>(Arrays.asList("Windows", "Linux", "Mac OSX")));
		exampleTool.setSupportedFunctionalities(new ArrayList<String>(Arrays.asList("verification of model properties", "process discovery based on event data", "conformance checking based on event data")));
		exampleTool.setContactName("Eric Verbeek");
		exampleTool.setContactMail("h.m.w.verbeek@tunnel"); // invalid -> must not be included in the document later
		exampleTool.setDateCreated(new Date());
		exampleTool.setLastUpdate(new Date());
		return exampleTool;
		
	}
}
