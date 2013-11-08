package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.CustomLayout;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentType;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class BPTShareableEntryContainer extends BPTShowEntryComponent {
	
//	private String userId;
	private Item item;
	private CustomLayout layout;
	
	public BPTShareableEntryContainer(BPTApplicationUI applicationUI, String entryId) {
		super(applicationUI, entryId);
	}

	@Override
	protected void buildLayout() {
		layout = new CustomLayout("shareable");
		addComponent(layout);
	}

	@Override
	protected void showNumberOfEntries(int numberOfEntries) {
		applicationUI.getSidebar().setNumberOfEntries(numberOfEntries);
	}

	@Override
	protected void show(IndexedContainer tableEntries) {
		item = tableEntries.getItem(tableEntries.getItemIds().iterator().next());
		BPTShareableEntry entry = new BPTShareableEntry(item, applicationUI);
		layout.addComponent(entry, "entry");
		layout.setImmediate(true);
	}
	
	@Override
	protected IndexedContainer getEntries(ArrayList<BPTToolStatus> statusList) {
		Map<String, Object> tool = toolRepository.get(entryId);
		return applicationUI.getContainerProvider().generateContainer(new ArrayList<Map>(Arrays.asList(tool)), BPTDocumentType.BPT_RESOURCES_TOOLS);
	}
	
}
