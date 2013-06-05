/*
 * File: UnknownRuleException.java
 * Author: Lars Geuer
 * Date: 10.4.2007
 */

package de.lgeuer.lancu.core;

public class UnknownRuleException extends Exception {

    private static final long serialVersionUID = 4403763345747716149L;

    public UnknownRuleException(int anId) {

	super("Unknown rule: " + anId);
    }
}