package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.Table;

public class BPTTable extends Table{

	public BPTTable(){
		super();
		setImmediate(true);
		setSelectable(true);
		setColumnReorderingAllowed(true);
        setColumnCollapsingAllowed(true);
        setContainerDataSource(BPTContainerProvider.getContainer());
        setWidth("100%");
		
	}

	
}
