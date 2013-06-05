/*
 * File: WordSyntax.java
 * Author: Lars Geuer
 * Date: 10.4.2007
 */

package de.lgeuer.lancu.core.syntaxold;


import java.io.Serializable;
import java.util.List;

import de.lgeuer.lancu.core.entity.Morpheme;
import de.lgeuer.lancu.util.id.UnknownIdException;


public interface WordSyntax extends Serializable {

    public List<Morpheme> getMorphemes(String word) throws UnknownIdException;

    public String parseWord(String aWord,List<Morpheme> morphemes);
}