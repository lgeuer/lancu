/*
 * File:NoMatchingRuleException.java
 * Author: Lars Geuer
 * Date: 11.4.2007
 */

package de.lgeuer.lancu.core;


public class NoMatchingRuleException extends LanguageViolationException {

    private static final long serialVersionUID = -3647139587254710228L;

    public NoMatchingRuleException(String message) {

	super(message);
    }
}