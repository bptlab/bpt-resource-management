package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.CustomLayout;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentType;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTUserRepository;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

@SuppressWarnings("serial")
public class BPTShareableEntryContainer extends BPTShowEntryComponent {
	
	private String userId;
	private Item item;
	private CustomLayout layout;
	
	public BPTShareableEntryContainer(BPTApplication application, String entryId) {
		super(application, entryId);
	}

	@Override
	protected void buildLayout() {
		layout = new CustomLayout("shareable");
		addComponent(layout);
	}

	@Override
	protected void showNumberOfEntries(int numberOfEntries) {
		application.getSidebar().setNumberOfEntries(numberOfEntries);
	}

	@Override
	protected void show(IndexedContainer tableEntries) {
		item = tableEntries.getItem(tableEntries.getItemIds().iterator().next());
		BPTShareableEntry entry = new BPTShareableEntry(item, application);
		layout.addComponent(entry, "entry");
		layout.setImmediate(true);
	}
	
	@Override
	protected IndexedContainer getEntries(ArrayList<BPTToolStatus> statusList) {
		Map<String, Object> tool = toolRepository.get(entryId);
		return application.getContainerProvider().generateContainer(new ArrayList<Map>(Arrays.asList(tool)), BPTDocumentType.BPT_RESOURCES_TOOLS);
	}
	
}
