/**
 * File: ItemAlreadyExistsException.java
 * Author: Lars Geuer
 * Date: 21.4.2007
 */

package de.lgeuer.lancu.core;


public class ItemAlreadyExistsException extends Exception {

    private static final long serialVersionUID = 873351495083581464L;

    public ItemAlreadyExistsException(String message) {
	
	super(message);
    }
}