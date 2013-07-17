package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

public class BPTPageSelector extends HorizontalLayout {
	
	private static final long serialVersionUID = 4698447540118612350L;
	private BPTApplication application;
	private Label entryFromTo;
	private int numberOfEntries;
	private List<Button> pageButtonList;
	
	public BPTPageSelector(BPTApplication application) {
		super();
		this.application = application;
		pageButtonList = new ArrayList<Button>();
	}
	
	public void setNumberOfEntries(int numberOfEntries) {
		removeAllComponents();
		pageButtonList.clear();
		entryFromTo = new Label("", Label.CONTENT_XHTML);
		if (numberOfEntries == 0) {
			entryFromTo.setValue("&nbsp;&nbsp;&nbsp;&nbsp;<b>No entry matches your search parameters</b>");
			addComponent(entryFromTo);
		} else {
			this.numberOfEntries = numberOfEntries;
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
			entryFromTo.requestRepaint();
			addComponent(entryFromTo);
			for (int i = 0; (i* 10) < numberOfEntries; i++) {
				final int x = i * 10;
				final Button pageButton = new Button(new Integer(i+1).toString());
				pageButton.setStyleName(BaseTheme.BUTTON_LINK);
				pageButton.setWidth("12px");
				pageButtonList.add(pageButton);
				pageButton.addListener(new Button.ClickListener(){

					private static final long serialVersionUID = -2746131404133732663L;

					public void buttonClick(ClickEvent event) {
						for (Button button : pageButtonList) {
							button.setEnabled(true);
						}
						pageButton.setEnabled(false);
						application.refreshAndClean(x);
					}
				});
				if (i == 0) {
					pageButton.setEnabled(false);
				}
				this.addComponent(pageButton);
			}
		}
	}
	
	public void switchToPage(int skippedEntries) {
		int lastEntry;
		int firstEntry = skippedEntries + 1;
		if (skippedEntries + 10 < numberOfEntries) {
			lastEntry = skippedEntries + 10;
		} else {
			lastEntry = numberOfEntries;
		}
		if (firstEntry == lastEntry) {
			entryFromTo.setValue("&nbsp;&nbsp;&nbsp;&nbsp;Entry <b>" + firstEntry + "</b> from " + numberOfEntries);
		} else {
			entryFromTo.setValue("&nbsp;&nbsp;&nbsp;&nbsp;Entries <b>" + firstEntry + " to " + lastEntry + "</b> from " + numberOfEntries);
		}
		entryFromTo.requestRepaint();
	}
}
