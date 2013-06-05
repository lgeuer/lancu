/*
 * File: InvalidPhonemeSequenceException.java
 * Author: Lars Geuer
 * Date: 10.4.2007
 */

package de.lgeuer.lancu.core;

import de.lgeuer.lancu.core.entity.Phoneme;

public class InvalidPhonemeSequenceException extends Exception {


    private static final long serialVersionUID = 3545285264313382572L;
    String sequence;

    public InvalidPhonemeSequenceException(String aSequence) {

	super(aSequence);
	sequence = aSequence;
    }


    public InvalidPhonemeSequenceException(String aSequence,Phoneme aPhoneme) {

	super("Unkown phoneme " + aPhoneme + " in string: " + aSequence);
	sequence = aSequence;
    }
}