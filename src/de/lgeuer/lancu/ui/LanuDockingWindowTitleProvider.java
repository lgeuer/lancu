package de.lgeuer.lancu.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.View;
import net.infonode.docking.title.DockingWindowTitleProvider;
import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.ui.module.UIModule;

public class LanuDockingWindowTitleProvider implements DockingWindowTitleProvider {

    @Override
    public String getTitle(DockingWindow window) {

	List<View> viewList = getViews(window);

	Language language = null;
	boolean hasDifferentLanguages = false;
	boolean hasModuleWithoutLanguage = false;
	List<UIModule> moduleList = new ArrayList<UIModule>();

	for(View view : viewList) {
	    Component component = view.getComponent();
	    if (component instanceof UIModule) {
		UIModule module = (UIModule)component;
		moduleList.add(module);
		try {
        		if(language == null) {
        		    language = module.getLanguage();
        		} else {
        		    if(!language.equals(module.getLanguage())) {
        			hasDifferentLanguages = true;
        		    }
        		}
		} catch(UnsupportedOperationException ex) {
		    //probably ManageLanguageModule
		    hasModuleWithoutLanguage = true;
		}
	    }
	}

	String title = "";
	
	if(language == null || hasDifferentLanguages || hasModuleWithoutLanguage) {
	    for(UIModule module : moduleList) {
		title += module.getName() + ", ";
	    }
	    title = title.replaceAll(", $", "");
	} else {
	    for(UIModule module : moduleList) {
		title += module.getShortName() + ", ";
	    }
	    title = title.replaceAll(", $", "");
	    title += " (" + language.getName() + ")";
	}

	return title;
    }

    private List<View> getViews(DockingWindow window) {

	ArrayList<View> viewList = new ArrayList<View>();

	if (window instanceof View) {
	    viewList.add((View)window);
	    return viewList;
	}


	for (int i = 0; i < window.getChildWindowCount();i++) {
	    viewList.addAll(getViews(window.getChildWindow(i)));
	}

	return viewList;
    }

}
