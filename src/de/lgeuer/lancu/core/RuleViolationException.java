/**
 * File: RuleViolationException.java
 * Author: Lars Geuer
 * Date: 14.3.2007
 */

package de.lgeuer.lancu.core;

import de.lgeuer.lancu.core.entity.AbstractRule;



public class RuleViolationException extends LanguageViolationException {

    private static final long serialVersionUID = 8277700387050264907L;
    private AbstractRule rule;
    private String sequence;
    
    public RuleViolationException(AbstractRule defaultRule, String aSequence) {
	
	super("A rule violation occured on '" + aSequence + "': " + defaultRule.getRule());

	rule = defaultRule;
	sequence = aSequence;
    }

    public AbstractRule getRule() {
	
	return rule;
    }


    public String getSequence() {
	
	return sequence;
    }
}