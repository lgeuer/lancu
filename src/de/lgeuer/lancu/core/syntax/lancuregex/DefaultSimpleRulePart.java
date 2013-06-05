/*
 * File: Rule.java
 * Author: Lars Geuer
 * Date: 14.3.2007
 */

package de.lgeuer.lancu.core.syntax.lancuregex;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.lgeuer.lancu.core.RuleDoesNotApplyException;
import de.lgeuer.lancu.core.UnknownConstantException;
import de.lgeuer.lancu.core.UnknownPhonemeStructureException;
import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;
import de.lgeuer.lancu.util.id.IdFactory;

import static de.lgeuer.lancu.core.syntax.lancuregex.DefaultRuleSyntaxNew.*;

public class DefaultSimpleRulePart extends DefaultRulePart {

    public DefaultSimpleRulePart(String aRule, Language aLanguage) throws SyntaxException, UnknownPhonemeStructureException {
	super(aRule, aLanguage);

	splitRule();
    }

    protected DefaultSimpleRulePart(int anId, String aRule, IdFactory anIdFactory, Language aLanguage) throws SyntaxException, UnknownPhonemeStructureException {

	super(anId, aRule, anIdFactory, aLanguage);
	splitRule();
    }
    
    private void splitRule() throws SyntaxException, UnknownPhonemeStructureException {

	String rule = getRule();
	//match single char or variable followed by an optional tag
	Pattern rulePattern = Pattern.compile(
		"(?<phoneme>" + PHONEME_CHAR + "|"  + PHONEME_STRUCTURE + ")" + 
		"(?<tag>" + TAG + ")?"
	);

	Matcher ruleMatcher = rulePattern.matcher(rule);
	
	if(!ruleMatcher.find()) {
	    throw new SyntaxException("Error while parsing simple rule: " + rule);
	}
	
	regex = ruleMatcher.group("phoneme");
	tag = DefaultRuleTag.newInstance(ruleMatcher.group("tag"));
    }
    
    /**	
     * Parses a phoneme sequence.
     *
     * @param phonemeSequence the original phoneme sequence 
     * @return the sequence after the rule has been applied
     */
    @Override
    public String parse(String phonemeSequence) throws RuleDoesNotApplyException, UnknownConstantException, UnknownPhonemeStructureException {

	return tag.parse(phonemeSequence);
    }
    
    @Override
    public boolean equals(Object o) {

	if (!(o instanceof DefaultSimpleRulePart)) {

	    return false;
	}

	DefaultSimpleRulePart otherRule = (DefaultSimpleRulePart) o;

	return getRule().equals(otherRule.getRule())
		&& this.language == otherRule.language;
    }

    @Override
    public boolean matches(String phonemeSequence) throws UnknownPhonemeStructureException {
	return phonemeSequence != null && phonemeSequence.matches(getClearedRegex()) && tag.matches(this);
    }
}

/*

S = VCV?
PST = $S$S
PSQ = af|ef || afe|f

*/

