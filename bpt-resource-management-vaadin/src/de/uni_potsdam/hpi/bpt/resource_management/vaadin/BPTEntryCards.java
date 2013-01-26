package de.uni_potsdam.hpi.bpt.resource_management.vaadin;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.BPTShowEntryComponent;


public class BPTEntryCards extends BPTShowEntryComponent{
	
	public BPTEntryCards(){
		
//		Embedded frame = new Embedded("Website", new ExternalResource("http://vaadin.com"));
//		frame.setAlternateText("Alternativtext");
//		frame.setType(Embedded.TYPE_BROWSER);
//		frame.setWidth("100%");
//		frame.setHeight("400px");
//		addComponent(frame);
		CustomLayout htmlLayout = new CustomLayout("test");
		addComponent(htmlLayout);
		
	}

	@Override
	protected void show(IndexedContainer tableEntries) {
		// TODO Auto-generated method stub
		
	}

}
