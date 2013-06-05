/*
 * File: WordSyntaxException.java
 * Author: Lars Geuer
 * Date: 10.4.2007
 */

package de.lgeuer.lancu.core.syntax.lancuregex;


public class WordSyntaxException extends SyntaxException {

    private static final long serialVersionUID = 2016263901417123435L;

    public WordSyntaxException(String word) {

	super("Invalid word: " + word);
    }
}
