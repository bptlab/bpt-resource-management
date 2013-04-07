package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import com.vaadin.data.Item;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.BaseTheme;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;

@SuppressWarnings("serial")
public class BPTEntry extends CustomLayout {
	
	private String entryId;
	private BPTEntry entry;
	private String userId;
	private Item item;
	private BPTEntryCards entryCards;
	private BPTApplication application;
	private BPTToolRepository toolRepository = BPTToolRepository.getInstance();
	
	public BPTEntry(Item item, BPTApplication application, BPTEntryCards entryCards) {
		super("entry");
		entry = this;
		this.item = item;
		this.entryCards = entryCards;
		this.application = application;
		entryId = item.getItemProperty("ID").getValue().toString();
		userId = item.getItemProperty("User ID").getValue().toString();
		this.setDebugId(entryId);
		
		addButtons();
		for (Object attributeName : item.getItemPropertyIds()) {
			addToLayout(attributeName.toString());
		}
	}

	private void addToLayout(String id) {
		if (id.equals("Logo")) {
			Object value = item.getItemProperty(id).getValue();
			Embedded image = (Embedded) value;
			image.setWidth("");
			image.setHeight("");
			this.addComponent(image, id.toString());
			image.addStyleName("bptlogo");
		}
		else if (!id.equals("User ID") && !id.equals("ID") && !id.equals("Description URL") && !id.equals("Provider URL") && !id.equals("Contact mail")) {
			Object value = item.getItemProperty(id).getValue();
			if (value.getClass() == Link.class) {
				Link link = (Link) value;
				if (link.getCaption().isEmpty()) {
					addDefaultComponent(id.toString());
				} else {
					this.addComponent(link, id.toString());
				}
			} else {
				if (id.equals("Provider")) {
					String providerURL = ((Link)item.getItemProperty("Provider URL").getValue()).getCaption();
					if (providerURL.isEmpty()) {
						Label label = new Label((String) value);
						this.addComponent(label, id.toString());
					} else {
						Link link = new Link((String) value, new ExternalResource(providerURL));
						this.addComponent(link, id.toString());
					}
				} else {
					String labelContent = value.toString();
					Label label = new Label(labelContent);
					if (id == "Description") {
						label.setContentMode(Label.CONTENT_XHTML);
						String descriptionURL = ((Link)item.getItemProperty("Description URL").getValue()).getCaption();
						if (!descriptionURL.isEmpty()) {
							if (labelContent.isEmpty()) {
								labelContent = "For a description of this tool see";
							}
							labelContent = labelContent + "&nbsp;<a href='" + descriptionURL + "' target='_blank'>more</a>";
							label.setValue(labelContent);
						} else if (labelContent.isEmpty()) {
							labelContent = "This tool has no description.";
							label.setValue(labelContent);
						}
					} else if (id.equals("Contact name")) {
						label.setContentMode(Label.CONTENT_XHTML);
						String mailAddress = ((Link)item.getItemProperty("Contact mail").getValue()).getCaption();
						mailAddress = mailAddress.replace("@", "(at)"); // for obfuscation
						labelContent = labelContent + "&nbsp;&lt;" + mailAddress + "&gt;";
						label.setValue(labelContent);
					}
					label.setWidth("90%"); // TODO: Korrekte Breite ... 90% geht ganz gut ... 500px war vorher drin
					if (labelContent.isEmpty()) {
						addDefaultComponent(id.toString());
					} else {
						this.addComponent(label, id.toString());
					}
				}
			}
		}
	}

	private void addDefaultComponent(String location) {
		Label label = new Label("(none)");
		label.setWidth("90%"); // TODO: Korrekte Breite ... 90% geht ganz gut ... 500px war vorher drin
		this.addComponent(label, location);
	}

	public void addButtons() {
		Button more = new Button("more");
		more.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				addOtherButtons();
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
			}
		});
		
		more.setStyleName(BaseTheme.BUTTON_LINK);
		more.addStyleName("bpt");
		this.addComponent(more, "button more");
		Button less = new Button("less");
		less.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				hideJavaScript();
				entry.setHeight("");
			}
		});
		less.setStyleName(BaseTheme.BUTTON_LINK);
		less.addStyleName("bpt");
		this.addComponent(less, "button less");
		
	}
	
	protected void addOtherButtons() {
		
		if(application.isLoggedIn() && application.getUser().equals(userId)){
			Button edit = new Button("edit");
			edit.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					application.edit(item);
				}
			});
			
			edit.setStyleName(BaseTheme.BUTTON_LINK);
			edit.addStyleName("bpt");
			this.addComponent(edit, "button edit");
			getWindow().executeJavaScript(getJavaScriptStringShow("edit"));
		}
		
		if(application.isLoggedIn() && (application.getUser().equals(userId) || application.isModerated())){
			Button delete = new Button("delete");
			delete.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					entryCards.addConfirmationWindowTo(entryId, "delete");
				}
			});
			
			delete.setStyleName(BaseTheme.BUTTON_LINK);
			delete.addStyleName("bpt");
			this.addComponent(delete, "button delete");
			getWindow().executeJavaScript(getJavaScriptStringShow("delete"));
			System.out.println("renderDeleteButton" + entryId);
		}
		
		BPTToolStatus actualState = toolRepository.getDocumentStatus(entryId);
		
		if(application.isLoggedIn() && application.isModerated() && actualState == BPTToolStatus.Unpublished){
			Button publish = new Button("publish");
			publish.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					entryCards.addConfirmationWindowTo(entryId, "publish");
				}
			});
		
			publish.setStyleName(BaseTheme.BUTTON_LINK);
			publish.addStyleName("bpt");
			this.addComponent(publish, "button publish");
			application.getMainWindow().executeJavaScript(getJavaScriptStringShow("publish"));
			
			Button reject = new Button("reject");
			reject.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					entryCards.addConfirmationWindowTo(entryId, "reject");
				}
			});
			
			reject.setStyleName(BaseTheme.BUTTON_LINK);
			reject.addStyleName("bpt");
			this.addComponent(reject, "button reject");
			application.getMainWindow().executeJavaScript(getJavaScriptStringShow("reject"));
		}
		
		if (application.isLoggedIn() && (application.getUser().equals(userId) || application.isModerated()) && actualState == BPTToolStatus.Published){
			Button unpublish = new Button("unpublish");
			unpublish.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					entryCards.addConfirmationWindowTo(entryId, "unpublish");
				}
			});
			
			unpublish.setStyleName(BaseTheme.BUTTON_LINK);
			unpublish.addStyleName("bpt");
			this.addComponent(unpublish, "button unpublish");
			application.getMainWindow().executeJavaScript(getJavaScriptStringShow("unpublish"));
		
		}
		
		if(application.isLoggedIn() && application.isModerated() && actualState == BPTToolStatus.Rejected){
			Button propose = new Button("propose");
			propose.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					entryCards.addConfirmationWindowTo(entryId, "propose");
				}
			});
			
			propose.setStyleName(BaseTheme.BUTTON_LINK);
			propose.addStyleName("bpt");
			this.addComponent(propose, "button propose");
			application.getMainWindow().executeJavaScript(getJavaScriptStringShow("propose"));
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
						"subNodes[j].style.display = 'block';" +
						"break;}" +
				"}" +
			"}" +
		"}";
		return js;
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
		
	}
	
	public void hideJavaScript(){
		application.getMainWindow().executeJavaScript(getJavaScriptStringHide());
	}
	
}
