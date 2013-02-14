package de.uni_potsdam.hpi.bpt.resource_management.vaadin;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTShowEntryComponent;


public class BPTEntryCards extends BPTShowEntryComponent{
	
	private CustomLayout layout;
	private BPTApplication application;
	private VerticalLayout vertical;
	private ArrayList<BPTEntry> entryList;
	private Boolean isInitial;
	
	public BPTEntryCards(BPTApplication application){
		
//		CustomLayout htmlLayout = new CustomLayout("test");
//		addComponent(htmlLayout);
		super();
		layout = new CustomLayout("cards");
		vertical = new VerticalLayout();
		entryList = new ArrayList<BPTEntry>();
		this.application = application;
		isInitial = true;
		addComponent(layout);
		layout.addComponent(vertical, "cards");
		show(dataSource);
		isInitial = false;
		
		
	}

	@Override
	protected void show(IndexedContainer entries) {
		vertical.removeAllComponents();
		for(Object id : entries.getItemIds()){
			Item item = entries.getItem(id);
			BPTEntry entry = new BPTEntry(item, application, this);
			vertical.addComponent(entry);
			entryList.add(entry);
			
			if(!isInitial){
				for(int i = 0; i < entryList.size(); i++){
					entryList.get(i).hideJavaScript();
				}
			}
			
		}

//		String html = "";
//		html = html + "<ul id=\"resource-list\">";
//		for(Object id : entries.getItemIds()){
//			String entryString = generateHtmlString(entries.getItem(id));
//			html = html + "<li class=\"entry\" id=\"" + id.toString() + "\">" + entryString + generateButtonString(id.toString()) + "</li>";
//		}
//		html = html + "</ul>";
//		Label htmlLabel = new Label(html);
//		Label htmlLabel = new Label("<li class=\"entry\"> Testeintrag </li>");
//		
//		
//		htmlLabel.setContentMode(Label.CONTENT_XHTML);
//		layout.addComponent(htmlLabel, "cards");
		
		
	}

	private String generateButtonString(String id) {
		return "<a class=\"button more\" href=\"javascript:showEntry('" + id + "')\" id=\"1_button_more\"> more </a>" +
	    "<a class=\"button less\" href=\"javascript:hideEntry('" + id + "')\" id=\"1_button_less\"> less </a>";
	}

	private String generateHtmlString(Item item) {
		String base = "";
		String extension = "<div class=\"extension\" id=\"" + item.getItemProperty("ID") + "_extension\">";
		for(Object id : item.getItemPropertyIds()){
			Object value = item.getItemProperty(id).getValue();
			String add = "";
			if(value.getClass() == Link.class){
				Link link = (Link) value;
				add = "<div class=\"" + id.toString() + "\"> <a href=\"" + link.getCaption() + "\">" + link.getCaption() + "</a> </div>";
			}
			else if(id == "ID"){
				add = "<img src=\"" + getImageFromItem((String) value.toString()) + "\" alt=\"logo\"> ";
			}
			else{
				if(id == "Name"){
					add =  "<h3 class=\"" + id.toString() + "\">" + value.toString() + "</h3>"; 
				}
				else if(id != "Logo" && id != "User ID"){
					add = "<div class=\"" + id.toString() + "\">" + id.toString() + ":" + value.toString() + "</div>";
				}
			}
			if(id == "ID" || id =="Name" || id == "Description" || id == "Provider" || id == "Download" || id =="Documentation"){
				base = base + add;
			}
			else{
				extension = extension + add;
			}
			
		}
		extension = extension + "</div>";
		return base + extension;
	}

	private String getImageFromItem(String itemId) {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("de.uni_potsdam.hpi.bpt.resource_management.bptrm");
		String databaseAddress = resourceBundle.getString("DB_EXTERNAL_ADDRESS");
		BPTToolRepository repository = application.getToolRepository(); 
		return databaseAddress + repository.getTableName() + "/" + itemId + "/logo";
	}
	
	
	public void addConfirmationWindowTo(String entryId, String status) {
		_id = entryId;
		addConfirmationWindow(null, status);
	}

}
