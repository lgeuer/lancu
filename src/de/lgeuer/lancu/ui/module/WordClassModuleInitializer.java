/*
 * File: WordClassModuleIntializer.java
 * Author: Lars Geuer
 * Date: 21.5.2007
 */

package de.lgeuer.lancu.ui.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.ui.UIController;



public class WordClassModuleInitializer implements LanguageModuleInitializer {

    private UIController controller;
    private Map<Language,UIModule> modules = new HashMap<Language,UIModule>();


    @Override
    public void initialize(UIController aController) {

	controller = aController;
    }


    @Override
    public List<UIModule> getLanguageModules(Language language) {

	ArrayList<UIModule> list = new ArrayList<UIModule>();

	if (modules.containsKey(language)) {

	    list.add(modules.get(language));
	} else {

	    UIModule module = new WordClassModule(controller,language);
	    modules.put(language,module);
	    list.add(module);
	}

	return list;
    }

}
