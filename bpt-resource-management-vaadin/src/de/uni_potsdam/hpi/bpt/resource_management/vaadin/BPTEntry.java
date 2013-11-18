package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.BaseTheme;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTDocumentType;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseSetRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseStatus;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

@SuppressWarnings({"serial", "rawtypes"})
public class BPTEntry extends CustomLayout {
	
	private String id, setId, userId;
	private BPTEntry entry;
	private Item item;
	private BPTEntryCards entryCards;
	private BPTApplicationUI applicationUI;
	private BPTExerciseSetRepository exerciseSetRepository = BPTExerciseSetRepository.getInstance();
	private BPTExerciseRepository exerciseRepository = BPTExerciseRepository.getInstance();
	private HorizontalLayout tabLayout, subEntryLayout;
	private Map<String, BPTSubEntry> subentries;
	
	public BPTEntry(Item item, BPTApplicationUI applicationUI, BPTEntryCards entryCards) {
		super("entry");
		entry = this;
		this.item = item;
		this.entryCards = entryCards;
		this.applicationUI = applicationUI;
		id = item.getItemProperty("ID").getValue().toString();
		setId = item.getItemProperty("Exercise Set ID").getValue().toString();
		userId = item.getItemProperty("User ID").getValue().toString();
		this.setId(this.setId);
		
		addButtons();
		for (Object attributeName : item.getItemPropertyIds()) {
			addToLayout(attributeName.toString());
		}
	}

	private void addToLayout(String id) {
		if (!id.equals("Exercise Set ID") && !id.equals("User ID") && !id.equals("ID") && !id.equals("Contact mail")) {
			Object value = item.getItemProperty(id).getValue();
			if (value.getClass() == Link.class) {
				Link link = (Link) value;
				if (link.getCaption().isEmpty()) {
					addDefaultComponent(id.toString());
				} else {
					this.addComponent(link, id.toString());
				}
			} 
			else {
					String labelContent = value.toString();
					Label label = new Label(labelContent);
					if (id.equals("Contact name")) {
						label.setContentMode(ContentMode.HTML);
						String mailAddress = ((Link)item.getItemProperty("Contact mail").getValue()).getCaption();
						mailAddress = mailAddress.replace("@", "(at)"); // for obfuscation
						labelContent = labelContent + "&nbsp;&lt;" + mailAddress + "&gt;";
						label.setValue(labelContent);
					}
					label.setWidth("90%");
					if (labelContent.isEmpty()) {
						addDefaultComponent(id.toString());
					} else {
						this.addComponent(label, id.toString());
					}
			}
		}
//		tabsheet = new TabSheet();
//		this.addComponent(tabsheet, "Tabs");
////			tabsheet.addStyleName("border");
//		List<Map> relatedEntries = exerciseRepository.getDocumentsBySetId(setId);
//		IndexedContainer entries = BPTContainerProvider.getInstance().generateContainer(relatedEntries, false);
//		for(Object entryId : entries.getItemIds()){
//			BPTSubEntry subEntry = new BPTSubEntry(entries.getItem(entryId));
//			tabsheet.addComponent(subEntry);
//			String languageOfEntry = item.getItemProperty("Language").getValue().toString();
//			tabsheet.getTab(subEntry).setCaption(languageOfEntry);
//			if(languageOfEntry.equals(application.getSelectedLanguage())){
//				tabsheet.setSelectedTab(subEntry);
//			}
//		}
		subentries = new HashMap<String, BPTSubEntry>();
		subEntryLayout = new HorizontalLayout();
		tabLayout = new HorizontalLayout();
		this.addComponent(subEntryLayout, "Tabs");
		List<Map> relatedEntries = exerciseRepository.getDocumentsBySetId(setId);
		IndexedContainer entries = BPTContainerProvider.getInstance().generateContainer(relatedEntries, BPTDocumentType.BPMAI_EXERCISES);
		for(Object entryId : entries.getItemIds()){
			Item subItem = entries.getItem(entryId);
			final String languageOfEntry = subItem.getItemProperty("Language").getValue().toString();
			BPTSubEntry subEntry = new BPTSubEntry(subItem);
			subEntry.setWidth("90%");
			subentries.put(languageOfEntry, subEntry);
			final Button tabButton = new Button(languageOfEntry);
			tabButton.addClickListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					subEntryLayout.removeAllComponents();
					subEntryLayout.addComponent(subentries.get(languageOfEntry));
					Iterator<Component> iterator = tabLayout.getComponentIterator();
					while(iterator.hasNext()){
						Component component = iterator.next();
						component.removeStyleName("chosenTab");
						component.requestRepaint();
				}
					tabButton.addStyleName("chosenTab");
					tabButton.requestRepaint();
				}
			});
			tabButton.setStyleName(BaseTheme.BUTTON_LINK);
			tabButton.addStyleName("tab");
			tabButton.setId(setId + "_" + languageOfEntry);
			tabLayout.addComponent(tabButton);
		}
		this.addComponent(tabLayout, "TabButtons");
		String language = subentries.keySet().iterator().next();
		subEntryLayout.addComponent(subentries.get(language));
		
		Iterator<Component> iterator = tabLayout.getComponentIterator();
		while(iterator.hasNext()){
			Component component = iterator.next();
			if(component.getCaption().equals(language)){
				component.addStyleName("chosenTab");
				component.requestRepaint();
				break;
			}
	}
	}


	private void addDefaultComponent(String location) {
		Label label = new Label("(none)");
		label.setWidth("90%"); // TODO: Korrekte Breite ... 90% geht ganz gut ... 500px war vorher drin
		this.addComponent(label, location);
	}

	public void addButtons() {
		// TODO: implement shareable entry page
//		Button share = new Button("share");
//		share.addClickListener(new Button.ClickListener(){
//			public void buttonClick(ClickEvent event) {
//				applicationUI.showSpecificEntry(entryId);
//			}
//		});
//		share.setStyleName(BaseTheme.BUTTON_LINK);
//		share.addStyleName("bpt");
//		this.addComponent(share, "button share");
		
		Button more = new Button("more");
		more.addClickListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				addOtherButtons();
				JavaScript.getCurrent().execute(getJavaScriptStringShow());
				entry.setHeight("");
			}

			private String getJavaScriptStringShow() {
				String js = 
		        "var nodes = document.getElementById('" + setId +"').childNodes[0].childNodes;" +
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
		more.addStyleName("whiteButtonHover");
		this.addComponent(more, "button more");
		Button less = new Button("less");
		less.addClickListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				hideJavaScript();
				entry.setHeight("");
			}
		});
		less.setStyleName(BaseTheme.BUTTON_LINK);
		less.addStyleName("bpt");
		less.addStyleName("whiteButtonHover");
		this.addComponent(less, "button less");
		
	}
	
	protected void addOtherButtons() {
		
		if(applicationUI.isLoggedIn() && applicationUI.getUser().equals(userId)){
			Button edit = new Button("edit");
			edit.addClickListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					applicationUI.edit(item);
				}
			});
			
			edit.setStyleName(BaseTheme.BUTTON_LINK);
			edit.addStyleName("bpt");
			edit.addStyleName("whiteButtonHover");
			this.addComponent(edit, "button edit");
			JavaScript.getCurrent().execute(getJavaScriptStringShow("edit"));
		}
		
		if(applicationUI.isLoggedIn() && (applicationUI.getUser().equals(userId) || applicationUI.isModerated())){
			Button delete = new Button("delete");
			delete.addClickListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					entryCards.addConfirmationWindowTo(setId, "delete");
				}
			});
			
			delete.setStyleName(BaseTheme.BUTTON_LINK);
			delete.addStyleName("bpt");
			delete.addStyleName("whiteButtonHover");
			this.addComponent(delete, "button delete");
			JavaScript.getCurrent().execute(getJavaScriptStringShow("delete"));
			System.out.println("renderDeleteButton" + setId);
		}
		
		BPTExerciseStatus actualState = exerciseSetRepository.getDocumentStatus(id);
		
		if(applicationUI.isLoggedIn() && applicationUI.isModerated() && actualState == BPTExerciseStatus.Unpublished){
			Button publish = new Button("publish");
			publish.addClickListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					entryCards.addConfirmationWindowTo(setId, "publish");
				}
			});
		
			publish.setStyleName(BaseTheme.BUTTON_LINK);
			publish.addStyleName("bpt");
			publish.addStyleName("whiteButtonHover");
			this.addComponent(publish, "button publish");
			JavaScript.getCurrent().execute(getJavaScriptStringShow("publish"));
			
			Button reject = new Button("reject");
			reject.addClickListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					entryCards.addConfirmationWindowTo(setId, "reject");
				}
			});
			
			reject.setStyleName(BaseTheme.BUTTON_LINK);
			reject.addStyleName("bpt");
			reject.addStyleName("whiteButtonHover");
			this.addComponent(reject, "button reject");
			JavaScript.getCurrent().execute(getJavaScriptStringShow("reject"));
		}
		
		if (applicationUI.isLoggedIn() && (applicationUI.getUser().equals(userId) || applicationUI.isModerated()) && actualState == BPTExerciseStatus.Published){
			Button unpublish = new Button("unpublish");
			unpublish.addClickListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					entryCards.addConfirmationWindowTo(setId, "unpublish");
				}
			});
			
			unpublish.setStyleName(BaseTheme.BUTTON_LINK);
			unpublish.addStyleName("bpt");
			unpublish.addStyleName("whiteButtonHover");
			this.addComponent(unpublish, "button unpublish");
			JavaScript.getCurrent().execute(getJavaScriptStringShow("unpublish"));
		
		}
		
		if(applicationUI.isLoggedIn() && applicationUI.isModerated() && actualState == BPTExerciseStatus.Rejected){
			Button propose = new Button("propose");
			propose.addClickListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					entryCards.addConfirmationWindowTo(setId, "propose");
				}
			});
			
			propose.setStyleName(BaseTheme.BUTTON_LINK);
			propose.addStyleName("bpt");
			propose.addStyleName("whiteButtonHover");
			this.addComponent(propose, "button propose");
			JavaScript.getCurrent().execute(getJavaScriptStringShow("propose"));
		}
		
	}

	private String getJavaScriptStringShow(String button) {
		String js = 
        "var nodes = document.getElementById('" + setId +"').childNodes[0].childNodes;" +
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
        "var nodes = document.getElementById('" + setId +"').childNodes[0].childNodes;" +
		"for(i=0; i<nodes.length; i+=1){" +
			"if(nodes[i].className == 'extension'){" +
				"nodes[i].style.display = 'none';}" +
			"if(nodes[i].className == 'button more'){" +
				"nodes[i].style.display = 'block';}" +
			"}";
		return js;
		
	}
	
	public void hideJavaScript() {
		JavaScript.getCurrent().execute(getJavaScriptStringHide());
	}
	
}
