package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentType;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

@SuppressWarnings("serial")
public class BPTTable extends BPTShowEntryComponent {

	public BPTTable(BPTApplication application) {
		super(application);
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
	protected void show(IndexedContainer sets) {
		table.setContainerDataSource(sets);
		table.setVisibleColumns(BPTVaadinResources.getVisibleAttributes(BPTDocumentType.BPMAI_EXERCISE_SETS));
	}

	@Override
	protected void buildLayout() {
		table = new Table();
		table.setImmediate(true);
		table.setSelectable(true);
		table.setColumnReorderingAllowed(true);
        table.setColumnCollapsingAllowed(true);
        table.setWidth("100%");
        addComponent(table);
        addListenerToTable();
	}

	@Override
	protected void showNumberOfEntries(int numberOfEntries) {
		// do nothing?
	}
}