package de.lgeuer.lancu.core;

import java.util.Observable;
import java.util.Observer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.lgeuer.lancu.core.entity.DefaultLanguage;
import de.lgeuer.lancu.core.entity.Inflection;
import de.lgeuer.lancu.core.entity.InflectionState;
import de.lgeuer.lancu.core.entity.Language;
import de.lgeuer.lancu.core.entity.WordClass;
import de.lgeuer.lancu.core.syntax.lancuregex.SyntaxException;


public class DefaultLanguageTest {

	private static final String SYLLABLE = "[aei]<name:V1>[dtkg]<name:C1>[aei]<name:V2>?";
	private Language language;

	@Before public void setUp() 
	throws SyntaxException {

		language = new DefaultLanguage();
		language.setSyllable(SYLLABLE);
	}


	@After public void tearDown() {

		language = null;
	}


	@Test public void commit()  throws Exception {

		int id = language.addWordClass("<word class>");
		WordClass wordClass = language.getWordClass(id);
		id = wordClass.addInflection("<inflection>");
		Inflection inflection = wordClass.getInflection(id);
		id = inflection.addInflectionState("<inflection state>");
		InflectionState state = inflection.getInflectionState(id);
		state.getRegularity(0).addRule("$S<change:V1=x>");

		language.commit();


		language.addWord("WRONG_SYLLABLE","<translation>","<description>",wordClass);


		try {

			language.commit();
			Assert.fail("Accepted wrong syllable!");
		}
		catch(ViolationException ex) {

			//everything ok
		}

		language.rollback();


		language.addWord("ade","<translation>","<description>",wordClass);

	}

	/**
	 * Cause it doesnt seem to work...
	 */
	@Test public void notifyObservers() {

		Observer observer = new Observer() {

			@Override
			public void update(Observable observable,Object o) {

				throw new UnsupportedOperationException();
			}
		};


		language.addObserver(observer);

		try {
			language.setName("blub");
			Assert.fail("update(...) not called: ");
		}
		catch(UnsupportedOperationException ex) {

			//everything is fine
		}
	}
}