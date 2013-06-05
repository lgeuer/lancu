/**
 * File: PhonemeStructure.java
 * Author: Lars Geuer
 * Date: 2.4.2007
 */

package de.lgeuer.lancu.core.entity;


import java.util.List;
import java.util.Observable;

import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;
import de.lgeuer.lancu.core.syntaxold.RuleSyntax;
import de.lgeuer.lancu.util.id.IdFactory;



public class PhonemeStructure extends  Observable implements LanguageItem {

    private static final long serialVersionUID = 877899262960832440L;

    private int id = IdFactory.VOID;

    /**
     * The name of the structure.
     */
    private String newName;
    private String name;

    /**
     * The language the structure contains to
     */
    private Language language;

    /**
     * whole structure
     */
    private String newStructure;
    private String structure;

    /**
     * whole stripped structure
     */
    private String newStrippedStructure;
    private String strippedStructure;

    /**
     * contains the names of all named groups in the structure in order of
     * their appearencie.
     */
    private List<String> newGroupNames;
    private List<String> groupNames;


    public PhonemeStructure(String aStructure,Language aLanguage) 
	throws SyntaxException {

	RuleSyntax syntax = aLanguage.getRuleSyntax();

	if (!syntax.checkPhonemeStructureSyntax(aStructure)) {

	    throw new IllegalArgumentException("Illegal syntax of phoneme structure: " + aStructure);
	}
	
	language = aLanguage;
	setStructure(aStructure);
    }


    public PhonemeStructure(String aName,String aStructure,Language aLanguage) 
	throws SyntaxException {

	this(aStructure,aLanguage);
	newName = aName;
	commit();
    }

    
    @Override
    public void rollback() {

	newName = null;
	newStructure = null;
	newStrippedStructure = null;
	newGroupNames = null;


	setChanged();
	notifyObservers();
    }


    @Override
    public void commit() {

	if (newName != null) {

	    name = newName;
	    newName = null;
	}
	
	if (newStructure != null) {

	    structure = newStructure;
	    newStructure = null;
	    strippedStructure = newStrippedStructure;
	    newStrippedStructure = null;
	    groupNames = newGroupNames;
	    newGroupNames = null;
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


    public void setStructure(String aStructure) 
	throws SyntaxException {

	RuleSyntax syntax = language.getRuleSyntax();

	if (!syntax.checkPhonemeStructureSyntax(aStructure)) {

	    throw new SyntaxException("Invalid phoneme structure: " + aStructure);
	}

	newStructure = aStructure;
	newStrippedStructure = syntax.stripStructure(newStructure);
	newGroupNames = syntax.getGroupNames(newStructure);

	setChanged();
	notifyObservers();
    }

    public String getStructure() {

	if (newStructure != null) {

	    return newStructure;
	}
	    
	return structure;
    }

    private String getStrippedStructure() {

	if (newStrippedStructure != null) {

	    return newStrippedStructure;
	}
	    
	return strippedStructure;
    }

    public List<String> getGroupNames() {
	
	if (newGroupNames != null) {

	    return newGroupNames;
	}

	return groupNames;
    }


    /**
     * Returns the name of the structure.
     */
    public String getName() {
	
	if (newName != null) {

	    return newName;
	}

	return name;
    }


    /**
     * Sets the name of the structure.
     */
    public void setName(String aName) {

	newName = aName;

	setChanged();
	notifyObservers();
    }


    /**
     * Splites the phoneme structure  by a given named group.
     * The returned String array contains the part of the structure before the 
     * group (at index 0), the group itself (at index 1)
     * and the part after the group (at index 2).
     * If the group is at the beginning or end of the structure the according 
     * array entry (0 or 2) is filled with an empty string.
     * 
     * @param namedGroup the name of the named group
     * @return an array with the three parts of the structure
     */
    @SuppressWarnings("unused")
    private String[] splitPhonemeStructureByGroup(String namedGroup) {

	RuleSyntax syntax = language.getRuleSyntax();

	if (newStructure != null) {
	    
	    return syntax.splitPhonemeStructureByGroup(newStructure,namedGroup);
	}

	return syntax.splitPhonemeStructureByGroup(structure,namedGroup);
    }


    public boolean matches(String sequence) {

	return sequence.matches(getStrippedStructure());
    }


    @Override
    public String toString() {
	
	if (newStructure != null) {

	    return newStructure;
	}

	return structure;
    }
}