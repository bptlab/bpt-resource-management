package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class BPTSearchComponent extends CustomComponent {

	private VerticalLayout layout;
	private BPTFullSearchComponent fullSearchComponent;
	private BPTTagSearchComponent tagSearchComponent;

	public BPTSearchComponent(BPTApplication application, String tagColumns, boolean newTagsAllowed) {
		fullSearchComponent = new BPTFullSearchComponent(application);
		tagSearchComponent = new BPTTagSearchComponent(application, tagColumns, newTagsAllowed);
		init();
	}

	private void init() {
		layout = new VerticalLayout();
		layout.setWidth("100%");
		layout.setHeight("100%");
		setCompositionRoot(layout);
		layout.addComponent(fullSearchComponent);
		layout.addComponent(tagSearchComponent);
	}

	public BPTFullSearchComponent getFullSearchComponent() {
		return fullSearchComponent;
	}

	public void setFullSearchComponent(BPTFullSearchComponent fullSearchComponent) {
		this.fullSearchComponent = fullSearchComponent;
	}

	public BPTTagSearchComponent getTagSearchComponent() {
		return tagSearchComponent;
	}

	public void setTagSearchComponent(BPTTagSearchComponent tagSearchComponent) {
		this.tagSearchComponent = tagSearchComponent;
	}

}
