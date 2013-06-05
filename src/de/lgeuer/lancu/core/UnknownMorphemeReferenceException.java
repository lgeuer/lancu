/**
 * File: UnknownMorphemeReferenceException.java
 * Author: Lars Geuer
 * Date: 10.4.2007
 */

package de.lgeuer.lancu.core;


public class UnknownMorphemeReferenceException extends Exception {

    private static final long serialVersionUID = -5362433436502576115L;
    private int id;
    
    public UnknownMorphemeReferenceException(int anId) {
	
	super("Unknown reference: " + anId);

	id = anId;
    }

    public int getReference() {
	
	return id;
    }
}