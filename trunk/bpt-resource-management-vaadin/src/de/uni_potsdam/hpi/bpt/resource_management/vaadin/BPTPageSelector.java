package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;


public class BPTPageSelector extends HorizontalLayout {
	
	private BPTApplication application;
	private Label entryFromTo;
	private int numberOfEntries;
	private List<Button> pageButtonList;
	
	public BPTPageSelector(BPTApplication application) {
		super();
		this.application = application;
		entryFromTo = new Label();
		this.addComponent(entryFromTo);
		pageButtonList = new ArrayList<Button>();
	}
	
	public void setNumberOfEntries(int numberOfEntries) {
		for(Button button : pageButtonList){
			removeComponent(button);
		}
		pageButtonList.clear();
		if(numberOfEntries == 0){
			entryFromTo.setCaption("No Entry matches your search parameters");
		}
		else{
			this.numberOfEntries = numberOfEntries;
			this.removeAllComponents();
			int lastentry;
			if(numberOfEntries < 10){
				lastentry = numberOfEntries;
			}
			else{
				lastentry = 10;
			}
			entryFromTo.setCaption("Entry 1 to " + lastentry +  " from " + numberOfEntries);
			for(Integer i = 0; (i* 10) < numberOfEntries; i++){
				final int x = i * 10;
				final Button pageButton = new Button(i.toString());
				pageButton.setStyleName(BaseTheme.BUTTON_LINK);
				pageButtonList.add(pageButton);
				pageButton.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						for(Button button : pageButtonList){
							button.setEnabled(true);
						}
						pageButton.setEnabled(false);
						application.refreshAndClean(x);
					}
				});
				if(i == 0){
					pageButton.setEnabled(false);
				}
				this.addComponent(pageButton);
			}
		}
	}
	
	private void switchToPage(int pageNumber) {
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
