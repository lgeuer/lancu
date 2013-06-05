/**
 * File: ViolationExceptionItem.java
 * Author: Lars Geuer
 * Date: 22.4.2007
 */

package de.lgeuer.lancu.core;


import java.util.ArrayList;
import java.util.List;

import de.lgeuer.lancu.core.entity.LanguageItem;


public class  ViolationExceptionItem {
    
    private String message;
    private LanguageItem item;
    private List<LanguageItem> violatedItems = new ArrayList<LanguageItem>();

    public  ViolationExceptionItem(String aMessage,LanguageItem anItem) {
	
	message = aMessage;
	item = anItem;
    }
    
    public String getMessage() {
	
	return message;
    }
    

    public LanguageItem getLanguageItem() {
	
	return item;
    }
    
    
    public void addViolatedItem(LanguageItem languageItem) {
	
	violatedItems.add(languageItem);
    }
    

    public List<LanguageItem> getViolatedItems() {
	
	return violatedItems;
    }
}
