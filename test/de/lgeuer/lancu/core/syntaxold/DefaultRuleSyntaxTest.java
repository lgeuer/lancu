package de.lgeuer.lancu.core.syntaxold;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.lgeuer.lancu.core.LanguageMock;
import de.lgeuer.lancu.core.RuleDoesNotApplyException;
import de.lgeuer.lancu.core.RuleViolationException;
import de.lgeuer.lancu.core.UnknownConstantException;
import de.lgeuer.lancu.core.UnknownPhonemeStructureException;
import de.lgeuer.lancu.core.entity.PhonemeStructure;
import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;

public class DefaultRuleSyntaxTest {

    private static final String SEQUENCE = "abb";

    private static final String RULE = "a<change:FIRST=y>.(x?.)<change:FIRST=(c:z)>";

    private static final String STRIPPED_RULE = "a.(x?.)";

    private static final String STRUCTURE = ".<name:C1>?.<name:V1>?.<name:V2>";

    private static final String STRIPPED_STRUCTURE = ".?.?.";

    private DefaultRuleSyntax syntax;

    private LanguageMock language;

    @Before
    public void setUp() {

	language = new LanguageMock();
	syntax = new DefaultRuleSyntax(language);
	language.setRuleSyntax(syntax);
    }

    @After
    public void tearDown() {

	language = null;
	syntax = null;
    }

    @Test
    public void checkPhonemeStructureSyntax() {

	String[] structure = { ".?", ".<name:V1>", "<name:V1>", ".<name:name>",
	".<name:V1" };
	boolean[] expected = { true, true, false, false, false };

	for (int i = 0; i < structure.length; i++) {

	    boolean actual = syntax.checkPhonemeStructureSyntax(structure[i]);

	    assertEquals(structure[i], expected[i], actual);
	}
    }

    @Test
    public void checkRuleSyntax() {

	String[] rule = { ".?", ".<delete>", "<delete>", ".<add:a>", "<add:a>",
		"<add:$S>", "<add:#S>", "$S<compare:V1==a>",
		"$S<compare:V1!=a>", "$S<compare:V1=a>", "$S<compare:a==V1>",
		"<compare:V1==a>", "$S<change:V1=a>", "$S<change:V1=(a:e)>",
	"$S<change:V1=(a:e,e:i)>" };
	boolean[] expected = { true, // void
		true, // delete
		false, // add
		true, true, false, false, true, // compare
		true, false, false, false, true, // change
		true, true };

	for (int i = 0; i < rule.length; i++) {

	    boolean actual = syntax.checkRuleSyntax(rule[i]);

	    assertEquals(rule[i], expected[i], actual);
	}
    }

    @Test
    public void stripVarName() {

	String structure1 = "$NAME";
	String structure2 = "${NAME}";
	String constant1 = "#NAME";
	String constant2 = "#{NAME}";

	String expected = "NAME";

	assertEquals(expected, syntax.stripVarName(structure1));
	assertEquals(expected, syntax.stripVarName(structure2));
	assertEquals(expected, syntax.stripVarName(constant1));
	assertEquals(expected, syntax.stripVarName(constant2));

    }

    @Test
    public void splitPhonemeStructureByGroup() {

	String group = "V1";
	String[] expected = { ".?", ".?", "." };
	String[] actual = syntax.splitPhonemeStructureByGroup(STRUCTURE, group);

	assertArrayEquals(expected, actual);
    }

    @Test
    public void getGroupNames() {

	List<String> actual = syntax.getGroupNames(STRUCTURE);
	List<String> expected = new ArrayList<String>();

	expected.add("C1");
	expected.add("V1");
	expected.add("V2");

	assertEquals("Compare list size.", expected.size(), actual
		.size());

	for (int i = 0; i < expected.size(); i++) {

	    assertEquals(expected.get(i), actual.get(i));
	}
    }

    @Test
    public void stripStructure() {

	String expected = STRIPPED_STRUCTURE;
	String actual = syntax.stripStructure(STRUCTURE);

	assertEquals(expected, actual);
    }

    @Test
    public void stripRule() {

	String expected = STRIPPED_RULE;
	String actual = syntax.stripRule(RULE);

	assertEquals(expected, actual);
    }

    @Test
    public void getNamedGroup() throws SyntaxException {

	String group = "C1";
	String sequence = "bar";
	String structure = "[^aeiou]<name:C1>?.<name:V1>?.";
	String expected = "b";
	String actual = syntax.getNamedGroup(group, sequence,
		new PhonemeStructure("XnameX", structure, language));

	assertEquals(expected, actual);

	group = "FIRST";
	expected = "b";
	actual = syntax.getNamedGroup(group, sequence, new PhonemeStructure(
		"XnameX", structure, language));

	assertEquals(expected, actual);

	sequence = "ar";
	expected = "a";
	actual = syntax.getNamedGroup(group, sequence, new PhonemeStructure(
		"XnameX", structure, language));

	assertEquals(expected, actual);
    }

    @Test
    public void replaceConstantsOK() throws UnknownConstantException {

	String string1 = "aNAMEa";
	String string2 = "a#{NAME}a";
	String string3 = "a#NAMEa";

	String expected1 = "aNAMEa";
	String expected2 = "a[values]a";
	String expected3 = "a[values]a";

	String actual1 = syntax.replaceConstants(string1);
	String actual2 = syntax.replaceConstants(string2);
	String actual3 = syntax.replaceConstants(string3);

	assertEquals("no constant", expected1, actual1);
	assertEquals("constant with brackets", expected2, actual2);
	assertEquals("constant without brackets", expected3, actual3);
    }

    @Test
    public void replaceConstantsUnknownConstantException()
    throws UnknownConstantException {

	String string = "a#DUMMYa";

	try {

	    syntax.replaceConstants(string);
	    fail("UnknownConstantException expected.");

	} catch (UnknownConstantException ex) {

	    assertTrue(true);
	}
    }

    @Test
    public void replacePhonemeStructures() throws UnknownConstantException,
    UnknownPhonemeStructureException {

	String string1 = "aNAMEa";
	String string2 = "a${NAME}a";
	String string3 = "a$NAME a";
	String string4 = "a$NAME{1,2}a";

	String expected1 = "aNAMEa";
	String expected2 = "a(-values-)a";
	String expected3 = "a(-values-) a";
	String expected4 = "a(-values){1,2}-a";

	String actual1 = syntax.replacePhonemeStructures(string1);
	String actual2 = syntax.replacePhonemeStructures(string2);
	String actual3 = syntax.replacePhonemeStructures(string3);
	String actual4 = syntax.replacePhonemeStructures(string4);

	assertEquals("no constant", expected1, actual1);
	assertEquals("constant with brackets", expected2, actual2);
	assertEquals("constant without brackets", expected3, actual3);
	assertEquals("constant with quantifier", expected4, actual4);
    }

    @Test
    public void getSequenceParts() throws UnknownPhonemeStructureException,
    UnknownConstantException {

	List<String[]> expected = new ArrayList<String[]>();

	String[] part0 = { "z", ".", "" };
	String[] part1 = { "-", "", "" };
	String[] part2 = { "a", "a", "<change:FIRST=y>" };
	String[] part3 = { "b", ".", "" };
	String[] part4 = { "b", "(x?.)", "<change:FIRST=(c:z)>" };
	String[] part5 = { "z", ".", "" };
	expected.add(part0);
	expected.add(part1);
	expected.add(part2);
	expected.add(part3);
	expected.add(part4);
	expected.add(part5);

	// "z-abbz", ".a<change:FIRST=y>.(x?.)<change:FIRST=(c:z)>."
	List<String[]> actual = syntax.getSequenceParts("z-" + SEQUENCE + "z", ".-" + RULE + ".");

	for (int i = 0; i < expected.size(); i++) {

	    assertEquals("part " + i + ", index 0", expected.get(i)[0], actual.get(i)[0]);
	    assertEquals("part " + i + ", index 1", expected.get(i)[1], actual.get(i)[1]);
	    assertEquals("part " + i + ", index 2", expected.get(i)[2], actual.get(i)[2]);
	}
    }

    @Test
    public void isTagSequence() {

	boolean result = syntax.isTagSequence("<change:FIRST=y>");
	assertTrue("change tag", result);

	result = syntax.isTagSequence("<change:>");
	assertTrue("empty change tag", result);

	result = syntax.isTagSequence("<name:>");
	assertTrue("named group", result);

	result = syntax.isTagSequence("<dummy:>");
	assertTrue("not existing tag", !result);

	result = syntax.isTagSequence("<>");
	assertTrue("none tag", !result);
    }

    @Test
    public void isPhonemeStructureName() {

	String string1 = "$NAME";
	String string2 = "${NAME}";
	String string3 = "#NAME";

	assertTrue(syntax.isPhonemeStructureName(string1));
	assertTrue(syntax.isPhonemeStructureName(string2));
	assertTrue(!syntax.isPhonemeStructureName(string3));

    }

    @Test 
    public void replaceQuantifiers() throws Exception {

	String rule1 = "#Z($S<compare:V1==a>|<add:a>$S)+";
	String rule2 = "#Z($S<compare:V1==a>|($S<compare:V1==e><add:a>|<add:a>$S))+";
	String sequence1 = "z-at";
	String sequence2 = "z-at-at";

	String expected1 = "#Z($S<compare:V1==a>|<add:a>$S)";
	String expected2 = "#Z($S<compare:V1==a>|<add:a>$S)($S<compare:V1==a>|<add:a>$S)";

	String expected3 = "#Z($S<compare:V1==a>|($S<compare:V1==e><add:a>|<add:a>$S))";
	String expected4 = "#Z($S<compare:V1==a>|($S<compare:V1==e><add:a>|<add:a>$S))($S<compare:V1==a>|($S<compare:V1==e><add:a>|<add:a>$S))";

	language.addPhonemeStructure(new PhonemeStructure("S", "[aeiou]<name:V1>t", language));
	language.addConstant("Z", "[xyz]");

	assertEquals(expected1, syntax.replaceQuantifiers(rule1, sequence1));
	assertEquals(expected2, syntax.replaceQuantifiers(rule1, sequence2));

	assertEquals(expected3, syntax.replaceQuantifiers(rule2, sequence1));
	assertEquals(expected4, syntax.replaceQuantifiers(rule2, sequence2));
    }

    @Test
    public void getTrimRuleRegexWithOr() {

	String rule = "(a|b|c)";

	String regex = syntax.getTrimRuleRegexWithOr(rule.length());

	Pattern p = Pattern.compile(regex);
	Matcher m = p.matcher(rule);
	m.find();

	assertEquals("group 1", "a", m.group(1));
	assertEquals("group 2", "b", m.group(2));
	assertEquals("group 3", "c", m.group(3));
    }

    @Test
    public void trimRule() throws UnknownPhonemeStructureException,
    RuleViolationException, UnknownConstantException, SyntaxException {

	String rule1 = "#Z($S<compare:V1==a>|<add:a>$S)++";
	String rule2 = "#Z($S<compare:V1==a>|($S<compare:V1==e><add:a>|<add:a>$S))++";
	String sequence1 = "z-at-at";
	String sequence2 = "z-et";

	String expected1 = "#Z$S$S";
	String expected2 = "#Z<add:a>$S";

	language.addPhonemeStructure(new PhonemeStructure("S",
		"[aeiou]<name:V1>t", language));
	language.addConstant("Z", "[xyz]");

//	String actual1 = "";
//	String actual2 = "";

	String actual1 = syntax.trimRule(rule1, sequence1);
	String actual2 = syntax.trimRule(rule1, sequence2);

	assertEquals(expected1, actual1);
	assertEquals(expected2, actual2);
	
	actual1 = syntax.trimRule(rule2, sequence1);
	actual2 = syntax.trimRule(rule2, sequence2);

	assertEquals(expected1, actual1);
	assertEquals(expected2, actual2);
    }

    @Test
    public void parseAddTag() throws SyntaxException, RuleViolationException,
    RuleDoesNotApplyException {

	PhonemeStructure phonemeStructure = new PhonemeStructure("XnameX",
		STRUCTURE, language);
	String changeTag = "<add:ADD>";

	String sequence1 = "aaa";
	String expected1 = "aaaADD";
	String actual1 = syntax
	.parseTag(sequence1, phonemeStructure, changeTag);
	assertEquals(expected1, actual1);

	String sequence2 = "";
	String expected2 = "ADD";
	String actual2 = syntax
	.parseTag(sequence2, phonemeStructure, changeTag);
	assertEquals(expected2, actual2);
    }

    @Test
    public void parseDeleteTag() throws SyntaxException,
    RuleViolationException, RuleDoesNotApplyException {

	PhonemeStructure phonemeStructure = new PhonemeStructure("XnameX",
		STRUCTURE, language);
	String changeTag = "<delete>";

	String sequence1 = "aaa";
	String expected1 = "";
	String actual1 = syntax
	.parseTag(sequence1, phonemeStructure, changeTag);
	assertEquals(expected1, actual1);
    }

    @Test
    public void parseChangeTag() throws SyntaxException,
    RuleViolationException, RuleDoesNotApplyException {

	PhonemeStructure phonemeStructure = new PhonemeStructure("XnameX",
		STRUCTURE, language);
	String changeTag = "<change:FIRST=b;V1=(a:x,b:y,c:c)>?";

	String sequence1 = "aaa";
	String expected1 = "bxa";
	String actual1 = syntax
	.parseTag(sequence1, phonemeStructure, changeTag);
	assertEquals(expected1, actual1);

	String sequence2 = "aba";
	String expected2 = "bya";
	String actual2 = syntax
	.parseTag(sequence2, phonemeStructure, changeTag);
	assertEquals(expected2, actual2);

	String sequence3 = "aca";
	String expected3 = "bca";
	String actual3 = syntax
	.parseTag(sequence3, phonemeStructure, changeTag);
	assertEquals(expected3, actual3);
    }
}
