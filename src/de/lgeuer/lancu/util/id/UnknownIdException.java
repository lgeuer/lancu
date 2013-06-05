/*
 * File: UnknownIdException.java
 * Author: Lars Geuer
 * Date: 21.4.2007
 */

package de.lgeuer.lancu.util.id;

public class UnknownIdException extends Exception {

    private static final long serialVersionUID = -1869847081483071779L;
    private int id;

    public UnknownIdException (String message,int anId) {

	super(message);

	id = anId;
    }


    public int getId() {

	return id;
    }
}