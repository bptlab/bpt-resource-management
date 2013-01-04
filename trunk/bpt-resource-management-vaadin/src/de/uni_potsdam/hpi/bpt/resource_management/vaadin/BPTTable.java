package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.ektorp.CouchDbConnector;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDatabase;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTTool;

public class BPTTable extends Table{

	private IndexedContainer visibleColumns;
	private IndexedContainer dataSource;
	private Collection<?> columnIds;
	
	public BPTTable(){
		super();
		setImmediate(true);
		setSelectable(true);
		setColumnReorderingAllowed(true);
        setColumnCollapsingAllowed(true);
        dataSource = BPTContainerProvider.getContainer();
        setContainerDataSource(dataSource);
        setWidth("100%");
        visibleColumns = new IndexedContainer();
        columnIds= dataSource.getContainerPropertyIds();
        for (Object columnId : columnIds){
        	visibleColumns.addContainerProperty(columnId, String.class, null);
        	System.out.println(visibleColumns.getContainerPropertyIds());
        }
        addListenerToTable();
	}
	public void filterBy(ArrayList<String> tagValues) {
		visibleColumns.removeAllItems();
		for (Object rowId : dataSource.getItemIds()){
			Item row = dataSource.getItem(rowId);
			if (columnShouldBeVisible(row, tagValues)){
				Item item = visibleColumns.addItem(rowId);
				for (Object columnId : columnIds){
					item.getItemProperty(columnId).setValue(row.getItemProperty(columnId).getValue());
				}
			}
			setContainerDataSource(visibleColumns);
		}
	}
	
	private boolean columnShouldBeVisible(Item item, ArrayList<String> tagValues) {
		
		ArrayList<String> itemAsArray = new ArrayList<String>();
		String[] relevantColumns = new String[] {"Availability", "Model type", "Platform", "Supported functionality"};
		for (Object propertyId : relevantColumns){ // for (Object propertyId : item.getItemPropertyIds())
			String property = item.getItemProperty(propertyId).getValue().toString();
			List<String> tags = Arrays.asList(property.split("\\s*,\\s*"));
			itemAsArray.addAll(tags);
		}
		for (int i = 0; i < tagValues.size(); i++){
			if (!itemAsArray.contains(tagValues.get(i))) return false;
			
		}
		return true;
	}
	
	private void addListenerToTable() {
		this.addListener( new Table.ValueChangeListener() {
			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
				if ((getItem(getValue()) != null)){
					openPopupFor(getItem(getValue()));
				}
								
			}

			private void openPopupFor(Item item) {
				Window popupWindow = new Window();
				popupWindow.setWidth("600px");
				String[] headers = getColumnHeaders();
				ArrayList<Object> itemAsArray = new ArrayList<Object>();
				for (Object propertyId : item.getItemPropertyIds()){
					Object property = item.getItemProperty(propertyId).getValue();
					itemAsArray.add(property);
				}
				final String name = itemAsArray.get(0).toString();
				for (int i = 0; i < headers.length; i++){
					popupWindow.addComponent(new Label(headers[i] + ":"));
					if (headers[i] == "Download" || headers[i] == "Documentation" || headers[i] == "Screencast") {
						popupWindow.addComponent(new Link (((Link)itemAsArray.get(i)).getCaption(), new ExternalResource(((Link)itemAsArray.get(i)).getCaption())));
						Link link = (((Link) itemAsArray.get(i)));
//						popupWindow.addComponent(link);
					}
					else if (headers[i] == "Contact mail" ) {
						popupWindow.addComponent(new Link (((Link)itemAsArray.get(i)).getCaption(), new ExternalResource("mailto:" + ((Link)itemAsArray.get(i)).getCaption())));
						// popupWindow.addComponent(((Link) itemAsArray.get(i)));
					}
					else popupWindow.addComponent(new Label(itemAsArray.get(i).toString()));
				}
				if (((BPTApplication)getApplication()).isLoggedIn()){
					
					Button deleteButton = new Button("delete");
					deleteButton.addListener(new Button.ClickListener(){
						public void buttonClick(ClickEvent event) {
							CouchDbConnector database = BPTDatabase.connect();
							database.delete(database.get(BPTTool.class, name));
						}
					});
					popupWindow.addComponent(deleteButton);
				}
				getWindow().addWindow(popupWindow);
				
			}
			});
	}
	
}
