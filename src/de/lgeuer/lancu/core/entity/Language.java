/**
 * File: Language.java
 * Author: Lars Geuer
 * Date: 14.3.2007
 */

package de.lgeuer.lancu.core.entity;


import java.io.Serializable;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.PatternSyntaxException;

import de.lgeuer.lancu.core.InvalidPhonemeSequenceException;
import de.lgeuer.lancu.core.ItemAlreadyExistsException;
import de.lgeuer.lancu.core.UnknownConstantException;
import de.lgeuer.lancu.core.UnknownMorphemeReferenceException;
import de.lgeuer.lancu.core.UnknownPhonemeStructureException;
import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;
import de.lgeuer.lancu.core.syntax.lancuregex.WordSyntaxException;
import de.lgeuer.lancu.core.syntaxold.RuleSyntax;
import de.lgeuer.lancu.core.syntaxold.WordSyntax;
import de.lgeuer.lancu.util.Transaction;
import de.lgeuer.lancu.util.id.Identifiable;
import de.lgeuer.lancu.util.id.UnknownIdException;



public abstract class  Language extends Observable implements Serializable,Identifiable,Transaction,Observer {

    private static final long serialVersionUID = 574169210239812862L;

    public abstract String getSyllableDelimiter();

    public abstract RuleSyntax getRuleSyntax();

    public abstract WordSyntax getWordSyntax();

    //public void addPhonemeStructure(String name,String structure);

    //public void removePhonemeStructure(PhonemeStructure structure);

    public abstract void setName(String aName);

    public abstract String getName();

    public abstract void setSyllable(String syllable) throws SyntaxException;

    public abstract PhonemeStructure getSyllable();

    public abstract PhonemeStructure getPhonemeStructure(String name) throws UnknownPhonemeStructureException;

    public abstract void addConstant(String name, String regex) throws PatternSyntaxException;
 
    public abstract void removeConstant(String name);

    public abstract String getConstant(String name) throws UnknownConstantException;

    public abstract void addPhoneme(String phoneme) throws ItemAlreadyExistsException;

    public abstract void removePhoneme(Phoneme phoneme);
    public abstract Collection<Phoneme> getPhonemes();

    public abstract boolean phonemeSequenceIsCorrect(String sequence);

    public abstract void addMorpheme(String morpheme,String description) throws InvalidPhonemeSequenceException,
								       UnknownMorphemeReferenceException,
								       ItemAlreadyExistsException;

    public abstract void removeMorpheme(Morpheme morpheme);

    public abstract Collection<Morpheme> getMorphemes();

    public abstract Morpheme getMorpheme(int id) throws UnknownIdException;

    public abstract int addWord(String aWord,String aTranslation,String aDescription,WordClass wordClass)
	throws WordSyntaxException, ItemAlreadyExistsException;

    public abstract void removeWord(Word word);

    public abstract Word getWord(int id) throws UnknownIdException;

    public abstract Collection<Word> getWords();

    public abstract int addWordClass(String aWord) throws ItemAlreadyExistsException ;

    public abstract void removeWordClass(WordClass wordClass);

    public abstract WordClass getWordClass(int id) throws UnknownIdException;

    public abstract Collection<WordClass> getWordClasses();

    public abstract void addPhonemeChangingRule(String rule) throws ItemAlreadyExistsException,SyntaxException, UnknownPhonemeStructureException;
    
    public abstract void removePhonemeChangingRule(AbstractRule rule);

    public abstract Collection<AbstractRule> getPhonemeChangingRules();    
}