/*
 * File: Rule.java
 * Author: Lars Geuer
 * Date: 14.3.2007
 */

package de.lgeuer.lancu.core.entity;


import java.util.Observable;

import de.lgeuer.lancu.core.LanguageViolationException;
import de.lgeuer.lancu.core.RuleDoesNotApplyException;
import de.lgeuer.lancu.core.UnknownPhonemeStructureException;
import de.lgeuer.lancu.core.ViolationException;
import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;
import de.lgeuer.lancu.core.syntax.sca2.Rule;
import de.lgeuer.lancu.util.id.Identifiable;


public abstract class AbstractRule extends Observable implements LanguageItem, Comparable<AbstractRule>, Identifiable {

    private static final long serialVersionUID = 9069726844434703186L;

    public static AbstractRule newInstance(String aRule, Language aLanguage) throws SyntaxException, UnknownPhonemeStructureException {
	return Rule.newInstance(aRule, aLanguage);
    }

    private int id;
    
    public abstract String getRule();

    public abstract void setRule(String aRule) throws LanguageViolationException;
    
    /**
     * Parses a phoneme sequence.
     *t
     * @param phonemeSequence the original phoneme sequence 
     * @return the sequence after the rule has been applied
     * @throws RuleDoesNotApplyException 
     */
    public abstract String parse(String phonemeSequence) throws LanguageViolationException, RuleDoesNotApplyException;

    @Override
    public abstract boolean equals(Object o);
    
    @Override
    public int compareTo(AbstractRule r) {
	
	return getRule().compareTo(r.getRule());
    }

    public void setId(int anId) {
	id = anId;
    }
    
    @Override
    public int getId() {
	return id;
    }
    
    @Override
    public abstract void commit() throws ViolationException;
}