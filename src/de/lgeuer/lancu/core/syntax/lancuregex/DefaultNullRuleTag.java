package de.lgeuer.lancu.core.syntax.lancuregex;

public class DefaultNullRuleTag extends DefaultRuleTag {

    @Override
    public String parse(String phonemeSequence) {
	return phonemeSequence;
    }

    @Override
    public boolean matches(DefaultRulePart defaultRulePart) {
	return true;
    }
}
