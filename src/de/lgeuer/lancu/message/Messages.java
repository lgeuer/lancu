/*
 * File: Messages.java
 * Author: Lars Geuer
 * Date: 23.4.2007
 */

package de.lgeuer.lancu.message;

import de.lgeuer.lancu.core.entity.Morpheme;
import de.lgeuer.lancu.core.entity.Word;


public interface Messages {

    public String morphemeViolation(Morpheme morpheme);   

    public String wordViolation(Word word);   
}