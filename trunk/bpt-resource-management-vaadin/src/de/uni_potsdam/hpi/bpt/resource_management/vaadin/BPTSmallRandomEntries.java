package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

public class BPTSmallRandomEntries extends BPTShowEntryComponent{

	private HorizontalLayout cardLayout, statistiksLayout;
	private CustomLayout layout;
	private Label numberOfEntriesLabel;
	
	public BPTSmallRandomEntries(BPTApplication application) {
		super(application);
	}

	@Override
	protected void buildLayout() {
		layout = new CustomLayout("cards");
		addComponent(layout);
		statistiksLayout = new HorizontalLayout();
		layout.addComponent(statistiksLayout, "statisticsRow");
		numberOfEntriesLabel = new Label();
		numberOfEntriesLabel.setImmediate(true);
		statistiksLayout.addComponent(numberOfEntriesLabel);
		Map<String, Integer> tagStatistics = BPTContainerProvider.getTagStatisticFor("availabilities");
		for(String string : tagStatistics.keySet()){
			statistiksLayout.addComponent(new Label(string + ": " + tagStatistics.get(string)));
		}
		cardLayout = new HorizontalLayout();
		layout.addComponent(cardLayout, "cards");
		addShowAllButton();
		addCharts();
	}

	private void addCharts() {
		BPTContainerProvider.getTagStatisticFor("availability");
		//get statistics from couch
		//send them to google api
	}

	private void addShowAllButton() {
		Button showAllButton = new Button("Show all entries");
		showAllButton.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					application.showAll();
				}
		});
		showAllButton.setStyleName(BaseTheme.BUTTON_LINK);
		showAllButton.addStyleName("bpt");
		addComponent(showAllButton);
	}

	@Override
	protected void showNumberOfEntries(int numberOfEntries) {
		numberOfEntriesLabel.setCaption(numberOfEntries + " Entries");
	}

	@Override
	protected void show(IndexedContainer entries) {
		this.cardLayout.removeAllComponents();
		for (Object id : entries.getItemIds()) {
			Item item = entries.getItem(id);
			BPTShortEntry entry = new BPTShortEntry(item, application, this);
			cardLayout.addComponent(entry);
		}
	}
	
	@Override
	protected void showEntries(ArrayList<BPTToolStatus> statusList) {
		show(BPTContainerProvider.getRandomEntries(statusList, 3));
	}

}
