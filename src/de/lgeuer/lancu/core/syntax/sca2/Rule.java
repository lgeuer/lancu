package de.lgeuer.lancu.core.syntax.sca2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import de.lgeuer.lancu.core.LanguageViolationException;
import de.lgeuer.lancu.core.RuleDoesNotApplyException;
import de.lgeuer.lancu.core.RuleViolationException;
import de.lgeuer.lancu.core.ViolationException;
import de.lgeuer.lancu.core.entity.AbstractRule;
import de.lgeuer.lancu.core.entity.Language;

public class Rule extends AbstractRule {

    private static final long serialVersionUID = -7462342310939308474L;
    
    private Language language;
    protected String rule;

    protected String target;
    protected String replacement;
    protected String environment;
    protected String environmentException;

    
    public static Rule newInstance(String aRule, Language aLanguage) {
	
	Rule rule = new Rule(aLanguage);
	rule.setRule(aRule);
	
	return rule;
    }
    
    public Rule(Language aLanguage) {
	language = aLanguage;
    }
    

    @Override
    public String getRule() {
	return rule;
    }

    @Override
    /**
     * Makes a rule
     * 
     * @param aRule something like "c/ca/_#/C_#" or more generally speaking: TARGET/REPLACMENT/ENVIRONMENT[/ENVIRONMENT_EXCEPTION]
     */
    public void setRule(String aRule) {
	
	rule = aRule;
	String[] ruleParts = aRule.split("/");
	
	if (ruleParts.length > 0) {
	    target = ruleParts[0];
	}

	if (ruleParts.length > 1) {
	    replacement = ruleParts[1];
	}
	
	if (ruleParts.length > 2) {
	    environment = ruleParts[2];
	}
	
	if (ruleParts.length > 3) {
	    environmentException = ruleParts[3];
	}
	
    }
    
    public boolean isValid() {
	
	if (replacement == null || environment == null) {
	    return false;
	}

	if (!environment.contains("_")) {
	    return false;
	}
	
	if (environmentException != null && !environmentException.contains("_")) {
	    return false;
	}
	
	return true;
    }

    @Override
    public String parse(String phonemeSequence) throws LanguageViolationException, RuleDoesNotApplyException {

	if (!isValid()) {
	    throw new RuleViolationException(this, rule);
	}

	StringBuffer result = new StringBuffer();
	
	Matcher environmentMatcher = Pattern.compile(parseEnvironmentRegex(environment, target)).matcher(" " + phonemeSequence + " ");
	Matcher environmentExceptionMatcher = null;
	if (environmentException != null) {
	    environmentExceptionMatcher = Pattern.compile(parseEnvironmentRegex(environmentException, target)).matcher(" " + phonemeSequence + " ");
	}
	
	boolean ruleApplies = false;
	while(environmentMatcher.find()) {
	    

	    if (environmentExceptionMatcher != null) {
		
		//we have an potential match, now check if the exception applies

		boolean exceptionApplies = false;
		environmentExceptionMatcher.reset();
		
		while(environmentExceptionMatcher.find()) {
		    if(environmentMatcher.start(2) == environmentExceptionMatcher.start(2) && environmentMatcher.end(2) == environmentExceptionMatcher.end(2)) {
			exceptionApplies = true;
			break;
		    }
		}
		
		if (exceptionApplies) {
		    //can't win them all...
		    //well, nothing is lost just yet, lets check the next environment match
		    break;
		}
	    }
	    
	    //we have a match!
	    ruleApplies = true;
	    environmentMatcher.appendReplacement(result, environmentMatcher.group("before") + replacement + environmentMatcher.group("after"));
	}

	environmentMatcher.appendTail(result);

	if(!ruleApplies) {
	    throw new RuleDoesNotApplyException("Environment does not match");
	}

	return result.toString().replaceAll("^ ", "").replaceAll(" $", "");
    }

    private String parseEnvironmentRegex(String anEnvironment, String aTarget) {

	String enviornmentRegex = Pattern.quote(anEnvironment);
	enviornmentRegex = enviornmentRegex.replace("(", "\\E(?:\\Q");
	enviornmentRegex = enviornmentRegex.replace(")", "\\E)?\\Q");
	enviornmentRegex = enviornmentRegex.replace("[", "\\E[\\Q");
	enviornmentRegex = enviornmentRegex.replace("]", "\\E]\\Q");
	enviornmentRegex = enviornmentRegex.replace("#", " ");

	String targetRegex = Pattern.quote(aTarget);
	targetRegex = targetRegex.replace("(", "\\E(?:\\Q");
	targetRegex = targetRegex.replace(")", "\\E)?\\Q");
	targetRegex = targetRegex.replace("[", "\\E[\\Q");
	targetRegex = targetRegex.replace("]", "\\E]\\Q");
	targetRegex = targetRegex.replace("#", " ");

	return "(?<before>" + enviornmentRegex.replace("_", "\\E)(?<target>" + targetRegex + ")(?<after>\\Q") + ")";
    }

    @Override
    public boolean equals(Object o) {
	return rule !=  null && (o instanceof Rule) && rule.equals(((Rule)o).rule);
    }

    @Override
    public void commit() throws ViolationException {
	throw new NotImplementedException();
    }

    @Override
    public void rollback() {
	throw new NotImplementedException();
    }

}
