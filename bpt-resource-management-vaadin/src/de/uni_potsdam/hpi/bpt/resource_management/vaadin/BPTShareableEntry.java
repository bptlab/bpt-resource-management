package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTUserRepository;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

@SuppressWarnings({"serial"})
public class BPTShareableEntry extends CustomLayout {
	
	private String entryId;
	private String userId;
	private Item item;
	private BPTApplicationUI applicationUI;
	private BPTToolRepository toolRepository = BPTToolRepository.getInstance();
	private BPTUserRepository userRepository = BPTUserRepository.getInstance();
	
	public BPTShareableEntry(Item item, BPTApplicationUI applicationUI) {
		super("shareableEntry");
		this.item = item;
		this.applicationUI = applicationUI;
		entryId = item.getItemProperty("ID").getValue().toString();
		userId = item.getItemProperty("User ID").getValue().toString();
		this.setId(entryId);
		
		addAdministrationButtons();
		for (Object attributeName : item.getItemPropertyIds()) {
			addToLayout(attributeName.toString());
		}
	}

	private void addToLayout(String id) {
		if (id.equals("Logo")) {
			Object value = item.getItemProperty(id).getValue();
			if (value != null) {
				Image image = (Image) value;
				image.setWidth("");
				image.setHeight("");
				this.addComponent(image, id);
				image.addStyleName("bptlogo");
			}
		} else if (!id.equals("User ID") && !id.equals("ID") && !id.equals("Description URL") && !id.equals("Provider URL") && !id.equals("Contact mail") && !id.equals("Date created")) {
			Object value = item.getItemProperty(id).getValue();
			if(value == null){
				return;
			}
			if (value.getClass() == Link.class) {
				String url = ((Link) value).getCaption();
				if (!url.isEmpty()) {
					Label label = new Label("<i><span style=\"margin-left: -1em\">" + id + "</span></i>" + "<span style=\"margin-left: 1em; display: block\"><a href='" + url + "' target='_blank'>" + url + "</a></span>");
					label.setContentMode(ContentMode.HTML);
					label.setWidth("90%");
					this.addComponent(label, id);
				}
			} else {
				if (id.equals("Provider")) {
					String providerURL = ((Link) item.getItemProperty("Provider URL").getValue()).getCaption();
					if (providerURL.isEmpty()) {
						Label label = new Label("<i><span style=\"margin-left: -1em\">" + id + "</span></i><br/><span style=\"margin-left: 1em; display: block\">" + (String) value + "</span>");
						label.setContentMode(ContentMode.HTML);
						label.setWidth("90%");
						this.addComponent(label, id);
					} else {
						Label label = new Label("<i><span style=\"margin-left: -1em\">" + id + "</span></i><br/>" + "<span style=\"margin-left: 1em; display: block\"><a href='" + providerURL + "' target='_blank'>" + (String) value + "</a></span>");
						label.setContentMode(ContentMode.HTML);
						this.addComponent(label, id);
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
						label.setContentMode(ContentMode.HTML);
						label.setWidth("90%"); // works fine, was 500px
						this.addComponent(label, id);
					}
				}
			}
		} else if (id.equals("User ID") && applicationUI.isModerated()) {
			String userId = item.getItemProperty(id).getValue().toString();
			Label label = new Label("<i><span style=\"margin-left: -1em\">OpenID of resource provider</span></i><span style=\"margin-left: 1em; display: block\">" + userId + "</span>");
			label.setContentMode(ContentMode.HTML);
			label.setWidth("90%");
			this.addComponent(label, "OpenID of resource provider");
			Map<String, Object> document = userRepository.readDocument(userId);
			String name = (String) document.get("name");
			String mailAddress = (String) document.get("mail_address");
			mailAddress = mailAddress.replace("@", "(at)"); // for obfuscation
			label = new Label("<i><span style=\"margin-left: -1em\">Contact of resource provider</span></i><span style=\"margin-left: 1em; display: block\">" + name + "&nbsp;&lt;" + mailAddress + "&gt;" + "</span>");
			label.setContentMode(ContentMode.HTML);
			label.setWidth("90%");
			this.addComponent(label, "Contact of resource provider");
		} 
	}

	public void addAdministrationButtons() {
		if (applicationUI.isLoggedIn()) {
			if (applicationUI.getUser().equals(userId)) {
				Button edit = new Button("edit");
				edit.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						applicationUI.edit(item);
					}
				});
				edit.setStyleName(BaseTheme.BUTTON_LINK);
				edit.addStyleName("bpt");
				this.addComponent(edit, "button edit");
				JavaScript.getCurrent().execute(getJavaScriptStringShow("edit"));
			}
			
			if ((applicationUI.getUser().equals(userId) || applicationUI.isModerated())) {
				Button delete = new Button("delete");
				delete.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						addConfirmationWindow("delete");
					}
				});
				delete.setStyleName(BaseTheme.BUTTON_LINK);
				delete.addStyleName("bpt");
				this.addComponent(delete, "button delete");
				JavaScript.getCurrent().execute(getJavaScriptStringShow("delete"));
			}
			
			BPTToolStatus actualState = toolRepository.getDocumentStatus(entryId);
			
			if (applicationUI.isModerated() && actualState == BPTToolStatus.Unpublished) {
				Button publish = new Button("publish");
				publish.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						addConfirmationWindow("publish");
					}
				});
				publish.setStyleName(BaseTheme.BUTTON_LINK);
				publish.addStyleName("bpt");
				this.addComponent(publish, "button publish");
				JavaScript.getCurrent().execute(getJavaScriptStringShow("publish"));
				
				Button reject = new Button("reject");
				reject.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						addConfirmationWindow("reject");
					}
				});
				reject.setStyleName(BaseTheme.BUTTON_LINK);
				reject.addStyleName("bpt");
				this.addComponent(reject, "button reject");
				JavaScript.getCurrent().execute(getJavaScriptStringShow("reject"));
			}
			
			if ((applicationUI.getUser().equals(userId) || applicationUI.isModerated()) && actualState == BPTToolStatus.Published) {
				Button unpublish = new Button("unpublish");
				unpublish.addClickListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						addConfirmationWindow("unpublish");
					}
				});
				unpublish.setStyleName(BaseTheme.BUTTON_LINK);
				unpublish.addStyleName("bpt");
				this.addComponent(unpublish, "button unpublish");
				JavaScript.getCurrent().execute(getJavaScriptStringShow("unpublish"));
			}
			
			if (applicationUI.isModerated() && actualState == BPTToolStatus.Rejected) {
				Button propose = new Button("propose");
				propose.addClickListener(new Button.ClickListener(){
					public void buttonClick(ClickEvent event) {
						addConfirmationWindow("propose");
					}
				});
				propose.setStyleName(BaseTheme.BUTTON_LINK);
				propose.addStyleName("bpt");
				this.addComponent(propose, "button propose");
				JavaScript.getCurrent().execute(getJavaScriptStringShow("propose"));
			}
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
			confirmationWindow.setContent(new Label("Deleting this entry - are you sure?"));
		} else if (status.equals("publish")) {
			confirmationWindow.setContent(new Label("Publishing this entry - are you sure?"));
		} else if (status.equals("reject")) {
			confirmationWindow.setContent(new Label("Rejecting this entry - are you sure?"));
			reasonForRejectionTextArea.setInputPrompt("Please describe the reason for rejecting the entry and/or provide hints for improving it.");
			reasonForRejectionTextArea.setRows(5);
			reasonForRejectionTextArea.setWidth("95%");
			reasonForRejectionTextArea.setWordwrap(true);
			confirmationWindow.setContent(reasonForRejectionTextArea);
		} else if (status.equals("unpublish")) { 
			confirmationWindow.setContent(new Label("Unpublishing this entry - are you sure?"));
		} else { // if status.equals("propose")
			confirmationWindow.setContent(new Label("Proposing this entry - are you sure?"));
		}
		Button confirmButton = new Button("Confirm");
		confirmationWindow.setContent(confirmButton);
		confirmButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				if (status.equals("delete")) {
					toolRepository.deleteDocument(entryId, ((BPTApplicationUI)getUI()).isModerated());
				} else if (status.equals("publish")) {
					toolRepository.publishDocument(entryId);
				} else if (status.equals("reject")) {
					toolRepository.rejectDocument(entryId, (String) reasonForRejectionTextArea.getValue());
				} else if (status.equals("unpublish")) { 
					boolean fromPublished = true;
					toolRepository.unpublishDocument(entryId, fromPublished, ((BPTApplicationUI)getUI()).isModerated());
				} else { // if status.equals("propose"))
					boolean fromRejected = false;
					toolRepository.unpublishDocument(entryId, fromRejected);
				}
				BPTContainerProvider.getInstance().refreshFromDatabase();
				applicationUI.removeWindow(confirmationWindow);
			}
		});
		applicationUI.addWindow(confirmationWindow);
	}

	public void removeButtons() {
		this.removeComponent("button edit");
		this.removeComponent("button delete");
		this.removeComponent("button publish");
		this.removeComponent("button reject");
		this.removeComponent("button unpublish");
		this.removeComponent("button propose");
		JavaScript.getCurrent().execute("var elements = document.getElementsByClassName('button edit');" +
				"for(var i = 0; i < elements.length; i++){elements[i].style.display = 'none';}");
	}
}
