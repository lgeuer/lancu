/*
 * File: Rule.java
 * Author: Lars Geuer
 * Date: 14.3.2007
 */

package de.lgeuer.lancu.core.syntax.lancuregex;

import static de.lgeuer.lancu.core.syntax.lancuregex.DefaultRuleSyntaxNew.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.lgeuer.lancu.core.RuleDoesNotApplyException;
import de.lgeuer.lancu.core.UnknownConstantException;
import de.lgeuer.lancu.core.UnknownPhonemeStructureException;
import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.util.id.IdFactory;


public class DefaultComplexRulePart extends DefaultRulePart {

    protected Map<Integer, DefaultRulePart> ruleParts;
    private String groupedComplexRegex;
    private String complexRegex;
    protected DefaultComplexRulePart(String aRule, Language aLanguage) throws SyntaxException, UnknownPhonemeStructureException {

	super(aRule, aLanguage);
	
	ruleParts = new HashMap<Integer, DefaultRulePart>();
	splitRule();
    }

    protected DefaultComplexRulePart(int anId, String aRule, Map<Integer, DefaultRulePart> someRuleParts, IdFactory anIdFactory, Language aLanguage) throws SyntaxException, UnknownPhonemeStructureException {

	super(anId, aRule, anIdFactory, aLanguage);

	ruleParts = someRuleParts;
	splitRule();
	
    }
    
    private void splitRule() throws SyntaxException, UnknownPhonemeStructureException {
	
	@SuppressWarnings("hiding")
	String rule = this.rule;

	String groupRegex = "\\([^()" + TAG_OPEN + TAG_CLOSE + "]*\\)";
	String variableRegex = Pattern.quote(PHONEME_STRUCTURE_IDENTIFIER) + "(?:" +
		Pattern.quote(PHONEME_STRUCTURE_BRACKET_OPEN) + "(?:[A-Z]+)" + PHONEME_STRUCTURE_BRACKET_CLOSE + "|" +
		"(?:[A-Z]+)" +
		")";
	String tagRegex = TAG_OPEN + "[^" + TAG_OPEN + "]*" + TAG_CLOSE;
	
	//match single char or variable followed by an tag
	//or an regex-group that MAY be followed by an tag
	Pattern rulePattern = Pattern.compile(
		"(?:" +
			"(?:" + PHONEME_CHAR + "|" + PHONEME_STRUCTURE + ")(?:" + tagRegex + ")" +
		")|(?:" +
			"(?:" + groupRegex  + ")" + "(?:" + tagRegex + ")" +
		")"
		);

	Matcher ruleMatcher = rulePattern.matcher(rule);
	
	while(ruleMatcher.find()) {
	    
	    String rulePart = ruleMatcher.group();
	    
	    if(rule.equals(rulePart)) {
		Pattern tagPattern = Pattern.compile(TAG_OPEN + "[^" + TAG_OPEN + TAG_CLOSE + "]*" + TAG_CLOSE + "$");
		Matcher tagMatcher = tagPattern.matcher(rule);
		
		if(!tagMatcher.find()) {
		    throw new SyntaxException("No Tag found at end of rule: " + rule);
		} 
		
		tag = DefaultRuleTag.newInstance(tagMatcher.group());
		complexRegex = rule.substring(0, tagMatcher.start());
		break;
 	    }
	    
	    int rulePartId = idFactory.getId();
	    
	    //check if the current part contains other rule parts and 
	    Map<Integer, DefaultRulePart> subRuleParts = new HashMap<Integer, DefaultRulePart>();
	    Pattern identifierPattern = Pattern.compile(INTERNAL_RULE_PART);
	    Matcher identifierMatcher = identifierPattern.matcher(rulePart);
	    
	    while(identifierMatcher.find()) {
	    
	    int subId = Integer.parseInt(identifierMatcher.group().replace(RULE_PART_IDENTIFIER + RULE_PART_BRACKET_OPEN + INTERNAL_VAR_SYMBOL, "").replace(RULE_PART_BRACKET_CLOSE, ""));
	    subRuleParts.put(subId, ruleParts.remove(subId));
	    }
	    
	    ruleParts.put(rulePartId, DefaultRulePart.newInstance(rulePartId, rulePart, subRuleParts, idFactory, language));
	    
	    if (ruleMatcher.end() < rule.length()) {
	    rule = rule.substring(0, ruleMatcher.start()) + 
	    	RULE_PART_IDENTIFIER + RULE_PART_BRACKET_OPEN + INTERNAL_VAR_SYMBOL + rulePartId + RULE_PART_BRACKET_CLOSE + 
	        	rule.substring(ruleMatcher.end());
	    } else {
		rule = rule.substring(0, ruleMatcher.start()) + 
	    	RULE_PART_IDENTIFIER + RULE_PART_BRACKET_OPEN + INTERNAL_VAR_SYMBOL + rulePartId + RULE_PART_BRACKET_CLOSE;
	    }
	    
	    complexRegex = rule;
	    
	    ruleMatcher = rulePattern.matcher(rule);
	}
	
	parseGroupedRegex();
    }

    /**
     * Parses a phoneme sequence.
     *
     * @param phonemeSequence the original phoneme sequence Arbeit
     * @return the sequence after the rule has been applied
     */
    @Override
    public String parse(String phonemeSequence) throws RuleDoesNotApplyException, UnknownConstantException, UnknownPhonemeStructureException {

	return null;
    }

    @Override
    public String getRule() {

	String completeRule = super.getRule();
	
	for (int i : ruleParts.keySet()) {
	    completeRule = completeRule.replace(RULE_PART_IDENTIFIER + RULE_PART_BRACKET_OPEN + INTERNAL_VAR_SYMBOL + i + RULE_PART_BRACKET_CLOSE , ruleParts.get(i).getRule());
	}
	
	return completeRule;
    }

    public void parseGroupedRegex() {

	groupedComplexRegex = complexRegex;
	
	for (int i : ruleParts.keySet()) {
	    groupedComplexRegex = groupedComplexRegex.replace(RULE_PART_IDENTIFIER + RULE_PART_BRACKET_OPEN + INTERNAL_VAR_SYMBOL + i + RULE_PART_BRACKET_CLOSE , "(?<rule" + i + ">" + ruleParts.get(i).getRegex());
	}
    }
    

    @Override
    public boolean equals(Object o) {

	if (!(o instanceof DefaultComplexRulePart)) {

	    return false;
	}

	DefaultComplexRulePart otherRule = (DefaultComplexRulePart) o;

	return getRule().equals(otherRule.getRule())
		&& this.language == otherRule.language;
    }

    @Override
    /**
     * TODO: Hier wird für Sie gebaut.
     */
    public boolean matches(String phonemeSequence) {
	if (phonemeSequence == null || !phonemeSequence.matches(groupedComplexRegex)) {
	    return false;
	}
	
	Pattern pattern = Pattern.compile(groupedComplexRegex);
	Matcher matcher = pattern.matcher(phonemeSequence);
	
	return matcher.matches();
	
//	if (matcher.find()) {
//	    for(Entry<Integer, DefaultRulePart> entry : ruleParts.entrySet()) {
//		String ruleMatched = matcher.group("rule" + entry.getKey());
//		if (entry.getValue().matches(ruleMatched);
//	    }
//	}
    }
}