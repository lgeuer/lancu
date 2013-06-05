/**
 * File: LanguageMock.java
 * Author: Lars Geuer
 * Date: 14.3.2007
 */

package de.lgeuer.lancu.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.lgeuer.lancu.core.entity.AbstractRule;
import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.core.entity.Morpheme;
import de.lgeuer.lancu.core.entity.Phoneme;
import de.lgeuer.lancu.core.entity.PhonemeStructure;
import de.lgeuer.lancu.core.entity.Word;
import de.lgeuer.lancu.core.entity.WordClass;
import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;
import de.lgeuer.lancu.core.syntaxold.DefaultRuleSyntax;
import de.lgeuer.lancu.core.syntaxold.DefaultWordSyntax;
import de.lgeuer.lancu.core.syntaxold.RuleSyntax;
import de.lgeuer.lancu.core.syntaxold.WordSyntax;
import de.lgeuer.lancu.util.id.IdFactory;


public class LanguageMock extends Language {

	private static final long serialVersionUID = 959240461333512799L;

	private int id = IdFactory.VOID;

    private String phonemeStructureDelimiter = "-";
    private RuleSyntax ruleSyntax = new DefaultRuleSyntax(this);
    private WordSyntax wordSyntax = new DefaultWordSyntax(this);
    private String[] constant = {"NAME","[values]"};
    private PhonemeStructure structure;
    private Map<Integer,Morpheme> morphemes = new HashMap<Integer,Morpheme>();
    private Map<String,Phoneme> phonemes = new HashMap<String,Phoneme>();

    public LanguageMock() {

	try {
	    structure  = new PhonemeStructure("NAME","values",this);
	}
	catch(SyntaxException ex) {
	    
	    //should not happen
	    throw new IllegalArgumentException(ex);
	}
    }

    public void setId(int anId) {

	id = anId;
    }


    @Override
    public int getId() {

	return id;
    }


    public void setSyllableDelimiter(String aDelimiter) {

	phonemeStructureDelimiter = aDelimiter;
    }

    @Override
    public String getSyllableDelimiter() {

	return phonemeStructureDelimiter;
    }


    public void  setRuleSyntax(RuleSyntax syntax) {

	ruleSyntax = syntax;
    }

    public void  setWordSyntax(WordSyntax syntax) {

	wordSyntax = syntax;
    }


    @Override
    public RuleSyntax getRuleSyntax() {

	return ruleSyntax;
    }

    @Override
    public WordSyntax getWordSyntax() {

	return wordSyntax;
    }


    @Override
    public void setName(String name) {

	notImplemented();
    }


    @Override
    public String getName() {

	notImplemented();

	return null;
    }


    public void addPhonemeStructure(PhonemeStructure aStructure) {

	structure = aStructure;
    }

    public void addPhonemeStructure(String aName,String aStructure)
	throws SyntaxException {
	
	structure = new PhonemeStructure(aName,aStructure,this);
    }

    public void removePhonemeStructure(String name) {

	structure = null;
    }

    public void removePhonemeStructure(PhonemeStructure aStructure) {
    }

    @Override
    public PhonemeStructure getPhonemeStructure(String name)throws UnknownPhonemeStructureException {

	if (name.equals(structure.getName())) {
	
	    return structure;
	}

	throw new UnknownPhonemeStructureException(name);
    }

    @Override
    public void addConstant(String name,String regex) throws PatternSyntaxException {

	Pattern.compile(regex); //to get exception if regex is wrong

	constant[0] = name;
	constant[1] = regex;
    }


    @Override
    public void removeConstant(String name) {

    }


    @Override
    public String getConstant(String name) throws UnknownConstantException {
	
	if (name.equals(constant[0])) {
	
	    return constant[1];
	}
	throw new UnknownConstantException(name);
    }

    public void addPhoneme(Phoneme phoneme) {
	
	phonemes.put(phoneme.getPhonemeAsString(),phoneme);
    }


    @Override
    public void addPhoneme(String aPhoneme) {
	
	Phoneme phoneme = new Phoneme(aPhoneme,this);
	phonemes.put(phoneme.getPhonemeAsString(),phoneme);
    }


    @Override
    public void removePhoneme(Phoneme phoneme) {

	notImplemented();
    }


    @Override
    public Collection<Phoneme> getPhonemes() {

	return phonemes.values();
    }


    @Override
    public boolean phonemeSequenceIsCorrect(String sequence) {
	
	return true;
    }

     public void addMorpheme(Morpheme morpheme) {
	
	 morphemes.put(morpheme.getId(),morpheme);
    }

    @Override
    public void addMorpheme(String aMorpheme, String aDescription) throws InvalidPhonemeSequenceException,
									  UnknownMorphemeReferenceException{

	Morpheme morpheme = new Morpheme(aMorpheme,aDescription,this);
	 morphemes.put(morpheme.getId(),morpheme);
    }

    @Override
    public void removeMorpheme(Morpheme morpheme) {
	
	notImplemented();
    }

    @Override
    public Collection<Morpheme> getMorphemes() {

	return morphemes.values();
    }

    @Override
    public Morpheme getMorpheme(int morphemeId) {

	//TODO: should throw exception if is null
	return morphemes.get(morphemeId);
    }

    public void addWord(Word word) {
	
	notImplemented();	
    }

    @Override
    public int addWord(String aWord,String aTranslation,String aDescription,WordClass wordClass) {

	notImplemented();

	return 0;
    }


    @Override
    public void removeWord(Word word) {

	notImplemented();
    }


    @Override
    public Word getWord(int wordId) {

	notImplemented();
	
	return null;
    }


    @Override
    public Collection<Word> getWords() {

	notImplemented();

	return null;
    }

    @Override
    public int addWordClass(String aClass) {

	notImplemented();

	return 0;
    }


    @Override
    public void removeWordClass(WordClass wordClass) {

	notImplemented();
    }


    @Override
    public Collection<WordClass> getWordClasses() {

	notImplemented();

	return null;
    }


    @Override
    public WordClass getWordClass(int wordClassId) {

	notImplemented();

	return null;
    }


    @Override
    public void addPhonemeChangingRule(String ruleString) {

	notImplemented();
    }


    @Override
    public void removePhonemeChangingRule(AbstractRule aRule) {

	notImplemented();
    }

    
    @Override
    public Collection<AbstractRule> getPhonemeChangingRules() {

	notImplemented();

	return null;
    }


    @Override
    public void setSyllable(String syllable) {

	notImplemented();
    }


    @Override
    public PhonemeStructure getSyllable() {

	notImplemented();

	return null;
    }


    @Override
    public void commit() {

	notImplemented();
    }


    @Override
    public void rollback() {

	notImplemented();
    }

    
    @Override
    public void update(Observable observable,Object o) {
	
	notImplemented();
    }



    private void notImplemented() {

	throw new UnsupportedOperationException("Method not yet implemented");
    }
}