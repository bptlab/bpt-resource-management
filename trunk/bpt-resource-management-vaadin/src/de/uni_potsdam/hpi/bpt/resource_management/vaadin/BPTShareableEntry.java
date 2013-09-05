package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTUserRepository;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

@SuppressWarnings("serial")
public class BPTShareableEntry extends CustomLayout {
	
	private String entryId;
	private String userId;
	private Item item;
	private BPTApplication application;
	private BPTToolRepository toolRepository = BPTToolRepository.getInstance();
	private BPTUserRepository userRepository = BPTUserRepository.getInstance();
	
	public BPTShareableEntry(Item item, BPTApplication application) {
		super("shareable_entry");
		this.item = item;
		this.application = application;
		entryId = item.getItemProperty("ID").getValue().toString();
		userId = item.getItemProperty("User ID").getValue().toString();
		this.setDebugId(entryId);
		
//		addButtons();
		addOtherButtons();
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
		} else if (!id.equals("User ID") && !id.equals("ID") && !id.equals("Description URL") && !id.equals("Provider URL") && !id.equals("Contact mail") && !id.equals("Date created")) {
			Object value = item.getItemProperty(id).getValue();
			if(value == null){
				return;
			}
			if (value.getClass() == Link.class) {
				String url = ((Link) value).getCaption();
				if (!url.isEmpty()) {
					Label label = new Label("<i><span style=\"margin-left: -1em\">" + id + "</span></i>" + "<span style=\"margin-left: 1em; display: block\"><a href='" + url + "' target='_blank'>" + url + "</a></span>");
					label.setContentMode(Label.CONTENT_XHTML);
					label.setWidth("90%");
					this.addComponent(label, id.toString());
				}
			} else {
				if (id.equals("Provider")) {
					String providerURL = ((Link) item.getItemProperty("Provider URL").getValue()).getCaption();
					if (providerURL.isEmpty()) {
						Label label = new Label("<i><span style=\"margin-left: -1em\">" + id + "</span></i><br/><span style=\"margin-left: 1em; display: block\">" + (String) value + "</span>");
						label.setContentMode(Label.CONTENT_XHTML);
						label.setWidth("90%");
						this.addComponent(label, id.toString());
					} else {
						Label label = new Label("<i><span style=\"margin-left: -1em\">" + id + "</span></i><br/>" + "<span style=\"margin-left: 1em; display: block\"><a href='" + providerURL + "' target='_blank'>" + (String) value + "</a></span>");
						label.setContentMode(Label.CONTENT_XHTML);
						this.addComponent(label, id.toString());
					}
				} else {
					String labelContent = value.toString();
					if (id.equals("Description")) {
						String descriptionURL = ((Link)item.getItemProperty("Description URL").getValue()).getCaption();
						if (!descriptionURL.isEmpty()) {
							if (labelContent.isEmpty()) {
								labelContent = "For a description of this tool see";
							}
							labelContent = labelContent + "&nbsp;<a href='" + descriptionURL + "' target='_blank'>more</a>";
						} else if (labelContent.isEmpty()) {
							labelContent = "This tool has no description.";
						}
					} else if (id.equals("Contact name")) {
						String mailAddress = ((Link)item.getItemProperty("Contact mail").getValue()).getCaption();
						mailAddress = mailAddress.replace("@", "(at)"); // for obfuscation
						labelContent = labelContent + "&nbsp;&lt;" + mailAddress + "&gt;";
					} else if (id.equals("Last update")) {
						Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						labelContent = formatter.format((Date)item.getItemProperty("Last update").getValue());
					}
					if (!labelContent.isEmpty()) {
						Label label;
						if (id == "Name") {
							label = new Label("<span style=\"display: block\">" + labelContent + "</span>");
						} else if (id == "Description") {
							label = new Label("<span style=\"display: block\">" + labelContent + "</span></br>");
						} else if (id == "Contact name") {
							label = new Label("<i><span style=\"margin-left: -1em\">Contact</span></i></br><span style=\"margin-left: 1em; display: block\">" + labelContent + "</span>");
						} else {
							label = new Label("<i><span style=\"margin-left: -1em\">" + id + "</span></i></br><span style=\"margin-left: 1em; display: block\">" + labelContent + "</span>");
						}
						label.setContentMode(Label.CONTENT_XHTML);
						label.setWidth("90%"); // TODO: Korrekte Breite ... 90% geht ganz gut ... 500px war vorher drin
						this.addComponent(label, id.toString());
					}
				}
			}
		} else if (id.equals("User ID") && application.isModerated()) {
			String userId = item.getItemProperty(id).getValue().toString();
			Label label = new Label("<i><span style=\"margin-left: -1em\">OpenID of resource provider</span></i><span style=\"margin-left: 1em; display: block\">" + userId + "</span>");
			label.setContentMode(Label.CONTENT_XHTML);
			label.setWidth("90%");
			this.addComponent(label, "OpenID of resource provider");
			Map<String, Object> document = userRepository.readDocument(userId);
			String name = (String) document.get("name");
			String mailAddress = (String) document.get("mail_address");
			mailAddress = mailAddress.replace("@", "(at)"); // for obfuscation
			label = new Label("<i><span style=\"margin-left: -1em\">Contact of resource provider</span></i><span style=\"margin-left: 1em; display: block\">" + name + "&nbsp;&lt;" + mailAddress + "&gt;" + "</span>");
			label.setContentMode(Label.CONTENT_XHTML);
			label.setWidth("90%");
			this.addComponent(label, "Contact of resource provider");
		} 
	}

//	public void addButtons() {
//		Button share = new Button("share");
//		share.addListener(new Button.ClickListener(){
//			public void buttonClick(ClickEvent event) {
//				addOtherButtons();
//				application.getMainWindow().executeJavaScript(getJavaScriptStringShow());
//				entry.setHeight("");
//			}
//		});
//		share.setStyleName(BaseTheme.BUTTON_LINK);
//		share.addStyleName("bpt");
//		this.addComponent(share, "button share");
//	}
	
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
			application.getMainWindow().executeJavaScript(getJavaScriptStringShow("edit"));
		}
		
		if(application.isLoggedIn() && (application.getUser().equals(userId) || application.isModerated())){
			Button delete = new Button("delete");
			delete.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					addConfirmationWindow("delete");
				}
			});
			
			delete.setStyleName(BaseTheme.BUTTON_LINK);
			delete.addStyleName("bpt");
			this.addComponent(delete, "button delete");
			application.getMainWindow().executeJavaScript(getJavaScriptStringShow("delete"));
//			System.out.println("renderDeleteButton" + entryId);
		}
		
		BPTToolStatus actualState = toolRepository.getDocumentStatus(entryId);
		
		if(application.isLoggedIn() && application.isModerated() && actualState == BPTToolStatus.Unpublished){
			Button publish = new Button("publish");
			publish.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					addConfirmationWindow("publish");
				}
			});
		
			publish.setStyleName(BaseTheme.BUTTON_LINK);
			publish.addStyleName("bpt");
			this.addComponent(publish, "button publish");
			application.getMainWindow().executeJavaScript(getJavaScriptStringShow("publish"));
			
			Button reject = new Button("reject");
			reject.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					addConfirmationWindow("reject");
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
					addConfirmationWindow("unpublish");
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
					addConfirmationWindow("propose");
				}
			});
			
			propose.setStyleName(BaseTheme.BUTTON_LINK);
			propose.addStyleName("bpt");
			this.addComponent(propose, "button propose");
			application.getMainWindow().executeJavaScript(getJavaScriptStringShow("propose"));
		}
		
	}

	private String getJavaScriptStringShow(String button) {
		return "document.getElementById('button edit " + button + "').style.display = 'block';";
	}
	
	protected void addConfirmationWindow(final String status) {
		final TextArea reasonForRejectionTextArea = new TextArea();
		final Window confirmationWindow = new Window("Notification");
		confirmationWindow.setWidth("400px");
		confirmationWindow.setModal(true);
		if (status.equals("delete")) {
			confirmationWindow.addComponent(new Label("Deleting this entry - are you sure?"));
		} else if (status.equals("publish")) {
			confirmationWindow.addComponent(new Label("Publishing this entry - are you sure?"));
		} else if (status.equals("reject")) {
			confirmationWindow.addComponent(new Label("Rejecting this entry - are you sure?"));
			reasonForRejectionTextArea.setInputPrompt("Please describe the reason for rejecting the entry and/or provide hints for improving it.");
			reasonForRejectionTextArea.setRows(5);
			reasonForRejectionTextArea.setWidth("95%");
			reasonForRejectionTextArea.setWordwrap(true);
			confirmationWindow.addComponent(reasonForRejectionTextArea);
		} else if (status.equals("unpublish")) { 
			confirmationWindow.addComponent(new Label("Unpublishing this entry - are you sure?"));
		} else { // if status.equals("propose")
			confirmationWindow.addComponent(new Label("Proposing this entry - are you sure?"));
		}
		Button confirmButton = new Button("Confirm");
		confirmationWindow.addComponent(confirmButton);
		confirmButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				if (status.equals("delete")) {
					toolRepository.deleteDocument(entryId, ((BPTApplication)getApplication()).isModerated());
				} else if (status.equals("publish")) {
					toolRepository.publishDocument(entryId);
				} else if (status.equals("reject")) {
					toolRepository.rejectDocument(entryId, (String) reasonForRejectionTextArea.getValue());
				} else if (status.equals("unpublish")) { 
					boolean fromPublished = true;
					toolRepository.unpublishDocument(entryId, fromPublished, ((BPTApplication)getApplication()).isModerated());
				} else { // if status.equals("propose"))
					boolean fromRejected = false;
					toolRepository.unpublishDocument(entryId, fromRejected);
				}
				BPTContainerProvider.refreshFromDatabase();
				application.getMainWindow().removeWindow(confirmationWindow);
			}
		});
		application.getMainWindow().addWindow(confirmationWindow);
	}
	
}
