/*
 * File: Morpheme.java
 * Author: Lars Geuer
 * Date: 10.4.2007
 */

package de.lgeuer.lancu.core.entity;

import java.util.Observable;

import de.lgeuer.lancu.core.InvalidPhonemeSequenceException;
import de.lgeuer.lancu.core.UnknownMorphemeReferenceException;
import de.lgeuer.lancu.util.id.IdFactory;



public class Morpheme extends Observable 
    implements LanguageItem,Comparable<Morpheme> {

    private static final long serialVersionUID = 7527396926030832207L;

    private int id = IdFactory.VOID;

    //tmp, used to store before commit/rollback
    private String newMorpheme;
    private String newDescription;

    private String morpheme;
    private String description; 
    private Language language;
    

    public Morpheme(String aMorpheme,String aDescription,Language aLanguage) 
	throws InvalidPhonemeSequenceException, 
	       UnknownMorphemeReferenceException {

	String delimiter = aLanguage.getSyllableDelimiter();
	aMorpheme = aMorpheme.replaceAll(delimiter + "{1,}",delimiter);
	aMorpheme = aMorpheme.replaceAll("^" + delimiter + "*","");
	aMorpheme = aMorpheme.replaceAll(delimiter + "*$","");

	newMorpheme = aMorpheme;
	newDescription = aDescription;
	language = aLanguage;

    }


    @Override
    public void rollback() {

	newMorpheme = null;
	newDescription = null;

	setChanged();
	notifyObservers();
    }

    
    @Override
    public void commit() throws InvalidPhonemeSequenceException, 
				UnknownMorphemeReferenceException {
	
	if (newMorpheme != null) {

	    if (this.check()) {
		
		morpheme =  newMorpheme;
		newMorpheme = null;
	    }
	    else {
		
		throw new InvalidPhonemeSequenceException(newMorpheme);
	    }
	}
	
	if (newDescription != null) {

	    description = newDescription;
	    newDescription = null;
	}
    }


    public void setId(int anId) {

	id = anId;
    }


    @Override
    public int getId() {

	return id;
    }


    public boolean check() {

	if (newMorpheme == null) {

	    return language.phonemeSequenceIsCorrect(morpheme);
	}
	return language.phonemeSequenceIsCorrect(newMorpheme);
    }


    public void setMorpheme(String aMorpheme) throws InvalidPhonemeSequenceException {

	String delimiter = language.getSyllableDelimiter();
	aMorpheme = aMorpheme.replaceAll(delimiter + "{1,}",delimiter);
	aMorpheme = aMorpheme.replaceAll("^" + delimiter + "*","");
	aMorpheme = aMorpheme.replaceAll(delimiter + "*$","");

	newMorpheme = aMorpheme;
	setChanged();
	notifyObservers();
    }


    public String getMorpheme() {

	if (newMorpheme != null) {

	    return newMorpheme;
	}
	return morpheme;
    }


    public void setDescription(String aDescription) {

	newDescription = aDescription;
	setChanged();
	notifyObservers();
    }

    
    public String getDescription() {

	if (newDescription != null) {

	    return newDescription;
	}
	return description;
    }

    @Override
    public boolean equals(Object o) {

	if (!(o instanceof Morpheme)) {
	    
	    return false;
	}	

	Morpheme otherMorpheme = (Morpheme)o;
	
	return getMorpheme().equals(otherMorpheme.getMorpheme()) 
	    && this.language == otherMorpheme.language;
    }


    @Override
    public int compareTo(Morpheme m) {

	return ((Integer)getId()).compareTo(m.getId());
    }
}