package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class BPTEntryCards extends BPTShowEntryComponent {
	
	private CustomLayout layout;
//	private VerticalLayout vertical;
//	private ArrayList<BPTEntry> entryList;
//	private Boolean isInitial;
	private NativeSelect sortSelect;
	private BPTPageSelector pageSelector;
	
	public BPTEntryCards(final BPTApplication application) {
		
		super(application);
		//TODO: brauchen wir das?
//		isInitial = false;
	}

	@Override
	protected void buildLayout() {
		layout = new CustomLayout("cards");
		VerticalLayout vertical = new VerticalLayout();
//		entryList = new ArrayList<BPTEntry>();
//		isInitial = true;
		setBPTPageSelector(new BPTPageSelector(application));
		
		HorizontalLayout selectLayout = new HorizontalLayout();
		sortSelect = new NativeSelect();
		sortSelect.addItem("Name");
		sortSelect.addItem("Provider");
		sortSelect.addItem("Last update");
		sortSelect.addItem("Date created");
		sortSelect.setValue("Name");
		sortSelect.setNullSelectionAllowed(false);
		sortSelect.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				application.refreshAndClean();
			}
		});
		sortSelect.setImmediate(true);
		selectLayout.addComponent(new Label("Sort entries by&nbsp;&nbsp;", Label.CONTENT_XHTML));
		selectLayout.addComponent(sortSelect);
		selectLayout.addComponent(new Label("&nbsp;&nbsp;&nbsp;&nbsp;", Label.CONTENT_XHTML));
		
		addComponent(layout);
		layout.addComponent(getBPTPageSelector(), "pageSelection");
		layout.addComponent(selectLayout, "sortSelect");
		layout.addComponent(vertical, "cards");
	}

	@Override
	protected void showNumberOfEntries(int numberOfEntries) {
		getBPTPageSelector().showNumberOfEntries(numberOfEntries);
	}
	
	@Override
	protected void show(IndexedContainer entries) {
//		((VerticalLayout) layout.getComponent("cards")).removeAllComponents();
		
		layout.removeComponent("cards");
		VerticalLayout vertical = new VerticalLayout();
		if (entries.size() == 0) {
			return;
		}
		for (Object id : entries.getItemIds()) {
			Item item = entries.getItem(id);
			BPTEntry entry = new BPTEntry(item, application, this);
			vertical.addComponent(entry);
//			entryList.add(entry);
//			if (!isInitial) {
//				for (int i = 0; i < entryList.size(); i++) {
//					entryList.get(i).hideJavaScript();
//				}
//			}
		}
		layout.addComponent(vertical, "cards");
	}

	public void addConfirmationWindowTo(String entryId, String status) {
		_id = entryId;
		addConfirmationWindow(null, status);
	}
	
	public String getSortValue(){
		return (String) sortSelect.getValue();
	}

	public BPTPageSelector getBPTPageSelector() {
		return pageSelector;
	}

	private void setBPTPageSelector(BPTPageSelector pageSelector) {
		this.pageSelector = pageSelector;
	}

}
