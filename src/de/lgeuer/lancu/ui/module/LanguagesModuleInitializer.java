/*
 * File: LanguagesModuleIntializer.java
 * Author: Lars Geuer
 * Date: 1.5.2007
 */

package de.lgeuer.lancu.ui.module;

import de.lgeuer.lancu.ui.UIController;


public class LanguagesModuleInitializer implements  ModuleIntializer {

    @Override
    public void initialize(UIController controller) {

	UIModule manageLanguages = new ManageLanguageModule(controller);
	controller.addModuleMenuItem(manageLanguages);
    }
}
