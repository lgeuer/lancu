/*
 * File: Phoneme.java
 * Author: Lars Geuer
 * Date: 10.4.2007
 */

package de.lgeuer.lancu.core.entity;

import java.util.Observable;

import de.lgeuer.lancu.util.id.IdFactory;



public class Phoneme extends Observable implements LanguageItem,Comparable<Phoneme> {

    private static final long serialVersionUID = 1820846808792009049L;

    private int id = IdFactory.VOID;

    private String newPhoneme;
    private String phoneme;


    public Phoneme(String aPhoneme,Language aLanguage) {

	newPhoneme = aPhoneme;
    }


    @Override
    public void rollback() {

	newPhoneme = null;

	setChanged();
	notifyObservers();
    }


    @Override
    public void commit() {

	phoneme = newPhoneme;

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


    public void setPhoneme(String aPhoneme) {

	newPhoneme = aPhoneme;
	setChanged();
	notifyObservers();
    }


    public String getPhonemeAsString() {

	if (newPhoneme != null) {

	    return newPhoneme;
	}

	return phoneme;
    }

    @Override
    public int compareTo(Phoneme p) {

	return getPhonemeAsString().compareTo(p.getPhonemeAsString());
    }
}