package de.lgeuer.lancu.core.syntax.lancuregex;

import static org.junit.Assert.*;

import org.junit.Test;

import de.lgeuer.lancu.core.LanguageMock;
import de.lgeuer.lancu.core.RuleDoesNotApplyException;
import de.lgeuer.lancu.core.UnknownConstantException;
import de.lgeuer.lancu.core.UnknownPhonemeStructureException;
import de.lgeuer.lancu.core.syntax.lancuregex.DefaultAddRuleTag;
import de.lgeuer.lancu.core.syntax.lancuregex.DefaultComplexRulePart;
import de.lgeuer.lancu.core.syntax.lancuregex.DefaultNullRuleTag;
import de.lgeuer.lancu.core.syntax.lancuregex.DefaultSimpleRulePart;
import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;

public class DefaultComplexRulePartTest {


    private LanguageMock language = new LanguageMock();
    
    @Test
    public void splitWithOneSimpleRulePart() throws SyntaxException, UnknownPhonemeStructureException {
	
	String ruleString = "a<add:e>b";

	DefaultComplexRulePart rule = new DefaultComplexRulePart(ruleString, language);
	
	assertEquals("Unexpected rule", "${?1}b", rule.rule);	
	assertEquals("Unexpected subrule count", 1, rule.ruleParts.size());	

	assertTrue("Unexpected rule type: " + rule.ruleParts.get(1).getClass(), rule.ruleParts.get(1) instanceof  DefaultSimpleRulePart);	
	assertEquals("Unexpected rule","a<add:e>", rule.ruleParts.get(1).rule );	
    }

    @Test
    public void splitWithOneComplexRulePart() throws SyntaxException, UnknownPhonemeStructureException {
	
	String ruleString = "(o<add:u>|.<add:o>)<add:e>b";

	DefaultComplexRulePart rule = new DefaultComplexRulePart(ruleString, language);
	
	assertEquals("Unexpected rule", "${?3}b", rule.rule);	
	assertEquals("Unexpected subrule count", 1, rule.ruleParts.size());	
	assertTrue("Unexpected tag type: " + rule.tag.getClass(), rule.tag instanceof  DefaultNullRuleTag);	

	
	//check subrule level 1
	assertTrue("Unexpected rule type: " + rule.ruleParts.get(3).getClass(), rule.ruleParts.get(3) instanceof  DefaultComplexRulePart);	

	DefaultComplexRulePart subRule = (DefaultComplexRulePart)rule.ruleParts.get(3);

	assertEquals("Unexpected rule","(${?1}|${?2})<add:e>", subRule.rule);	
	assertTrue("Unexpected tag type: " + subRule.tag.getClass(), subRule.tag instanceof  DefaultAddRuleTag);	
	assertEquals("Unexpected subrule count", 2, subRule.ruleParts.size());	

	//check subrule 1 level 2
	assertTrue("Unexpected rule type: " + subRule.ruleParts.get(1).getClass(), subRule.ruleParts.get(1) instanceof  DefaultSimpleRulePart);	
	assertEquals("Unexpected rule","o<add:u>", subRule.ruleParts.get(1).rule);	
	assertTrue("Unexpected tag type: " + subRule.ruleParts.get(1).tag.getClass(), subRule.ruleParts.get(1).tag instanceof  DefaultAddRuleTag);	

	//check subrule 2 level 2
	assertTrue("Unexpected rule type: " + subRule.ruleParts.get(2).getClass(), subRule.ruleParts.get(2) instanceof  DefaultSimpleRulePart);	
	assertEquals("Unexpected rule",".<add:o>", subRule.ruleParts.get(2).rule);	
	assertTrue("Unexpected tag type: " + subRule.ruleParts.get(2).tag.getClass(), subRule.ruleParts.get(2).tag instanceof  DefaultAddRuleTag);	
    }
    
    @Test
    public void getRegex() throws SyntaxException, UnknownPhonemeStructureException {
	
	String ruleString = "(o<add:u>|.<add:o>)<add:e>b";

	DefaultComplexRulePart rule = new DefaultComplexRulePart(ruleString, language);
	
	assertEquals("Unexpected rule regex", "(o|.)b", rule.getRegex());
    }

    @Test
    public void getRule() throws SyntaxException, UnknownPhonemeStructureException {
	
	String ruleString = "(o<add:u>|.<add:o>)<add:e>b";

	DefaultComplexRulePart rule = new DefaultComplexRulePart(ruleString, language);
	
	assertEquals("Unexpected rule string", ruleString, rule.getRule());	
    }
    
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
    
    @Test
    public void matchWithTag() throws SyntaxException, UnknownPhonemeStructureException {
	
	String aRuleString = "a<add:e>b";
	DefaultComplexRulePart rule = new DefaultComplexRulePart(aRuleString, language);
	
	
	assertTrue("Should match", rule.matches("ab"));
    }

    @Test
    public void matchWithPhonemeStructureAndTag() throws SyntaxException, UnknownPhonemeStructureException {
	
	language.addPhonemeStructure("S","[aei]f");
	String aRuleString = "$S<add:e>";
	DefaultComplexRulePart rule = new DefaultComplexRulePart(aRuleString, language);
	
	
	assertTrue("Should match", rule.matches("af"));
    }

    @Test
    public void matchWithTwoPossibleTags() throws SyntaxException, UnknownPhonemeStructureException {
	
	String aRuleString = "([aeio]<name:V1><compare:V1=e>|a<add:e>)b";
	DefaultComplexRulePart rule = new DefaultComplexRulePart(aRuleString, language);
	
	
	assertTrue("Should match", rule.matches("ab"));
    }


    @Test
    public void shouldNotMatch() throws SyntaxException, UnknownPhonemeStructureException {
	
	String aRuleString = "a<add:e>b";
	DefaultComplexRulePart rule = new DefaultComplexRulePart(aRuleString, language);
	
	assertFalse("Should not match", rule.matches("eb"));
    }

    @Test
    public void parseRuleWithPhonemeStructureAndTag() throws RuleDoesNotApplyException, UnknownConstantException, UnknownPhonemeStructureException, SyntaxException {
	
	String aRuleString = "a<add:e>b";
	DefaultComplexRulePart rule = new DefaultComplexRulePart(aRuleString, language);
	
	assertEquals("Unexpected parse result", "ae", rule.parse("a"));
    }
    
    @Test
    public void parseRuleWithTag() throws RuleDoesNotApplyException, UnknownConstantException, UnknownPhonemeStructureException, SyntaxException {
	
	String aRuleString = "a<add:e>b";
	DefaultComplexRulePart rule = new DefaultComplexRulePart(aRuleString, language);
	
	assertEquals("Unexpected parse result", "ae", rule.parse("a"));
    }
}
