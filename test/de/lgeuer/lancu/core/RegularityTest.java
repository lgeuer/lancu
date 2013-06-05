package de.lgeuer.lancu.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.lgeuer.lancu.core.entity.AbstractRule;
import de.lgeuer.lancu.core.entity.DefaultLanguage;
import de.lgeuer.lancu.core.entity.InflectionState;
import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.core.entity.Regularity;
import de.lgeuer.lancu.core.entity.Word;
import de.lgeuer.lancu.core.entity.WordClass;
import de.lgeuer.lancu.core.entity.WordState;
import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;
import de.lgeuer.lancu.util.id.UnknownIdException;

public class RegularityTest {

    //private DefaultRuleSyntax syntax;
    private Language language;
    private InflectionState state;
    private AbstractRule rule1;
    private AbstractRule rule2;
    private AbstractRule rule3;
    private Regularity regular;

    @Before public void setUp() 
	throws UnknownIdException,
	       ItemAlreadyExistsException,
	       SyntaxException, UnknownPhonemeStructureException {
	
	language = new DefaultLanguage("test");
	//syntax = new DefaultRuleSyntax(language);
	//language.setRuleSyntax(syntax);
	language.setSyllable("a<name:V1>[as]<name:L2>?");
	state = new InflectionState("test state",language);
	regular = state.getRegularity(0);

	int id1 = regular.addRule("$S<add:i>");
	int id2 = regular.addRule("$S<add:ii>");
	int id3 = regular.addRule("$S<change:L2=(s:t,*:r)>");

	rule1 = regular.getRule(id1);
	rule2 = regular.getRule(id2);
	rule3 = regular.getRule(id3);
    }


    @After public void tearDown() {
	
	language = null;
	//syntax = null;
	state = null;
	rule1 = null;
	rule2 = null;
	rule3 = null;
    }


    @Test public void naturalOrder() {


	AbstractRule[] expected = {rule1,rule2,rule3};


	Collection<AbstractRule> rules = regular.getRules();
	Iterator<AbstractRule> it = rules.iterator();


	Assert.assertEquals("rule Set size",3,rules.size());

	for (int i = 0;i < 3;i++) {

	    Assert.assertEquals("index " + i,expected[i].getId(),it.next().getId());
	}
    }


    @Test public void moveUp() throws UnknownIdException {


	AbstractRule[] expected = {rule3,rule1,rule2};

	regular.moveUp(rule3);
	regular.moveUp(rule3);


	Collection<AbstractRule> rules = regular.getRules();
	Iterator<AbstractRule> it = rules.iterator();

	for (int i = 0;i < 3;i++) {

	    Assert.assertEquals("index " + i,expected[i].getId(),it.next().getId());
	}
    }


    @Test public void moveDown() throws UnknownIdException {


	AbstractRule[] expected = {rule1,rule2,rule3};

	regular.moveDown(rule3);
	regular.moveDown(rule3);


	Collection<AbstractRule> rules = regular.getRules();
	Iterator<AbstractRule> it = rules.iterator();

	for (int i = 0;i < 3;i++) {

	    Assert.assertEquals("index " + i,expected[i].getId(),it.next().getId());
	}
    }

    @Test public void applySequence()  throws Exception {

	//    public String applySequence(String aSequence,WordState wordState) throws NoMatchingRuleException,
	//							     UnknownConstantException,
	//							     UnknownPhonemeStructureException {

	/*
	language = new LanguageMock();
	syntax = new DefaultRuleSyntax(language);
	language.setRuleSyntax(syntax);
	state = new InflectionState("test state",language);
	regular = state.getRegularity(0);

	int id1 = regular.addRule(".?");
	int id2 = regular.addRule("..?");
	int id3 = regular.addRule("...?");

	rule1 = regular.getRule(id1);
	rule2 = regular.getRule(id2);
	rule3 = regular.getRule(id3);
	*/
	List<Regularity> list = new ArrayList<Regularity>();
	WordClass wc = new WordClass("wordClass",language);
	Word w = new Word("a","t","d",wc,language);
	WordState ws  = new WordState(w,list,language);
	list.add(regular);


	Assert.assertEquals("ai",regular.applySequence("a",ws));
	Assert.assertEquals("aaii",regular.applySequence("aa",ws));
	Assert.assertEquals("at",regular.applySequence("as",ws));
    }
}

