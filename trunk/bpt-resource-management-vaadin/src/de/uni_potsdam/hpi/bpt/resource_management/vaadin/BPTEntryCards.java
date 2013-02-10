package de.uni_potsdam.hpi.bpt.resource_management.vaadin;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTShowEntryComponent;


public class BPTEntryCards extends BPTShowEntryComponent{
	
	private VerticalLayout layout;
	private BPTApplication application;
	
	public BPTEntryCards(BPTApplication application){
		
//		CustomLayout htmlLayout = new CustomLayout("test");
//		addComponent(htmlLayout);
		super();
		layout = new VerticalLayout();
		this.application = application;
		addComponent(layout);
		show(dataSource);
		
	}

	@Override
	protected void show(IndexedContainer entries) {
		layout.removeAllComponents();
		String html = "Tool Support for Business Process Management <br/>" +
		"On this page, we will collect and promote innovative scientific and industrial tools that support BPM in any of several fields. <br/>" + 
		"The collection will comprise tools such as <br/>" + 
		"<ul>" +
		    "<li>graphical model editors,</li>" +
		    "<li>process model repositories,</li>" +
		    "<li>tools for verification and performance analysis,</li>" +
		    "<li>software to enact process models and adapt them during runtime,</li>" +
		    "<li>tools for process mining and conformance checking, and many more. </li>" +
		"</ul>";
		html = html + "<ul id=\"resource-list\">";
		for(Object id : entries.getItemIds()){
			String entryString = generateHtmlString(entries.getItem(id));
			html = html + "<li class=\"entry\" id=\"" + id.toString() + "\">" + entryString + generateButtonString(id.toString()) + "</li>";
		}
		html = html + "</ul>";
//		html = html + generateJavaScriptString();
		Label htmlLabel = new Label(html);
		
		
		htmlLabel.setContentMode(Label.CONTENT_XHTML);
		layout.addComponent(htmlLabel);
		
		
	}

//	private String generateJavaScriptString() {
//		String js;
//		js = "<script type=\"text/javascript\">";
//		js = js + "function showEntry(id){" +                                          
//	        "var divId = id + \"_extension\"" +
//	                "document.getElementById(divId).style.display = \"block\";" + 
//	                "var button_id = id + \"_button_more\";" +
//	                "document.getElementById(button_id).style.display = \"none\";" +
//	                "var button_id = id + \"_button_less\";" +
//	                "document.getElementById(button_id).style.display = \"block\";" +
//	        "}";
//		js = js + "function hideEntry(id){" +                                          
//		        "var divId = id + \"_extension\"" +
//		                "document.getElementById(divId).style.display = \"none\";" + 
//		                "var button_id = id + \"_button_more\";" +
//		                "document.getElementById(button_id).style.display = \"block\";" +
//		                "var button_id = id + \"_button_less\";" +
//		                "document.getElementById(button_id).style.display = \"none\";" +
//		        "}";
//		return js;
//	}

	private String generateButtonString(String id) {
		return "<a class=\"button more\" href=\"javascript:showEntry('" + id + "')\" id=\"1_button_more\"> more </a>" +
	    "<a class=\"button less\" href=\"javascript:hideEntry('" + id + "')\" id=\"1_button_less\"> less </a>";
	}

	private String generateHtmlString(Item item) {
		String entry = "";
		for(Object id : item.getItemPropertyIds()){
			Object value = item.getItemProperty(id).getValue();

			if(value.getClass() == Link.class){
				Link link = (Link) value;
				entry = entry + "<div class=\"" + id.toString() + "\"> <a href=\"" + link.getCaption() + "\">" + link.getCaption() + "</a> </div>";
			}
			else if(id == "ID"){
				entry = entry + "<img src=\"" + getImageFromItem((String) value.toString()) + "\" alt=\"logo\"> ";
			}
			else{
				if(id == "Name"){
					entry = entry + "<h3 class=\"" + id.toString() + "\">" + value.toString() + "</h3>"; 
				}
				else if(id != "Logo"){
					entry = entry + "<div class=\"" + id.toString() + "\">" + id.toString() + ":" + value.toString() + "</div>";
				}
			}
		}
		return entry;
	}

	private String getImageFromItem(String itemId) {
		BPTToolRepository repository = application.getToolRepository(); 
		return repository.getDatabaseAddress() + repository.getTableName() + "/" + itemId + "/logo";
	}

}
