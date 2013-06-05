/*
 * File: RuleDoesNotApplyException.java
 * Author: Lars Geuer
 * Date: 6.6.2007
 */

package de.lgeuer.lancu.core;

public class RuleDoesNotApplyException extends Exception {

    private static final long serialVersionUID = 4970126084841881824L;

    public RuleDoesNotApplyException(String message) {

	super(message);
    }

    public RuleDoesNotApplyException(String message,Throwable cause) {

	super(message,cause);
    }

    public RuleDoesNotApplyException(Throwable cause) {

	super(cause);
    }
}