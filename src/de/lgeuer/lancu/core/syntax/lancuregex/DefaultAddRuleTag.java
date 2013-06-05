package de.lgeuer.lancu.core.syntax.lancuregex;

public class DefaultAddRuleTag extends DefaultRuleTag {

    String phonemesToAdd;
    public DefaultAddRuleTag(String tagParams) {
	phonemesToAdd = tagParams;
    }
    @Override
    public String parse(String phonemeSequence) {
	return phonemeSequence + phonemesToAdd;
    }

    @Override
    public boolean matches(DefaultRulePart defaultRulePart) {
	return true;
    }
}
