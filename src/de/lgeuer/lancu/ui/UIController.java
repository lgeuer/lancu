/*
 * File: UIController.java
 * Author: Lars Geuer
 * Date: 24.4.2007
 */

package de.lgeuer.lancu.ui;

import javax.swing.JMenu;

import de.lgeuer.lancu.ui.module.LanguageModuleInitializer;
import de.lgeuer.lancu.ui.module.UIModule;


public interface UIController {


    public void display();
    
    public void openUIModule(UIModule module);

    public void closeUIModule(UIModule module);

    public void addMenu(JMenu menu);

    public void addLanguageMenuItem(LanguageModuleInitializer initializer);

    public void addModuleMenuItem(UIModule module);
    
    /**
     * module may be <code>null<code>
     */
    public void fireError(String message,UIModule module);

    /**
     * module may be <code>null<code>
     */
    public void fireWarning(String message,UIModule module);

    /**
     * module may be <code>null<code>
     */
    public void fireConfirmation(String message,UIModule module);

    /**
     * module may be <code>null<code>
     */
    public boolean confirm(String message,UIModule module);

    public void repaint(UIModule module);

    public void repaint();
}