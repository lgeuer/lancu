/*
 * File: WordClass.java
 * Author: Lars Geuer
 * Date: 18.4.2007
 */

package de.lgeuer.lancu.core.entity;

import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import de.lgeuer.lancu.core.ItemAlreadyExistsException;
import de.lgeuer.lancu.util.id.IdFactory;
import de.lgeuer.lancu.util.id.UnknownIdException;


public class WordClass extends Observable implements LanguageItem, Observer,
	Comparable<WordClass> {

    private static final long serialVersionUID = -285010805132827140L;

    private int id = IdFactory.VOID;

    private IdFactory inflectionIdFactory = new IdFactory();

    private String newName;

    private String name;

    private Map<Integer, Inflection> inflectionsToAdd = new TreeMap<Integer, Inflection>();

    private Map<Integer, Inflection> inflectionsToRemove = new TreeMap<Integer, Inflection>();

    private Map<Integer, Inflection> inflections = new TreeMap<Integer, Inflection>();

    private Language language;

    public WordClass(String aName, Language aLanguage) {

	newName = aName;
	language = aLanguage;
    }

    @Override
    public void rollback() {

	newName = null;

	for (Inflection inflection : inflectionsToAdd.values()) {

	    inflection.deleteObserver(this);
	}

	for (Inflection inflection : inflectionsToRemove.values()) {

	    inflection.addObserver(this);
	}

	inflectionsToAdd.clear();
	inflectionsToRemove.clear();

	setChanged();
	notifyObservers();
    }

    @Override
    public void commit() {

	if (newName != null) {

	    name = newName;
	    newName = null;
	}

	if (!inflectionsToAdd.isEmpty()) {

	    inflections.putAll(inflectionsToAdd);
	    inflectionsToAdd.clear();
	}

	if (!inflectionsToRemove.isEmpty()) {

	    for (Inflection inflection : inflectionsToRemove.values()) {

		inflections.remove(inflection);
		inflection.setId(IdFactory.VOID);
	    }

	    inflectionsToRemove.clear();
	}

	for (Inflection inflection : inflections.values()) {

	    inflection.commit();
	}

	setChanged();
	notifyObservers();
    }

    @Override
    public void update(Observable observable, Object o) {

	setChanged();
	notifyObservers();
    }

    public void setId(int anId) {

	id = anId;
    }

    @Override
    public int getId() {

	return id;
    }

    public String getName() {

	if (newName != null) {

	    return newName;
	}

	return name;
    }

    public void setName(String aName) {

	newName = aName;

	setChanged();
	notifyObservers();
    }

    public int addInflection(String aInflection)
	    throws ItemAlreadyExistsException {

	Inflection inflection = new Inflection(aInflection, language);

	if (getInflections().contains(inflection)) {

	    throw new ItemAlreadyExistsException("Inflection already exists: "
		    + aInflection);
	}

	inflection.setId(inflectionIdFactory.getId());
	inflection.addObserver(this);

	inflectionsToAdd.put(inflection.getId(), inflection);

	setChanged();
	notifyObservers();

	return inflection.getId();
    }

    public void removeInflection(Inflection aInflection) {

	int inflectionId = aInflection.getId();

	if (inflectionsToAdd.containsValue(aInflection)) {

	    inflectionsToAdd.remove(inflectionId);
	} else {

	    inflectionsToRemove.put(inflectionId, aInflection);
	}

	aInflection.deleteObserver(this);

	setChanged();
	notifyObservers();
    }

    public Inflection getInflection(int inflectionId) throws UnknownIdException {

	if (inflections.containsKey(inflectionId) && !inflectionsToRemove.containsKey(inflectionId)) {

	    return inflections.get(inflectionId);
	}

	if (inflectionsToAdd.containsKey(inflectionId)) {

	    return inflectionsToAdd.get(inflectionId);
	}

	throw new UnknownIdException("Unknown inflection: " + inflectionId, inflectionId);
    }

    public ArrayList<Inflection> getInflections() {

	TreeMap<Integer, Inflection> inflectionMap = new TreeMap<Integer, Inflection>();
	inflectionMap.putAll(inflections);

	for (Inflection inflection : inflectionsToRemove.values()) {

	    inflectionMap.remove(inflection);
	}

	inflectionMap.putAll(inflectionsToAdd);

	return new ArrayList<Inflection>(inflectionMap.values());
    }

    public boolean contains(InflectionState state) {

	for (Inflection inflection : getInflections()) {

	    if (inflection.contains(state)) {

		return true;
	    }
	}

	return false;
    }

    @Override
    public boolean equals(Object o) {

	if (!(o instanceof WordClass)) {

	    return false;
	}

	WordClass otherClass = (WordClass) o;

	return getName().equals(otherClass.getName())
		&& this.language == otherClass.language;
    }

    @Override
    public int compareTo(WordClass wc) {

	return getName().compareTo(wc.getName());
    }

    @Override
    public String toString() {

	return getName();
    }
}
