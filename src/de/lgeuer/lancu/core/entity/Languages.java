/*
 * File: Languages.java
 * Author: Lars Geuer
 * Date: 24.4.2007
 */

package de.lgeuer.lancu.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

public class Languages extends Observable implements Observer, Serializable {

    private static final long serialVersionUID = -7405246193502123189L;
    private Map<String, Language> languages = Collections
	    .synchronizedMap(new TreeMap<String, Language>());

    public synchronized void init(Languages l) {

	languages.clear();
	languages.putAll(l.languages);

	for (Language language : languages.values()) {

	    language.addObserver(this);
	}

	setChanged();
	notifyObservers();
    }

    public Map<String, Language> getMap() {

	return Collections.unmodifiableMap(languages);
    }

    public synchronized void addLanguage(String name) {

	if (languages.containsKey(name)) {

	    throw new IllegalArgumentException("Language already exists: "
		    + name);
	}

	synchronized (languages) {

	    Language language = new DefaultLanguage(name);
	    language.addObserver(this);

	    languages.put(language.getName(), language);

	    setChanged();
	    notifyObservers();
	}
    }

    public synchronized void removeLanguage(Language language) {

	languages.remove(language.getName());

	language.deleteObserver(this);

	setChanged();
	notifyObservers();
    }

    public synchronized int size() {

	return languages.size();
    }

    @Override
    public synchronized void update(Observable observable, Object o) {

	List<Language> tmpLanguages = new ArrayList<Language>(languages
		.values());

	synchronized (languages) {

	    languages.clear();
	    for (Language language : tmpLanguages) {

		languages.put(language.getName(), language);
	    }
	}

	setChanged();
	notifyObservers();
    }
}
