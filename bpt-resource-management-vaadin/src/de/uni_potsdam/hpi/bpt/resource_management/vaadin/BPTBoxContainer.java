package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentStatus;

public class BPTBoxContainer extends CustomComponent {

	private HorizontalLayout layout;
	private CheckBox unpublished, published, rejected;
	
	public BPTBoxContainer(){
		
		layout = new HorizontalLayout();
		setCompositionRoot(layout);

		published = new CheckBox("published");
		published.setValue(true);
		published.setImmediate(true);
		layout.addComponent(published);
		
		unpublished = new CheckBox("unpublished");
		layout.addComponent(unpublished);
		unpublished.setImmediate(true);
		
		
		rejected = new CheckBox("rejected");
		layout.addComponent(rejected);
		rejected.setImmediate(true);
		
		published.addListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		    	
		    	refreshTable();
		    }
		});
		unpublished.addListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		    	
		    	refreshTable();
		    }
		});
		rejected.addListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		    	
		    	refreshTable();
		    }
		});
	}
	
	private void refreshTable(){
		ArrayList<BPTDocumentStatus> statusList = new ArrayList<BPTDocumentStatus>();
		if ((Boolean) published.getValue()) statusList.add(BPTDocumentStatus.Published);
		if ((Boolean) unpublished.getValue()) statusList.add(BPTDocumentStatus.Unpublished);
		if ((Boolean) rejected.getValue()) statusList.add(BPTDocumentStatus.Rejected);
		BPTDocumentStatus[] statusArray =  statusList.toArray(new BPTDocumentStatus[statusList.size()]);
		
		((BPTApplication)getApplication()).getTable().refreshContent(statusArray);
	}

}
