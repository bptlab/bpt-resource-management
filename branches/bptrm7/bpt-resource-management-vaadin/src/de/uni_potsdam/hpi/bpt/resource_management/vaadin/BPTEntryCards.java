package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings({"serial"})
public class BPTEntryCards extends BPTShowEntryComponent {
	
	private CustomLayout layout;
	private NativeSelect sortSelect;
	private BPTPageSelector topPageSelector, bottomPageSelector;
	private VerticalLayout vertical;
	
	public BPTEntryCards(final BPTApplicationUI applicationUI) {
		super(applicationUI);
	}

	public BPTEntryCards(BPTApplicationUI applicationUI, boolean loadEntries) {
		super(applicationUI, loadEntries);
	}

	@Override
	protected void buildLayout() {
		layout = new CustomLayout("entryCards");
		vertical = new VerticalLayout();
		topPageSelector = new BPTPageSelector(applicationUI);
		bottomPageSelector = new BPTPageSelector(applicationUI);
		
		HorizontalLayout selectLayout = new HorizontalLayout();
		sortSelect = new NativeSelect();
		sortSelect.addItem("Name");
		sortSelect.addItem("Provider");
		sortSelect.addItem("Last update");
		sortSelect.addItem("Date created");
		sortSelect.setValue("Name");
		sortSelect.setNullSelectionAllowed(false);
		sortSelect.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				applicationUI.refreshAndClean();
			}
		});
		sortSelect.setImmediate(true);
		selectLayout.addComponent(new Label("Sort entries by&nbsp;&nbsp;", ContentMode.HTML));
		selectLayout.addComponent(sortSelect);
		selectLayout.addComponent(new Label("&nbsp;&nbsp;&nbsp;&nbsp;", ContentMode.HTML));
		
		addComponent(layout);
		layout.addComponent(topPageSelector, "topPageSelector");
		layout.addComponent(selectLayout, "sortSelect");
		layout.addComponent(vertical, "cards");
		
		Button backToStartButton = new Button("Back to start page");
		backToStartButton.setStyleName(BaseTheme.BUTTON_LINK);
		backToStartButton.addClickListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				applicationUI.showStartPage();
			}
		});
		layout.addComponent(backToStartButton, "linkToStartPage");
		layout.addComponent(bottomPageSelector, "bottomPageSelector");
	}

	@Override
	protected void showNumberOfEntries(int numberOfEntries) {
		topPageSelector.showNumberOfEntries(numberOfEntries);
		bottomPageSelector.showNumberOfEntries(numberOfEntries);
	}
	
	@Override
	protected void show(IndexedContainer entries) {
		vertical.removeAllComponents();
		
		if (entries.size() == 0) {
			return;
		}
		for (Object id : entries.getItemIds()) {
			Item item = entries.getItem(id);
			BPTEntry entry = new BPTEntry(item, applicationUI, this);
			vertical.addComponent(entry);
		}
	}

	public void addConfirmationWindowTo(String entryId, String status) {
		_id = entryId;
		addConfirmationWindow(null, status);
	}
	
	public String getSortValue(){
		return (String) sortSelect.getValue();
	}

	public void switchToPage(int skip) {
		topPageSelector.switchToPage(skip);
		bottomPageSelector.switchToPage(skip);
	}
}
