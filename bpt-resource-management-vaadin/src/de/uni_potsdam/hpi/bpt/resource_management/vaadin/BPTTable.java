package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

@SuppressWarnings("serial")
public class BPTTable extends BPTShowEntryComponent {

	private Table table;
	
	public BPTTable(){
		super();
		table = new Table();
		table.setImmediate(true);
		table.setSelectable(true);
		table.setColumnReorderingAllowed(true);
        table.setColumnCollapsingAllowed(true);
        table.setWidth("100%");
        addComponent(table);
        addListenerToTable();
        show(dataSource);
       
	}
	
	private void addListenerToTable() {
		table.addListener(new Table.ValueChangeListener() {
			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
				if ((table.getItem(table.getValue()) != null)){
					showSelectedEntry(table.getItem(table.getValue()));
				}		
			}
		});
			
}

	@Override
	protected void show(IndexedContainer tableEntries) {
		table.setContainerDataSource(dataSource);
		table.setVisibleColumns(BPTVaadinResources.getVisibleAttributes());
	}
}