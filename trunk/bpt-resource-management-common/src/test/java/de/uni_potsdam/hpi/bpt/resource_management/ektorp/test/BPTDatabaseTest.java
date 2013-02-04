package de.uni_potsdam.hpi.bpt.resource_management.ektorp.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.ektorp.DocumentNotFoundException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentTypes;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;

@FixMethodOrder(MethodSorters.JVM)
public class BPTDatabaseTest {
	
	private BPTToolRepository repository;
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
		new ArrayList<String>(Arrays.asList("open source", "freeware")),
		new ArrayList<String>(Arrays.asList("BPMN", "EPC", "Petri Net")),
		new ArrayList<String>(Arrays.asList("Windows", "Linux", "Mac OSX")),
		new ArrayList<String>(Arrays.asList("verification of model properties", "process discovery based on event data", "conformance checking based on event data")),
		"Eric Verbeek",
		"h.m.w.verbeek@tunnel", 
		new String(),
		new Date(),
		new Date(),
	};
	private final Object[] secondTool = new Object[] {
		"Activiti",
		"<b>Activiti</b> is an open-source workflow engine written in Java that can execute business processes described in BPMN 2.0.",
		"Alfresco",
		"http://www.activiti.org/download.html",
		"http://www.example.org",
		"http://www.activiti.org/screenshots.html",
		new ArrayList<String>(Arrays.asList("open source")),
		new ArrayList<String>(Arrays.asList("BPMN")),
		new ArrayList<String>(Arrays.asList("Windows", "Linux", "Mac OSX")),
		new ArrayList<String>(Arrays.asList("graphical model editor", "model repository", "process engine")),
		"Tijs Rademakers",
		"test@example.org",
		new String(),
		new Date(),
		new Date(),
	};
	private final Object[] thirdTool = new Object[] {
		"Signavio Process Editor",
		"",
		"Signavio GmbH",
		"http://www.example.org",
		"http://www.signavio.com/",
		"http://www.example.org",
		new ArrayList<String>(Arrays.asList("free for academics", "commercial")),
		new ArrayList<String>(Arrays.asList("BPMN", "EPC", "Petri Net", "UML Activity Diagram", "Workflow Net")),
		new ArrayList<String>(Arrays.asList("SaaS")),
		new ArrayList<String>(Arrays.asList("graphical model editor", "model repository", "verification of model properties")),
		"Signavio GmbH",
		"info@signavio.com", 
		new String(),
		new Date(),
		new Date(),
	};
	private final Object[] fourthTool = new Object[] {
		"Yaoqiang BPMN Editor",
		"Yaoqiang BPMN Editor is a graphical editor for business process diagrams, compliant with OMG specifications (BPMN 2.0).",
		"",
		"http://sourceforge.net/projects/bpmn/",
		"http://sourceforge.net/projects/bpmn/",
		"http://www.example.org",
		new ArrayList<String>(Arrays.asList("open source")),
		new ArrayList<String>(Arrays.asList("BPMN")),
		new ArrayList<String>(Arrays.asList("Windows")),
		new ArrayList<String>(Arrays.asList("graphical model editor")),
		"blenta",
		"shi_yaoqiang@yahoo.com", 
		new String(),
		new Date(),
		new Date(),
	};
	
	private final Object[] fifthTool = new Object[] {
		"WebSphere Business Modeler Advanced",
		"<b>IBM WebSphere(R) Business Modeler Advanced Version 7 is IBM's premier advanced business process modeling and analysis tool for business users.It offers process modeling, simulation, and analysis capabilities to help business users understand, document, and deploy business processes for continuous improvement.</b><br><br><ul><li>Enables business users to design, model, and deploy vital business processes</li><li>Allows users to make informed decisions before deployment through advanced simulation capabilities based on modeled and actual data</li><li>Provides integrated industry content to help business users jumpstart solution development</li><li>Accelerates process optimization by allowing users to visualize and identify bottlenecks and inefficiencies in processes</li><li>Provides enhanced integration with the IBM BPM Suite and WebSphere Dynamic Process Edition through role-based business spaces, a unified end user interface that integrates BPM content for a holistic management of business processes</li><li>Enables subject matter experts to share models and collaborate to translate business intent into process models using a Web browser with WebSphere Business Compass</li></ul>",
		"",
		"http://www.example.org",
		"http://www-01.ibm.com/software/integration/wbimodeler/advanced/",
		"http://www.example.org",
		new ArrayList<String>(Arrays.asList("commercial")),
		new ArrayList<String>(Arrays.asList("BPMN")),
		new ArrayList<String>(Arrays.asList("Windows")),
		new ArrayList<String>(Arrays.asList("graphical model editor", "model repository")),
		"IBM",
		"test@example.org",
		new String(),
		new Date(),
		new Date(),
	};
	
	public BPTDatabaseTest(){
		repository = new BPTToolRepository();
		numberOfDocuments = repository.numberOfDocuments();
	}
	
	@Test(expected = DocumentNotFoundException.class)
	public void testDocumentNotFound() {
		exampleToolFromDatabase = repository.readDocument("trololol");
	}
	
	@Test
	public void testCreateDocument() {
		toolIdentifiers[0] = repository.createDocument(generateDocument(firstTool));
		toolIdentifiers[1] = repository.createDocument(generateDocument(secondTool));
		assertEquals(numberOfDocuments + 2, repository.numberOfDocuments());
		toolIdentifiers[2] = repository.createDocument(generateDocument(thirdTool));
		toolIdentifiers[3] = repository.createDocument(generateDocument(fourthTool));
		toolIdentifiers[4] = repository.createDocument(generateDocument(fifthTool));
	}
	
	private Map<String, Object> generateDocument(Object[] tool) {
		Map<String, Object> document = new HashMap<String, Object>();
		String[] keys = BPTDocumentTypes.getDocumentKeys("bpt_resources_tools");
		for (int i = 0; i < keys.length; i++) {
			document.put(keys[i], tool[i]);
		}
		return document;
	}

	@Test
	public void testUpdateDocument() {
		exampleToolFromDatabase = repository.readDocument(toolIdentifiers[0]);
		exampleToolFromDatabase.put("download_url", "http://www.promtools.org");
		repository.updateDocument(exampleToolFromDatabase);
	}
	
	@Test
	public void testDeleteDocument() {
		for (String _id : toolIdentifiers) {
			exampleToolFromDatabase = repository.readDocument(_id);
			repository.deleteDocument(_id);
		}
		assertEquals(numberOfDocuments - 5, repository.numberOfDocuments());
	}

}
