package de.lgeuer.lancu.ui.module;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.ui.UIController;

public class MinialUIModule extends UIModule {
    
    private static final long serialVersionUID = -2787675130713734958L;
    private String name;
    private Language language;

    public MinialUIModule(String name, Language language, UIController controller, JPanel panel, LayoutManager layoutManager) {

	super(controller, layoutManager);
	
//	controller.openUIModule(this);
	this.name = name;
	this.language = language;
	this.add(panel);
    }

    @Override
    public String getName() {
	if (language != null) {
	    return name + " (" + language.getName() + ")";
	}
	return name;
    }

    @Override
    public String getShortName() {
	return name;
    }

    @Override
    public Language getLanguage() {
	return language;
    }

    @Override
    public void setUp() {
    }

    @Override
    public void shutDown() {
    }

}
