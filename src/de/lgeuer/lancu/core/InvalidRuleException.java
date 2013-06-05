/**
 * File: InvalidRuleException.java
 * Author: Lars Geuer
 * Date: 11.4.2007
 */

package de.lgeuer.lancu.core;


public class InvalidRuleException extends Exception {

    private static final long serialVersionUID = -5252814676741948685L;
    private String rule;
    
    public InvalidRuleException(String aRule) {
	
	super("Invalid rule: " + aRule);
    }
    
    public String getRule() {

	return rule;
    }
}