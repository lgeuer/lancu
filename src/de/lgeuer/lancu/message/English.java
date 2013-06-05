/*
 * File: DefaultEnglishMessages.java
 * Author: Lars Geuer
 * Date: 23.4.2007
 */

package de.lgeuer.lancu.message;

import de.lgeuer.lancu.core.entity.Morpheme;
import de.lgeuer.lancu.core.entity.Word;


public class English implements Messages,Names {


    @Override
    public String morphemeViolation(Morpheme morpheme) {

	return "Morpheme '" + morpheme.getMorpheme() + "' contains not allowed phonemes or violates the syllable structure.";
    }


    @Override
    public String wordViolation(Word word) {

	return "Word '" + word.getRoot() + "' contains not allowed phonemes or violates the syllable structure.";
    }


    @Override
    public String defaultRegularity() {

	return "Regular";
    }
}