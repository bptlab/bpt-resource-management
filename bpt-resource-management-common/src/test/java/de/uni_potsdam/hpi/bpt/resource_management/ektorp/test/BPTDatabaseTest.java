package de.uni_potsdam.hpi.bpt.resource_management.ektorp.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDatabase;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentRepository;

@FixMethodOrder(MethodSorters.JVM)
public class BPTDatabaseTest {
	
	private CouchDbConnector database;
	private BPTDocumentRepository repository;
	private Map<String, Object> exampleToolFromDatabase;
	private static int numberOfDocuments;
	private static String[] toolIdentifiers = new String[5];
	
	private final Object[] firstTool = new Object[] {
		"ProM",
		"<b>ProM</b> is an <b>extensible</b> framework that supports a wide variety of process mining techniques in the form of plug-ins. It is <u>platform independent</u> as it is implemented in Java, and can be downloaded <u>free of charge</u>.",
		"Eindhoven University of Technology",
		"http://www.example.org",
		"http://www.promtools.org/prom6/pack-docs.html",
		"http://www.example.org",
		new HashSet<String>(Arrays.asList("open source", "freeware")),
		new HashSet<String>(Arrays.asList("BPMN", "EPC", "Petri Net")),
		new HashSet<String>(Arrays.asList("Windows", "Linux", "Mac OSX")),
		new HashSet<String>(Arrays.asList("verification of model properties", "process discovery based on event data", "conformance checking based on event data")),
		"Eric Verbeek",
		"h.m.w.verbeek@tunnel"
	};
	private final Object[] secondTool = new Object[] {
		"Activiti",
		"<b>Activiti</b> is an open-source workflow engine written in Java that can execute business processes described in BPMN 2.0.",
		"Alfresco",
		"http://www.activiti.org/download.html",
		"http://www.example.org",
		"http://www.activiti.org/screenshots.html",
		new HashSet<String>(Arrays.asList("open source")),
		new HashSet<String>(Arrays.asList("BPMN")),
		new HashSet<String>(Arrays.asList("Windows", "Linux", "Mac OSX")),
		new HashSet<String>(Arrays.asList("graphical model editor", "model repository", "process engine")),
		"Tijs Rademakers",
		"test@example.org"
	};
	private final Object[] thirdTool = new Object[] {
		"Signavio Process Editor",
		"",
		"Signavio GmbH",
		"http://www.example.org",
		"http://www.signavio.com/",
		"http://www.example.org",
		new HashSet<String>(Arrays.asList("free for academics", "commercial")),
		new HashSet<String>(Arrays.asList("BPMN", "EPC", "Petri Net", "UML Activity Diagram", "Workflow Net")),
		new HashSet<String>(),
		new HashSet<String>(Arrays.asList("graphical model editor", "model repository", "verification of model properties")),
		"Signavio GmbH",
		"info@signavio.com"
	};
	private final Object[] fourthTool = new Object[] {
		"Yaoqiang BPMN Editor",
		"Yaoqiang BPMN Editor is a graphical editor for business process diagrams, compliant with OMG specifications (BPMN 2.0).",
		"",
		"http://sourceforge.net/projects/bpmn/",
		"http://sourceforge.net/projects/bpmn/",
		"http://www.example.org",
		new HashSet<String>(Arrays.asList("open source")),
		new HashSet<String>(Arrays.asList("BPMN")),
		new HashSet<String>(Arrays.asList("Windows")),
		new HashSet<String>(Arrays.asList("graphical model editor")),
		"blenta",
		"shi_yaoqiang@yahoo.com"
	};
	
	private final Object[] fifthTool = new Object[] {
		"WebSphere Business Modeler Advanced",
		"<b>IBM WebSphere(R) Business Modeler Advanced Version 7 is IBM's premier advanced business process modeling and analysis tool for business users.It offers process modeling, simulation, and analysis capabilities to help business users understand, document, and deploy business processes for continuous improvement.</b><br><br><ul><li>Enables business users to design, model, and deploy vital business processes</li><li>Allows users to make informed decisions before deployment through advanced simulation capabilities based on modeled and actual data</li><li>Provides integrated industry content to help business users jumpstart solution development</li><li>Accelerates process optimization by allowing users to visualize and identify bottlenecks and inefficiencies in processes</li><li>Provides enhanced integration with the IBM BPM Suite and WebSphere Dynamic Process Edition through role-based business spaces, a unified end user interface that integrates BPM content for a holistic management of business processes</li><li>Enables subject matter experts to share models and collaborate to translate business intent into process models using a Web browser with WebSphere Business Compass</li></ul>",
		"",
		"http://www.example.org",
		"http://www-01.ibm.com/software/integration/wbimodeler/advanced/",
		"http://www.example.org",
		new HashSet<String>(Arrays.asList("commercial")),
		new HashSet<String>(Arrays.asList("BPMN")),
		new HashSet<String>(Arrays.asList("Windows")),
		new HashSet<String>(Arrays.asList("graphical model editor", "model repository")),
		"IBM",
		"test@example.org"
	};
	
	public BPTDatabaseTest(){
		database = BPTDatabase.connect("bpt_resources");
		repository = new BPTDocumentRepository(database);
		numberOfDocuments = repository.numberOfDocuments();
	}
	
	@Test(expected = DocumentNotFoundException.class)
	public void testDocumentNotFound() {
		exampleToolFromDatabase = database.get(Map.class, "trololol");
	}
	
	@Test
	public void testCreateDocument() {
		toolIdentifiers[0] = repository.createDocument("BPTTool", new ArrayList<Object>(Arrays.asList(firstTool)));
		toolIdentifiers[1] = repository.createDocument("BPTTool", new ArrayList<Object>(Arrays.asList(secondTool)));
		assertEquals(repository.numberOfDocuments(), numberOfDocuments + 2);
		toolIdentifiers[2] = repository.createDocument("BPTTool", new ArrayList<Object>(Arrays.asList(thirdTool)));
		toolIdentifiers[3] = repository.createDocument("BPTTool", new ArrayList<Object>(Arrays.asList(fourthTool)));
		toolIdentifiers[4] = repository.createDocument("BPTTool", new ArrayList<Object>(Arrays.asList(fifthTool)));
	}
	
	@Test
	public void testUpdateDocument() {
		exampleToolFromDatabase = database.get(Map.class, toolIdentifiers[0]);
		exampleToolFromDatabase.put("download_url", "http://www.promtools.org");
		database.update(exampleToolFromDatabase);
	}
	
	@Test
	public void testDeleteDocument() {
		for (String id : toolIdentifiers) {
			exampleToolFromDatabase = database.get(Map.class, id);
			database.delete(exampleToolFromDatabase);
		}
		assertEquals(repository.numberOfDocuments(), numberOfDocuments - 5);
	}

}
