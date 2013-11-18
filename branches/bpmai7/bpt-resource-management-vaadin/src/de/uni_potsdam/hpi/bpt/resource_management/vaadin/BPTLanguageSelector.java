package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.ArrayList;

import com.vaadin.ui.NativeSelect;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

public class BPTLanguageSelector extends NativeSelect{

	private BPTApplication application;

	public BPTLanguageSelector(final BPTApplication application){
		super();
		this.application = application;
		ArrayList<String> languageList = BPTContainerProvider.getInstance().getUniqueLanguages();
		setImmediate(true);
		for(String language : languageList){
			addItem(language);
		}
		
		addListener(new ValueChangeListener() {

			public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
				application.refreshAndClean();
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
