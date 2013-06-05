/**
 * File: UnknownWordException.java
 * Author: Lars Geuer
 * Date: 10.4.2007
 */

package de.lgeuer.lancu.core;


public class UnknownWordException extends Exception {

    private static final long serialVersionUID = -3552151295941536467L;

    public UnknownWordException(String message) {
	
	super(message);
    }
}