/*
 * File: Word.java
 * Author: Lars Geuer
 * Date: 10.4.2007
 */

package de.lgeuer.lancu.core.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.lgeuer.lancu.core.LanguageViolationException;
import de.lgeuer.lancu.core.syntax.lancuregex.WordSyntaxException;
import de.lgeuer.lancu.core.syntaxold.WordSyntax;
import de.lgeuer.lancu.util.id.IdFactory;
import de.lgeuer.lancu.util.id.UnknownIdException;



public class Word extends Observable implements LanguageItem,Observer,Comparable<Word> {

    private static final long serialVersionUID = 3297308641040639944L;

    private int id = IdFactory.VOID;

    private String word;
    private String oldWord;
    private String description;
    private String oldDescription;
    private String translation;
    private String oldTranslation;
    private WordClass wordClass;
    private WordClass oldWordClass;
    private List<Morpheme> morphemes; //morphemes in word

    private List<WordState> wordStates = Collections.synchronizedList(new ArrayList<WordState>());
    private Language language;


    public Word(String aWord,String aTranslation,String aDescription,
	    WordClass aWordClass,Language aLanguage) {

	word = aWord;
	oldWord = aWord;

	translation = aTranslation;
	oldTranslation = aTranslation;

	description = aDescription;
	oldDescription = aDescription;

	wordClass = aWordClass;
	oldWordClass = aWordClass;

	if (wordClass != null) {

	    wordClass.addObserver(this);
	}

	language = aLanguage;


	try {

	    morphemes = language.getWordSyntax().getMorphemes(word);

	    for (Morpheme morpheme:morphemes) {

		morpheme.addObserver(this);
	    }

	    wordStates = createWordStates();
	}
	catch(LanguageViolationException ex) {
	    //do nothing - not commited -> no error reports
	} catch (UnknownIdException e) {
	    //do nothing - not commited -> no error reports
	}
    }


    private List<WordState> createWordStates() throws LanguageViolationException, UnknownIdException {


	List<WordState> states = new ArrayList<WordState>();

	if (wordClass == null) {

	    return states;
	}

	List<Inflection> inflections = new ArrayList<Inflection>(wordClass.getInflections());
	List<List<Regularity>> regularityList = new ArrayList<List<Regularity>>();

	if (inflections.size() < 1) {

	    return states;
	}

	//enter states of first inflection
	for (InflectionState state:inflections.get(0).getInflectionStates()) {

	    List<Regularity> regularities = new ArrayList<Regularity>();

	    //first regularity in list is "regular"
	    regularities.add(state.getRegularities().iterator().next());

	    regularityList.add(regularities);
	}
	//enter rest of the states
	for (int i = 1;i < inflections.size();i++) {

	    //get all existing word states
	    List<List<Regularity>> tmpRegularityList = new ArrayList<List<Regularity>>(regularityList);
	    regularityList.clear();

	    //raise the word states to a power of [no of inflection states]
	    for (InflectionState state:inflections.get(i).getInflectionStates()) {

		for (List<Regularity> tmpRegularities:tmpRegularityList) {

		    List<Regularity> regularities = new ArrayList<Regularity>(tmpRegularities);
		    regularities.add(state.getRegularities().iterator().next());

		    regularityList.add(regularities);
		}
	    }
	}

	//create word states
	for (List<Regularity> regularities:regularityList) {

	    states.add(new WordState(this,regularities,language));
	}

	return states;
    }


    @Override
    public void rollback() {

	word = oldWord;
	description = oldDescription;
	translation = oldTranslation;

	if (wordClass != oldWordClass) {

	    wordClass.deleteObserver(this);
	    wordClass = oldWordClass;

	    if (wordClass != null) {

		wordClass.addObserver(this);
	    }
	}

	setChanged();
	notifyObservers();
    }


    @Override
    public void commit() throws UnknownIdException,
    LanguageViolationException {

	oldWord = word;
	oldDescription = description;
	oldTranslation = translation;
	oldWordClass = wordClass;

	for (Morpheme morpheme:morphemes) {

	    morpheme.deleteObserver(this);
	}

	morphemes = language.getWordSyntax().getMorphemes(word);

	for (Morpheme morpheme:morphemes) {

	    morpheme.addObserver(this);
	}

	wordStates = createWordStates();
    }


    public void update() throws UnknownIdException,
    LanguageViolationException {

	for (Morpheme morpheme:morphemes) {

	    morpheme.deleteObserver(this);
	}

	morphemes = language.getWordSyntax().getMorphemes(word);

	for (Morpheme morpheme:morphemes) {

	    morpheme.addObserver(this);
	}

	wordStates = createWordStates();
	setChanged();
	notifyObservers();
    }


    @Override
    public void update(Observable observable,Object o) {

	if (observable instanceof Morpheme) {

	    for (Morpheme morpheme:morphemes) {

		morpheme.deleteObserver(this);
	    }

	    try {

		morphemes = language.getWordSyntax().getMorphemes(word);
	    }
	    catch(UnknownIdException ex) {

		throw new RuntimeException(ex);
	    }


	    for (Morpheme morpheme:morphemes) {

		morpheme.addObserver(this);
	    }
	}

	if (observable instanceof WordClass) { 

	    try {

		wordStates = createWordStates();
	    }
	    catch(IllegalStateException ex) {

		//not commited -> no error
		wordStates.clear();		
	    }
	    catch(Exception ex) {

		if (ex instanceof RuntimeException) {

		    throw (RuntimeException)ex;
		}

		//not commited -> no error
		wordStates.clear();
	    }
	}
	else {

	    //added by lars
	    for (WordState state:wordStates) {

		try {

		    state.update();
		}
		catch(Exception ex) {

		    //not commited -> no error
		}
	    }
	}

	setChanged();
	notifyObservers();
    }


    public void setId(int anId) {

	id = anId;
    }


    @Override
    public int getId() {

	return id;
    }


    public boolean check() { 

	WordSyntax wordSyntax = language.getWordSyntax();
	List<Morpheme> tmpMorphemes = null;


	if (wordClass == null ||
		!language.getWordClasses().contains(wordClass)) {

	    return false;
	}

	try {

	    tmpMorphemes = wordSyntax.getMorphemes(word);
	    createWordStates();
	}
	catch (Exception ex) {

	    return false;
	}


	for (WordState wordState:wordStates) {

	    if (!wordState.check()) {

		return false;
	    }
	}

	return language.phonemeSequenceIsCorrect(wordSyntax.parseWord(getRoot()
		,tmpMorphemes));	
    }


    public void setRoot(String aWord) throws  WordSyntaxException {

	word = aWord;

	try {

	    morphemes = null;
	    morphemes = language.getWordSyntax().getMorphemes(word);

	    for (Morpheme morpheme:morphemes) {

		morpheme.addObserver(this);
	    }

	    wordStates = createWordStates();
	}
	catch(UnknownIdException ex) {

	    //do nothing - not commited -> no error reports
	}
	catch(IllegalStateException ex) {

	    //do nothing - not commited -> no error reports
	} catch (LanguageViolationException e) {

	    //do nothing - not commited -> no error reports
	}

	setChanged();
	notifyObservers();
    }


    /**
     * With references
     */
    public String getRoot() {

	return word;
    }


    /**
     * Without references
     */
    public String getParsedRoot() {

	return language.getWordSyntax().parseWord(word,morphemes);
    }


    public void setTranslation(String aTranslation) {

	translation = aTranslation;
	setChanged();
	notifyObservers();
    }


    public String getTranslation() {

	return translation;
    }


    public void setDescription(String aDescription) {

	description = aDescription;
	setChanged();
	notifyObservers();
    }


    public String getDescription() {

	return description;
    }


    public void setWordClass(WordClass aWordClass) {

	if (aWordClass == null) {

	    throw new NullPointerException("Word class was null.");
	}

	if (wordClass != null) {

	    wordClass.deleteObserver(this);
	}

	wordClass = aWordClass;
	wordClass.addObserver(this);

	setChanged();
	notifyObservers();
    }


    public WordClass getWordClass() {

	return wordClass;
    }

    public List<WordState> getWordStates() {

	return wordStates;
    }

    @Override
    public boolean equals(Object o) {

	if (!(o instanceof Word)) {

	    return false;
	}

	Word otherWord = (Word)o;

	return this.language == otherWord.language &&
	this.getRoot().equals(otherWord.getRoot()) &&
	this.getTranslation().equals(otherWord.getTranslation()) &&
	this.getWordClass().equals(otherWord.getWordClass());
    }


    @Override
    public int compareTo(Word w) {

	return getRoot().compareTo(w.getRoot());
    }


    @Override
    public String toString() {


	try {

	    return language.getWordSyntax().parseWord(getRoot(),morphemes);
	}
	catch(NullPointerException ex) {

	    //no morphemes set
	    return getRoot();
	}
    }
}
