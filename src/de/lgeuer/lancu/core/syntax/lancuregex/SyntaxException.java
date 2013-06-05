/*
 * File: SyntaxException.java
 * Author: Lars Geuer
 * Date: 10.4.2007
 */

package de.lgeuer.lancu.core.syntax.lancuregex;


public class SyntaxException extends Exception {

    private static final long serialVersionUID = 7643479504590674605L;

    public SyntaxException(String message) {

	super(message);
    }
}
