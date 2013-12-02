package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.data.Item;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings({"serial"})
public class BPTShortEntry extends CustomLayout {

	private BPTApplicationUI applicationUI;
	private String entryId;

	public BPTShortEntry(Item item, BPTApplicationUI applicationUI, BPTSmallRandomEntries bptSmallRandomEntries) {
		super("shortEntry");
		addEntryInformation(item);
		addButton();
		this.applicationUI = applicationUI;
		entryId = item.getItemProperty("ID").getValue().toString();
	}

	private void addEntryInformation(Item item) {
		String name = (String) item.getItemProperty("Name").getValue();
		Label nameLabel = new Label("<span style=\"display: block\">" + name + "</span>");
		nameLabel.setContentMode(ContentMode.HTML);
		nameLabel.setWidth("200px");
		addComponent(nameLabel, "Name");
		Object value = item.getItemProperty("Logo").getValue();
		if (value != null) {
			Image image = (Image) value;
			image.setWidth("");
			image.setHeight("");
			addComponent(image, "Logo");
			image.addStyleName("bptlogo");
		}
		
		String providerURL = ((Link) item.getItemProperty("Provider URL").getValue()).getCaption();
		String provider = (item.getItemProperty("Provider").getValue()).toString();
		if (providerURL.isEmpty()) {
			Label label = new Label("<i><span style=\"margin-left: -1em\">" + "Provider" + "</span></i><br/><span style=\"margin-left: 1em; display: block\">" + provider + "</span>");
			label.setContentMode(ContentMode.HTML);
			label.setWidth("175px");
			this.addComponent(label, "Provider");
		} else {
			Label label = new Label("<i><span style=\"margin-left: -1em\">" + "Provider" + "</span></i><br/>" + "<span style=\"margin-left: 1em; display: block\"><a href='" + providerURL + "' target='_blank'>" + provider + "</a></span>");
			label.setContentMode(ContentMode.HTML);
			label.setWidth("175px");
			this.addComponent(label, "Provider");
		}
	}

	private void addButton() {
		
		Button showSingleEntryButton = new Button("Show single entry");
		showSingleEntryButton.addClickListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					applicationUI.showSpecificEntry(entryId);
				}
		});
		
		showSingleEntryButton.setStyleName(BaseTheme.BUTTON_LINK);
		showSingleEntryButton.addStyleName("bpt");
		addComponent(showSingleEntryButton, "button share");
	}

}