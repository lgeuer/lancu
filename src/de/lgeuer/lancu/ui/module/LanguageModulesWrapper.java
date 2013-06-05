/*
 * File: PhonemeModule.java
 * Author: Lars Geuer
 * Date: 1.5.2007
 */

package de.lgeuer.lancu.ui.module;

import net.infonode.docking.DockingWindow;
import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.ui.UIController;



public class LanguageModulesWrapper extends UIModule {

    private static final long serialVersionUID = 6863029726693072944L;

    private UIController controller;
    private Language language;

    private PhonemeModule phonemeModule;

    private WordClassModule wordClassModule;

    private VocabularyModule vocabularyModule;

    private DockingWindow window;

    public LanguageModulesWrapper(UIController aController,Language aLanguage) {

	super(aController);
	controller = aController;
	language = aLanguage;
    }
    
    @Override
    public DockingWindow getDockingWindow() {
        window = super.getDockingWindow();
        return window;
    }


    @Override
    public String getName() {

	return language.getName() + " - All";
    }


    @Override
    public String getShortName() {

	return language.getName() + " - All";
    }


    @Override
    public Language getLanguage() {

	return language;
    }


    @Override
    public void setUp() {

	phonemeModule = new PhonemeModule(controller, language);
	wordClassModule = new WordClassModule(controller, language);
	vocabularyModule = new VocabularyModule(controller, language);

	controller.openUIModule(phonemeModule);
	controller.openUIModule(wordClassModule);
	controller.openUIModule(vocabularyModule);
	
	window.close();
	controller.closeUIModule(this);

    }

    @Override
    public void shutDown() {

//	phonemeModule.shutDown();
//	wordClassModule.shutDown();
//	vocabularyModule.shutDown();
    }
}