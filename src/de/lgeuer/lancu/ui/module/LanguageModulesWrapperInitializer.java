/*
 * File: PhonemeModuleIntializer.java
 * Author: Lars Geuer
 * Date: 1.5.2007
 */

package de.lgeuer.lancu.ui.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.ui.UIController;



public class LanguageModulesWrapperInitializer implements LanguageModuleInitializer {

    private UIController controller;
    private Map<Language,UIModule> moduleWrappers = new HashMap<Language,UIModule>();


    @Override
    public void initialize(UIController aController) {

	controller = aController;
    }


    @Override
    public List<UIModule> getLanguageModules(Language language) {

	ArrayList<UIModule> list = new ArrayList<UIModule>();

	if (moduleWrappers.containsKey(language)) {

	    list.add(moduleWrappers.get(language));
	} else {

	    UIModule module = new LanguageModulesWrapper(controller, language);
	    moduleWrappers.put(language,module);
	    list.add(module);
	}

	return list;
    }

}
