/**
 * File: UnknownConstantException.java
 * Author: Lars Geuer
 * Date: 4.4.2007
 */

package de.lgeuer.lancu.core;


public class UnknownConstantException extends LanguageViolationException {

    private static final long serialVersionUID = 5635055140062261584L;
    private String constant;
    
    public UnknownConstantException(String aConstant) {
	
	super("Unknown constant: " + aConstant);

	constant = aConstant;
    }

    public String getConstant() {
	
	return constant;
    }
}