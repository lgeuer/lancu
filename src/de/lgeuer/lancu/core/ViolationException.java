/**
 * File: ViolationException.java
 * Author: Lars Geuer
 * Date: 22.4.2007
 */

package de.lgeuer.lancu.core;

import java.util.ArrayList;
import java.util.List;


public class ViolationException extends Exception {

    private static final long serialVersionUID = -1235153084483062684L;
    private List<ViolationExceptionItem> items = 
	new ArrayList<ViolationExceptionItem>();
    private boolean violationOccured = false;


    public ViolationException() {
	
	super();
    }


    public void addItem(ViolationExceptionItem item) {

	items.add(item);
	violationOccured = true;
    }


    public List<ViolationExceptionItem> getItems() {

	return items;
    }


    public boolean violationOccured() {

	return violationOccured;
    }
}