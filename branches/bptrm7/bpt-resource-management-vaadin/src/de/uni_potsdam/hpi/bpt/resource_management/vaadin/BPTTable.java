package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentType;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

@SuppressWarnings("serial")
public class BPTTable extends BPTShowEntryComponent {

	public BPTTable(BPTApplicationUI applicationUI) {
		super(applicationUI);
	}

	private Table table;
	
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
		table.setContainerDataSource(tableEntries);
		table.setVisibleColumns(BPTVaadinResources.getVisibleAttributes(BPTDocumentType.BPT_RESOURCES_TOOLS));
	}

	@Override
	protected void buildLayout() {
		table = new Table();
		table.setImmediate(true);
		table.setSelectable(true);
		table.setColumnReorderingAllowed(true);
        table.setColumnCollapsingAllowed(true);
        table.setWidth("100%");
//        addComponent(table, "cards");
        addListenerToTable();
	}

	@Override
	protected void showNumberOfEntries(int numberOfEntries) {
		// do nothing?
	}
}