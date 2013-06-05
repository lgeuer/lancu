package de.lgeuer.lancu.core.syntax.lancuregex;

import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;

import static de.lgeuer.lancu.core.syntax.lancuregex.DefaultRuleSyntaxNew.*;

public abstract class DefaultRuleTag {

    public static DefaultRuleTag newInstance(String aTag) throws SyntaxException {

	if(aTag == null || aTag == "") return new DefaultNullRuleTag();
	
	aTag = aTag.replaceAll("^" + TAG_OPEN, "").replaceAll(TAG_CLOSE + "$", "");
	
	if(aTag.startsWith(ADD_TAG_IDENTIFIER)) return new DefaultAddRuleTag(aTag.replaceAll( "^" + ADD_TAG_IDENTIFIER, ""));

	throw new SyntaxException("Unkown tag type: " + aTag);
    }
    
    public static DefaultRuleTag newInstance() throws SyntaxException {
	return newInstance("");
    }
    
    public abstract String parse(String phonemeSequence);

    public abstract boolean matches(DefaultRulePart defaultRulePart);
}
