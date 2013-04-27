package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class BPTEntryCards extends BPTShowEntryComponent {
	
	private CustomLayout layout;
	private BPTApplication application;
	private VerticalLayout vertical;
	private ArrayList<BPTEntry> entryList;
	private Boolean isInitial;
	
	public BPTEntryCards(BPTApplication application) {
		
//		CustomLayout htmlLayout = new CustomLayout("test");
//		addComponent(htmlLayout);
		super();
		layout = new CustomLayout("cards");
		vertical = new VerticalLayout();
		entryList = new ArrayList<BPTEntry>();
		this.application = application;
		isInitial = true;
		layout.addStyleName("scroll");
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

	}

	public void addConfirmationWindowTo(String entryId, String status) {
		_id = entryId;
		addConfirmationWindow(null, status);
	}

}
