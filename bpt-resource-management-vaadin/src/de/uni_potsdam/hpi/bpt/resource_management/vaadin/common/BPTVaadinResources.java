package de.uni_potsdam.hpi.bpt.resource_management.vaadin.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentType;

/**
 * Contains resources required by Vaadin to display various components.
 * 
 * @author tw
 * @author bu
 * 
 */
@SuppressWarnings({ "serial", "unchecked", "deprecation" })
public class BPTVaadinResources {
	
	private static class ImageStreamSource implements StreamSource {
		
		private InputStream stream;
		
		private ImageStreamSource(InputStream stream) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n = 0;
			try {
				while ((n = stream.read(buf)) >= 0) {
				    baos.write(buf, 0, n);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.stream = new ByteArrayInputStream(baos.toByteArray());
		}
		
		public InputStream getStream() {
			return stream;
		}
	}
	
	private static List<Object[]> propertiesOfVisibleItems = new ArrayList<Object[]>() {
	    { 
	    	add(new Object[] {"_id", "ID", String.class, BPTPropertyValueType.IGNORE, null, false, false, false});
	    	add(new Object[] {"_attachments", "Logo", Image.class, BPTPropertyValueType.IMAGE, "logo", false, true, false});
	    	add(new Object[] {"name", "Name", String.class, BPTPropertyValueType.IGNORE, null, true, true, false});
	    	add(new Object[] {"description", "Description", Component.class, BPTPropertyValueType.RICH_TEXT, null, true, false, true});
	    	add(new Object[] {"description_url", "Description URL", Component.class, BPTPropertyValueType.LINK, null, true, false, true});
	    	add(new Object[] {"provider", "Provider", String.class, BPTPropertyValueType.IGNORE, null, true, true, true});
	    	add(new Object[] {"provider_url", "Provider URL", Component.class, BPTPropertyValueType.LINK, null, true, false, true});
	    	add(new Object[] {"download_url", "Download URL", Component.class, BPTPropertyValueType.LINK, null, true, true, true});
	    	add(new Object[] {"documentation_url", "Documentation URL", Component.class, BPTPropertyValueType.LINK, null, true, false, true});
	    	add(new Object[] {"screencast_url", "Screencast URL", Component.class, BPTPropertyValueType.LINK, null, true, false, true});
	    	add(new Object[] {"tutorial_url", "Tutorial URL", Component.class, BPTPropertyValueType.LINK, null, true, false, true});
	    	add(new Object[] {"availabilities", "Availability", String.class, BPTPropertyValueType.LIST, null, true, true, true});
	    	add(new Object[] {"model_types", "Model type", String.class, BPTPropertyValueType.LIST, null, true, true, true});
	    	add(new Object[] {"platforms", "Platform", String.class, BPTPropertyValueType.LIST, null, true, true, true});
	    	add(new Object[] {"supported_functionalities", "Supported functionality", String.class, BPTPropertyValueType.LIST, null, true, true, true});
	    	add(new Object[] {"contact_name", "Contact name", String.class, BPTPropertyValueType.IGNORE, null, true, false, true});
	    	add(new Object[] {"contact_mail", "Contact mail", Component.class, BPTPropertyValueType.EMAIL, null, true, false, true}); 
	    	add(new Object[] {"user_id", "User ID", String.class, BPTPropertyValueType.IGNORE, null, true, false, true});
	    	add(new Object[] {"date_created", "Date created", Date.class, BPTPropertyValueType.DATE, null, true, false, false});
	    	add(new Object[] {"last_update", "Last update", Date.class, BPTPropertyValueType.DATE, null, true, true, true});
//	    	add(new Object[] {"notification_date", "Date of first notification", Date.class, BPTPropertyValueType.DATE, null, false, false, false});
	    	add(new Object[] {"name_lowercase", "Name (lower case)", String.class, BPTPropertyValueType.IGNORE, null, true, true, false});
	    	add(new Object[] {"provider_lowercase", "Provider (lower case)", String.class, BPTPropertyValueType.IGNORE, null, true, true, false});
	    }
	};
	
	private static List<Object[]> propertiesOfVisibleUserItems = new ArrayList<Object[]>() {
		{
			add(new Object[] {"name", "Name", String.class, BPTPropertyValueType.IGNORE, null, false, true, true});
			add(new Object[] {"mail_address", "Mail", Component.class, BPTPropertyValueType.EMAIL, null, false, true, true});
			add(new Object[] {"is_moderator", "Moderator", Component.class, BPTPropertyValueType.CHECKBOX, null, true, true, true});
			add(new Object[] {"_id", "ID", String.class, BPTPropertyValueType.IGNORE, null, false, true, true});
        }
    };

	
	/**
	 * Returns resources required by Vaadin to display various components.
	 * 
	 * @param documentType the document type
	 * @return list of array containing the resource elements
	 * 
	 * array element #0: attribute name under which the value is stored in CouchDB
	 * array element #1: attribute name displayed in Vaadin
	 * array element #2: property data type for Vaadin table
	 * array element #3: BPTPropertyValueType enum type to identify how to generate the specific Vaadin components that are shown
	 * array element #4: attachment file name
	 * array element #5: true if attribute is modifiable by user (attachments to be handled separately)
	 * array element #6: true if attribute shall be visible in BPTShowEntryComponent
	 * array element #7: true if attribute shall be visible in window where selected entry is shown (_attachments are handled separately)
	 * 
	 */
	public static List<Object[]> getPropertyArray(BPTDocumentType type) {
		switch (type) {
			case BPT_RESOURCES_TOOLS : return propertiesOfVisibleItems;
			case BPT_RESOURCES_USERS : return propertiesOfVisibleUserItems;
			default : return null;
		}
	}
	
	/**
	 * @param documentType the document type
	 * @return attribute names under which the values are stored in the database
	 * 
	 */
	public static ArrayList<String> getDocumentKeys(boolean modifiableOnly, BPTDocumentType type) {
		ArrayList<String> values = new ArrayList<String>();
		List<Object[]> propertyArray = getPropertyArray(type);
		for (Object[] entry : propertyArray) {
			if (!modifiableOnly || (Boolean)entry[5]) {
				values.add((String)entry[0]);
			}
		}	
		return values;
	}
	
	/**
	 * @param documentType the document type
	 * @return attribute names displayed in Vaadin
	 * 
	 */
	public static ArrayList<String> getColumnNames(BPTDocumentType type) {
		ArrayList<String> values = new ArrayList<String>();
		List<Object[]> propertyArray = getPropertyArray(type);
		for (Object[] entry : propertyArray) {
			values.add((String)entry[1]);
		}
		return values;
	}
	
	/**
	 * @param documentType the document type
	 * @return property data type for Vaadin table
	 * 
	 */
	public static ArrayList<Class<?>> getPropertyDataTypes(BPTDocumentType type) {
		ArrayList<Class<?>> values = new ArrayList<Class<?>>();
		List<Object[]> propertyArray = getPropertyArray(type);
		for (Object[] entry : propertyArray) { 
			values.add((Class<?>)entry[2]);
		}
		return values;
	}
	
	/**
	 * @param documentType the document type
	 * @return BPTPropertyValueType enum type to identify how to generate the specific Vaadin components that are shown
	 * 
	 */
	public static ArrayList<BPTPropertyValueType> getPropertyValueTypes(BPTDocumentType type) {
		ArrayList<BPTPropertyValueType> values = new ArrayList<BPTPropertyValueType>();
		List<Object[]> propertyArray = getPropertyArray(type);
		for (Object[] entry : propertyArray) {
			values.add((BPTPropertyValueType)entry[3]);
		}
		return values;
	}
	
	/**
	 * @param documentType the document type
	 * @return columns (including lists) whose entries are relevant for searching by tags
	 * 
	 */
	public static String[] getColumnsWithTags() {
		return new String[] {"Availability", "Model type", "Platform", "Supported functionality"};
	}
	
	/**
	 * @param document the database document as java.util.Map
	 * @param documentColumnName the name of the attribute
	 * @param valueType required to what type of Vaadin component will be generated - see return methods below
	 * @return returns the specific Vaadin component or a String if the value type is IGNORE
	 * 
	 */
	public static Object generateComponent(BPTDocumentRepository repository, Map<String, Object> document, String documentColumnName, BPTPropertyValueType valueType, String attachmentName) {
		Object value = document.get(documentColumnName);
		switch (valueType) {
			case LINK : return asLink((String)value);
			case EMAIL : return asEmailLink((String)value);
			case LIST : return asFormattedString((ArrayList<String>)value);
			case DATE : return asDate((String)value);
			case RICH_TEXT : return asRichText((String)value);
			case IMAGE : return asImage(repository, document, attachmentName);
			case CHECKBOX : return asCheckBox(repository, document, documentColumnName, (Boolean)value);
			default : return value;
		}
	}

	private static String asFormattedString(ArrayList<String> stringList) {
		return stringList.toString().replace("[", "").replace("]", "");
	}
	
	private static Link asLink(String linkString) {
		return new Link(linkString, new ExternalResource(linkString));
	}
	
	private static Link asEmailLink(String emailLinkString) {
		return new Link(emailLinkString, new ExternalResource("mailto:" + emailLinkString));
	}
	
	private static Date asDate(String dateString) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static Label asRichText(String richTextString) {
		Label richText = new Label(richTextString);
	    richText.setContentMode(Label.CONTENT_XHTML);
	    return richText;
	}
	
	private static Image asImage(final BPTDocumentRepository repository, final Map<String, Object> tool, final String attachmentName) {
		
		if (tool.containsKey("_attachments")) {
			InputStream attachmentInputStream = repository.readAttachment((String)tool.get("_id"), attachmentName);
			Image image = new Image(null, new StreamResource(new ImageStreamSource(attachmentInputStream), attachmentName));
			try {
				attachmentInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
//			image.setMimeType((String)((Map<String, Object>)((Map<String, Object>)tool.get("_attachments")).get(attachmentName)).get("content_type"));

			// default image size is icon size
			image.setWidth("15px");
			image.setHeight("15px");
		    return image;
		} else {
			return null;
		}
	}
	
	private static CheckBox asCheckBox(final BPTDocumentRepository repository, final Map<String, Object> document, final String key, Boolean value) {
		final CheckBox checkbox = new CheckBox();
		checkbox.setValue(value);
		checkbox.setImmediate(true);
		checkbox.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				Map<String, Object> newValues = new HashMap<String, Object>();
				newValues.put("_id", (String) document.get("_id"));
				newValues.put(key, (Boolean)checkbox.getValue());
				repository.updateDocument(newValues);
			}
		});
		return checkbox;
	}

	public static String[] getVisibleAttributes(BPTDocumentType type) {
		List<String> visibleAttributes = new ArrayList<String>();
		List<Object[]> propertyArray = getPropertyArray(type);
		for (Object[] entry : propertyArray) {
			if ((Boolean)entry[6]) {
				visibleAttributes.add((String)entry[1]);
			}
		}
		return visibleAttributes.toArray(new String[visibleAttributes.size()]);
	}
	
}
