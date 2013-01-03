package de.uni_potsdam.hpi.bpt.resource_management.ektorp.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
	private BPTTool exampleToolFromDatabase;
	private int numberOfDocuments;
	
	public BPTDatabaseTest(){
		database = BPTDatabase.connect();
		repository = new BPTToolRepository(database);
		numberOfDocuments = repository.getNumberOfDocuments();
	}
	
	@Test(expected = DocumentNotFoundException.class)
	public void testDocumentNotFound() {
		exampleToolFromDatabase = database.get(BPTTool.class, "ProM");
	}
	
	@Test
	public void testCreateDocument() {
		database.create(generateExampleToolOne());
		database.create(generateExampleToolTwo());
		assertEquals(repository.getNumberOfDocuments(), numberOfDocuments + 2);
		database.create(generateExampleToolThree());
		database.create(generateExampleToolFour());
		database.create(generateExampleToolFive());
		numberOfDocuments = repository.getNumberOfDocuments();
	}
	
	@Test
	public void testUpdateDocument() {
		exampleToolFromDatabase = database.get(BPTTool.class, "ProM");
		exampleToolFromDatabase.setDownloadURL("http://www.promtools.org");
		database.update(exampleToolFromDatabase);
	}
	
	@Test
	public void testDeleteDocument() {
		String[] toolNames = new String[] {"ProM", "Activiti", "Signavio Process Editor", "Yaoqiang BPMN Editor", "WebSphere Business Modeler Advanced"};
		for (String name : toolNames) {
			exampleToolFromDatabase = database.get(BPTTool.class, name);
			database.delete(exampleToolFromDatabase);
		}
		assertEquals(repository.getNumberOfDocuments(), numberOfDocuments - 5);
	}
	
	private BPTTool generateExampleToolOne() {
		BPTTool exampleTool = new BPTTool();
		exampleTool.setName("ProM");
		exampleTool.setDescription("<b>ProM</b> is an <b>extensible</b> framework that supports a wide variety of process mining techniques in the form of plug-ins. It is <u>platform independent</u> as it is implemented in Java, and can be downloaded <u>free of charge</u>.");
		exampleTool.setProvider("Eindhoven University of Technology");
		exampleTool.setDocumentationURL("http://www.promtools.org/prom6/pack-docs.html");
		exampleTool.setAvailabilities(new HashSet<String>(Arrays.asList("open source", "freeware")));
		exampleTool.setModelTypes(new HashSet<String>(Arrays.asList("BPMN", "EPC", "Petri Net")));
		exampleTool.setPlatforms(new HashSet<String>(Arrays.asList("Windows", "Linux", "Mac OSX")));
		exampleTool.setSupportedFunctionalities(new HashSet<String>(Arrays.asList("verification of model properties", "process discovery based on event data", "conformance checking based on event data")));
		exampleTool.setContactName("Eric Verbeek");
		exampleTool.setContactMail("h.m.w.verbeek@tunnel"); // invalid -> must not be included in the document later
		exampleTool.setDateCreated(new Date());
		exampleTool.setLastUpdate(new Date());
		return exampleTool;
	}
	
	private BPTTool generateExampleToolTwo() {
		BPTTool exampleTool = new BPTTool();
		exampleTool.setName("Activiti");
		exampleTool.setDescription("<b>Activiti</b> is an open-source workflow engine written in Java that can execute business processes described in BPMN 2.0.");
		exampleTool.setProvider("Alfresco");
		exampleTool.setDownloadURL("http://www.activiti.org/download.html");
		exampleTool.setScreencastURL("http://www.activiti.org/screenshots.html");
		exampleTool.setAvailabilities(new HashSet<String>(Arrays.asList("open source")));
		exampleTool.setModelTypes(new HashSet<String>(Arrays.asList("BPMN")));
		exampleTool.setPlatforms(new HashSet<String>(Arrays.asList("Windows", "Linux", "Mac OSX")));
		exampleTool.setSupportedFunctionalities(new HashSet<String>(Arrays.asList("graphical model editor", "model repository", "process engine")));
		exampleTool.setContactName("Tijs Rademakers");
		exampleTool.setDateCreated(new Date());
		exampleTool.setLastUpdate(new Date());
		return exampleTool;
	}
	
	private BPTTool generateExampleToolThree() {
		BPTTool exampleTool = new BPTTool();
		exampleTool.setName("Signavio Process Editor");
		exampleTool.setProvider("Signavio GmbH");
		exampleTool.setDocumentationURL("http://www.signavio.com/");
		exampleTool.setAvailabilities(new HashSet<String>(Arrays.asList("free for academics", "commercial")));
		exampleTool.setModelTypes(new HashSet<String>(Arrays.asList("BPMN", "EPC", "Petri Net", "UML Activity Diagram", "Workflow Net")));
		exampleTool.setPlatforms(new HashSet<String>());
		exampleTool.setSupportedFunctionalities(new HashSet<String>(Arrays.asList("graphical model editor", "model repository", "verification of model properties")));
		exampleTool.setContactName("Signavio GmbH");
		exampleTool.setContactMail("info@signavio.com");
		exampleTool.setDateCreated(new Date());
		exampleTool.setLastUpdate(new Date());
		return exampleTool;
	}
	
	private BPTTool generateExampleToolFour() {
		BPTTool exampleTool = new BPTTool();
		exampleTool.setName("Yaoqiang BPMN Editor");
		exampleTool.setDescription("Yaoqiang BPMN Editor is a graphical editor for business process diagrams, compliant with OMG specifications (BPMN 2.0).");
		exampleTool.setDownloadURL("http://sourceforge.net/projects/bpmn/");
		exampleTool.setDocumentationURL("http://sourceforge.net/projects/bpmn/");
		exampleTool.setAvailabilities(new HashSet<String>(Arrays.asList("open source")));
		exampleTool.setModelTypes(new HashSet<String>(Arrays.asList("BPMN")));
		exampleTool.setPlatforms(new HashSet<String>(Arrays.asList("Windows")));
		exampleTool.setSupportedFunctionalities(new HashSet<String>(Arrays.asList("graphical model editor")));
		exampleTool.setContactName("blenta");
		exampleTool.setContactMail("shi_yaoqiang@yahoo.com");
		exampleTool.setDateCreated(new Date());
		exampleTool.setLastUpdate(new Date());
		return exampleTool;
	}
	
	private BPTTool generateExampleToolFive() {
		BPTTool exampleTool = new BPTTool();
		exampleTool.setName("WebSphere Business Modeler Advanced");
		exampleTool.setDescription("<b>IBM WebSphere(R) Business Modeler Advanced Version 7 is IBM's premier advanced business process modeling and analysis tool for business users.It offers process modeling, simulation, and analysis capabilities to help business users understand, document, and deploy business processes for continuous improvement.</b><br><br><ul><li>Enables business users to design, model, and deploy vital business processes</li><li>Allows users to make informed decisions before deployment through advanced simulation capabilities based on modeled and actual data</li><li>Provides integrated industry content to help business users jumpstart solution development</li><li>Accelerates process optimization by allowing users to visualize and identify bottlenecks and inefficiencies in processes</li><li>Provides enhanced integration with the IBM BPM Suite and WebSphere Dynamic Process Edition through role-based business spaces, a unified end user interface that integrates BPM content for a holistic management of business processes</li><li>Enables subject matter experts to share models and collaborate to translate business intent into process models using a Web browser with WebSphere Business Compass</li></ul>");
		exampleTool.setDocumentationURL("http://www-01.ibm.com/software/integration/wbimodeler/advanced/");
		exampleTool.setAvailabilities(new HashSet<String>(Arrays.asList("commercial")));
		exampleTool.setModelTypes(new HashSet<String>(Arrays.asList("BPMN")));
		exampleTool.setPlatforms(new HashSet<String>(Arrays.asList("Windows")));
		exampleTool.setSupportedFunctionalities(new HashSet<String>(Arrays.asList("graphical model editor", "model repository")));
		exampleTool.setContactName("IBM");
		exampleTool.setDateCreated(new Date());
		exampleTool.setLastUpdate(new Date());
		return exampleTool;
	}

}
