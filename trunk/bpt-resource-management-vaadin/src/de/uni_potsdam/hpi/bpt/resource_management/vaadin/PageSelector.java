package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;


public class PageSelector extends HorizontalLayout{
	
	private BPTApplication application;
	private Label entryFromTo;
	private int numberOfEntries;
	private List<Button> pageButtonList;
	
	public PageSelector(){
		super();
		entryFromTo = new Label();
		this.addComponent(entryFromTo);
	}
	
	public void setNumberOfEntries(int numberOfEntries){
		if(numberOfEntries == 0){
			entryFromTo.setCaption("No Entry matches your search parameters");
		}
		else{
			Button pageButton;
			this.numberOfEntries = numberOfEntries;
			this.removeAllComponents();
			int lastentry;
			if(numberOfEntries < 10){
				lastentry = numberOfEntries;
			}
			else{
				lastentry = 10;
			}
			entryFromTo = new Label("Entry 1 to " + lastentry +  " from " + numberOfEntries);
			for(Integer i = 0; (i* 10) < numberOfEntries; i++){
				final int x = i * 10;
				pageButton = new Button(i.toString());
				pageButton.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						application.refreshAndClean(x);
					}
				});
				pageButton.setDisableOnClick(true);
				this.addComponent(pageButton);
			}
		}
	}
	
	private void switchToPage(int pageNumber){
		int lastEntry;
		int firstEntry = (pageNumber * 10) + 1;
		if((pageNumber * 10) + 10 < numberOfEntries){
			lastEntry = (pageNumber * 10) + 10;
		}
		else{
			lastEntry = numberOfEntries;
		}
		entryFromTo.setCaption("Entry " + firstEntry + " to " + lastEntry + " from " + numberOfEntries);		 
	}
}
