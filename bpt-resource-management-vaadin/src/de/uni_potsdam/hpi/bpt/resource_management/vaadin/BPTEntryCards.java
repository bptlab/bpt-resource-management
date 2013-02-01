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
		
//		Embedded frame = new Embedded("Website", new ExternalResource("http://vaadin.com"));
//		frame.setAlternateText("Alternativtext");
//		frame.setType(Embedded.TYPE_BROWSER);
//		frame.setWidth("100%");
//		frame.setHeight("400px");
//		addComponent(frame);
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
		String html = "<ul>";
		for(Object id : entries.getItemIds()){
			String entryString = generateHtmlString(entries.getItem(id));
			html = html + "<li> <div>" + entryString + generateButtonString() + "</div> </li>";
		}
		Label htmlLabel = new Label(html);
		html = html + "</ul>";
		
		htmlLabel.setContentMode(Label.CONTENT_XHTML);
		layout.addComponent(htmlLabel);
		
		
	}

	private String generateButtonString() {
		return "<a href=\"javascript\"> <img src=\"ziel.jpg\" alt=\"button\"> </a>";
	}

	private String generateHtmlString(Item item) {
		String entry = "";
		for(Object id : item.getItemPropertyIds()){
			Object value = item.getItemProperty(id).getValue();
			System.out.println(id);
			if (value.getClass() == Embedded.class) System.out.println("bild");
			else if(value.getClass() == Link.class){
				Link link = (Link) value;
				entry = entry + "<p> <a href=\"" + link.getCaption() + "\">" + link.getCaption() + "</a> </p>";
			}
			else if(id == "ID"){
				entry = entry + "<img src=\"" + getImageFromItem((String) value.toString()) + "\" alt=\"logo\"> ";
			}
			else{
				if(id == "Name"){
					entry = entry + "<h3>" + value.toString() + "</h3>"; 
				}
				else{
					entry = entry + "<p>" + value.toString() + "</p>";
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
