package de.uni_potsdam.hpi.bpt.resource_management;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentType;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseSetRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTTopic;

public class BPTExcelImporter {
	
	private static BPTExerciseSetRepository exerciseSetRepository = BPTExerciseSetRepository.getInstance();
	private static BPTExerciseRepository exerciseRepository = BPTExerciseRepository.getInstance();
	private static String contactMail = "";
	private static String contactName = "";
	
	public static int createExercisesFromExcelFile(InputStream fileInputStream, String user, ZipFile zipFile) {
		int numberOfExerciseSets = 0;
		try {
		    List<String> topicTags, modelingLanguageTags, taskTypeTags, otherTags, languages;
		    String set_id;
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
			HSSFSheet sheet = workbook.getSheetAt(0);
			HSSFRow row;
			int numberOfRows = sheet.getPhysicalNumberOfRows();
			
			System.out.println("Excel import - number of rows: " + numberOfRows);
			
			for(int i = 1; i < numberOfRows; i++) {
				languages = new ArrayList<String>();
				row = sheet.getRow(i);
				topicTags = splitAndTrimTags(row.getCell(0));
				modelingLanguageTags = splitAndTrimTags(row.getCell(1));
				taskTypeTags = splitAndTrimTags(row.getCell(2));
				otherTags = splitAndTrimTags(row.getCell(3));
//				System.out.println("The first topic is: " + BPTTopic.getValueOf(topicTags.get(0), "English"));
				set_id = exerciseSetRepository.nextAvailableSetId(BPTTopic.getValueOf(topicTags.get(0), "English"));
				while(i+1 < numberOfRows && sheet.getRow(i+1).getCell(0).getStringCellValue().isEmpty()){
					List<String> attachments = new ArrayList<String>();
					String url, pdf, doc;
					i++;
					row = sheet.getRow(i);
					if (i >= numberOfRows) {
						return numberOfExerciseSets;
					}
					languages.add(row.getCell(4).toString());
					url = copyFromCell(row.getCell(7));
					pdf = copyFromCell(row.getCell(8));
					doc = copyFromCell(row.getCell(9));
					attachments = splitAndTrimTags(row.getCell(10));
					
					String documentId = exerciseRepository.createDocument(generateDocument(new Object[] {
							// order of parameters MUST accord to the one given in BPTDocumentTypes.java
							set_id,
							row.getCell(5).toString(),
							row.getCell(4).toString(),
							row.getCell(6).toString(),
							url,
							attachments,
							pdf,
							doc
						}, BPTDocumentType.BPMAI_EXERCISES));
					
					if (!pdf.isEmpty()) {
						Map<String, Object> document = exerciseRepository.readDocument(documentId);
						String documentRevision = (String)document.get("_rev");
						InputStream pdfInputStream = zipFile.getInputStream(zipFile.getEntry(pdf));
						exerciseRepository.createAttachmentFromInputStream(documentId, documentRevision, pdf, pdfInputStream, "application/pdf");
					}
					if (!doc.isEmpty()) {
						Map<String, Object> document = exerciseRepository.readDocument(documentId);
						String documentRevision = (String)document.get("_rev");
						InputStream docInputStream = zipFile.getInputStream(zipFile.getEntry(doc));
						String docMimeType = doc.endsWith(".doc") ? "application/msword" : "application/octet-stream";
						exerciseRepository.createAttachmentFromInputStream(documentId, documentRevision, doc, docInputStream, docMimeType);
					}
					for(String attachement : attachments){
						Map<String, Object> document = exerciseRepository.readDocument(documentId);
						String documentRevision = (String)document.get("_rev");
						InputStream inputStream = zipFile.getInputStream(zipFile.getEntry(attachement));
						String mimeType;
						if (attachement.endsWith(".pdf")) {
							mimeType = "application/pdf";
						} else if (attachement.endsWith(".doc")) {
							mimeType = "application/msword";
						} else if (attachement.endsWith(".xls")) {
							mimeType = "application/vnd.ms-excel";
						} else {
							mimeType = "application/octet-stream";
						}
						exerciseRepository.createAttachmentFromInputStream(documentId, documentRevision, attachement, inputStream, mimeType);
					}
				}
				
				exerciseSetRepository.createDocument(generateDocument(new Object[] {
						// order of parameters MUST accord to the one given in BPTDocumentTypes.java
						set_id,
						languages,
						topicTags,
						modelingLanguageTags,
						taskTypeTags,
						otherTags,
						contactName,
						contactMail,
						user,
						new Date(),
						new Date(),
					}, BPTDocumentType.BPMAI_EXERCISE_SETS));
				
				numberOfExerciseSets++;
					
//				System.out.println("set_id: " + set_id);
//				System.out.println("topics: " + topicTags);
//				System.out.println("modelingLaguages" + modelingLanguageTags);
//				System.out.println("taskType" + taskTypeTags);
//				System.out.println("other" + otherTags);
//				System.out.println("languages: " + languages);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
			
		} catch (IOException e) {
			e.printStackTrace();
			return -2;
		}
		return numberOfExerciseSets;
	}
	
	private static String copyFromCell(HSSFCell cell) {
		return cell == null ? new String() : cell.getStringCellValue();
	}

	private static List<String> splitAndTrimTags(HSSFCell cell) {
		List<String> resultList = new ArrayList<String>();
		if (cell != null && !cell.getStringCellValue().isEmpty()) {
			resultList = Arrays.asList(cell.getStringCellValue().split(","));
			for (String tag : resultList) {
				tag.trim();
			}
		}
		return resultList;
	}

	private static Map<String, Object> generateDocument(Object[] values, BPTDocumentType type) {
		Map<String, Object> document = new HashMap<String, Object>();
		String[] keys = BPTDocumentType.getDocumentKeys(type);
		for(int i = 0; i < keys.length; i++) {
				document.put(keys[i], values[i]);
		}
		return document;
	}
}
