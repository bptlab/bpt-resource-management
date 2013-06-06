package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Arrays;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class BPTEntryCards extends BPTShowEntryComponent {
	
	private CustomLayout layout;
	private BPTApplication application;
	private VerticalLayout vertical;
	private ArrayList<BPTEntry> entryList;
	private Boolean isInitial;
	private NativeSelect sortSelect;
	
	public BPTEntryCards(BPTApplication application) {
		
		super();
		layout = new CustomLayout("cards");
		vertical = new VerticalLayout();
		entryList = new ArrayList<BPTEntry>();
		this.application = application;
		isInitial = true;
		sortSelect = new NativeSelect("Sort entries by", Arrays.asList("Name", "Provider", "Last update", "Date created"));
		sortSelect.setValue("Name");
		sortSelect.setNullSelectionAllowed(false);
		sortSelect.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				show(dataSource);
			}
		});
		sortSelect.setImmediate(true);
		
		addComponent(layout);
		layout.addComponent(sortSelect, "sortSelect");
		layout.addComponent(vertical, "cards");
		show(dataSource);
		isInitial = false;
	}

	@Override
	protected void show(IndexedContainer entries) {
		vertical.removeAllComponents();
		for(BPTEntry entry : entryList){
			entry.removeAllComponents();
		}
		entryList.clear();
		if(entries.size() == 0){
			return;
		}
		IndexedContainer sortedEntries = sortEntries(entries);
		for(Object id : entries.getItemIds()){
			Item item = entries.getItem(id);
			System.out.println(item.getItemPropertyIds());
			
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

	private IndexedContainer sortEntries(IndexedContainer entries) {
		String sort = (String) sortSelect.getValue();
		Object[] propertyId = {sort};
		boolean[] ascending = {sort=="Name" || sort=="Provider"};
		entries.sort(propertyId, ascending);
		return entries;
	}

	public void addConfirmationWindowTo(String entryId, String status) {
		_id = entryId;
		addConfirmationWindow(null, status);
	}

}
