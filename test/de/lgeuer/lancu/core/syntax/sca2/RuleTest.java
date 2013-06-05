package de.lgeuer.lancu.core.syntax.sca2;

import org.junit.Test;

import de.lgeuer.lancu.core.LanguageMock;
import de.lgeuer.lancu.core.LanguageViolationException;
import de.lgeuer.lancu.core.RuleDoesNotApplyException;
import static org.junit.Assert.*;

public class RuleTest {

    @Test
    public void setRule() {
	String ruleString = "c/ca/_#/C_#";

	Rule rule = new Rule(new LanguageMock());
	rule.setRule(ruleString);
	assertEquals("the returned rule should be the one we set", ruleString, rule.rule);
	assertEquals("should be the target part of the rule", "c", rule.target);
	assertEquals("should be the replacment part of the rule", "ca", rule.replacement);
	assertEquals("should be the environment part of the rule", "_#", rule.environment);
	assertEquals("should be the exception part of the rule", "C_#", rule.environmentException);
    }

    @Test
    public void parseSimpleRule() throws LanguageViolationException, RuleDoesNotApplyException {

	String ruleString = "c/ca/_#";

	Rule rule = new Rule(new LanguageMock());
	rule.setRule(ruleString);
	assertEquals("paca", rule.parse("pac"));
    }

    @Test (expected=RuleDoesNotApplyException.class)
    public void parseSimpleRuleWithException() throws LanguageViolationException, RuleDoesNotApplyException {

	String ruleString = "c/ca/_#/a_#";

	Rule rule = new Rule(new LanguageMock());
	rule.setRule(ruleString);
	assertEquals("pac", rule.parse("pac"));
    }

    @Test
    public void parseRuleWithMultipleWords() throws LanguageViolationException, RuleDoesNotApplyException {

	String ruleString = "k/s/#_";

	Rule rule = new Rule(new LanguageMock());
	rule.setRule(ruleString);
	assertEquals("ota sa", rule.parse("ota ka"));
    }

    @Test
    public void parseRuleWithParantheses() throws LanguageViolationException, RuleDoesNotApplyException {

	String ruleString = "k/s/#_a(a)c";

	Rule rule = new Rule(new LanguageMock());
	rule.setRule(ruleString);
	assertEquals("sac", rule.parse("kac"));
	assertEquals("saac", rule.parse("kaac"));
    }

    @Test
    public void parseRuleInsertion() throws LanguageViolationException, RuleDoesNotApplyException {

	String ruleString = "/j/_kt";

	Rule rule = new Rule(new LanguageMock());
	rule.setRule(ruleString);
	assertEquals("ajkt", rule.parse("akt"));
    }

    @Test
    public void parseRuleWithNOunceCategoriesInTarget() throws LanguageViolationException, RuleDoesNotApplyException {

	String ruleString = "[ao]u/o/_";

	Rule rule = new Rule(new LanguageMock());
	rule.setRule(ruleString);
	assertEquals("nok", rule.parse("nauk"));
	assertEquals("nok", rule.parse("nouk"));

	try {
	    rule.parse("nuk");
	    fail("Rule should not apply");
	} catch(RuleDoesNotApplyException ex) {
	    //everything is fine
	}
    }

    @Test
    public void parseRuleWithNOunceCategoriesInEnvironment() throws LanguageViolationException, RuleDoesNotApplyException {

	String ruleString = "k/s/_[ie]";

	Rule rule = new Rule(new LanguageMock());
	rule.setRule(ruleString);
	assertEquals("asi", rule.parse("aki"));
	assertEquals("ase", rule.parse("ake"));

	try {
	    rule.parse("ako");
	    fail("Rule should not apply");
	} catch(RuleDoesNotApplyException ex) {
	    //everything is fine
	}
    }

    @Test
    public void parseRuleWithNOunceCategoriesInEnvironmentWithWordBoundary() throws LanguageViolationException, RuleDoesNotApplyException {

	String ruleString = "m/n/_[dt#]";

	Rule rule = new Rule(new LanguageMock());
	rule.setRule(ruleString);
	assertEquals("era-and", rule.parse("era-amd"));
	assertEquals("era-ant", rule.parse("era-amt"));
	assertEquals("era-an", rule.parse("era-am"));

	try {
	    rule.parse("era-amf");
	    fail("Rule should not apply");
	} catch(RuleDoesNotApplyException ex) {
	    //everything is fine
	}
    }
}
