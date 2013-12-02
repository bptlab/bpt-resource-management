package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings({"serial"})
public class BPTPageSelector extends HorizontalLayout {
	
	private BPTApplicationUI application;
	private Label entryFromTo;
	private int numberOfEntries, skip;
	private List<Button> pageButtonList;
	private Button firstPageButton, previousPageButton, nextPageButton, lastPageButton;
	
	public BPTPageSelector(BPTApplicationUI applicationUI) {
		super();
		this.application = applicationUI;
		pageButtonList = new ArrayList<Button>();
		skip = 0;
	}
	
	public void showNumberOfEntries(final int numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
		removeAllComponents();
		pageButtonList.clear();
		entryFromTo = new Label("", ContentMode.HTML);
		if (numberOfEntries == 0) {
			entryFromTo.setValue("&nbsp;&nbsp;&nbsp;&nbsp;<b>No entry matches your search parameters</b>");
			addComponent(entryFromTo);
		} else {
			this.removeAllComponents();
			int lastentry;
			if (numberOfEntries < 10) {
				lastentry = numberOfEntries;
			} else {
				lastentry = 10;
			}
			entryFromTo.setValue("&nbsp;&nbsp;&nbsp;&nbsp;Entries <b>1 to " + lastentry + "</b> from " + numberOfEntries);
			entryFromTo.setWidth("175px");
			entryFromTo.setImmediate(true);
//			entryFromTo.requestRepaint();
			addComponent(entryFromTo);
			
			firstPageButton = new Button("<<");
			firstPageButton.setImmediate(true);
			firstPageButton.setStyleName(BaseTheme.BUTTON_LINK);
			firstPageButton.addClickListener(new Button.ClickListener(){

					private static final long serialVersionUID = -2746131404133732663L;

					public void buttonClick(ClickEvent event) {
						application.refreshAndClean(0);
					}
				});
			firstPageButton.setEnabled(false);
			firstPageButton.setWidth("15px");
			addComponent(firstPageButton);
			
			previousPageButton = new Button("<");
			previousPageButton.setImmediate(true);
			previousPageButton.setStyleName(BaseTheme.BUTTON_LINK);
			previousPageButton.addClickListener(new Button.ClickListener(){

					private static final long serialVersionUID = -2746131404133732663L;

					public void buttonClick(ClickEvent event) {
						application.refreshAndClean(skip - 10);
					}
				});
			previousPageButton.setEnabled(false);
			previousPageButton.setWidth("12px");
			addComponent(previousPageButton);
			
			for (int i = 0; (i* 10) < numberOfEntries; i++) {
				final int x = i * 10;
				final Button pageButton = new Button(new Integer(i+1).toString());
				pageButton.setStyleName(BaseTheme.BUTTON_LINK);
				pageButton.setWidth("12px");
				pageButtonList.add(pageButton);
				pageButton.addClickListener(new Button.ClickListener(){

					private static final long serialVersionUID = -2746131404133732663L;

					public void buttonClick(ClickEvent event) {
						application.refreshAndClean(x);
					}
				});
				addComponent(pageButton);
			}
			
			nextPageButton = new Button(">");
			nextPageButton.setImmediate(true);
			nextPageButton.setStyleName(BaseTheme.BUTTON_LINK);
			nextPageButton.addClickListener(new Button.ClickListener(){

					private static final long serialVersionUID = -2746131404133732663L;

					public void buttonClick(ClickEvent event) {
						application.refreshAndClean(skip + 10);
					}
				});
			nextPageButton.setWidth("12px");
			addComponent(nextPageButton);
			
			lastPageButton = new Button(">>");
			lastPageButton.setImmediate(true);
			lastPageButton.setStyleName(BaseTheme.BUTTON_LINK);
			lastPageButton.addClickListener(new Button.ClickListener(){

					private static final long serialVersionUID = -2746131404133732663L;

					public void buttonClick(ClickEvent event) {
						if(numberOfEntries % 10 == 0){
							application.refreshAndClean(((numberOfEntries / 10) * 10) - 10);
						}
						else{
							application.refreshAndClean((numberOfEntries / 10) * 10);
						}
					}
				});
			lastPageButton.setWidth("15px");
			addComponent(lastPageButton);
			
			if(numberOfEntries <= 10){
				nextPageButton.setEnabled(false);
				lastPageButton.setEnabled(false);
			}
			pageButtonList.get(0).setEnabled(false);
		}
	}
	
	public void switchToPage(int skippedEntries) {
		
		if(numberOfEntries == 0) return;
		
		pageButtonList.get(skip / 10).setEnabled(true);
		pageButtonList.get(skippedEntries / 10).setEnabled(false);
		
		this.skip = skippedEntries;
		int lastEntry;
		int firstEntry = skippedEntries + 1;
		
		if(firstEntry == 1){
			firstPageButton.setEnabled(false);
			previousPageButton.setEnabled(false);
		}
		else{
			firstPageButton.setEnabled(true);
			previousPageButton.setEnabled(true);
		}
		
		if (skippedEntries + 10 < numberOfEntries) {
			lastEntry = skippedEntries + 10;
			nextPageButton.setEnabled(true);
			lastPageButton.setEnabled(true);
		} else {
			lastEntry = numberOfEntries;
			nextPageButton.setEnabled(false);
			lastPageButton.setEnabled(false);
		}
		if (firstEntry == lastEntry) {
			entryFromTo.setValue("&nbsp;&nbsp;&nbsp;&nbsp;Entry <b>" + firstEntry + "</b> from " + numberOfEntries);
		} else {
			entryFromTo.setValue("&nbsp;&nbsp;&nbsp;&nbsp;Entries <b>" + firstEntry + " to " + lastEntry + "</b> from " + numberOfEntries);
			//TODO: add Navigation at bottom
		}
//		entryFromTo.requestRepaint();
	}

	public void hide() {
		this.setVisible(false);
	}
	
	public void show() {
		this.setVisible(true);
	}
}
