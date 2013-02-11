package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

public class BPTEntry extends CustomLayout{
	
	private CustomLayout layout;
	private String entryId;
	private BPTEntry entry;
	
	public BPTEntry(Item item) {
		super("entry");
		entry = this;
		entryId = item.getItemProperty("ID").getValue().toString();
		this.setDebugId(entryId);
		Button more = new Button("more");
		more.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				getWindow().executeJavaScript(getJavaSrciptStringShow());
				entry.setHeight("");
			}

			private String getJavaSrciptStringShow() {
				String js = 
		        "var nodes = document.getElementById('" + entryId +"').childNodes[0].childNodes;" +
				"for(i=0; i<nodes.length; i+=1){" +
					"if(nodes[i].className == 'extension'){" +
						"nodes[i].style.display = 'block';" +
					"}}";
				return js;
			}});
		
		more.setStyleName(BaseTheme.BUTTON_LINK);
		this.addComponent(more, "button more");
		Button less = new Button("less");
		less.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				getWindow().executeJavaScript(getJavaSrciptStringHide());
				entry.setHeight("");
			}

			private String getJavaSrciptStringHide() {
				String js = 
		        "var nodes = document.getElementById('" + entryId +"').childNodes[0].childNodes;" +
				"for(i=0; i<nodes.length; i+=1){" +
					"if(nodes[i].className == 'extension'){" +
						"nodes[i].style.display = 'none';" +
					"}}";
				return js;
				
			}});
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
