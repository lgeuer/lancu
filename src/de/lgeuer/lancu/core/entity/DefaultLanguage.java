/*
 * File: DefaultLanguage.java
 * Author: Lars Geuer
 * Date: 4.4.2007
 */

package de.lgeuer.lancu.core.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.lgeuer.lancu.Environment;
import de.lgeuer.lancu.core.InvalidPhonemeSequenceException;
import de.lgeuer.lancu.core.ItemAlreadyExistsException;
import de.lgeuer.lancu.core.UnknownConstantException;
import de.lgeuer.lancu.core.UnknownMorphemeReferenceException;
import de.lgeuer.lancu.core.UnknownPhonemeStructureException;
import de.lgeuer.lancu.core.ViolationException;
import de.lgeuer.lancu.core.ViolationExceptionItem;
import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;
import de.lgeuer.lancu.core.syntaxold.DefaultRuleSyntax;
import de.lgeuer.lancu.core.syntaxold.DefaultWordSyntax;
import de.lgeuer.lancu.core.syntaxold.RuleSyntax;
import de.lgeuer.lancu.core.syntaxold.WordSyntax;
import de.lgeuer.lancu.util.id.IdFactory;
import de.lgeuer.lancu.util.id.UnknownIdException;


public class DefaultLanguage extends Language {

    private static final long serialVersionUID = -9157136132027243269L;

    private int id = IdFactory.VOID;

    private String oldName;

    private String name;

    private String syllableDelimiter = "-";

    private RuleSyntax ruleSyntax = new DefaultRuleSyntax(this);

    private WordSyntax wordSyntax = new DefaultWordSyntax(this);

    private PhonemeStructure syllable;

    private Map<String, String> oldConstants = Collections
	    .synchronizedMap(new TreeMap<String, String>());

    private Map<String, String> constants = Collections
	    .synchronizedMap(new TreeMap<String, String>());

    private Map<String, Phoneme> oldPhonemes = Collections
	    .synchronizedMap(new TreeMap<String, Phoneme>());

    private Map<String, Phoneme> phonemes = Collections
	    .synchronizedMap(new TreeMap<String, Phoneme>());

    /*
         * private Map<String,PhonemeStructure> oldPhonemeStructures =
         * Collections.synchronizedMap(new TreeMap<String,PhonemeStructure>());
         * private Map<String,PhonemeStructure> phonemeStructures =
         * Collections.synchronizedMap(new TreeMap<String,PhonemeStructure>());
         */

    private Map<Integer, Morpheme> oldMorphemes = Collections
	    .synchronizedMap(new TreeMap<Integer, Morpheme>());

    private Map<Integer, Morpheme> morphemes = Collections
	    .synchronizedMap(new TreeMap<Integer, Morpheme>());

    private Map<Integer, Word> oldWords = Collections
	    .synchronizedMap(new TreeMap<Integer, Word>());

    private Map<Integer, Word> words = Collections
	    .synchronizedMap(new TreeMap<Integer, Word>());

    private Map<Integer, WordClass> oldWordClasses = Collections
	    .synchronizedMap(new TreeMap<Integer, WordClass>());

    private Map<Integer, WordClass> wordClasses = Collections
	    .synchronizedMap(new TreeMap<Integer, WordClass>());

    private Map<Integer, AbstractRule> oldPhonemeChangingRules = Collections
	    .synchronizedMap(new TreeMap<Integer, AbstractRule>());

    private Map<Integer, AbstractRule> phonemeChangingRules = Collections
	    .synchronizedMap(new TreeMap<Integer, AbstractRule>());

    // only used to reset flag
    private List<AbstractRule> inflectionStateRules = Collections
	    .synchronizedList(new ArrayList<AbstractRule>());

    private List<AbstractRule> oldInflectionStateRules = Collections
	    .synchronizedList(new ArrayList<AbstractRule>());

    private static IdFactory languageIdFactory = new IdFactory();

    // private IdFactory phonemeStructureIdFactory = new IdFactory();
    private IdFactory phonemeIdFactory = new IdFactory();

    private IdFactory morphemeIdFactory = new IdFactory();

    private IdFactory wordIdFactory = new IdFactory();

    private IdFactory wordClassIdFactory = new IdFactory();

    private IdFactory phonemeChangingRuleIdFactory = new IdFactory();

    public DefaultLanguage() {

	try {

	    syllable = new PhonemeStructure("S", ".*", this);
	} catch (SyntaxException ex) {

	    // should not happen
	    throw new IllegalArgumentException(ex);
	}

	setId(languageIdFactory.getId());
    }

    public DefaultLanguage(String aName) {

	this();
	name = aName;
	oldName = aName;
    }

    @Override
    public void rollback() {

	name = oldName;

	if (!constants.equals(oldConstants)) {

	    synchronized (constants) {

		constants.clear();
		constants.putAll(oldConstants);
	    }
	}

	if (!phonemes.equals(oldPhonemes)) {

	    synchronized (phonemes) {

		for (Phoneme phoneme : phonemes.values()) {

		    phoneme.deleteObserver(this);
		}

		phonemes.clear();
		phonemes.putAll(oldPhonemes);

		for (Phoneme phoneme : phonemes.values()) {

		    phoneme.addObserver(this);
		}
	    }

	    for (Phoneme phoneme : phonemes.values()) {

		phoneme.rollback();
	    }
	}

	if (!morphemes.equals(oldMorphemes)) {

	    synchronized (morphemes) {

		for (Morpheme morpheme : morphemes.values()) {

		    morpheme.deleteObserver(this);
		}

		morphemes.clear();
		morphemes.putAll(oldMorphemes);

		for (Morpheme morpheme : morphemes.values()) {

		    morpheme.deleteObserver(this);
		}
	    }

	    for (Morpheme morpheme : morphemes.values()) {

		morpheme.rollback();
	    }
	}

	if (!words.equals(oldWords)) {

	    synchronized (words) {

		for (Word word : words.values()) {

		    word.deleteObserver(this);
		}

		words.clear();
		words.putAll(oldWords);

		for (Word word : words.values()) {

		    word.addObserver(this);
		}
	    }

	    for (Word word : words.values()) {

		word.rollback();
	    }
	}

	if (!wordClasses.equals(oldWordClasses)) {

	    synchronized (wordClasses) {

		for (WordClass wordClass : wordClasses.values()) {

		    wordClass.deleteObserver(this);
		}

		wordClasses.clear();
		wordClasses.putAll(oldWordClasses);

		for (WordClass wordClass : wordClasses.values()) {

		    wordClass.addObserver(this);
		}
	    }

	    for (WordClass wordClass : wordClasses.values()) {

		wordClass.rollback();
	    }
	}

	if (!phonemeChangingRules.equals(oldPhonemeChangingRules)) {

	    synchronized (phonemeChangingRules) {

		for (AbstractRule rule : phonemeChangingRules.values()) {

		    rule.deleteObserver(this);
		}

		phonemeChangingRules.clear();
		phonemeChangingRules.putAll(oldPhonemeChangingRules);

		for (AbstractRule rule : phonemeChangingRules.values()) {

		    rule.addObserver(this);
		}
	    }

	    for (AbstractRule rule : phonemeChangingRules.values()) {

		rule.rollback();
	    }
	}

	if (!inflectionStateRules.equals(oldInflectionStateRules)) {

	    synchronized (inflectionStateRules) {

		for (AbstractRule rule : inflectionStateRules) {

		    rule.deleteObserver(this);
		}

		inflectionStateRules.clear();
		inflectionStateRules.addAll(oldInflectionStateRules);
		// rolled back by InflectionState.rollback() called through
		// WordClass.rollback()

		for (AbstractRule rule : inflectionStateRules) {

		    rule.addObserver(this);
		}
	    }
	}

	syllable.rollback();

	setChanged();
	notifyObservers();
    }

    @Override
    public void commit() throws ViolationException {

	// check before commit
	ViolationException violationEx = new ViolationException();

	for (Morpheme morpheme : morphemes.values()) {

	    if (!morpheme.check()) {

		String message = Environment.getEnvironment().getMessages()
			.morphemeViolation(morpheme);
		ViolationExceptionItem item = new ViolationExceptionItem(
			message, morpheme);
		violationEx.addItem(item);
	    }
	}

	for (Word word : words.values()) {

	    if (!word.check()) {

		String message = Environment.getEnvironment().getMessages()
			.wordViolation(word);
		ViolationExceptionItem item = new ViolationExceptionItem(
			message, word);
		violationEx.addItem(item);
	    }
	}

	if (violationEx.violationOccured()) {

	    throw violationEx;
	}

	// commit
	oldName = name;

	if (!constants.equals(oldConstants)) {

	    synchronized (oldConstants) {

		oldConstants.clear();
		oldConstants.putAll(constants);
	    }
	}

	if (!phonemes.equals(oldPhonemes)) {

	    synchronized (phonemes) {

		oldPhonemes.clear();
		oldPhonemes.putAll(phonemes);
	    }

	    for (Phoneme phoneme : phonemes.values()) {

		phoneme.commit();
	    }
	}

	if (!morphemes.equals(oldPhonemes)) {

	    synchronized (oldMorphemes) {

		oldMorphemes.clear();
		oldMorphemes.putAll(morphemes);
	    }

	    try {

		for (Morpheme morpheme : morphemes.values()) {

		    morpheme.commit();
		}
	    } catch (InvalidPhonemeSequenceException ex) {

		throw new RuntimeException(ex);
	    } catch (UnknownMorphemeReferenceException ex) {

		throw new RuntimeException(ex);
	    }
	}

	if (!words.equals(oldWords)) {

	    synchronized (oldWords) {

		oldWords.clear();
		oldWords.putAll(words);
	    }

	    try {

		for (Word word : words.values()) {

		    word.commit();
		}

	    } catch (Exception ex) {

		if (ex instanceof RuntimeException) {

		    throw (RuntimeException) ex;
		}

		throw new RuntimeException(ex);
	    }
	}

	if (!wordClasses.equals(oldWordClasses)) {

	    synchronized (oldWordClasses) {

		oldWordClasses.clear();
		oldWordClasses.putAll(wordClasses);
	    }

	    for (WordClass wordClass : wordClasses.values()) {

		wordClass.commit();
	    }
	}

	if (!phonemeChangingRules.equals(oldPhonemeChangingRules)) {

	    synchronized (oldPhonemeChangingRules) {

		oldPhonemeChangingRules.clear();
		oldPhonemeChangingRules.putAll(phonemeChangingRules);
	    }

	    for (AbstractRule rule : phonemeChangingRules.values()) {

		rule.commit();
	    }
	}

	if (!inflectionStateRules.equals(oldInflectionStateRules)) {

	    synchronized (oldInflectionStateRules) {

		oldInflectionStateRules.clear();
		oldInflectionStateRules.addAll(inflectionStateRules);
		// commited by InflectionState.commit() called through
		// WordClass.commit()
	    }
	}

	syllable.commit();
    }

    @Override
    public void update(Observable observable, Object o) {

	setChanged();
	notifyObservers(observable);
    }

    public void setId(int anId) {

	id = anId;
    }

    @Override
    public int getId() {

	return id;
    }

    @Override
    public String getName() {

	return name;
    }

    @Override
    public void setName(String aName) {

	name = aName;

	this.setChanged();
	this.notifyObservers();
    }

    @Override
    public String getSyllableDelimiter() {

	return syllableDelimiter;
    }

    @Override
    public RuleSyntax getRuleSyntax() {

	return ruleSyntax;
    }

    @Override
    public WordSyntax getWordSyntax() {

	return wordSyntax;
    }

    /*
         * public void addPhonemeStructure(String aName,String aStructure) {
         * 
         * if (words.contains(aWord)) {
         * 
         * throw new ItemAlreadyExistsException("Word already exists: " +
         * aWord); }
         * 
         * PhonemeStructure structure = new
         * PhonemeStructure(aName,aStructure,this);
         * structure.setId(phonemeStructureIdFactory.getId());
         * phonemeStructures.put(structure.getName(),structure); hasChanged =
         * true; }
         * 
         * 
         * public void removePhonemeStructure(PhonemeStructure structure) {
         * 
         * if (phonemeStructures.remove(structure.getName()) != null) {
         * 
         * structure.setId(IdFactory.VOID); hasChanged = true; } }
         */

    @Override
    public PhonemeStructure getPhonemeStructure(String phonemeStructureName)
	    throws UnknownPhonemeStructureException {

	if (phonemeStructureName.equals(syllable.getName())) {

	    return syllable;
	}

	/*
         * not implemented 
         * 
         * PhonemeStructure structure = phonemeStructures.get(name);
         * 
         * if(structure == null) {
         * 
         * throw new UnknownPhonemeStructureException(structure); }
         * 
         * return structure;
         */

	throw new UnknownPhonemeStructureException(phonemeStructureName);
    }

    /**
         * @throws IllegalArgumentException
         *                 if the structure is invalid
         */
    @Override
    public void setSyllable(String aStructure) throws SyntaxException {

	syllable.setStructure(aStructure);
	setChanged();
	notifyObservers();

    }

    @Override
    public PhonemeStructure getSyllable() {

	return syllable;
    }

    @Override
    public void addConstant(String constantName, String regex)
	    throws PatternSyntaxException {

	// throws PatternSyntaxException if syntax is invalid
	Pattern.compile(regex);

	constants.put(constantName, regex);
	setChanged();
	notifyObservers();
    }

    @Override
    public void removeConstant(String constantName) {

	if (constants.remove(constantName) != null) {

	    setChanged();
	    notifyObservers();
	}
    }

    @Override
    public String getConstant(String constantName)
	    throws UnknownConstantException {

	String constant = constants.get(name);

	if (constant == null) {

	    throw new UnknownConstantException(constant);
	}

	return constant;
    }

    @Override
    public void addPhoneme(String aPhoneme) throws ItemAlreadyExistsException {

	if (phonemes.containsKey(aPhoneme)) {

	    throw new ItemAlreadyExistsException("Phoneme already exists: "
		    + aPhoneme);
	}

	if (ruleSyntax.containsRuleSymbol(aPhoneme) || aPhoneme.length() != 1) {

	    throw new IllegalArgumentException("Illegal Phoneme: " + aPhoneme);
	}

	Phoneme phoneme = new Phoneme(aPhoneme, this);
	phoneme.setId(phonemeIdFactory.getId());
	phoneme.addObserver(this);

	phonemes.put(phoneme.getPhonemeAsString(), phoneme);

	setChanged();
	notifyObservers();
    }

    @Override
    public void removePhoneme(Phoneme phoneme) {

	if (phonemes.remove(phoneme.getPhonemeAsString()) != null) {

	    phoneme.setId(IdFactory.VOID);
	    phoneme.deleteObserver(this);

	    setChanged();
	    notifyObservers();
	}
    }

    @Override
    public Collection<Phoneme> getPhonemes() {

	return phonemes.values();
    }

    @Override
    public boolean phonemeSequenceIsCorrect(String aSequence) {

	String sequence = aSequence.replaceAll(syllableDelimiter, "");

	for (String aSyllable : aSequence.split(syllableDelimiter)) {

	    if (!syllable.matches(aSyllable)) {

		return false;
	    }

	    for (char letter : sequence.toCharArray()) {

		Phoneme phoneme = phonemes.get(Character.toString(letter));

		if (phoneme == null) {

		    return false;
		}
	    }
	}

	return true;
    }

    @Override
    public void addMorpheme(String aMorpheme, String aDescription)
	    throws InvalidPhonemeSequenceException, ItemAlreadyExistsException,
	    UnknownMorphemeReferenceException {

	if (morphemes.containsValue(aMorpheme)) {

	    throw new ItemAlreadyExistsException("Morpheme already exists: "
		    + aMorpheme);
	}

	Morpheme morpheme = new Morpheme(aMorpheme, aDescription, this);
	morpheme.setId(morphemeIdFactory.getId());
	morpheme.addObserver(this);

	morphemes.put(morpheme.getId(), morpheme);

	setChanged();
	notifyObservers();
    }

    @Override
    public void removeMorpheme(Morpheme morpheme) {

	if (morphemes.remove(morpheme.getId()) != null) {

	    morpheme.setId(IdFactory.VOID);
	    morpheme.deleteObserver(this);

	    setChanged();
	    notifyObservers();
	}
    }

    @Override
    public Collection<Morpheme> getMorphemes() {

	return morphemes.values();
    }

    @Override
    public Morpheme getMorpheme(int morphemeId) throws UnknownIdException {

	Morpheme morpheme = morphemes.get(morphemeId);

	if (morpheme == null) {

	    throw new UnknownIdException("Unknow morpheme:" + morphemeId, morphemeId);
	}

	return morpheme;
    }

    @Override
    public int addWord(String aWord, String aTranslation, String aDescription,
	    WordClass wordClass) throws ItemAlreadyExistsException {

	Word word = new Word(aWord, aTranslation, aDescription, wordClass, this);

	if (words.containsValue(word)) {

	    throw new ItemAlreadyExistsException("Word already exists: "
		    + aWord);
	}

	word.setId(wordIdFactory.getId());
	word.addObserver(this);
	words.put(word.getId(), word);

	setChanged();
	notifyObservers();

	return word.getId();
    }

    @Override
    public void removeWord(Word word) {

	if (words.remove(word.getId()) != null) {

	    word.setId(IdFactory.VOID);
	    word.deleteObserver(this);

	    setChanged();
	    notifyObservers();
	}
    }

    @Override
    public Word getWord(int wordId) throws UnknownIdException {

	Word word = words.get(wordId);

	if (word == null) {

	    throw new UnknownIdException("Unknown word: " + wordId, wordId);
	}

	return word;
    }

    @Override
    public Collection<Word> getWords() {

	return words.values();
    }

    @Override
    public int addWordClass(String aName) throws ItemAlreadyExistsException {

	WordClass wordClass = new WordClass(aName, this);

	if (wordClasses.containsValue(wordClass)) {

	    throw new ItemAlreadyExistsException("WordClass already exists: "
		    + aName);
	}

	wordClass.setId(wordClassIdFactory.getId());
	wordClass.addObserver(this);

	wordClasses.put(wordClass.getId(), wordClass);

	setChanged();
	notifyObservers();

	return wordClass.getId();
    }

    @Override
    public void removeWordClass(WordClass wordClass) {

	if (wordClasses.remove(wordClass.getId()) != null) {

	    wordClass.setId(IdFactory.VOID);
	    wordClass.deleteObserver(this);

	    setChanged();
	    notifyObservers();
	}
    }

    @Override
    public WordClass getWordClass(int wordClassId) throws UnknownIdException {

	WordClass wordClass = wordClasses.get(wordClassId);

	if (wordClass == null) {

	    throw new UnknownIdException("Unknown word class: " + wordClassId, wordClassId);
	}

	return wordClass;
    }

    @Override
    public Collection<WordClass> getWordClasses() {

	return wordClasses.values();
    }

    @Override
    public void addPhonemeChangingRule(String ruleString)
	    throws ItemAlreadyExistsException, SyntaxException, UnknownPhonemeStructureException {

	if (phonemeChangingRules.containsValue(ruleString)) {

	    throw new ItemAlreadyExistsException(
		    "Rule already exists: " + ruleString);
	}

	AbstractRule rule = AbstractRule.newInstance(ruleString, this);
	rule.setId(phonemeChangingRuleIdFactory.getId());
	rule.addObserver(this);

	phonemeChangingRules.put(rule.getId(), rule);

	setChanged();
	notifyObservers();
    }

    @Override
    public void removePhonemeChangingRule(AbstractRule aRule) {

	if (phonemeChangingRules.remove(aRule.getId()) != null) {

	    aRule.setId(IdFactory.VOID);
	    aRule.deleteObserver(this);

	    setChanged();
	    notifyObservers();
	}
    }

    @Override
    public Collection<AbstractRule> getPhonemeChangingRules() {

	return phonemeChangingRules.values();
    }
}