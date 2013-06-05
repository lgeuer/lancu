/*
 * File: WordState.java
 * Author: Lars Geuer
 * Date: 26.4.2007
 */

package de.lgeuer.lancu.core.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import de.lgeuer.lancu.core.LanguageViolationException;
import de.lgeuer.lancu.util.id.UnknownIdException;



public class WordState implements Serializable,Comparable<WordState> {

    private static final long serialVersionUID = -5941951109350903131L;
    private String word;
    private Word  owner;
    private List<Regularity> regularities;
    private List<Morpheme> morphemes; //morphemes in word
    private Language language;

    private Vector<AbstractRule> parsedRules = new Vector<AbstractRule>();


    public WordState(Word anOwner,List<Regularity> newRegularities,Language aLanguage) throws LanguageViolationException, UnknownIdException {

	regularities = newRegularities;
	owner = anOwner;
	language = aLanguage;

	update();
    }


    public void setRegularities(List<Regularity> newRegularities) {

	regularities = newRegularities;
    }


    public List<Regularity> getRegularities() {

	return regularities;
    }


    public void update() throws LanguageViolationException, UnknownIdException {
	String tmpWord = owner.toString();

	for (Regularity regularity:regularities) {
	    
	    parsedRules.clear();
	    tmpWord = regularity.applySequence(tmpWord,this);
	    System.out.println("update("  + owner.getRoot() + "): " 
			       + tmpWord);
	}

	word = tmpWord;
	
	morphemes = language.getWordSyntax().getMorphemes(word);
    }


    public boolean check() {


	String delimiter = language.getSyllableDelimiter();
	PhonemeStructure syllable = language.getSyllable();

	for (String wordPart:getParsedWord().split(delimiter)) {

	    if (!syllable.matches(wordPart)) {

		return false;
	    }
	}



	try {
	    
	    language.getWordSyntax().getMorphemes(word);
	}
	catch (UnknownIdException ex) {

	    return false;
	}
	
	return true;
    }
    
    
    /**
     * With references
     */
    public String getWord() {

	return word;
    }


    /**
     * Without references
     */
    public String getParsedWord() {

	return language.getWordSyntax().parseWord(word,morphemes);
    }


    public void addAppliedRule(AbstractRule rule) {

	parsedRules.add(rule);
    }
    
    public boolean hasBeenApplied(AbstractRule rule) {
	
	System.out.println("hasBeenApplied(" + rule + "): " 
			   + parsedRules.contains(rule));
	return parsedRules.contains(rule);
    }


    @Override
    public int compareTo(WordState w) {

	return getWord().compareTo(w.getWord());
    }


    @Override
    public String toString() {
	
	return word.replaceAll(language.getSyllableDelimiter(),"");
    }
}