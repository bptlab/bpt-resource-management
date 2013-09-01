package de.uni_potsdam.hpi.bpt.resource_management.vaadin.common;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.vaadin.imagefilter.Image;

import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTMimeTypes;

/**
 * Contains resources required by Vaadin to display various components.
 * 
 * @author tw
 * @author bu
 * 
 */
@SuppressWarnings({ "serial", "unchecked" })
public class BPTVaadinResources {
	
	private static List<Object[]> propertiesOfVisibleSetItems = new ArrayList<Object[]>() {
	    { 
	    	add(new Object[] {"_id", "ID", String.class, BPTPropertyValueType.IGNORE, null, false, true, false});
	    	add(new Object[] {"set_id", "Exercise Set ID", String.class, BPTPropertyValueType.IGNORE, null, true, false, false});
//	    	add(new Object[] {"_attachments", "Logo", Embedded.class, BPTPropertyValueType.IMAGE, "logo", false, true, false});
//	    	add(new Object[] {"title", "Title", String.class, BPTPropertyValueType.IGNORE, null, true, true, false});
//	    	add(new Object[] {"language", "Language", String.class, BPTPropertyValueType.IGNORE, null, true, true, false});
//	    	add(new Object[] {"description", "Description", Component.class, BPTPropertyValueType.RICH_TEXT, null, true, false, true});
	    	add(new Object[] {"topics", "Topics", String.class, BPTPropertyValueType.LIST, null, true, true, true});
	    	add(new Object[] {"modeling_languages", "Modeling Languages", String.class, BPTPropertyValueType.LIST, null, true, true, true});
	    	add(new Object[] {"task_types", "Task Types", String.class, BPTPropertyValueType.LIST, null, true, true, true});
	    	add(new Object[] {"other_tags", "Other tags", String.class, BPTPropertyValueType.LIST, null, true, true, true});
	    	add(new Object[] {"languages", "Languages", String.class, BPTPropertyValueType.LIST, null, true, true, true});
//	    	add(new Object[] {"exercise_url", "Exercise URL", Component.class, BPTPropertyValueType.LINK, null, true, true, true});
	    	add(new Object[] {"contact_name", "Contact name", String.class, BPTPropertyValueType.IGNORE, null, true, false, true});
	    	add(new Object[] {"contact_mail", "Contact mail", Component.class, BPTPropertyValueType.EMAIL, null, true, false, true}); 
	    	add(new Object[] {"user_id", "User ID", String.class, BPTPropertyValueType.IGNORE, null, true, false, false});
	    	add(new Object[] {"date_created", "Date created", Date.class, BPTPropertyValueType.DATE, null, true, false, false});
	    	add(new Object[] {"last_update", "Last update", Date.class, BPTPropertyValueType.DATE, null, true, true, true});
//	    	add(new Object[] {"notification_date", "Date of first notification", Date.class, BPTPropertyValueType.DATE, null, false, false, false});
//	    	add(new Object[] {"names_of_attachments", "Attachments", Component.class, BPTPropertyValueType.LINK_ATTACHMENT, null, true, true, false});
	    }
	};
	
	private static List<Object[]> propertiesOfVisibleItems = new ArrayList<Object[]>() {
	    { 
	    	add(new Object[] {"_id", "ID", String.class, BPTPropertyValueType.IGNORE, null, false, true, false});
	    	add(new Object[] {"set_id", "Exercise Set ID", String.class, BPTPropertyValueType.IGNORE, null, true, false, false});
	    	add(new Object[] {"_attachments", "Logo", Embedded.class, BPTPropertyValueType.IMAGE, "logo", false, true, false});
	    	add(new Object[] {"title", "Title", String.class, BPTPropertyValueType.IGNORE, null, true, true, false});
	    	add(new Object[] {"language", "Language", String.class, BPTPropertyValueType.IGNORE, null, true, true, false});
	    	add(new Object[] {"description", "Description", Component.class, BPTPropertyValueType.RICH_TEXT, null, true, false, true});
	    	add(new Object[] {"exercise_url", "Exercise URL", Component.class, BPTPropertyValueType.LINK, null, true, true, true});
//	    	add(new Object[] {"contact_name", "Contact name", String.class, BPTPropertyValueType.IGNORE, null, true, false, true});
//	    	add(new Object[] {"contact_mail", "Contact mail", Component.class, BPTPropertyValueType.EMAIL, null, true, false, true}); 
	    	add(new Object[] {"names_of_attachments", "Attachments", Component.class, BPTPropertyValueType.LINK_ATTACHMENT, null, true, true, false});
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
	public static List<Object[]> getEntrySets() {
		return propertiesOfVisibleSetItems;
	}
	
	public static List<Object[]> getEntries(){
		return propertiesOfVisibleItems;
	}
	
	/**
	 * @param documentType the document type
	 * @return attribute names under which the values are stored in the database
	 * 
	 */
	public static ArrayList<String> getDocumentKeys(boolean modifiableOnly, boolean isEntrySet) {
		ArrayList<String> values = new ArrayList<String>();
		List<Object[]> propertyArray;
		if(isEntrySet){
			propertyArray = propertiesOfVisibleSetItems;
		}
		else{
			propertyArray = propertiesOfVisibleItems;
		}
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
	public static ArrayList<String> getColumnNames() {
		ArrayList<String> values = new ArrayList<String>();
		for (Object[] entry : propertiesOfVisibleSetItems) {
			values.add((String)entry[1]);
		}
		return values;
	}
	
	/**
	 * @param documentType the document type
	 * @return property data type for Vaadin table
	 * 
	 */
	public static ArrayList<Class<?>> getPropertyDataTypes() {
		ArrayList<Class<?>> values = new ArrayList<Class<?>>();
		for (Object[] entry : propertiesOfVisibleSetItems) {
			values.add((Class<?>)entry[2]);
		}
		return values;
	}
	
	/**
	 * @param documentType the document type
	 * @return BPTPropertyValueType enum type to identify how to generate the specific Vaadin components that are shown
	 * 
	 */
	public static ArrayList<BPTPropertyValueType> getPropertyValueTypes() {
		ArrayList<BPTPropertyValueType> values = new ArrayList<BPTPropertyValueType>();
		for (Object[] entry : propertiesOfVisibleSetItems) {
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
	 * @param tool the database document as java.util.Map
	 * @param documentColumnName the name of the attribute
	 * @param valueType required to what type of Vaadin component will be generated - see return methods below
	 * @return returns the specific Vaadin component or a String if the value type is IGNORE
	 * 
	 */
	public static Object generateComponent(BPTDocumentRepository repository, Map<String, Object> tool, String documentColumnName, BPTPropertyValueType valueType, String attachmentName, Application application) {
		Object value = tool.get(documentColumnName);
		switch (valueType) {
			case LINK : return asLink((String)value);
			case EMAIL : return asEmailLink((String)value);
			case LIST : return asFormattedString((ArrayList<String>)value);
			case DATE : return asDate((String)value);
			case RICH_TEXT : return asRichText((String)value);
//			case IMAGE : return asImage(repository, tool, attachmentName);
			case LINK_ATTACHMENT : return asListOfAttachmentLinks(repository, tool, ((ArrayList<String>)value), application);
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
	
//	private static Embedded asImage(final BPTDocumentRepository repository, final Map<String, Object> tool, final String attachmentName) {
//		
//		if (tool.containsKey("_attachments")) {
//			InputStream attachmentInputStream = repository.readAttachment((String)tool.get("_id"), attachmentName);
//			Image image = new Image(attachmentInputStream, true);
//			try {
//				attachmentInputStream.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//			image.setMimeType((String)((Map<String, Object>)((Map<String, Object>)tool.get("_attachments")).get(attachmentName)).get("content_type"));
//
//			// default image size is icon size
//			image.setWidth("15px");
//			image.setHeight("15px");
//		    return image;
//		} else {
//			return new Embedded();
//		}
//	}

	private static ArrayList<Link> asListOfAttachmentLinks(final BPTDocumentRepository repository, final Map<String, Object> tool, ArrayList<String> namesOfAttachments, Application application) {
		ArrayList<Link> links = new ArrayList<Link>();
		for (final String attachmentName : namesOfAttachments) {
			StreamResource attachment = new StreamResource(new StreamResource.StreamSource() {
				public InputStream getStream() {
					return repository.readAttachment((String)tool.get("_id"), attachmentName);
				}
			}, attachmentName, application);
			Link link = new Link(attachmentName, attachment);
			link.setTargetName("_blank");
			String mimeType = attachment.getMIMEType();
			if (mimeType.equals(BPTMimeTypes.PDF.toString())) {
				link.setIcon(new ThemeResource("images/logo-pdf-16px.png"));
			} else if (mimeType.equals(BPTMimeTypes.DOC.toString())) {
				link.setIcon(new ThemeResource("images/logo-doc-16px.png"));
			} else if (mimeType.equals(BPTMimeTypes.DOCX.toString())) {
				link.setIcon(new ThemeResource("images/logo-docx-16px.png"));
			}
			links.add(link);
		}
		return links;
	}

	public static String[] getVisibleAttributes() {
		List<String> visibleAttributes = new ArrayList<String>();		
		for (Object[] entry : propertiesOfVisibleSetItems) {
			if ((Boolean)entry[6]) {
				visibleAttributes.add((String)entry[1]);
			}
		}
		return visibleAttributes.toArray(new String[visibleAttributes.size()]);
	}
	
}
