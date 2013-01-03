package de.uni_potsdam.hpi.bpt.resource_management.ektorp.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDatabase;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTTool;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;

@FixMethodOrder(MethodSorters.JVM)
public class BPTDatabaseTest {
	
	private CouchDbConnector database;
	private BPTToolRepository repository;
	private BPTTool exampleToolCreated;
	private BPTTool exampleToolFromDatabase;
	private int numberOfDocuments;

	@Before
	public void setUp() throws Exception {
		database = BPTDatabase.connect();
		repository = new BPTToolRepository(database);
		numberOfDocuments = 0; // repository.getNumberOfDocuments();
	}
	
	@Test(expected = DocumentNotFoundException.class)
	public void testDocumentNotFound() {
		exampleToolFromDatabase = database.get(BPTTool.class, "ProM");
	}
	
	@Test
	public void testCreateDocument() {
		exampleToolCreated = generateExampleTool();
		database.create(exampleToolCreated);
		assertEquals(repository.getNumberOfDocuments(), numberOfDocuments + 1);
	}
	
	@Test
	public void testUpdateDocument() {
		exampleToolFromDatabase = database.get(BPTTool.class, "ProM");
		exampleToolFromDatabase.setDownloadURL("http://www.promtools.org");
		database.update(exampleToolFromDatabase);
	}
	
	@Test
	public void testDeleteDocument() {
		exampleToolFromDatabase = database.get(BPTTool.class, "ProM");
		database.delete(exampleToolFromDatabase);
		assertEquals(repository.getNumberOfDocuments(), numberOfDocuments);
	}
	
	private static BPTTool generateExampleTool() {
		BPTTool exampleTool = new BPTTool();
		exampleTool.setName("ProM");
		exampleTool.setDescription("<b>ProM</b> is an <b>extensible</b> framework that supports a wide variety of process mining techniques in the form of plug-ins. It is <u>platform independent</u> as it is implemented in Java, and can be downloaded <u>free of charge</u>.");
		exampleTool.setProvider("Eindhoven University of Technology");
		exampleTool.setDocumentationURL("http://www.promtools.org/prom6/pack-docs.html");
		exampleTool.setAvailabilities(new ArrayList<String>(Arrays.asList("open source", "freeware")));
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
