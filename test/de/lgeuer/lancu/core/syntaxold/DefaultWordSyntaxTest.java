package de.lgeuer.lancu.core.syntaxold;

import java.util.ArrayList;
import java.util.List;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.lgeuer.lancu.core.LanguageMock;
import de.lgeuer.lancu.core.entity.Morpheme;
import de.lgeuer.lancu.core.entity.Phoneme;
import de.lgeuer.lancu.core.syntaxold.DefaultWordSyntax;
import de.lgeuer.lancu.util.id.UnknownIdException;

public class DefaultWordSyntaxTest {

	private DefaultWordSyntax syntax;
	private LanguageMock language;
	private Phoneme phoneme1;
	private Phoneme phoneme2;
	private Phoneme phoneme3;
	private Morpheme morpheme1;
	private Morpheme morpheme2;
	private Morpheme morpheme3;


	@Before public void setUp() {

		language = new LanguageMock();
		syntax = new DefaultWordSyntax(language);
		language.setWordSyntax(syntax);

		phoneme1 = new Phoneme("b",language);
		phoneme2 = new Phoneme("l",language);
		phoneme3 = new Phoneme("a",language);

		language.addPhoneme(phoneme1);
		language.addPhoneme(phoneme2);
		language.addPhoneme(phoneme3);

		try {

			morpheme1 = new Morpheme("bla","",language);
			morpheme1.setId(1);
			morpheme1.commit();
			morpheme2 = new Morpheme("la","",language);
			morpheme2.setId(2);
			morpheme2.commit();
			morpheme3 = new Morpheme("ba","",language);
			morpheme3.setId(3);
			morpheme3.commit();

		}
		catch(Exception ex) {

			ex.printStackTrace();
			return;
		}

		language.addMorpheme(morpheme1);	
		language.addMorpheme(morpheme2);	
		language.addMorpheme(morpheme3);

		language.addMorpheme(morpheme1);
		language.addMorpheme(morpheme2);
		language.addMorpheme(morpheme3);
	}


	@After public void tearDown() {

		language = null;
		syntax = null;
		phoneme1 = null;
		phoneme2 = null;
		phoneme3 = null;
		morpheme1 = null;
		morpheme2 = null;
		morpheme3 = null;
	}

	@Test public void getMorphemes() throws UnknownIdException {

		String word = "{1}{2}{3}";

		List<Morpheme> expected = new ArrayList<Morpheme>();
		List<Morpheme> actual = syntax.getMorphemes(word);

		expected.add(morpheme1);
		expected.add(morpheme2);
		expected.add(morpheme3);

		for (int i = 0;i < 3;i++) {

			Assert.assertEquals(expected.get(i),actual.get(i));
		}
	}


	@Test public void parseWord() throws UnknownIdException {

		String word = "{1}{2}{1}";
		String expected = morpheme1.getMorpheme() + "-"
		+ morpheme2.getMorpheme() + "-"
		+ morpheme1.getMorpheme();
		List<Morpheme> morphemes = syntax.getMorphemes(word);

		String actual = syntax.parseWord(word,morphemes);


		for (int i = 0;i < 3;i++) {

			Assert.assertEquals(expected,actual);
		}
	}
}