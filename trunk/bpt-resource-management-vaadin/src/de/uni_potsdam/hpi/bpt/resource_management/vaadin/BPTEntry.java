package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.BaseTheme;

public class BPTEntry extends CustomLayout{
	
	private CustomLayout layout;
	
	public BPTEntry(Item item) {
		super("entry");
		this.setDebugId(item.getItemProperty("ID").getValue().toString());
		Button more = new Button("more");
		more.setStyleName(BaseTheme.BUTTON_LINK);
		this.addComponent(more, "button more");
		Button less = new Button("less");
		more.setStyleName(BaseTheme.BUTTON_LINK);
		this.addComponent(less, "button less");
		for(Object id : item.getItemPropertyIds()){
			if (id != "Logo" && id != "User ID" && id != "ID"){
				Object value = item.getItemProperty(id).getValue();
				if(value.getClass() == Link.class){
					Link link = (Link) value;
					this.addComponent(link, id.toString());
				}
				else{
					Label label = new Label(value.toString());
					if(id == "Description"){
						label.setContentMode(Label.CONTENT_XHTML);
					}
					this.addComponent(label, id.toString());
				}
			}
			
		}
		
	}
}
