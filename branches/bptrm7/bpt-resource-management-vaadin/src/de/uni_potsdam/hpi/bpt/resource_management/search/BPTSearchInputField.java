package de.uni_potsdam.hpi.bpt.resource_management.search;

import com.vaadin.ui.ComboBox;

@SuppressWarnings("serial")
public class BPTSearchInputField extends ComboBox {
	
	public BPTSearchInputField() {
		super();
		setWidth("300px");
		setImmediate(true);
		setNullSelectionAllowed(false);
	}
	
	@Override
	public Object addItem() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return super.addItem();
	}
	
}
