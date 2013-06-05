/*
 * File: PhonemeModule.java
 * Author: Lars Geuer
 * Date: 1.5.2007
 */

package de.lgeuer.lancu.ui.module;

import java.awt.FlowLayout;


import net.infonode.docking.DockingWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.View;

import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.ui.UIController;



public class PhonemeModule extends UIModule {

    private static final long serialVersionUID = -7755600855343716814L;

    private static String moduleName = "Phonemes";

    private UIController controller;
    private Language language;

    private PhonemeList phonemeList;
    private SyllableForm syllableForm;

    public PhonemeModule(UIController aController,Language aLanguage) {

	super(aController);
	controller = aController;
	language = aLanguage;

	phonemeList = new PhonemeList(controller,language);
	syllableForm = new SyllableForm(controller,language);
    }

    @Override
    public DockingWindow getDockingWindow() {
	return new SplitWindow(true, 0.2f, 
		new View("Phonemes (" + language.getName() + ")", null, new MinialUIModule("Phonemes", language, controller, phonemeList, new FlowLayout(FlowLayout.LEFT))), 
		new View("Syllable (" + language.getName() + ")", null, new MinialUIModule("Syllable", language, controller, syllableForm, new FlowLayout(FlowLayout.LEFT)))
	);
    }

    @Override
    public String getName() {

	return moduleName + " (" + language.getName() + ")";
    }


    @Override
    public String getShortName() {

	return moduleName;
    }


    @Override
    public Language getLanguage() {

	return language;
    }


    @Override
    public void setUp() {

	phonemeList.setUp();
	syllableForm.setUp();

    }

    @Override
    public void shutDown() {

	phonemeList.shutdown();
	syllableForm.shutdown();
    }
}