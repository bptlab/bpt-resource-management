package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.BaseTheme;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseSetRepository;
import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTExerciseStatus;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

@SuppressWarnings("serial")
public class BPTEntry extends CustomLayout {
	
	private String id, setId, userId;
	private BPTEntry entry;
	private Item item;
	private BPTEntryCards entryCards;
	private BPTApplication application;
	private BPTExerciseSetRepository exerciseSetRepository = BPTExerciseSetRepository.getInstance();
	private BPTExerciseRepository exerciseRepository = BPTExerciseRepository.getInstance();
	private HorizontalLayout tabLayout, subEntryLayout;
	private Map<String, BPTSubEntry> subentries;
	
	public BPTEntry(Item item, BPTApplication application, BPTEntryCards entryCards) {
		super("entry");
		entry = this;
		this.item = item;
		this.entryCards = entryCards;
		this.application = application;
		id = item.getItemProperty("ID").getValue().toString();
		setId = item.getItemProperty("Exercise Set ID").getValue().toString();
		userId = item.getItemProperty("User ID").getValue().toString();
		this.setDebugId(this.setId);
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
		IndexedContainer entries = BPTContainerProvider.getInstance().generateContainer(relatedEntries, false);
		for(Object entryId : entries.getItemIds()){
			Item subItem = entries.getItem(entryId);
			final String languageOfEntry = subItem.getItemProperty("Language").getValue().toString();
			BPTSubEntry subEntry = new BPTSubEntry(subItem);
			subentries.put(languageOfEntry, subEntry);
			Button tabButton = new Button(languageOfEntry);
			tabButton.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					subEntryLayout.removeAllComponents();
					subEntryLayout.addComponent(subentries.get(languageOfEntry));
				}
			});
			tabButton.setStyleName(BaseTheme.BUTTON_LINK);
			tabLayout.addComponent(tabButton);
		}
		this.addComponent(tabLayout, "TabButtons");
		subEntryLayout.addComponent(subentries.values().iterator().next());
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
		less.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				hideJavaScript();
				entry.setHeight("");
			}
		});
		less.setStyleName(BaseTheme.BUTTON_LINK);
		less.addStyleName("bpt");
		less.addStyleName("redButtonHover");
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
			edit.addStyleName("redButtonHover");
			this.addComponent(edit, "button edit");
			getWindow().executeJavaScript(getJavaScriptStringShow("edit"));
		}
		
		if(application.isLoggedIn() && (application.getUser().equals(userId) || application.isModerated())){
			Button delete = new Button("delete");
			delete.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					entryCards.addConfirmationWindowTo(setId, "delete");
				}
			});
			
			delete.setStyleName(BaseTheme.BUTTON_LINK);
			delete.addStyleName("bpt");
			delete.addStyleName("redButtonHover");
			this.addComponent(delete, "button delete");
			getWindow().executeJavaScript(getJavaScriptStringShow("delete"));
			System.out.println("renderDeleteButton" + setId);
		}
		
		BPTExerciseStatus actualState = exerciseSetRepository.getDocumentStatus(id);
		
		if(application.isLoggedIn() && application.isModerated() && actualState == BPTExerciseStatus.Unpublished){
			Button publish = new Button("publish");
			publish.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					entryCards.addConfirmationWindowTo(setId, "publish");
				}
			});
		
			publish.setStyleName(BaseTheme.BUTTON_LINK);
			publish.addStyleName("bpt");
			publish.addStyleName("redButtonHover");
			this.addComponent(publish, "button publish");
			application.getMainWindow().executeJavaScript(getJavaScriptStringShow("publish"));
			
			Button reject = new Button("reject");
			reject.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					entryCards.addConfirmationWindowTo(setId, "reject");
				}
			});
			
			reject.setStyleName(BaseTheme.BUTTON_LINK);
			reject.addStyleName("bpt");
			reject.addStyleName("redButtonHover");
			this.addComponent(reject, "button reject");
			application.getMainWindow().executeJavaScript(getJavaScriptStringShow("reject"));
		}
		
		if (application.isLoggedIn() && (application.getUser().equals(userId) || application.isModerated()) && actualState == BPTExerciseStatus.Published){
			Button unpublish = new Button("unpublish");
			unpublish.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					entryCards.addConfirmationWindowTo(setId, "unpublish");
				}
			});
			
			unpublish.setStyleName(BaseTheme.BUTTON_LINK);
			unpublish.addStyleName("bpt");
			unpublish.addStyleName("redButtonHover");
			this.addComponent(unpublish, "button unpublish");
			application.getMainWindow().executeJavaScript(getJavaScriptStringShow("unpublish"));
		
		}
		
		if(application.isLoggedIn() && application.isModerated() && actualState == BPTExerciseStatus.Rejected){
			Button propose = new Button("propose");
			propose.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					entryCards.addConfirmationWindowTo(setId, "propose");
				}
			});
			
			propose.setStyleName(BaseTheme.BUTTON_LINK);
			propose.addStyleName("bpt");
			propose.addStyleName("redButtonHover");
			this.addComponent(propose, "button propose");
			application.getMainWindow().executeJavaScript(getJavaScriptStringShow("propose"));
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
	
	public void hideJavaScript(){
		application.getMainWindow().executeJavaScript(getJavaScriptStringHide());
	}
	
}
