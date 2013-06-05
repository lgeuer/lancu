package de.lgeuer.lancu.core.syntax.lancuregex;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.lgeuer.lancu.core.LanguageMock;
import de.lgeuer.lancu.core.RuleDoesNotApplyException;
import de.lgeuer.lancu.core.UnknownConstantException;
import de.lgeuer.lancu.core.UnknownPhonemeStructureException;
import de.lgeuer.lancu.core.syntax.lancuregex.DefaultAddRuleTag;
import de.lgeuer.lancu.core.syntax.lancuregex.DefaultNullRuleTag;
import de.lgeuer.lancu.core.syntax.lancuregex.DefaultSimpleRulePart;
import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;

public class DefaultSimpleRulePartTest {


    private LanguageMock language;
    
    @Before
    public void before() throws SyntaxException {
	language = new LanguageMock();
	language.addPhonemeStructure("S",".*");
    }

    @Test
    public void splitRuleWithCharAndTag() throws SyntaxException, UnknownPhonemeStructureException {
	
	String aRuleString = "a<add:e>";
	DefaultSimpleRulePart rule = new DefaultSimpleRulePart(aRuleString, language);
	
	assertEquals("Unespected rule",aRuleString, rule.rule);	
	assertEquals("Unespected phoneme","a", rule.regex);	
	assertNotNull("Tag schould not be null", rule.tag);
	assertTrue("Unespected tag type: " + rule.tag.getClass(), rule.tag instanceof DefaultAddRuleTag);	
    }

    @Test
    public void splitRuleWithShortPhonemeStructureAndTag() throws SyntaxException, UnknownPhonemeStructureException {
	
	String aRuleString = "$S<add:e>";
	DefaultSimpleRulePart rule = new DefaultSimpleRulePart(aRuleString, language);
	
	assertEquals("Unexpected rule",aRuleString, rule.rule);	
	assertEquals("Unexpected phoneme","$S", rule.regex);	
	assertNotNull("Tag schould not be null", rule.tag);
	assertTrue("Unespected tag type: " + rule.tag.getClass(), rule.tag instanceof DefaultAddRuleTag);	
    }


    @Test
    public void splitRuleWithLongPhonemeStructureAndTag() throws SyntaxException, UnknownPhonemeStructureException {
	
	language.addPhonemeStructure("SYLLABLE",".*");

	String aRuleString = "${SYLLABLE}<add:e>";
	DefaultSimpleRulePart rule = new DefaultSimpleRulePart(aRuleString, language);
	
	assertEquals("Unexpected rule",aRuleString, rule.rule);	
	assertEquals("Unexpected phoneme","${SYLLABLE}", rule.regex);	
	assertNotNull("Tag schould not be null", rule.tag);
	assertTrue("Unespected tag type: " + rule.tag.getClass(), rule.tag instanceof DefaultAddRuleTag);	
    }

    @Test
    public void splitRuleWithCharAndWithoutTag() throws SyntaxException, UnknownPhonemeStructureException {
	
	String aRuleString = "a";
	DefaultSimpleRulePart rule = new DefaultSimpleRulePart(aRuleString, language);
	
	assertEquals("Unexpected rule",aRuleString, rule.rule);	
	assertEquals("Unexpected phoneme","a", rule.regex);	
	assertNotNull("Tag schould not be null", rule.tag);
	assertTrue("Unespected tag type: " + rule.tag.getClass(), rule.tag instanceof DefaultNullRuleTag);	
    }
    
    @Test
    public void matchWithTag() throws SyntaxException, UnknownPhonemeStructureException {
	
	String aRuleString = "a<add:e>";
	DefaultSimpleRulePart rule = new DefaultSimpleRulePart(aRuleString, language);
	
	
	assertTrue("Should match", rule.matches("a"));
    }

    @Test
    public void matchWithPhonemeStructureAndTag() throws SyntaxException, UnknownPhonemeStructureException {
	
	language.addPhonemeStructure("S","[aei]f");
	String aRuleString = "$S<add:e>";
	DefaultSimpleRulePart rule = new DefaultSimpleRulePart(aRuleString, language);
	
	
	assertTrue("Should match", rule.matches("af"));
    }

    @Test
    public void shouldNotMatch() throws SyntaxException, UnknownPhonemeStructureException {
	
	String aRuleString = "a<add:e>";
	DefaultSimpleRulePart rule = new DefaultSimpleRulePart(aRuleString, language);
	
	assertFalse("Should not match", rule.matches("e"));
    }

    @Test
    public void parseRuleWithPhonemeStructureAndTag() throws RuleDoesNotApplyException, UnknownConstantException, UnknownPhonemeStructureException, SyntaxException {
	
	String aRuleString = "$S<add:e>";
	DefaultSimpleRulePart rule = new DefaultSimpleRulePart(aRuleString, language);
	
	assertEquals("Unexpected parse result", "ae", rule.parse("a"));
    }
    
    @Test
    public void parseRuleWithTag() throws RuleDoesNotApplyException, UnknownConstantException, UnknownPhonemeStructureException, SyntaxException {
	
	String aRuleString = "a<add:e>";
	DefaultSimpleRulePart rule = new DefaultSimpleRulePart(aRuleString, language);
	
	assertEquals("Unexpected parse result", "ae", rule.parse("a"));
    }
}
