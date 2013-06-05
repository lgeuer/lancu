/*
 * File: Rule.java
 * Author: Lars Geuer
 * Date: 14.3.2007
 */

package de.lgeuer.lancu.core.entity;

import java.util.List;
import java.util.Observable;

import de.lgeuer.lancu.core.InvalidRuleException;
import de.lgeuer.lancu.core.RuleDoesNotApplyException;
import de.lgeuer.lancu.core.RuleViolationException;
import de.lgeuer.lancu.core.UnknownConstantException;
import de.lgeuer.lancu.core.UnknownPhonemeStructureException;
import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;
import de.lgeuer.lancu.core.syntaxold.RuleSyntax;
import de.lgeuer.lancu.util.id.IdFactory;


public class RuleOld extends Observable implements LanguageItem, Comparable<RuleOld> {

    private static final long serialVersionUID = 766193377054595429L;

    private int id = IdFactory.VOID;

    private Language language;

    private String newRule;

    private String rule;

    public RuleOld(String aRule, Language aLanguage) throws SyntaxException {

	if (!aLanguage.getRuleSyntax().checkRuleSyntax(aRule)) {

	    throw new SyntaxException("Illegal rule syntax: " + aRule);
	}

	language = aLanguage;
	newRule = aRule;

    }

    @Override
    public void rollback() {

	newRule = null;

	setChanged();
	notifyObservers();
    }

    @Override
    public void commit() {

	if (newRule != null) {

	    rule = newRule;
	    newRule = null;
	}

	setChanged();
	notifyObservers();
    }

    public void setId(int anId) {

	id = anId;
    }

    @Override
    public int getId() {

	return id;
    }

    public String getRule() {

	if (newRule != null) {

	    return newRule;
	}

	return rule;
    }

    public void setRule(String aRule) throws InvalidRuleException {

	if (!language.getRuleSyntax().checkRule(aRule)) {

	    throw new InvalidRuleException(aRule);
	}

	newRule = aRule;

	setChanged();
	notifyObservers();
    }

    /**
     * Parses a phoneme sequence.
     *
     * @param phonemeSequence the original phoneme sequence 
     * @return the sequence after the rule has been applied
     */
    public String parse(String phonemeSequence) throws RuleDoesNotApplyException, UnknownConstantException, UnknownPhonemeStructureException {

	String result = "";
	RuleSyntax syntax = language.getRuleSyntax();
	String tmpRule;

	tmpRule = syntax.replaceConstants(getRule());

	try {

	    tmpRule = syntax.trimRule(tmpRule, phonemeSequence);
	} catch (RuleViolationException ex) {

	    ex.printStackTrace();

	    throw new RuleDoesNotApplyException("rule: " + getRule() +  ", sequence: " + phonemeSequence);
	} catch (IllegalStateException ex) {

	    ex.printStackTrace();

	    throw new RuleDoesNotApplyException("rule: " + getRule()
		    + ", sequence: " + phonemeSequence);
	} catch (RuntimeException ex) {

	    System.out.println("parse: " + tmpRule + "/" + phonemeSequence);
	    ex.printStackTrace();
	}

	//String tmpSequence = syntax.replacePhonemeStructures(tmpRule)
	List<String[]> sequenceParts = null;
	try {
	    sequenceParts = syntax.getSequenceParts(phonemeSequence, tmpRule);
	} catch (RuntimeException ex) {

	    System.out.println("parse: " + tmpRule + "/" + phonemeSequence);
	    ex.printStackTrace();
	    throw ex;
	}

	System.out.println("sequenceParts.size():" + sequenceParts.size());

	for (String[] part : sequenceParts) {

	    String sequence = part[0];
	    String ruleRegex = part[1];
	    String ruleTag = part[2];
	    PhonemeStructure structure;

	    System.out.println("tmpRule: " + tmpRule);
	    System.out.println("phonemeSequence: " + phonemeSequence);
	    System.out.println("sequence: " + sequence);
	    System.out.println("ruleRegex: " + ruleRegex);
	    System.out.println("ruleTag: " + ruleTag);

	    //if tag has to be parsed
	    if (!ruleTag.equals("")) {

		System.out.println("1");
		if (syntax.isPhonemeStructureName(ruleRegex)) {
		    System.out.println("2");

		    structure = language.getPhonemeStructure(syntax.stripVarName(ruleRegex));
		} else {

		    System.out.println("3");
		    try {

			structure = new PhonemeStructure(syntax
				.replacePhonemeStructures(ruleRegex), language);
			System.out.println("4");
		    } catch (SyntaxException ex) {

			//should not happen
			throw new IllegalArgumentException(ex);
		    }
		}

		result += syntax.parseTag(sequence, structure, ruleTag);
		System.out.println("5");
	    } else {

		//use unchanged sequence
		result += sequence;
		System.out.println("6");
	    }
	}

	System.out.println("7");
	return result;
    }

    @Override
    public boolean equals(Object o) {

	if (!(o instanceof RuleOld)) {

	    return false;
	}

	RuleOld otherRule = (RuleOld) o;

	return getRule().equals(otherRule.getRule())
		&& this.language == otherRule.language;
    }

    @Override
    public int compareTo(RuleOld r) {

	return getRule().compareTo(r.getRule());
    }
}