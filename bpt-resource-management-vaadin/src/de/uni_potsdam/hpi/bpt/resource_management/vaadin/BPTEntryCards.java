package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.tools.ant.taskdefs.Sleep;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
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
		HorizontalLayout selectLayout = new HorizontalLayout();
//		sortSelect = new NativeSelect(Arrays.asList("Name", "Provider", "Last update", "Date created"));
		sortSelect = new NativeSelect();
		sortSelect.addItem("Name");
		sortSelect.addItem("Provider");
		sortSelect.addItem("Last update");
		sortSelect.addItem("Date created");
		sortSelect.setValue("Name");
		sortSelect.setNullSelectionAllowed(false);
		sortSelect.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				show(dataSource);
			}
		});
		sortSelect.setImmediate(true);
		selectLayout.addComponent(new Label("Sort entries by&nbsp;&nbsp;", Label.CONTENT_XHTML));
		selectLayout.addComponent(sortSelect);
		selectLayout.addComponent(new Label("&nbsp;&nbsp;&nbsp;&nbsp;", Label.CONTENT_XHTML));
		
		addComponent(layout);
		layout.addComponent(selectLayout, "sortSelect");
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
