package de.lgeuer.lancu.core;


import org.junit.After;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.lgeuer.lancu.core.entity.AbstractRule;
import de.lgeuer.lancu.core.entity.PhonemeStructure;
import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;
import de.lgeuer.lancu.core.syntaxold.DefaultRuleSyntax;


public class RuleTest {

    private static final String SEQUENCE = "x-cta";
    private static final String RULE = ".$S<change:FIRST=(c:z)>";
    private static final String STRUCTURE = ".<name:V1>.?.";
    private DefaultRuleSyntax syntax;
    private LanguageMock language;
    private AbstractRule rule;

    @Before public void setUp() 
	throws SyntaxException, UnknownPhonemeStructureException {
	
	language = new LanguageMock();
	syntax = new DefaultRuleSyntax(language);
	language.setRuleSyntax(syntax);
	language.addPhonemeStructure(new PhonemeStructure("S",STRUCTURE,language));
	rule = AbstractRule.newInstance(RULE,language);
    }


    @After public void tearDown() {

	language = null;
	syntax = null;
	rule = null;
    }


    @Test public void parse() throws LanguageViolationException, RuleDoesNotApplyException {

	//most checks are done in DefaultRuleSyntaxTest
	//just general prosessing of Rule.parse() is done here
	
	String expected = "x-zta";
	String actual = rule.parse(SEQUENCE);
	
	Assert.assertEquals(expected,actual);
    }
}