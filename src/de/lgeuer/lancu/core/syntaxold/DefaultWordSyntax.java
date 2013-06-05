/*
 * File: DefaultWordSyntax.java
 * Author: Lars Geuer
 * Date: 10.4.2007
 */

package de.lgeuer.lancu.core.syntaxold;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.core.entity.Morpheme;
import de.lgeuer.lancu.util.id.UnknownIdException;


public class DefaultWordSyntax implements WordSyntax {

    private static final long serialVersionUID = 4147769529040177515L;
    private static final String REFERENCE_BEGIN = "{";
    private static final String REFERENCE_END = "}";

    
    private Language language;

    public DefaultWordSyntax(Language aLanguage) {

	language = aLanguage;
    }


    @Override
    public List<Morpheme> getMorphemes(String word) throws UnknownIdException {

	List<Morpheme> morphemes = new ArrayList<Morpheme>();
	String referenceRegex = Pattern.quote(REFERENCE_BEGIN) 
	    + "([^" + REFERENCE_BEGIN + REFERENCE_END + "]*)" 
	    + Pattern.quote(REFERENCE_END);

	Pattern pattern = Pattern.compile(referenceRegex);
	Matcher matcher = pattern.matcher(word);

	while (matcher.find()) {

	    String reference = matcher.group(1);
	    Morpheme morpheme = language.getMorpheme(Integer.parseInt(reference));

	    morphemes.add(morpheme);
	}
	
	
	return morphemes;
    }


    @Override
    public String parseWord(String aWord,List<Morpheme> morphemes) {

	String referenceRegex = Pattern.quote(REFERENCE_BEGIN) 
	    + "([^" + REFERENCE_BEGIN + REFERENCE_END + "]*)" 
	    + Pattern.quote(REFERENCE_END);
	String word = aWord;
	String delimiter = language.getSyllableDelimiter();

	for (int i = 0;i < morphemes.size();i++) {

	    word = word.replaceFirst(referenceRegex,delimiter + morphemes.get(i).getMorpheme() + delimiter);
	}

	word = word.replaceAll(Pattern.quote(delimiter + delimiter),delimiter);
	word = word.replaceFirst("^" + Pattern.quote(delimiter),"");
	word = word.replaceFirst(Pattern.quote(delimiter) + "$","");
	

	return word;
    }
}