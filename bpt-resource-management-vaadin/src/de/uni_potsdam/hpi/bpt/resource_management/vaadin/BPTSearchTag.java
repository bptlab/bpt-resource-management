package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
public class BPTSearchTag extends CustomComponent{
	
	private Label label;
	private Button deleteButton;
	private BPTSearchTag searchTag = this;

	public BPTSearchTag(final BPTSearchTagBox searchtagbox, String value) {
		Layout layout = new HorizontalLayout();
		setCompositionRoot(layout);
		deleteButton = new Button("x");
        deleteButton.setStyleName(BaseTheme.BUTTON_LINK);
        label = new Label(value);
        layout.addComponent(deleteButton);
        layout.addComponent(label);
        
        deleteButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				searchtagbox.removeTag(searchTag);
			}
		});
                   
	}
	public String getValue(){
		return (String) label.getValue();
	}
	
}
