package de.uni_potsdam.hpi.bpt.resource_management.vaadin.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.ui.Component;

public class BPTVaadinResources {
	
	private static List<Object[]> bptTools = new ArrayList<Object[]>() {
	    { 
	    	add(new Object[] {"_id", "ID", String.class, BPTPropertyValueType.IGNORE});
	    	add(new Object[] {"name", "Name", String.class, BPTPropertyValueType.IGNORE});
	    	add(new Object[] {"description", "Description", String.class, BPTPropertyValueType.IGNORE});
	    	add(new Object[] {"provider", "Provider", String.class, BPTPropertyValueType.IGNORE});
	    	add(new Object[] {"download_url", "Download", Component.class, BPTPropertyValueType.LINK});
	    	add(new Object[] {"documentation_url", "Documentation", Component.class, BPTPropertyValueType.LINK});
	    	add(new Object[] {"screencast_url", "Screencast", Component.class, BPTPropertyValueType.LINK});
	    	add(new Object[] {"availabilities", "Availability", String.class, BPTPropertyValueType.SET});
	    	add(new Object[] {"model_types", "Model type", String.class, BPTPropertyValueType.SET});
	    	add(new Object[] {"platforms", "Platform", String.class, BPTPropertyValueType.SET});
	    	add(new Object[] {"supported_functionalities", "Supported functionality", String.class, BPTPropertyValueType.SET});
	    	add(new Object[] {"contact_name", "Contact name", String.class, BPTPropertyValueType.IGNORE});
	    	add(new Object[] {"contact_mail", "Contact mail", Component.class, BPTPropertyValueType.EMAIL});
	    	add(new Object[] {"date_created", "Date created", String.class, BPTPropertyValueType.IGNORE});
	    	add(new Object[] {"last_update", "Last update", String.class, BPTPropertyValueType.IGNORE});
	    }
	};
	
	public static List<Object[]> getEntries(String documentType) {
		if (documentType.equals("BPTTool")) {
			return bptTools;
		}
		return new ArrayList<Object[]>();
	}
	
	public static ArrayList<String> getDocumentKeys(String documentType) {
		ArrayList<String> values = new ArrayList<String>();
		if (documentType.equals("BPTTool")) {
			for (Object[] entry : bptTools) {
				values.add((String)entry[0]);
			}
		}
		return values;
	}
	
	public static ArrayList<String> getColumnNames(String documentType) {
		ArrayList<String> values = new ArrayList<String>();
		if (documentType.equals("BPTTool")) {
			for (Object[] entry : bptTools) {
				values.add((String)entry[1]);
			}
		}
		return values;
	}
	
	public static ArrayList<Class<?>> getPropertyDataTypes(String documentType) {
		ArrayList<Class<?>> values = new ArrayList<Class<?>>();
		if (documentType.equals("BPTTool")) {
			for (Object[] entry : bptTools) {
				values.add((Class<?>)entry[2]);
			}
		}
		return values;
	}
	
	public static ArrayList<BPTPropertyValueType> getPropertyValueTypes(String documentType) {
		ArrayList<BPTPropertyValueType> values = new ArrayList<BPTPropertyValueType>();
		if (documentType.equals("BPTTool")) {
			for (Object[] entry : bptTools) {
				values.add((BPTPropertyValueType)entry[3]);
			}
		}
		return values;
	}
}
