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

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentTypes;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseSetRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTUserRepository;

@FixMethodOrder(MethodSorters.JVM)
public class BPTDatabaseTest {
	
	private BPTExerciseSetRepository exerciseSetRepository;
	private BPTUserRepository userRepository;
	private Map<String, Object> exampleExerciseFromDatabase;
	private static int numberOfDocuments;
	private static String[] exerciseIdentifiers = new String[5];
	private static final String userId = "http://www.example.com/openid-user";	
	
	private final Object[] firstExercise = new Object[] {
		"3007",
		"Hospital Process",
		"en",
		"A hospital wants to establish a rating workflow for their doctors. To make the workflow reliable two different roles are assigned. The first one is a referee from the newly created quality assurance department while the second one represents the managing director of the hospital. Both roles execute all of their tasks independently from each other.",
		new ArrayList<String>(Arrays.asList("Process Modeling Languages")),
		new ArrayList<String>(Arrays.asList("BPMN Process")),
		new ArrayList<String>(Arrays.asList("Create models")),
		new ArrayList<String>(),
		"BPM Academic Initiative",
		"bpt-feedback@hpi.uni-potsdam.de", 
		userId,
		new Date(),
		new Date(),
		null
	};
	
	private final Object[] secondExercise = new Object[] {
		"1011",
		"Place/transition nets modeling",
		"en",
		"a) Expand the given place/transition net by incorporating the two pedestrian traffic lights F1 and F2. Each of these traffic lights should be represented by two places (one for the red light and one for the green light). The traffic light F1 (F2) should show the green light only as long as KFZ1 (KFZ2) shows the green light. Otherwise it should show the red light. <br/> b) Expand the given place/transition net by incorporating one pedestrian traffic light the signal of which applies to both directions of the corssroads. The net should be safe and fair. I.e. the pedestrian traffic light is only allowed to show the green light as long as the other two traffic lights show the red light. Furthermore, the three traffic lights should alternate in showing the green light. <br/> c) Consider a German traffic light. It is different to a Dutch traffic light as it has a fourth phase: After the red light is shown, the red and the yellow light are shown at the same time. <br/> <ul><li>Create a place/transition net that could behave like a German traffic light. The net should contain three places, one for each color. All state transitions should be supported.</li><li>Create a place/transition net that exactly behaves like a German traffic light. Make sure that all state transitions are correct.</li><li>Create the reachabiligy graph for the last net. Are there dead states?</li></ul>",
		new ArrayList<String>(Arrays.asList("Process Modeling Languages")),
		new ArrayList<String>(Arrays.asList("Petri Net")),
		new ArrayList<String>(Arrays.asList("Create models", "Analyze models", "Understand models")),
		new ArrayList<String>(),
		"BPM Academic Initiative",
		"bpt-feedback@hpi.uni-potsdam.de", 
		userId,
		new Date(),
		new Date(),
		null
	};
	
	private final Object[] thirdExercise = new Object[] {
		"1011",
		"Stellen-/Transitionsnetze",
		"de",
		"a.) Erstellen Sie ein <b>Petri-Netz</b>, das neben den beiden KFZ-Ampeln auch die beiden Fußgängerampeln F1 und F2 modelliert. Eine Fußgängerampel wird dabei durch zwei Stellen (für rot und grün) dargestellt. Die Fußgängerampel F1 (F2) soll genau dann grün anzeigen, wenn die KFZ-Ampel KFZ1 (KFZ2) grün zeigt, ansonsten rot. <br/> b.) Erstellen Sie ein Petri-Netz, das neben den beiden KFZ-Ampeln auch eine Fußgängerampel modelliert, deren Signal für alle Richtungen gleichzeitig gilt. Sie soll sicher und fair geschaltet sein. D.h. die Fußgängerampel darf nur dann grün zeigen, wenn beide KFZ-Ampeln rot zeigen, und die Grünphasen der drei Ampeln sollen sich reihum abwechseln. <br/> c.) Betrachten wir im Folgenden nur eine Ampel. Die deutsche Ampelschaltung unterscheidet sich von der niederländischen dadurch, dass es eine weitere Phase gibt. Sie folgt auf die Rotphase und in ihr leuchten das rote und das gelbe Signal gleichzeitig. <br/> <ul><li>i. Erstellen Sie ein Petri-Netz, das sich wie eine deutsche Ampelschaltung verhalten kann. Das Netz soll drei Stellen enthalten, die jeweils den Zustand des entsprechenden Signals (rot, gelb, grün) modellieren. Alle Zustandsübergänge sollen unterstützt werden.</li><li>ii. Erstellen Sie ein Petri-Netz, das sich genau wie eine deutsche Ampelschaltung verhält. Stellen Sie sicher, dass alle Zustandsübergänge korrekt sind.</li><li>iii. Geben Sie für die letzte Lösung den Erreichbarkeitsgraphen an. Gibt es Dead States?</li></ul>", 
		new ArrayList<String>(Arrays.asList("Prozessmodellierungssprachen")),
		new ArrayList<String>(Arrays.asList("Petri-Netz")),
		new ArrayList<String>(Arrays.asList("Modelle erstellen", "Modelle analysieren", "Modelle verstehen")),
		new ArrayList<String>(),
		"BPM Academic Initiative",
		"bpt-feedback@hpi.uni-potsdam.de", 
		userId,
		new Date(),
		new Date(),
		null
	};
	
	private final Object[] fourthExercise = new Object[] {
		"1111",
		"Business Process Intelligence",
		"de",
		"Erklären Sie die Rolle von Business Process Intelligence im Geschäftsprozessmanagement und erläutern Sie, wie in welchem Zusammenhang Key Performance Indicators dazu stehen.", 
		new ArrayList<String>(Arrays.asList("Methoden des BPM")),
		new ArrayList<String>(),
		new ArrayList<String>(),
		new ArrayList<String>(Arrays.asList("Key Performance Indicators")),
		"BPM Academic Initiative",
		"bpt-feedback@hpi.uni-potsdam.de", 
		userId,
		new Date(),
		new Date(),
		null
	};
	
	private final Object[] fifthExercise = new Object[] {
		"2069",
		"Verhaltenskompatibilität",
		"de",
		"<ul><li>Übersetzen Sie die Choreographie in Workflow Module.</li><li>Sind die interagierenden Workflow Module kompatibel?</li><li>Falls sie nicht kompatibel sind: Durch welche Änderungen könnte Kompatibilität hergestellt werden?</li></ul>",
		new ArrayList<String>(Arrays.asList("Prozessmodellierungssprachen")),
		new ArrayList<String>(Arrays.asList("Petri-Netz", "Workflow-Netz", "BPMN-Prozess")),
		new ArrayList<String>(Arrays.asList("Modelle erstellen", "Modelle analysieren", "Modelle verstehen")),
		new ArrayList<String>(),
		"BPM Academic Initiative",
		"bpt-feedback@hpi.uni-potsdam.de", 
		userId,
		new Date(),
		new Date(),
		null
	};
	
	public BPTDatabaseTest(){
		exerciseSetRepository = new BPTExerciseSetRepository();
		exerciseSetRepository.disableMailProvider();
		userRepository = new BPTUserRepository();
		numberOfDocuments = exerciseSetRepository.numberOfDocuments();
	}
	
	@Test(expected = DocumentNotFoundException.class)
	public void testDocumentNotFound() {
		exampleExerciseFromDatabase = exerciseSetRepository.readDocument("trololol");
	}
	
	@Test
	public void testCreateUser() {
		userRepository.isModerator(userId, "Example User", "mail@example.org");
	}
	
	@Test
	public void testCreateDocument() {
		exerciseIdentifiers[0] = exerciseSetRepository.createDocument(generateDocument(firstExercise));
		exerciseIdentifiers[1] = exerciseSetRepository.createDocument(generateDocument(secondExercise));
		assertEquals(numberOfDocuments + 2, exerciseSetRepository.numberOfDocuments());
		exerciseIdentifiers[2] = exerciseSetRepository.createDocument(generateDocument(thirdExercise));
		exerciseIdentifiers[3] = exerciseSetRepository.createDocument(generateDocument(fourthExercise));
		exerciseIdentifiers[4] = exerciseSetRepository.createDocument(generateDocument(fifthExercise));
	}
	
	private Map<String, Object> generateDocument(Object[] exercise) {
		Map<String, Object> document = new HashMap<String, Object>();
		String[] keys = BPTDocumentTypes.getDocumentKeys("bpmai_exercises");
		for (int i = 0; i < keys.length; i++) {
			document.put(keys[i], exercise[i]);
		}
		return document;
	}

	@Test
	public void testUpdateDocument() {
		exampleExerciseFromDatabase = exerciseSetRepository.readDocument(exerciseIdentifiers[4]);
		exampleExerciseFromDatabase.put("topics", new ArrayList<String>(Arrays.asList("Prozessmodellierungssprachen", "Prozesschoreographien")));
		exerciseSetRepository.updateDocument(exampleExerciseFromDatabase);
	}
	
	@Test
	public void testDeleteDocument() {
		for (String _id : exerciseIdentifiers) {
			exampleExerciseFromDatabase = exerciseSetRepository.readDocument(_id);
			exerciseSetRepository.deleteDocument(_id);
		}
		assertEquals(numberOfDocuments - 5, exerciseSetRepository.numberOfDocuments());
	}
	
	@Test
	public void testDeleteUser() {
		userRepository.deleteDocument(userId);
	}

}
