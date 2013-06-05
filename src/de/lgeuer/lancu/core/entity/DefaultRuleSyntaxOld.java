/*
 * File: DefaultRuleSyntax.java
 * Author: Lars Geuer
 * Date: 14.3.2007
 */

package de.lgeuer.lancu.core.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;

import de.lgeuer.lancu.LoggerFactory;
import de.lgeuer.lancu.core.RuleDoesNotApplyException;
import de.lgeuer.lancu.core.RuleViolationException;
import de.lgeuer.lancu.core.UnknownConstantException;
import de.lgeuer.lancu.core.UnknownPhonemeStructureException;
import de.lgeuer.lancu.core.syntax.lancuregex.DefaultRule;
import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;
import de.lgeuer.lancu.core.syntaxold.RuleSyntax;

public class DefaultRuleSyntaxOld implements RuleSyntax {

    private static final long serialVersionUID = -6417040487126520710L;

    private static final Logger logger = LoggerFactory.getLogger(DefaultRuleSyntaxOld.class);

    private final String TAG_OPEN = "<";

    private final String TAG_CLOSE = ">";

    private final String CHANGE_TAG_IDENTIFIER = "change:";

    private final String ADD_TAG_IDENTIFIER = "add:";

    private final String DELETE_TAG_IDENTIFIER = "delete";

    private final String COMPARE_TAG_IDENTIFIER = "compare:";

    private final String NAME_TAG_IDENTIFIER = "name:";

    private final String NAMED_GROUP_DELIMITER = ";";

    private final String ASSIGMENT_DELIMITER = ",";

    private final String ASSIGNER = "=";

    private final String CHANGE_TO_ASSIGNER = ":";

    private final String DEPENDENT_CHANGE_TAG_OPEN = "(";

    private final String DEPENDENT_CHANGE_TAG_CLOSE = ")";

    private final String CONSTANT_IDENTIFIER = "#";

    private final String CONSTANT_BRACKET_OPEN = "{";

    private final String CONSTANT_BRACKET_CLOSE = "}";

    private final String PHONEME_STRUCTURE_IDENTIFIER = "$";

    private final String PHONEME_STRUCTURE_BRACKET_OPEN = "{";

    private final String PHONEME_STRUCTURE_BRACKET_CLOSE = "}";

    private final String OPERATOR_MATCHES = "==";

    private final String OPERATOR_DOESNT_MATCH = "!=";

    private final String ANY_SYMBOL = "*";

    private final String VAR_SYMBOL = "[A-Z0-9]";

    private final String PHONEME = "[^<>;:$#.=!(){}\\[\\]\\|\\&\\\\^?*+]";

    private final String CONSTANT = Pattern.quote(CONSTANT_IDENTIFIER) + "("
    + VAR_SYMBOL + "+|" + Pattern.quote(CONSTANT_BRACKET_OPEN)
    + VAR_SYMBOL + "+" + Pattern.quote(CONSTANT_BRACKET_CLOSE) + ")";

    private final String PHONEME_STRUCTURE = Pattern
    .quote(PHONEME_STRUCTURE_IDENTIFIER)
    + "("
    + VAR_SYMBOL
    + "+|"
    + Pattern.quote(PHONEME_STRUCTURE_BRACKET_OPEN)
    + VAR_SYMBOL
    + "+"
    + Pattern.quote(PHONEME_STRUCTURE_BRACKET_CLOSE) + ")";

    private final String FIRST_GROUP = "FIRST";

    private final String LAST_GROUP = "LAST";

    private Language language;

    /**
     * Creates a DefaultRuleSyntax object.
     * 
     * @param aLanguage
     *                the language the ruleSyntax is owned by
     */
    public DefaultRuleSyntaxOld(Language aLanguage) {

	language = aLanguage;
    }

    /**
     * Checks if the given phoneme structure is valid.<br />
     * 
     * @param structure
     *                the phoneme structure to check
     * @deprecated Use checkPhonemeStructureSyntax(String) instead.
     */
    @Override
    @SuppressWarnings("deprecation")
    @Deprecated
    public boolean checkSyntax(String structure) {

	return checkPhonemeStructureSyntax(structure);
    }

    /**
     * Checks if the syntax of a phoneme structure is valid.<br />
     * 
     * @param structure
     *                the phoneme structure to check
     */

    @Override
    public boolean checkPhonemeStructureSyntax(String structure) {

	String noTag = "[^" + TAG_OPEN + TAG_CLOSE + "]";
	String nameTag = TAG_OPEN + NAME_TAG_IDENTIFIER + VAR_SYMBOL + "+"
	+ TAG_CLOSE;
	String regex = noTag + "(" + nameTag + "|" + noTag + ")*";

	String stripped = stripStructure(structure);

	return regexCorrect(structure) && structure.matches(regex)
	&& !containsRuleSymbol(stripped);
    }

    private boolean regexCorrect(String string) {

	String noTag = "[^" + TAG_OPEN + TAG_CLOSE + "]";
	String tag = TAG_OPEN + noTag + "+" + TAG_CLOSE;

	try {
	    String tmp = string.replaceAll(tag, "");
	    tmp = tmp.replaceAll(CONSTANT, "(.)");
	    tmp = tmp.replaceAll(PHONEME_STRUCTURE, "(.)");
	    Pattern.compile(tmp);
	} catch (PatternSyntaxException ex) {

	    return false;
	}

	return true;
    }

    /**
     * Checks if the syntax of a rule is valid.<br />
     * 
     * @param rule
     *                the rule to check
     */
    @Override
    public boolean checkRuleSyntax(String rule) {

	String phonemeRegex = "[^" + TAG_OPEN + TAG_CLOSE + CONSTANT_IDENTIFIER
	+ PHONEME_STRUCTURE_IDENTIFIER + "]";
	String noTag = "(" + phonemeRegex + "|" + CONSTANT + "|"
	+ PHONEME_STRUCTURE + ")";
	String deleteTag = noTag + TAG_OPEN + DELETE_TAG_IDENTIFIER + TAG_CLOSE;

	String addTag = TAG_OPEN + ADD_TAG_IDENTIFIER + PHONEME + "+"
	+ TAG_CLOSE;

	String compareTag = noTag + TAG_OPEN + COMPARE_TAG_IDENTIFIER
	+ VAR_SYMBOL + "+(==|!=)" + PHONEME + TAG_CLOSE;

	String dependentChangingRule = PHONEME + "+" + ":" + PHONEME + "+";
	String dependentChangingRuleWithAsterisk = "(" + PHONEME + "+|\\*)"
	+ ":" + PHONEME + "+";
	String dependentChanging = Pattern.quote("(") + "("
	+ dependentChangingRule + ",)*"
	+ dependentChangingRuleWithAsterisk + Pattern.quote(")");
	String changingRule = VAR_SYMBOL + "+" + "=" + "(" + dependentChanging
	+ "|" + PHONEME + "+)";
	String changeTag = noTag + TAG_OPEN + CHANGE_TAG_IDENTIFIER + "("
	+ changingRule + ";)*" + changingRule + TAG_CLOSE;

	String regex = "(" + noTag + "|" + deleteTag + "|" + addTag + "|"
	+ compareTag + "|" + changeTag + ")+";

	if (!rule.matches(regex)) {

	    return false;
	}

	String tmp = rule.replaceAll(TAG_OPEN + "[^" + TAG_OPEN + "]*"
		+ TAG_CLOSE, "");
	tmp = tmp.replaceAll("(" + PHONEME_STRUCTURE + "|" + CONSTANT + ")",
	".");

	try {

	    Pattern.compile(tmp);
	    return true;
	} catch (Exception ex) {

	    return false;
	}
    }

    /**
     * Checks if the given rule is valid.<br />
     * 
     * <b>NOT JET IMPLEMENTED!!!</b>
     * 
     * @param rule
     *                the rule to check
     */
    @Override
    public boolean checkRule(String rule) {

	// TODO: implement check

	return true;
    }

    @Override
    public boolean containsRuleSymbol(String string) {

	String symbol = "[" + TAG_OPEN + TAG_CLOSE + CONSTANT_IDENTIFIER
	+ PHONEME_STRUCTURE_IDENTIFIER + NAMED_GROUP_DELIMITER
	+ ASSIGMENT_DELIMITER + ASSIGNER + CHANGE_TO_ASSIGNER
	+ DEPENDENT_CHANGE_TAG_OPEN + DEPENDENT_CHANGE_TAG_CLOSE
	+ CONSTANT_BRACKET_OPEN + CONSTANT_BRACKET_CLOSE
	+ PHONEME_STRUCTURE_BRACKET_OPEN
	+ PHONEME_STRUCTURE_BRACKET_CLOSE + OPERATOR_MATCHES
	+ OPERATOR_DOESNT_MATCH + "]";
	String regex = ".*" + symbol + ".*";

	return string.matches(regex);
    }

    /**
     * Returns a list with the names of the groups in structure. The names
     * appear in the list in the order of there appeares in the structure.
     * 
     * @param structure
     *                a phoneme structure
     */
    @Override
    public List<String> getGroupNames(String structure) {

	List<String> groupNames = new ArrayList<String>();
	Matcher matcher;
	Pattern pattern;

	// grep name of named groups and store them
	// TODO: Pattern.quote(...)
	pattern = Pattern.compile(TAG_OPEN + NAME_TAG_IDENTIFIER + "([^"
		+ TAG_OPEN + TAG_CLOSE + "]*)" + TAG_CLOSE);
	matcher = pattern.matcher(structure);

	while (matcher.find()) {

	    groupNames.add(matcher.group(1));
	}

	return groupNames;
    }

    /**
     * Removes all name tag form the structure.
     * 
     * @param structure
     *                the structure to work with
     */
    @Override
    public String stripStructure(String structure) {

	// return aStructure.replaceAll("<name:[^([^/]<)([^/]>)]*[^\\\\]>","");
	String regex = TAG_OPEN + NAME_TAG_IDENTIFIER + "[^" + TAG_OPEN
	+ TAG_CLOSE + "]*" + TAG_CLOSE;

	return structure.replaceAll(regex, "");
    }

    /**
     * Removes all rule tags from a rule.
     * 
     * @param rule
     *                the string to work with
     */
    @Override
    public String stripRule(String rule) {

	String regex = TAG_OPEN + "[^" + TAG_OPEN + TAG_CLOSE + "]*"
	+ TAG_CLOSE;

	return rule.replaceAll(regex, "");
    }

    /**
     * Splits a sequence in its different parts.
     * 
     * A part is an substring of a phoneme sequence/rule being either a tag
     * or a part where no tag applies. A part is a string array with three
     * elements. The first element contains the matched sequence part, the
     * second element contains the associated rule part. The third parts
     * contains the associated tag. The first and third element of the array
     * may be an empty string, if the rule part does not have an associated
     * sequence part (when the rule part is optional) or tag.
     */
    @Override
    public List<String[]> getSequenceParts(String phonemeSequence, String rule)
    throws UnknownPhonemeStructureException, UnknownConstantException {
	
	    logger.debug("getSequenceParts(): phonemeSequence: " +  phonemeSequence);
	    logger.debug("getSequenceParts(): rule: " +  rule);

	List<String[]> parts = new ArrayList<String[]>();
	Matcher matcher;
	Pattern pattern;

	String notOpenCloseRegex = "[^" + TAG_OPEN + TAG_CLOSE + "]";
	String tagRegex = TAG_OPEN + notOpenCloseRegex + "*" + TAG_CLOSE;
	String letterOrStructure = "("
	    + Pattern.quote(PHONEME_STRUCTURE_IDENTIFIER
		    + PHONEME_STRUCTURE_BRACKET_OPEN) + "?" + VAR_SYMBOL
		    + "*" + Pattern.quote(PHONEME_STRUCTURE_BRACKET_OPEN) + "?|.)";

	// split up the rule
	// one char followed by a tag
	pattern = Pattern.compile("(" + letterOrStructure + tagRegex + "|"
		// a char sequence in brackets followed by a tag
		+ "\\(" + notOpenCloseRegex + "*\\)" + tagRegex + ")");

	matcher = pattern.matcher(rule);

	int lastMatchEnd = 0;
	while (matcher.find()) {

	    String[] part = new String[3];
	    String[] ruleParts;

	    if (lastMatchEnd != matcher.start()) {
		
		//FIXME: "(.(-..?.))" becomes splitted to "(.", ...

		// get not matched part

		//there might be syllable delemiters in there which must be saves separately
		String notMatchedPart = rule.substring(lastMatchEnd, matcher.start());
		String[] splitted = notMatchedPart.split(language.getSyllableDelimiter());

		if (notMatchedPart.startsWith(language.getSyllableDelimiter())) {
		    String[] part2 = new String[3];
		    part2[0] =  "-";
		    part2[1] =  "";
		    part2[2] = "";
		    parts.add(part2);
		}

		for (int i = 0 ; splitted.length > i ; i++) {

		    String[] part2 = new String[3];
		    part2[1] =  splitted[i];
		    part2[2] = "";
		    parts.add(part2);
		    logger.debug("getSequenceParts(): matched rule part: " +  part2[1]);

		    while (splitted.length > i+1) {
			part2 = new String[3];
			part2[0] =  "-";
			part2[1] =  "";
			part2[2] = "";
			parts.add(part2);
		    }
		}

		if (notMatchedPart.endsWith(language.getSyllableDelimiter())) {
		    String[] part2 = new String[3];
		    part2[0] =  "-";
		    part2[1] =  "";
		    part2[2] = "";
		    parts.add(part2);

		}

	    }

	    ruleParts = matcher.group().split(Pattern.quote(TAG_OPEN));
	    part[1] = ruleParts[0];
	    part[2] = TAG_OPEN + ruleParts[1];

	    logger.debug("getSequenceParts(): matched rule part: " +  part[1] + ", tag was: " +  part[2]);
	    parts.add(part);

	    lastMatchEnd = matcher.end();
	}

	if (lastMatchEnd != rule.length()) {

	    // get last part if necessary
	    String[] part = new String[3];
	    part[1] = rule.substring(lastMatchEnd, rule.length());
	    part[2] = "";

	    logger.debug("getSequenceParts(): matched rule part: " +  part[1]);
	    parts.add(part);
	}

	// split up phoneme sequence
	lastMatchEnd = 0;
	String before = "";
	//String after = rule.replaceAll(language.getSyllableDelimiter(), language.getSyllableDelimiter() + language.getSyllableDelimiter());
	String after = stripStructure(replaceConstants(replacePhonemeStructures(stripRule(rule))));
	after = after.replaceAll(language.getSyllableDelimiter() + "+",
		language.getSyllableDelimiter());
	after = after.replaceAll("^" + language.getSyllableDelimiter() + "*", "");

	for (String[] part : parts) {

	    logger.debug("getSequenceParts(): looking for: " +part[1]);
	    if (part[0] != null && part[0].equals(language.getSyllableDelimiter())) {
		logger.debug("getSequenceParts(): skipping syllable delimiter");
		after = after.replaceFirst(language.getSyllableDelimiter(), "");
		before += language.getSyllableDelimiter();
		continue;
	    }

	    logger.debug("getSequenceParts(): before: " + before);
	    logger.debug("getSequenceParts(): after: " + after);

	    if (isPhonemeStructureName(part[1])) {

		String structure = language.getPhonemeStructure(
			stripVarName(part[1])).toString();
		structure = stripStructure(structure);

		if (after.startsWith(language.getSyllableDelimiter())) {

		    after = after.replaceFirst(language.getSyllableDelimiter(), "");
		    before += language.getSyllableDelimiter();
		}

		if (after.startsWith("(" + language.getSyllableDelimiter())) {

		    after = after.replaceFirst("\\(" + language.getSyllableDelimiter(), "(");
		    before += language.getSyllableDelimiter();
		}

		after = after.replaceFirst(Pattern.quote(structure), "");

		logger.debug("getSequenceParts(): before:" + before);
		logger.debug("getSequenceParts(): after:" + after);
		logger.debug("getSequenceParts(): structure:" + structure);

		pattern = Pattern.compile("^(" + before + ")(" + structure + ")(" + after + ")$");
		before += structure;
		before = before.replaceAll("\\(","(?:"); //change to non-capturing group
		before = before.replaceAll("^" + language.getSyllableDelimiter(), "");
	    } else {

		String regex = stripStructure(replaceConstants(part[1]));

		if (regex.startsWith(language.getSyllableDelimiter())
			&& !after.startsWith(language.getSyllableDelimiter())) {

		    after = language.getSyllableDelimiter() + after;
		}

		if (!regex.startsWith(language.getSyllableDelimiter())
			&& after.startsWith(language.getSyllableDelimiter())) {

		    regex = language.getSyllableDelimiter() + regex;
		}

		after = after.replaceAll("^" + Pattern.quote(regex), "");
		//		after = after.replaceAll("^" + language.getSyllableDelimiter(), "");

		before = before.replaceAll("\\(","(?:"); //change to non-capturing group

		logger.debug("getSequenceParts(): before:" + before);
		logger.debug("getSequenceParts(): after: " + after);
		logger.debug("getSequenceParts(): regex: " + regex);

		pattern = Pattern.compile("^(" + before + ")(" + regex + ")(" + after + ")$");
		before += regex;
	    }

	    matcher = pattern.matcher(phonemeSequence);
	    matcher.find();

	    logger.debug("getSequenceParts(): pattern: " + pattern);
	    logger.debug("getSequenceParts(): phonemeSequence: " + phonemeSequence);

	    part[0] = matcher.group(2);
	    logger.debug("getSequenceParts(): group 2 matched in phonemeSequence: " + part[0]);

	    lastMatchEnd = matcher.end();
	}

	return parts;
    }

    /**
     * Returns true if whole string is a tag.
     */
    @Override
    public boolean isTagSequence(String string) {

	String regex = TAG_OPEN + "(" + CHANGE_TAG_IDENTIFIER + "|"
	+ NAME_TAG_IDENTIFIER + ")" + "[^" + TAG_OPEN + TAG_CLOSE
	+ "]*" + TAG_CLOSE;
	return string.matches(regex);
    }

    /**
     * Returns true if whole string is a phoneme structure name.
     */
    @Override
    public boolean isPhonemeStructureName(String string) {

	String regex = Pattern.quote(PHONEME_STRUCTURE_IDENTIFIER)
	+ VAR_SYMBOL
	+ "+|"
	+ Pattern.quote(PHONEME_STRUCTURE_IDENTIFIER
		+ PHONEME_STRUCTURE_BRACKET_OPEN) + VAR_SYMBOL + "+"
		+ Pattern.quote(PHONEME_STRUCTURE_BRACKET_CLOSE);

	return string.matches(regex);
    }

    /**
     * Returns true if whole string is a constant name.
     */
    public boolean isConstantName(String string) {

	String regex = Pattern.quote(CONSTANT_IDENTIFIER) + VAR_SYMBOL + "+|"
	+ Pattern.quote(CONSTANT_IDENTIFIER + CONSTANT_BRACKET_OPEN)
	+ VAR_SYMBOL + "+" + Pattern.quote(CONSTANT_BRACKET_CLOSE);

	return string.matches(regex) && checkSyntax(string);
    }

    /**
     * Removes the variable identifier form the variable name.
     */
    @Override
    public String stripVarName(String string) {

	if (string.contains(CONSTANT_BRACKET_OPEN)
		|| string.contains(PHONEME_STRUCTURE_BRACKET_OPEN)) {

	    return string.substring(2, string.length() - 1);
	}

	return string.substring(1, string.length());
    }

    /**
     * Trims a rule.
     * 
     * Removes unnecessary parts from the rule string (e.g. rule parts which
     * are not applicable because of not fulfilled compare tags).
     */
    @Override
    public String trimRule(String rule, String sequence)
    throws UnknownPhonemeStructureException, RuleViolationException,
    UnknownConstantException {

	String tmpRule = replaceQuantifiers(rule, sequence);

	if (("(" + tmpRule + ")").matches(getTrimRuleRegexWithOr(tmpRule.length()))) {

	    return trimRule("", "(" + tmpRule + ")", sequence, 0);
	}

	return trimRule("", tmpRule, sequence, 0);
    }

    private String trimRule(String ruleAlreadyParsed, String ruleToParse,
	    String sequence, int iteration) throws RuleViolationException,
	    UnknownPhonemeStructureException, UnknownConstantException {

	logger.debug("trimRule(): already parsed: " + ruleAlreadyParsed);
	logger.debug("trimRule(): to parse: " + ruleToParse);
	logger.debug("trimRule(): sequence: " + sequence);
	logger.debug("trimRule(): iteration: " + iteration);

	if (ruleToParse.contains("|")) {

	    String regexWithOr = getTrimRuleRegexWithOr(ruleToParse.length());

	    // will be filled with resulting rule parts
	    String before;
	    String after;
	    String orRulePart;
	    List<String> orParts = new ArrayList<String>();
	    String rule = null;

	    Pattern orPattern = Pattern.compile(regexWithOr);
	    Matcher firstOrMatcher = orPattern.matcher(ruleToParse);

	    // get first top level "or"
	    firstOrMatcher.find();
	    orRulePart = firstOrMatcher.group();

	    // remove brackets
	    //orRulePart = orRulePart.substring(1, orRulePart.length() - 1);

	    logger.debug("trimRule(): orRulePart: " + orRulePart);

	    //split top level or-group
	    Matcher orPartsMatcher = orPattern.matcher(orRulePart);
	    orPartsMatcher.find();
	    for (int groupNr = 1;groupNr <= orPartsMatcher.groupCount() && orPartsMatcher.group(groupNr) != null;groupNr++) {
		orParts.add(orPartsMatcher.group(groupNr));
	    }


	    before = ruleAlreadyParsed + ruleToParse.substring(0, firstOrMatcher.start());
	    after = ruleToParse.substring(firstOrMatcher.end());

	    if (logger.isDebugEnabled()) {
		logger.debug("trimRule(): before: " + before);
		logger.debug("trimRule(): after: " + after);
		for( String orPart : orParts) {
		    logger.debug("trimRule(): orPart: " + orPart);
		}
	    }

	    // get parts of current top level 'or': part|part|part
	    for (String part : orParts) {

		logger.debug("trimRule(): parsing or-Part: " + part);

		try {

		    logger.debug("trimRule(): Start next iteration");
		    rule = trimRule(before, part + after, sequence, iteration + 1);
		    break; //part matches

		} catch (IllegalStateException ex) {

		    logger.debug("trimRule(): Part '" + part + "' doesn't match the sequence. Continue with next part", ex);
		}
	    }

	    if (rule == null) {

		try {

		    // nothing matched => rule does not apply
		    throw new RuleViolationException(DefaultRule.newInstance(ruleAlreadyParsed
			    + ruleToParse, language), sequence);
		} catch (SyntaxException ex) {

		    throw new Error("Invalid rule syntax while parsing: "
			    + ruleAlreadyParsed + ruleToParse, ex);
		}
	    }

	    return rule;
	}

	// else: ruleRest.contains("|")

	String rule = "";

	if ((ruleAlreadyParsed + ruleToParse).contains(TAG_OPEN + COMPARE_TAG_IDENTIFIER)) {

	    // parse compare tag

	    logger.debug("trimRule(): Pass to getSequenceParts: sequence = " + sequence);
	    logger.debug("trimRule(): Pass to getSequenceParts: rule = " + ruleAlreadyParsed + ruleToParse);

	    List<String[]> ruleParts = getSequenceParts(sequence, ruleAlreadyParsed
		    + ruleToParse);

	    for (String[] part : ruleParts) {

		PhonemeStructure structure;
		String sequencePart = part[0];
		String ruleRegex = part[1];
		String ruleTag = part[2];

		logger.debug("trimRule(): sequencePart: " + sequencePart);
		logger.debug("trimRule(): ruleRegex: " + ruleRegex);
		logger.debug("trimRule(): ruleTag: " + ruleTag);

		if (ruleTag.startsWith(TAG_OPEN + COMPARE_TAG_IDENTIFIER)) {

		    ruleTag = ruleTag.replaceFirst(Pattern.quote(TAG_OPEN
			    + COMPARE_TAG_IDENTIFIER), "");
		    ruleTag = ruleTag.substring(0, ruleTag.length() - 1);

		    if (isPhonemeStructureName(ruleRegex)) {

			structure = language.getPhonemeStructure(stripVarName(ruleRegex));
		    } else {

			try {
			    structure = new PhonemeStructure(
				    replacePhonemeStructures(ruleRegex),
				    language);

			} catch (SyntaxException ex) {

			    // should not happen
			    throw new IllegalArgumentException(ex);
			}
		    }

		    if (!comareTagIsTrue(ruleTag, sequencePart, structure)) {

			throw new IllegalStateException("No match found");
		    }

		    rule += ruleRegex;
		} else {

		    rule += ruleRegex + ruleTag;
		}
	    }

	    // no exception thrown in for loop -> all compare tags passed
	    // --> regex ok

	} else { // ruleRest.contains(TAG_OPEN + COMPARE_TAG_IDENTIFIER)

	    rule = ruleAlreadyParsed + ruleToParse;
	}

	// check if rule applies
	String strippedRule = stripStructure(replaceConstants(replacePhonemeStructures(stripRule(rule))));
	logger.debug("trimRule(): rule: " + rule);
	logger.debug("trimRule(): strippedRule: " + strippedRule);

	String tmpSequence = sequence;

//	if (!tmpSequence.startsWith(language.getSyllableDelimiter())) {
//	    tmpSequence = language.getSyllableDelimiter() + tmpSequence;
//	}
//
//	if (!tmpSequence.endsWith(language.getSyllableDelimiter())) {
//	    tmpSequence = language.getSyllableDelimiter() + tmpSequence;
//	}

	tmpSequence = tmpSequence.replaceAll(language.getSyllableDelimiter() + "+", language.getSyllableDelimiter());

	logger.debug("trimRule(): tmpSequence: " + tmpSequence);

	if (tmpSequence.matches(strippedRule)) {

	    return rule;
	}

	throw new IllegalStateException("No match found");
    }

    @SuppressWarnings("unused")
    private String getTrimRuleRegexWithoutOr(int ruleLength) {

	int maxNo = (ruleLength / 4) + 1; // for each of (at least) 2
	// parts
	String regex;
	String part = "";
	String noBracketNoOr = "[^()|]";
	String bracketOpen = Pattern.quote("(");
	String bracketClose = Pattern.quote(")");

	// regex = \(part(\|part)+\)
	// part == [^()|]*(\(part\))?[^()|]*

	for (int i = 0; i < maxNo; i++) {

	    part += noBracketNoOr + "*(" + bracketOpen;
	}

	for (int i = 0; i < maxNo; i++) {

	    part += ")?" + noBracketNoOr + "*";
	}

	regex = part + "(" + Pattern.quote("|") + part + ")+";

	return regex;
    }

    String getTrimRuleRegexWithOr(int ruleLength) {

	int maxNo = (ruleLength / 4) + 1; // for each of (at least) 2
	// parts
	String regex;
	String part = "";
	String subpart = "";
	String noBracketNoOr = "[^()|]";
	String noBracket = "[^()]";
	String bracketOpen = "\\(";
	String bracketClose = "\\)";

	// regex == \(part1(\|part1)+\)
	// part == [^()|]*(\(subpart\))?[^()|]*
	// subpart == [^()]*(\(subpart\))?[^()]*

	for (int i = 0; i < maxNo; i++) {

	    subpart += noBracket + "*(?:" + bracketOpen;
	}

	for (int i = 0; i < maxNo; i++) {

	    subpart += noBracket + "*" + bracketClose + ")?" + noBracket + "*";
	}

	part = noBracketNoOr + "*(?:" + bracketOpen + subpart + bracketClose
	+ ")?" + noBracketNoOr + "*";
	part = "(" + part + ")";

	//thats what we want:
	//regex = part + "(?:" + "\\|" +  part + ")+"; 
	//but it does NOT work, since the second capturing group will be overwritten if there are more than two groups
	//see java/util/regex/Pattern.html#cg for more information

	regex = part;

	for (int i = 0; i < maxNo; i++) {

	    regex += "(?:\\|" + part + ")?";
	}

	return bracketOpen + regex + bracketClose;
    }

    /**
     * Returns a regex matching a group
     */
    private String getGroupRegex(int ruleLength) {

	int maxNo = (ruleLength / 4) + 1; // for each of (at least) 2 parts
	String regex;
	String part = "";
	String noBracket = "[^()]";
	String bracketOpen = "\\(";
	String bracketClose = "\\)";

	// regex == [^()]*(\(regex\))?[^()]*

	for (int i = 0; i < maxNo; i++) {

	    part += noBracket + "*(" + bracketOpen;
	}

	for (int i = 0; i < maxNo; i++) {

	    part += bracketClose + ")?" + noBracket + "*";
	}

	regex = bracketOpen + part + bracketClose;

	return regex;
    }

    /**
     * Replaces the regex quantifiers from <code>rule</code> if the
     * quantifier related to a capturing group with the concrete number of
     * occurrences of this group in <code>sequence</code>
     * 
     * E.g. the rule "(.a)+" will become ".a.a.a" when parsed with the
     * sequence "tatara"
     */
    protected String replaceQuantifiers(String rule, String sequence)
    throws UnknownPhonemeStructureException, UnknownConstantException {

	String delemiter = language.getSyllableDelimiter();
	String parsedRule = "";
	String groupRegex = "(" + getGroupRegex(rule.length()) + ")";
	String quantifierRegex = "(" + Pattern.quote("{") + "[0-9]*(,[0-9]*)?" + Pattern.quote("}") + "|" + "[?*+]" + ")+";
	String regex = groupRegex + quantifierRegex;

	Pattern quantifiedGroupPattern = Pattern.compile(regex);
	Matcher quantifiedGroupMatcher = quantifiedGroupPattern.matcher(rule);
	boolean matchFound = false;
	int lastMatchEnd = 0;

	while (quantifiedGroupMatcher.find()) {

	    matchFound = true;

	    // get not matched part
	    if (lastMatchEnd < quantifiedGroupMatcher.start()) {

		parsedRule += rule.substring(lastMatchEnd,
			quantifiedGroupMatcher.start());
	    }

	    String captured = quantifiedGroupMatcher.group();
	    captured = stripStructure(replaceConstants(replacePhonemeStructures(stripRule(captured))));
	    // change all capturing groups to non-capturing groups
	    // TODO: "Special constructs"
	    captured = captured.replaceAll("\\((\\?[=!<>][=!]?:?)?", "(?:");

	    // group(1) maches the group without quantifier
	    String nonStrippedGroup = quantifiedGroupMatcher.group(1);
	    String group = quantifiedGroupMatcher.group(1);
	    group = stripStructure(replaceConstants(replacePhonemeStructures(stripRule(group))));
	    group = group.replaceAll("\\((\\?[=!<>][=!]?:?)?", "(?:");

	    String before = parsedRule;
	    before = stripStructure(replaceConstants(replacePhonemeStructures(stripRule(before))));
	    before = before.replaceAll("\\((\\?[=!<>][=!]?:?)?", "(?:");

	    String after = rule.substring(quantifiedGroupMatcher.end(), rule
		    .length());
	    after = stripStructure(replaceConstants(replacePhonemeStructures(stripRule(after))));
	    after = after.replaceAll("\\((\\?[=!<>][=!]?:?)?", "(?:");

	    String buildedRule = "(" + before + ")(" + captured + ")" + after;

	    Pattern sequencePattern = Pattern.compile(buildedRule);
	    Matcher sequenceMatcher = sequencePattern.matcher(sequence
		    + delemiter);

	    sequenceMatcher.find();
	    // sequence part matching captured
	    String sequenceGroup = sequenceMatcher.group(2);

	    // TODO: thats just a quick and dirty workarount
	    if (delemiter.equals(Character.toString(sequenceGroup
		    .charAt(sequenceGroup.length() - 1)))) {

		Pattern gPattern = Pattern.compile("(" + group + ")" + after);
		Matcher gMatcher = gPattern.matcher(sequence + delemiter);

		int lastMatch = sequenceMatcher.end() - 1;
		while (gMatcher.find(lastMatch)) {

		    sequenceGroup += gMatcher.group(1);

		    if (delemiter.equals(Character.toString(sequenceGroup
			    .charAt(gMatcher.end() - 1)))) {

			lastMatch = gMatcher.end() - 1;
		    } else {

			lastMatch = gMatcher.end();
		    }
		}

		sequenceGroup = sequenceGroup.replaceAll(delemiter + "+",
			delemiter);
		sequenceGroup = sequenceGroup.replaceAll(delemiter + "$", "");
		sequenceGroup += delemiter;
	    }

	    // count times group (without quantifier) is in sequence part
	    // and add group x times to resulting rule
	    Pattern groupPattern = Pattern.compile(group);
	    Matcher groupMatcher = groupPattern.matcher(sequenceGroup);

	    int lastMatch = 0;
	    while (groupMatcher.find(lastMatch)) {

		// add WITH surounding brackets
		// in case it is part of an or-expession
		parsedRule += nonStrippedGroup;

		if (groupMatcher.group().matches(".*" + delemiter)) {

		    lastMatch = groupMatcher.end() - 1;
		} else {

		    lastMatch = groupMatcher.end();
		}

	    }

	    lastMatchEnd = quantifiedGroupMatcher.end();
	}

	// get last not matched part
	if (matchFound && lastMatchEnd < rule.length()) {

	    parsedRule += rule.substring(lastMatchEnd, quantifiedGroupMatcher.start());
	} else {
	    if (lastMatchEnd < rule.length()) {

		parsedRule = rule;
	    }
	}

	return parsedRule;
    }

    /**
     * Parses a tag.
     * 
     * @param phonemeSequence
     *                the string to parse.
     * @param structure
     *                the PhonemeStructure
     * @param aTag
     * @return the resulting phoneme sequence
     */
    @Override
    public String parseTag(String phonemeSequence, PhonemeStructure structure,
	    String aTag) throws RuleDoesNotApplyException {

	// check if optional
	if (aTag.endsWith("?") && phonemeSequence.equals("")) {

	    return "";
	}

	if (aTag.startsWith(TAG_OPEN + ADD_TAG_IDENTIFIER)) {

	    // strip tag identifier
	    String tag = aTag.replaceFirst(Pattern.quote(TAG_OPEN
		    + ADD_TAG_IDENTIFIER), "");
	    tag = tag.split(TAG_CLOSE)[0];

	    return phonemeSequence + tag;

	} else if (aTag.startsWith(TAG_OPEN + CHANGE_TAG_IDENTIFIER)) {

	    // strip tag identifier
	    String tag = aTag.replaceFirst(Pattern.quote(TAG_OPEN
		    + CHANGE_TAG_IDENTIFIER), "");
	    tag = tag.split(TAG_CLOSE)[0];

	    return parseChangeTag(tag, phonemeSequence, structure);

	} else if (aTag.startsWith(TAG_OPEN + DELETE_TAG_IDENTIFIER)) {

	    return "";

	} else {

	    throw new IllegalArgumentException("Unknown tag: " + aTag);
	}
    }

    /**
     * Parses a change tag and returns the new value of the phoneme
     * sequence.
     * 
     * @param tag
     *                the tag to parse
     * @param phonemeStructure
     *                the structure
     * @param phonemeSequence
     *                the sequence to start with
     * @return the changed sequence
     */
    private String parseChangeTag(String tag, String phonemeSequence,
	    PhonemeStructure structure) throws RuleDoesNotApplyException {

	String current = new String(phonemeSequence);

	// split named groups
	for (String namedGroup : tag.split(NAMED_GROUP_DELIMITER)) {

	    // distinguish between dependent and non-dependent changing
	    if (namedGroup.contains(ASSIGNER + DEPENDENT_CHANGE_TAG_OPEN)) {

		current = parseDependentChanging(namedGroup, current, structure);
	    } else {

		current = parseNonDependentChanging(namedGroup, current,
			structure);
	    }
	}

	return current;
    }

    /**
     * Parses a change tag with dependigs and returns the new value of the
     * sequence.
     * 
     * @param tag
     *                the tag to parse
     * @param phonemeSequence
     *                the sequence to start with
     * @param structure
     *                the structure
     * @return the changed sequence
     */
    private String parseDependentChanging(String tag, String phonemeSequence,
	    PhonemeStructure structure) throws RuleDoesNotApplyException {

	// for the changing rules (FROM is key and TO is value)
	Map<String, String> changings = new HashMap<String, String>();

	String[] tagParts = tag.split(ASSIGNER);
	String groupName = tagParts[0];
	String currentValue;
	String replacment;

	// cut leading and tailing bracket
	tagParts[1] = tagParts[1].substring(1, tagParts[1].length() - 1);

	// store rules in map
	for (String assignment : tagParts[1].split(ASSIGMENT_DELIMITER)) {

	    String[] fromToPair = assignment.split(CHANGE_TO_ASSIGNER);

	    // if it should be changed to ""
	    if (fromToPair.length == 1) {

		String tempFromToPair = new String(fromToPair[0]);

		fromToPair = new String[2];
		fromToPair[0] = tempFromToPair;
		fromToPair[1] = "";
	    }

	    changings.put(fromToPair[0], fromToPair[1]);
	}

	// get value of named phoneme
	currentValue = getNamedGroup(groupName, phonemeSequence, structure);
	replacment = changings.get(currentValue);

	if (replacment == null) {

	    replacment = changings.get(ANY_SYMBOL);
	}

	if (replacment == null) {

	    // no rule for given value
	    // exception catched by parseChangeTag()
	    throw new RuleDoesNotApplyException("rule: " + structure + TAG_OPEN
		    + tag + TAG_CLOSE + ", sequence: " + phonemeSequence);
	}

	return replaceNamedGroup(phonemeSequence, groupName, replacment,
		structure);
    }

    /**
     * Parses a change tag without dependigs and returns the new value of
     * the phoneme sequence.
     * 
     * @param tag
     *                the tag to parse
     * @param phonemeSequence
     *                the sequence to start with
     * @return the changed sequence
     */
    private String parseNonDependentChanging(String tag,
	    String phonemeSequence, PhonemeStructure structure) {

	String[] tagParts = tag.split(ASSIGNER);
	String groupName = tagParts[0];
	String replacment = tagParts[1];

	return replaceNamedGroup(phonemeSequence, groupName, replacment,
		structure);
    }

    /**
     * Checks a compare tag.
     * 
     * Returns true if the compare tag is true for the given phoneme
     * sequence.
     */
    private boolean comareTagIsTrue(String tag, String sequence,
	    PhonemeStructure structure) throws UnknownConstantException {

	String operatorRegex = Pattern.quote(OPERATOR_MATCHES) + "|"
	+ Pattern.quote(OPERATOR_DOESNT_MATCH);
	Matcher matcher = Pattern.compile(operatorRegex).matcher(tag);
	matcher.find();
	String operator = matcher.group();
	String[] comparators = tag.split(operatorRegex);

	// replace constants and named groups
	if (isConstantName(comparators[0])) {

	    comparators[0] = language.getConstant(comparators[0]);
	} else if (structure.getGroupNames().contains(comparators[0])) {

	    comparators[0] = getNamedGroup(comparators[0], sequence, structure);
	}

	if (isConstantName(comparators[1])) {

	    comparators[1] = language.getConstant(comparators[1]);
	} else if (structure.getGroupNames().contains(comparators[1])) {

	    comparators[1] = getNamedGroup(comparators[1], sequence, structure);
	}

	// perform comperation
	if (operator.equals(OPERATOR_MATCHES)) {

	    return comparators[0].equals(comparators[1]);
	}

	if (operator.equals(OPERATOR_DOESNT_MATCH)) {

	    return !comparators[0].equals(comparators[1]);
	}

	throw new IllegalArgumentException("Unknown operator: " + operator);
    }

    /**
     * Replaces the named group in the phoneme sequence.
     * 
     * @param namedGroup
     *                the name of the named group
     * @param sequence
     *                the phoneme sequence to get the value from
     * @param replacment
     *                the sequence to replace the group with
     * @param structure
     *                the phoneme structure
     * @return the change sequence
     */
    private String replaceNamedGroup(String sequence, String namedGroup,
	    String replacment, PhonemeStructure structure) {

	String returnValue = "";
	List<String> groupNames = structure.getGroupNames();

	if (namedGroup.equals(FIRST_GROUP)) {

	    for (int i = 0; i < groupNames.size(); i++) {

		returnValue = replaceExplicitNamedGroup(sequence, groupNames
			.get(i), replacment, structure);

		if (!returnValue.equals("")) {

		    break;
		}
	    }
	} else {
	    if (namedGroup.equals(LAST_GROUP)) {

		for (int i = groupNames.size(); i > 0; i--) {

		    returnValue = replaceExplicitNamedGroup(sequence,
			    groupNames.get(i - 1), replacment, structure);

		    if (!returnValue.equals("")) {

			break;
		    }
		}
	    } else {
		returnValue = replaceExplicitNamedGroup(sequence, namedGroup,
			replacment, structure);
	    }
	}

	return returnValue;
    }

    /**
     * Replaces the named group in the sequence excluding LAST and FIRST.
     * 
     * @param namedGroup
     *                the name of the named group
     * @param sequence
     *                the phoneme sequence to get the value from
     * @param replacment
     *                the sequence to replace the group with
     * @param structure
     *                the phoneme structure
     * @return the change sequence
     */
    private String replaceExplicitNamedGroup(String sequence,
	    String namedGroup, String replacment, PhonemeStructure structure) {

	String[] sequenceParts = splitPhonemeStructureByGroup(structure
		.toString(), namedGroup);

	String before = "(" + sequenceParts[0] + ")";
	String group = "(" + sequenceParts[1] + ")";
	String after = "(" + sequenceParts[2] + ")";

	Pattern getNumberPattern;
	Pattern groupPattern;
	Matcher groupMatcher;
	Matcher getNumberMatcher;
	String groupRegex;
	String structureBefore;
	String structureAfter;
	int groupsToIgnore;

	// get number of groups before the end of the named group
	getNumberPattern = Pattern.compile(before + group);
	getNumberMatcher = getNumberPattern.matcher("");

	groupsToIgnore = getNumberMatcher.groupCount();

	// get value
	groupRegex = before + group + after;

	groupPattern = Pattern.compile(groupRegex);
	groupMatcher = groupPattern.matcher(sequence);

	if (groupMatcher.find() && !groupMatcher.group(2).equals("")) {

	    // matches the whole part before the group
	    structureBefore = groupMatcher.group(1);

	    // matches the whole part after the group
	    structureAfter = groupMatcher.group(groupsToIgnore + 1);

	    return structureBefore + replacment + structureAfter;
	}

	return "";
    }

    /**
     * Splites the phoneme structure by a given named group. The returned
     * String array contains the part of the structure before the group (at
     * index 0), the group itself (at index 1) and the part after the group
     * (at index 2). If the group is at the beginning or end of the
     * structure the according array entry (0 or 2) is filled with an empty
     * string.
     * 
     * @param structure
     *                the whole structure
     * @param namedGroup
     *                the name of the named group
     * @return an array with the three parts of the structure
     */
    @Override
    public String[] splitPhonemeStructureByGroup(String structure,
	    String namedGroup) {

	// TODO: a group in a group isn't possible (not necessary as long as
	// only syllable is used)
	String groupTemplateRegex = "("
	    // single char ...
	    + "[^\\(\\)\\?]"
	    // .., list of possible (single) chars (by using '[]')
	    + "|\\[[^\\(\\)\\?\\[\\]]*\\]"
	    // ...or group
	    + "|(\\([^\\(\\)\\?]*\\))"
	    // ... followed by a named group
	    + ")" + TAG_OPEN + NAME_TAG_IDENTIFIER + namedGroup + TAG_CLOSE
	    + "\\??";

	Pattern pattern = Pattern.compile(groupTemplateRegex);
	Matcher matcher = pattern.matcher(structure);

	if (matcher.find()) {

	    String[] structureParts = new String[3];

	    structureParts[0] = stripStructure(structure.substring(0, matcher
		    .end()
		    - matcher.group().length()));
	    structureParts[1] = stripStructure(matcher.group());
	    structureParts[2] = stripStructure(structure.substring(matcher
		    .end()));

	    return structureParts;
	}

	throw new IllegalArgumentException("Cannot find group " + namedGroup
		+ " in phoneme structure '" + structure + "'");
    }

    /**
     * Replaces the constants name with the value (a regular expression).
     * 
     * @param string
     *                the string in which the constants will be replaced.
     * @return the changed string
     * @throws UnknownConstantException
     *                 if the constant is not defined in the language
     */
    @Override
    public String replaceConstants(String string)
    throws UnknownConstantException {

	String result = new String(string);

	// to get constants in string
	String regex = Pattern.quote(CONSTANT_IDENTIFIER) + VAR_SYMBOL + "+|"
	+ Pattern.quote(CONSTANT_IDENTIFIER + CONSTANT_BRACKET_OPEN)
	+ VAR_SYMBOL + "+" + Pattern.quote(CONSTANT_BRACKET_CLOSE);

	Pattern pattern = Pattern.compile(regex);
	Matcher matcher = pattern.matcher(string);

	while (matcher.find()) {

	    String constant = matcher.group();
	    String replacment;

	    if (constant.contains(CONSTANT_BRACKET_OPEN)) {

		replacment = language.getConstant(stripVarName(constant));
	    } else {

		replacment = language.getConstant(stripVarName(constant));
	    }

	    result = result.replaceAll(Pattern.quote(constant), replacment);
	}

	return result;
    }

    /**
     * Replaces the phoneme structures name with the linked regex.
     * 
     * @param string
     *                the string in which the phoneme structure will be
     *                replaced.
     * @return the changed string
     * @throws UnknownPhonemeStructureException
     *                 if the structure is not defined in the language
     */
    @Override
    public String replacePhonemeStructures(String string)
    throws UnknownPhonemeStructureException {

	String result = new String(string);

	// to get constants in string
	String regex = Pattern.quote(PHONEME_STRUCTURE_IDENTIFIER)
	+ VAR_SYMBOL
	+ "+|"
	+ Pattern.quote(PHONEME_STRUCTURE_IDENTIFIER
		+ PHONEME_STRUCTURE_BRACKET_OPEN) + VAR_SYMBOL + "+"
		+ Pattern.quote(PHONEME_STRUCTURE_BRACKET_CLOSE);

	Pattern pattern = Pattern.compile(regex);
	Matcher matcher = pattern.matcher(string);

	while (matcher.find()) {

	    String name = matcher.group();
	    PhonemeStructure structure = language.getPhonemeStructure(stripVarName(name));

	    // if the syllable can occur a various of times 
	    // ("$S{1,2}", or something like that), we have to tread it 
	    // different, to make sure that both of this get the same result:
	    //
	    // $S{1,2}$S -> xxx-xxx bzw. xxx-xxx-xxx 
	    // a$Sa      -> a-xxx-a
	    //
	    // we handle this special case first
	    // $S{1,2} => (-xxx){1,2}-
	    Matcher matcher2 = Pattern.compile(Pattern.quote(name) + "(\\{[0-9,]*\\})").matcher(result);
	    while( matcher2.find()) {
		String quantifier = matcher2.group(1);
		result = result.replaceAll(Pattern.quote(name) + "(\\{[0-9,]*\\})" + quantifier, 
			"(" + language.getSyllableDelimiter() + structure.toString() + ")" + quantifier + language.getSyllableDelimiter());
	    }
	    //	    result = result.replaceAll(Pattern.quote(name) + "(\\{[0-9,]*\\})", 
	    //		    "(" + language.getSyllableDelimiter() + structure.toString() + ")\\1-");

	    // now we take care of the normal case
	    // $S => -xxx-
	    result = result.replaceAll(Pattern.quote(name), 
		    "(" + language.getSyllableDelimiter() 
		    + structure.toString() + language.getSyllableDelimiter() + ")");
	}

	result = replaceRegex(language.getSyllableDelimiter() + "+", result, language.getSyllableDelimiter());
	result = replaceRegex("^" + language.getSyllableDelimiter(), result, "");
	result = replaceRegex(language.getSyllableDelimiter() + "$", result, "");
	result = replaceRegex("^" + "(\\(+)" + language.getSyllableDelimiter(), result, "\\1");  //FIXME: Cant do that, cause we do not know if any quantifiers are used
	result = replaceRegex(language.getSyllableDelimiter() + "(\\)+)$", result, "\\1");
	result = replaceRegex(language.getSyllableDelimiter() + "(\\(+)" + language.getSyllableDelimiter(), result, language.getSyllableDelimiter() + "\\1");
	result = replaceRegex(language.getSyllableDelimiter() + "(\\)+)" + language.getSyllableDelimiter(), result, language.getSyllableDelimiter() + "\\1");
	result = replaceRegex(language.getSyllableDelimiter() + "\\)\\(" + language.getSyllableDelimiter(), result, language.getSyllableDelimiter() + ")(");

	return result;
    }
    
    private String replaceRegex(String regex, String input, String replacement) {
	
	Pattern pattern = Pattern.compile(regex);
	Matcher matcher = pattern.matcher(input);
	
	while (matcher.find()) {

	    for (int groupId = 1;replacement.contains("\\" + groupId);groupId++) {
		replacement = replacement.replace("\\" + groupId, matcher.group(groupId));
	    }
	    
	    input = input.replaceFirst(regex, replacement);
	}
	return input;
    }

    /**
     * Gets the current value of the named group out of a string.
     * 
     * The value may be a empty string when the group is optional. Note that
     * FIRST/LAST will return the FIRST/LAST named group, not the first or
     * last phoneme. Not named phonemes will be omitted.
     * 
     * @param namedGroup
     *                the name of the named group
     * @param phonemeSequence
     *                the sequence to get the value from
     * @param structure
     *                the whole structure, matching the sequence
     * @return the value of the named group in phonemeSequence
     */
    @Override
    public String getNamedGroup(String namedGroup, String phonemeSequence,
	    PhonemeStructure structure) {

	String returnValue = "";
	List<String> groupNames = structure.getGroupNames();

	if (namedGroup.equals(FIRST_GROUP)) {

	    for (int i = 0; i < groupNames.size(); i++) {

		returnValue = getExpicitNamedGroup(groupNames.get(i),
			phonemeSequence, structure);

		if (!returnValue.equals("")) {

		    break;
		}
	    }
	}

	else {

	    if (namedGroup.equals(LAST_GROUP)) {
		for (int i = groupNames.size(); i > 0; i--) {

		    returnValue = getExpicitNamedGroup(groupNames.get(i - 1),
			    phonemeSequence, structure);

		    if (!returnValue.equals("")) {

			break;
		    }
		}
	    } else {

		returnValue = getExpicitNamedGroup(namedGroup, phonemeSequence,
			structure);
	    }
	}

	return returnValue;
    }

    /**
     * Gets the current value of the named group (excluding FIRST and LAST)
     * out of a string.
     * 
     * The value may be a empty string when the group is optional.
     * 
     * @param namedGroup
     *                the name of the named group
     * @param aStructure
     *                the structure to get the value from
     * @return the value of the named group in aStructure
     */
    private String getExpicitNamedGroup(String namedGroup,
	    String phonemeSequence, PhonemeStructure structure) {

	String[] structureParts = splitPhonemeStructureByGroup(structure.toString(), namedGroup);
	String before = structureParts[0];
	String group = "(" + structureParts[1] + ")"; // grouped to get the
	// group later
	String after = structureParts[2];

	Pattern getNumberPattern;
	Pattern groupPattern;
	Matcher groupMatcher;
	Matcher getNumberMatcher;
	String groupRegex;
	int groupsToIgnore;

	// get number of groups before the needed one
	getNumberPattern = Pattern.compile(before);
	getNumberMatcher = getNumberPattern.matcher("");

	groupsToIgnore = getNumberMatcher.groupCount();

	// get value
	groupRegex = before + group + after;

	groupPattern = Pattern.compile(groupRegex);
	groupMatcher = groupPattern.matcher(phonemeSequence);

	groupMatcher.find();

	return groupMatcher.group(groupsToIgnore + 1);
    }
}
