package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentType;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTVaadinResources;

@SuppressWarnings("serial")
public class BPTAdministrator extends VerticalLayout {
	
	private Table table;
	
	public BPTAdministrator(final BPTApplication application) {
		
		table = new Table();
		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		table.setWidth("98%");
		table.setFooterVisible(true);
		table.setPageLength(10);
		addComponent(table);
		
		table.setContainerDataSource(BPTContainerProvider.getInstance().getUsers());
		table.setVisibleColumns(BPTVaadinResources.getVisibleAttributes(BPTDocumentType.BPT_RESOURCES_USERS));
	}
	
}
