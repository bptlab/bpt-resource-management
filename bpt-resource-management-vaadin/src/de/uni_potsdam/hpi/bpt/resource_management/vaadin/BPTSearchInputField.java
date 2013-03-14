package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.ComboBox;

public class BPTSearchInputField extends ComboBox{
	
	public BPTSearchInputField(){
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
