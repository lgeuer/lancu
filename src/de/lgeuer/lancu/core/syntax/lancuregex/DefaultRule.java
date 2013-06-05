/*
 * File: Rule.java
 * Author: Lars Geuer
 * Date: 14.3.2007
 */

package de.lgeuer.lancu.core.syntax.lancuregex;

import de.lgeuer.lancu.core.LanguageViolationException;
import de.lgeuer.lancu.core.RuleDoesNotApplyException;
import de.lgeuer.lancu.core.UnknownConstantException;
import de.lgeuer.lancu.core.UnknownPhonemeStructureException;
import de.lgeuer.lancu.core.entity.AbstractRule;
import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;

public class DefaultRule extends AbstractRule {

    private static final long serialVersionUID = -9080160895635686740L;

    protected DefaultRulePart rulePart;

    public static DefaultRule newInstance(String aRule, Language aLanguage) throws SyntaxException, UnknownPhonemeStructureException {
	
	return new DefaultRule(DefaultRulePart.newInstance(aRule, aLanguage));
    }
    
    private DefaultRule(DefaultRulePart aRulePart) {
	super();
	rulePart = aRulePart;
    }
    
    @Override
    public int getId() {
	return rulePart.getId();
    }


    @Override
    public String getRule() {
	return rulePart.getRule();
    }


    @Override
    public String parse(String phonemeSequence) throws RuleDoesNotApplyException, UnknownConstantException, UnknownPhonemeStructureException {
	return rulePart.parse(phonemeSequence);
    }


    @Override
    public boolean equals(Object o) {
	return rulePart.equals(o);
    }


    @Override
    public String toString() {
	return rulePart.toString();
    }

    @Override
    public void rollback() {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void commit() {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void setRule(String aRule) throws LanguageViolationException {
	// TODO Auto-generated method stub
	
    }
    
}