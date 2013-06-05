/*
 * File: UIModule.java
 * Author: Lars Geuer
 * Date: 24.4.2007
 */

package de.lgeuer.lancu.ui.module;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.View;

import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.ui.UIController;


public abstract class UIModule extends JPanel {

    private static final long serialVersionUID = -3812471871202592426L;
    private UIController controller;

    public UIModule(UIController controller) {
	this.controller = controller;
    }
    
    public UIModule(UIController controller, LayoutManager layoutManager) {
	super();
	this.controller = controller;
    }

    @Override
    public abstract String getName();

    public abstract String getShortName();

    /**
     * Optinal method
     *
     * @throws UnsupportedOperationException if not implemented
     */
    public abstract Language getLanguage();

    public DockingWindow getDockingWindow() {
	return new View(getName(), null, this);
    }
    
    public abstract void setUp();
    
    public abstract void shutDown();

    public void remove() {
	controller.closeUIModule(this);	
    }
}