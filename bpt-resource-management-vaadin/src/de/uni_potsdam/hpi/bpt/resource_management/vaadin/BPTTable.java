package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;

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
	}
	public void filterBy(ArrayList<String> tagValues) {
		visibleColumns.removeAllItems();
		for (Object rowId : dataSource.getItemIds()){
			System.out.println(rowId);
			System.out.println(dataSource.getItem(rowId));
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
		
		String itemAsString = item.toString();
		for (int i = 0; i < tagValues.size(); i++){
			if (!itemAsString.contains(tagValues.get(i))) return false;
		}
		return true;
	}
}
