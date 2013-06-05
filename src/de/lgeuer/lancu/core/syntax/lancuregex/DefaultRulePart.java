/*
 * File: Rule.java
 * Author: Lars Geuer
 * Date: 14.3.2007
 */

package de.lgeuer.lancu.core.syntax.lancuregex;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.lgeuer.lancu.core.RuleDoesNotApplyException;
import de.lgeuer.lancu.core.UnknownConstantException;
import de.lgeuer.lancu.core.UnknownPhonemeStructureException;
import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.core.entity.PhonemeStructure;
import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;
import de.lgeuer.lancu.util.id.IdFactory;

import static de.lgeuer.lancu.core.syntax.lancuregex.DefaultRuleSyntaxNew.*;

public abstract class DefaultRulePart {

    protected int id = IdFactory.VOID;
    protected IdFactory idFactory;
    protected Language language;
    /**
     * the rule as passed in
     */
    protected final String rule;
    
    /**
     * the regex with subrule identifieres 
     */
    protected String regex;
    protected DefaultRuleTag tag = DefaultRuleTag.newInstance();

	
    protected static DefaultRulePart newInstance(int anId, String aRule, Map<Integer, DefaultRulePart> someRuleParts, IdFactory anIdFactory, Language aLanguage) throws SyntaxException, UnknownPhonemeStructureException {
	if (someRuleParts != null && someRuleParts.size() > 0) {
	    return new DefaultComplexRulePart(anId, aRule, someRuleParts, anIdFactory, aLanguage);
	}
	return new DefaultSimpleRulePart(anId, aRule, anIdFactory, aLanguage);
    }
    
    protected static DefaultRulePart newInstance(String aRule, Language aLanguage) throws SyntaxException, UnknownPhonemeStructureException {
	
	if (isComplexRule(aRule)) {
	    return new DefaultComplexRulePart(aRule, aLanguage);
	}
	return new DefaultSimpleRulePart(aRule, aLanguage);
    }

    private static boolean isComplexRule(String aRule) {

	String singleChar = "[^" + TAG_OPEN + TAG_CLOSE + PHONEME_STRUCTURE_IDENTIFIER  + RULE_PART_IDENTIFIER + "]";
	String variable = PHONEME_STRUCTURE_IDENTIFIER + "(?:" +
		PHONEME_STRUCTURE_BRACKET_OPEN + "(?:[A-Z]*|[0-9]*)" + PHONEME_STRUCTURE_BRACKET_CLOSE + "|" +
		"(?:[A-Z]*|[0-9]*)" +
		")";
	
	//match single char or variable followed by a tag
	Pattern rulePattern = Pattern.compile(
		"^(" + singleChar + "|"  + variable + ")" + 
		"(" + TAG_OPEN + "[^" + TAG_OPEN + "]*" + TAG_CLOSE + ")$"
	);
	
	return !rulePattern.matcher(aRule).find() || !aRule.contains(TAG_OPEN);
    }
    
    protected DefaultRulePart(String aRule, Language aLanguage) throws SyntaxException {

	super();
	rule = aRule;
	language = aLanguage;
	idFactory = new IdFactory();
	id = idFactory.getId();
    }

    protected DefaultRulePart(int anId, String aRule, IdFactory anIdFactory, Language aLanguage) throws SyntaxException {

	super();
	rule = aRule;
	language = aLanguage;
	idFactory = anIdFactory;
	id = anId;
    }
    
    public int getId() {

 	return id;
     }

    /**
     * Returns the internal string representation of the rule
     *
     * @return the internal rule representation
     */
    public String getRule() {

	return rule;
    } 

    /**
     * Returns the regex of the rule
     * 
     * @return the regex
     */
    public String getRegex() {
	return regex;
    }

    /**
     * Returns the rule-regex with all phoneme structures replaces
     * 
     * @return the regex
     */
    public String getClearedRegex() throws UnknownPhonemeStructureException {
	String clearedRegex = getRegex();
	
	Pattern pattern = Pattern.compile(PHONEME_STRUCTURE);
	Matcher matcher = pattern.matcher(regex);
	while (matcher.find()) {
	    String structureString = matcher.group();
	    String structureName = matcher.group("structurename1");
	    if (structureName == null || "".equals(structureName)) {
		structureName = matcher.group("structurename2");
	    }
	    PhonemeStructure structure = language.getPhonemeStructure(structureName);
	    clearedRegex = clearedRegex.replace(structureString, structure.getStructure());
	}
	return clearedRegex;
    }


    public abstract String parse(String phonemeSequence) throws RuleDoesNotApplyException, UnknownConstantException, UnknownPhonemeStructureException;
    public abstract boolean matches(String phonemeSequence) throws UnknownPhonemeStructureException;
}