package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;

public class BPTEntry extends CustomLayout{
	
	private CustomLayout layout;
	private String entryId;
	private BPTEntry entry;
	private String userId;
	private Item item;
	private BPTEntryCards entryCards;
	private BPTApplication application;
	
	
	public BPTEntry(Item item, BPTApplication application, BPTEntryCards entryCards) {
		super("entry");
		entry = this;
		this.item = item;
		this.entryCards = entryCards;
		this.application = application;
		entryId = item.getItemProperty("ID").getValue().toString();
		userId = item.getItemProperty("User ID").getValue().toString();
		this.setDebugId(entryId);
		addButtons(application);
		for (Object id : item.getItemPropertyIds()) {
			if (id == "Logo") {
				Object value = item.getItemProperty(id).getValue();
				Embedded image = (Embedded) value;
				image.setWidth("");
				image.setHeight("");
				this.addComponent(image, id.toString());
				image.addStyleName("bptlogo");
			}
			else if (id != "User ID" && id != "ID") {
				Object value = item.getItemProperty(id).getValue();
				if(value.getClass() == Link.class){
					Link link = (Link) value;
					if (id.equals("Contact mail")) link.addStyleName("bpt2");
					this.addComponent(link, id.toString());
				}
				else {
					Label label = new Label(value.toString());
					if(id == "Description") {
						label.setContentMode(Label.CONTENT_XHTML);
					}
					label.setWidth("524px");
					this.addComponent(label, id.toString());
				}
			}
			
		}
		
	}

	private void addButtons(final BPTApplication application) {
		Button more = new Button("more");
		more.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				getWindow().executeJavaScript(getJavaScriptStringShow());
				entry.setHeight("");
			}

			private String getJavaScriptStringShow() {
				String js = 
		        "var nodes = document.getElementById('" + entryId +"').childNodes[0].childNodes;" +
				"for(i=0; i<nodes.length; i+=1){" +
					"if(nodes[i].className == 'extension'){" +
						"nodes[i].style.display = 'block';}" +
					"if(nodes[i].className == 'button more'){" +
						"nodes[i].style.display = 'none';}" +
					"}";
				return js;
			}});
		
		more.setStyleName(BaseTheme.BUTTON_LINK);
		more.addStyleName("bpt");
		this.addComponent(more, "button more");
		Button less = new Button("less");
		less.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				getWindow().executeJavaScript(getJavaScriptStringHide());
				entry.setHeight("");
			}

			private String getJavaScriptStringHide() {
				String js = 
		        "var nodes = document.getElementById('" + entryId +"').childNodes[0].childNodes;" +
				"for(i=0; i<nodes.length; i+=1){" +
					"if(nodes[i].className == 'extension'){" +
						"nodes[i].style.display = 'none';}" +
					"if(nodes[i].className == 'button more'){" +
						"nodes[i].style.display = 'block';}" +
					"}";
				return js;
				
			}});
		less.setStyleName(BaseTheme.BUTTON_LINK);
		less.addStyleName("bpt");
		this.addComponent(less, "button less");
		
		
		if(application.isLoggedIn() && application.getUser().equals(userId)){
			Button edit = new Button("edit");
			edit.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					application.edit(item);
				}});
			
			edit.setStyleName(BaseTheme.BUTTON_LINK);
			edit.addStyleName("bpt");
			this.addComponent(edit, "button edit");
			application.getMainWindow().executeJavaScript(getJavaScriptStringShow("edit"));
		}
		
		if(application.isLoggedIn() && (application.getUser().equals(userId) || application.isModerated())){
			Button delete = new Button("delete");
			delete.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					entryCards.addConfirmationWindowTo(entryId, "delete");
				}});
			
			delete.setStyleName(BaseTheme.BUTTON_LINK);
			delete.addStyleName("bpt");
			this.addComponent(delete, "button delete");
			application.getMainWindow().executeJavaScript(getJavaScriptStringShow("delete"));
		}
		
		if(application.isLoggedIn() && application.isModerated()){
			
			BPTToolStatus actualState = application.getToolRepository().getDocumentStatus(entryId);
			if (actualState == BPTToolStatus.Unpublished){
				
			
			Button publish = new Button("publish");
			publish.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					entryCards.addConfirmationWindowTo(entryId, "publish");
				}});
			
					publish.setStyleName(BaseTheme.BUTTON_LINK);
					publish.addStyleName("bpt");
					this.addComponent(publish, "button publish");
					application.getMainWindow().executeJavaScript(getJavaScriptStringShow("publish"));
					
					Button reject = new Button("reject");
					reject.addListener(new Button.ClickListener(){
						public void buttonClick(ClickEvent event) {
							entryCards.addConfirmationWindowTo(entryId, "reject");
						}});
					
					reject.setStyleName(BaseTheme.BUTTON_LINK);
					reject.addStyleName("bpt");
					this.addComponent(reject, "button reject");
					application.getMainWindow().executeJavaScript(getJavaScriptStringShow("reject"));
			
			}
			else if (actualState == BPTToolStatus.Published){
				Button unpublish = new Button("unpublish");
				unpublish.addListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						entryCards.addConfirmationWindowTo(entryId, "unpublish");
					}});
				
				unpublish.setStyleName(BaseTheme.BUTTON_LINK);
				unpublish.addStyleName("bpt");
				this.addComponent(unpublish, "button unpublish");
				application.getMainWindow().executeJavaScript(getJavaScriptStringShow("unpublish"));
			
			}
			else {
			
			Button propose = new Button("propose");
			propose.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					entryCards.addConfirmationWindowTo(entryId, "propose");
				}});
			
			propose.setStyleName(BaseTheme.BUTTON_LINK);
			propose.addStyleName("bpt");
			this.addComponent(propose, "button propose");
			application.getMainWindow().executeJavaScript(getJavaScriptStringShow("propose"));
			}
		}
		
	}
	
	private String getJavaScriptStringShow(String button) {
		String js = 
        "var nodes = document.getElementById('" + entryId +"').childNodes[0].childNodes;" +
		"for(i=0; i<nodes.length; i+=1){" +
			"if(nodes[i].className == 'extension'){" +
				"var subNodes = nodes[i].childNodes;" +
				"for(j=0; j<subNodes.length; j+=1){" +
					"if(subNodes[j].className == 'button edit " + button + "'){" +
						"subNodes[j].style.display = 'block';}" +
				"}" +
			"}" +
		"}";
		return js;
	}
	
	
}
