package de.uni_potsdam.hpi.bpt.resource_management;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;

@SuppressWarnings("rawtypes")
public class BPTExporter {
	
	static class ValueComparator implements Comparator<String> {
		Map<String, Integer> base;
		
		ValueComparator(Map<String, Integer> base) {
			this.base = base;
		}
		
		public int compare(String a, String b) {
			Integer x = base.get(a);
			Integer y = base.get(b);
			if (x.equals(y)) {
				return a.compareTo(b);
			}
			return x.compareTo(y);
		}
    }
	
	public File generateStatisticsForPublishedTools() {
		File file = new File(BPTUtils.getTempFolder() + System.getProperty("file.separator") + "statistics.csv");
		Map<String, String> attributesAndColumns = new HashMap<String, String>();
		attributesAndColumns.put("availabilities", "Availability");
		attributesAndColumns.put("model_types", "Model type");
		attributesAndColumns.put("platforms", "Platform");
		attributesAndColumns.put("supported_functionalities", "Supported functionality");
		Map<String, Integer> counts;
		ValueComparator vc;
		TreeMap<String, Integer> sorted;
		try {
			FileWriter writer = new FileWriter(file, false);
			for (String attribute : attributesAndColumns.keySet()) {

				writer.write(attributesAndColumns.get(attribute) + ";Count;");
				writer.write(System.getProperty("line.separator"));
				counts = BPTToolRepository.getInstance().getTagStatisticFor(attribute);
				vc = new ValueComparator(counts);
		        sorted = new TreeMap<String, Integer>(vc);
		        sorted.putAll(counts);
				for (String tag : sorted.descendingKeySet()) {
					writer.write(tag + ";" + counts.get(tag) + ";");
					writer.write(System.getProperty("line.separator"));
				}
				writer.write(System.getProperty("line.separator"));
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public File generateFileWithTools(List<Map> tools) {
		File file = new File(BPTUtils.getTempFolder() + System.getProperty("file.separator") + "tools.csv");
		try {
			FileWriter writer = new FileWriter(file, false);
			List<String> attributeNames = Arrays.asList("_id", "name", "description", "description_url", "provider", "provider_url", "download_url", "documentation_url", "availabilities", "model_types", "platforms", "supported_functionalities", "contact_name", "contact_mail", "last_update");
			List<String> columnNames = Arrays.asList("ID", "Name", "Description", "URL of description", "Provider", "URL of provider", "URL of download", "URL of documentation", "Availability", "Model type", "Platform", "Supported functionality", "Contact name", "Contact mail", "Last update");
			for (String columnName : columnNames) {
				writer.write(columnName + ";");
			}
			for (Map tool : tools) {
				writer.write(System.getProperty("line.separator"));
				for (String attributeName : attributeNames) {
					Object content = tool.get(attributeName);
					if (content instanceof List || content instanceof ArrayList) {
						writer.write(content.toString().replace("[", "").replace("]", "").replaceAll(";", ",") + ";");
					} else {
						writer.write(content.toString().replaceAll(";", ",").replaceAll("(\r\n|\n)", " ") + ";");
					}
					
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
}
