package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import com.vaadin.ui.NativeSelect;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

@SuppressWarnings("serial")
public class BPTLanguageSelector extends NativeSelect {

	public BPTLanguageSelector(final BPTApplicationUI applicationUI) {
		super();
		ArrayList<String> languageList = BPTContainerProvider.getInstance().getUniqueLanguages();
		setImmediate(true);
		for(String language : languageList){
			addItem(language);
		}
		
		addValueChangeListener(new ValueChangeListener() {

			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
				applicationUI.refreshAndClean();
			}
        });
	}
        
	public String getLanguage(){
		if(getValue() != null){
			return getValue().toString();
		}
		return "Deutsch";
	}
}
