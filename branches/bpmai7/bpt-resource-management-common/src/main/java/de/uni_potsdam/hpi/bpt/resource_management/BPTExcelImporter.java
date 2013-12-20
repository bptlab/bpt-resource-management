package de.uni_potsdam.hpi.bpt.resource_management;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import org.apache.http.impl.client.cache.FileResource;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentType;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseSetRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTTopic;

public class BPTExcelImporter {

	private static int numberOfCellsForSet = 5;
	private static int numberOfCellsForEntry = 5;
	private static BPTExerciseSetRepository exerciseSetRepository = BPTExerciseSetRepository.getInstance();
	private static BPTExerciseRepository exerciseRepository = BPTExerciseRepository.getInstance();
	private static String contactMail = "";
	private static String contactName = "";
	
	public static void createUploadsFromExcelFile(InputStream fileInputStream, String user, ZipFile zipFile){
		try {
//			POIFSFileSystem fs = new POIFSFileSystem(fileInputStream);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
			HSSFSheet sheet = workbook.getSheetAt(0);
			HSSFRow row;
		    String set_id;
		    List<String> topicTags, modelingLanguageTags, taskTypeTags, otherTags, languages;
			int rows = sheet.getPhysicalNumberOfRows();
			
			System.out.println("numberofrows: " + rows);
			
			for(int i = 0; i < rows; i++){
				row = sheet.getRow(i);
				topicTags = Arrays.asList(row.getCell(0).toString().split(","));
				modelingLanguageTags = Arrays.asList(row.getCell(1).toString().split(","));
				taskTypeTags = Arrays.asList(row.getCell(2).toString().split(","));
				otherTags = Arrays.asList(row.getCell(3).toString().split(","));
				set_id = exerciseSetRepository.nextAvailableSetId(BPTTopic.valueOf(topicTags.get(0)));
				languages = new ArrayList<String>();
				while(i+1 < rows && sheet.getRow(i+1).getCell(0) == null){
					i++;
					row = sheet.getRow(i);
					if(i >= rows){
						return;
					}
					languages.add(row.getCell(4).toString());
					List<String> attachements;
					String pdf, doc, url;
					
					if(row.getCell(7) == null){
						url = new String();
					}
					else{
						url = row.getCell(7).toString();
					}
					
					if(row.getCell(8) == null){
						pdf = new String();
					}
					else{
						pdf = row.getCell(8).toString();
					}
					
					if(row.getCell(9) == null){
						doc = new String();
					}
					else{
						doc = row.getCell(9).toString();
					}
					
					if(row.getCell(10) == null){
						attachements = new ArrayList<String>();
					}
					else{
						attachements = Arrays.asList(row.getCell(10).toString().split(","));
					}
					String documentId = exerciseRepository.createDocument(generateDocument(new Object[] {
							// order of parameters MUST accord to the one given in BPTDocumentTypes.java
							set_id,
							row.getCell(5).toString(),
							row.getCell(4).toString(),
							row.getCell(6).toString(),
							url,
							attachements,
							pdf,
							doc
						}, BPTDocumentType.BPMAI_EXERCISES));
					
					Map<String, Object> document = exerciseRepository.readDocument(documentId);
					String documentRevision = (String)document.get("_rev");
					System.out.println("pdf: " + pdf);
					System.out.println("getEntry: " + zipFile.getEntry(pdf));
					
					if(!pdf.isEmpty()){
						InputStream pdfInputStream = zipFile.getInputStream(zipFile.getEntry(pdf));
						exerciseRepository.createAttachmentFromInputStream(documentId, documentRevision, pdf, pdfInputStream, "application/pdf");
					}
					if(!doc.isEmpty()){
						InputStream docInputStream = zipFile.getInputStream(zipFile.getEntry(doc));
						String docMimeType;
						if(doc.endsWith("x")){
							docMimeType = "application/octet-stream";
						}
						else{
							docMimeType = "application/msword";
						}
						exerciseRepository.createAttachmentFromInputStream(documentId, documentRevision, doc, docInputStream, docMimeType);
					}
					for(String attachement : attachements){
						InputStream inputStream = zipFile.getInputStream(zipFile.getEntry(attachement));
						String mimeType;
						if(attachement.endsWith("pdf")){
							mimeType = "application/pdf";
						}
						else if(attachement.endsWith("doc")){
							mimeType = "application/msword";
						}
						else if(attachement.endsWith("xls")){
							mimeType = "application/vnd.ms-excel";
						}
						else{
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
					
				System.out.println("set_id: " + set_id);
				System.out.println("topics: " + topicTags);
				System.out.println("modelingLaguages" + modelingLanguageTags);
				System.out.println("taskType" + taskTypeTags);
				System.out.println("other" + otherTags);
				System.out.println("languages: " + languages);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
