/*
 * File: LanguageModuleIntializer.java
 * Author: Lars Geuer
 * Date: 28.4.2007
 */

package de.lgeuer.lancu.ui.module;

import java.util.List;

import de.lgeuer.lancu.core.entity.Language;


public interface LanguageModuleInitializer extends ModuleIntializer {

    /**
     * Should return the same instance every time it is called with the same language.
     */
    public List<UIModule> getLanguageModules(Language language);
}