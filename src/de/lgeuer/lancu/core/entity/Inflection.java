/*
 * File: Inflection.java
 * Author: Lars Geuer
 * Date: 18.4.2007
 */

package de.lgeuer.lancu.core.entity;

import java.util.Collection;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import de.lgeuer.lancu.core.ItemAlreadyExistsException;
import de.lgeuer.lancu.util.id.IdFactory;
import de.lgeuer.lancu.util.id.UnknownIdException;



public class Inflection extends Observable implements LanguageItem,Observer,Comparable<Inflection> {

    private static final long serialVersionUID = 8414184589428177910L;
    private int id = IdFactory.VOID;
    private IdFactory stateIdFactory = new IdFactory();
    private String newName;
    private String name;

    private Map<Integer,InflectionState> statesToAdd = new TreeMap<Integer,InflectionState>();
    private Map<Integer,InflectionState> statesToRemove = new TreeMap<Integer,InflectionState>();
    private Map<Integer,InflectionState> states = new TreeMap<Integer,InflectionState>();
    private Language language;


    public Inflection(String aName,Language aLanguage) {

	name = aName;
	language = aLanguage;
    }


    @Override
    public void rollback() {

	for (InflectionState state:statesToAdd.values()) {

	    state.deleteObserver(this);
	}

	for (InflectionState state:statesToRemove.values()) {

	    state.addObserver(this);
	}

	newName = null;
	statesToAdd.clear();
	statesToRemove.clear();

	setChanged();
	notifyObservers();
    }


    @Override
    public void commit() {


	if (newName != null) {

	    name = newName;
	    newName = null;
	}

	if (!statesToAdd.isEmpty()) {
	    
	    states.putAll(statesToAdd);
	    statesToAdd.clear();
	}

	if (!statesToRemove.isEmpty()) {
	  
	    for(InflectionState state:statesToRemove.values()) {

		states.remove(state);
		state.setId(IdFactory.VOID);
	    }

	    statesToRemove.clear();
	}

	for(InflectionState state:states.values()) {

	    state.commit();
	}

	setChanged();
	notifyObservers();
    }


    @Override
    public void update(Observable observable,Object o) {

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


    public int addInflectionState(String aInflectionState) 
	throws ItemAlreadyExistsException {

	InflectionState state = new InflectionState(aInflectionState,language);

	if (getInflectionStates().contains(state)) {
	    
	    throw new ItemAlreadyExistsException("Inflection state already exists: " 
						 + state);
	}

	state.setId(stateIdFactory.getId());
	state.addObserver(this);

	statesToAdd.put(state.getId(),state);

	setChanged();
	notifyObservers();

	return state.getId();
    } 


    public void removeInflectionState(InflectionState aInflectionState) {

	int inflectionStateId = aInflectionState.getId();
	aInflectionState.deleteObserver(this);

	if(statesToAdd.containsValue(aInflectionState)) {

	    statesToAdd.remove(inflectionStateId);
	}
	else {

	    statesToRemove.put(inflectionStateId,aInflectionState);
	}

	setChanged();
	notifyObservers();
    }


    public InflectionState getInflectionState(int inflectionStateId) throws UnknownIdException {

	if (states.containsKey(inflectionStateId) && !statesToRemove.containsKey(inflectionStateId)) {

	    return states.get(inflectionStateId);
	}
	    
	if (statesToAdd.containsKey(inflectionStateId)) {

	    return statesToAdd.get(inflectionStateId);
	}


	throw new UnknownIdException("Unknown inflection state: " + inflectionStateId,inflectionStateId);
    }


    public Collection<InflectionState> getInflectionStates()  {

	TreeMap<Integer,InflectionState> stateMap = new TreeMap<Integer,InflectionState>();
	stateMap.putAll(states);
	
	for (InflectionState state:statesToRemove.values()) {

	    stateMap.remove(state);
	}

	stateMap.putAll(statesToAdd);

	return stateMap.values();
    }


    public boolean contains(InflectionState state) {
	
	return getInflectionStates().contains(state);
    }

    @Override
    public boolean equals(Object o) {

	if (!(o instanceof Inflection)) {
	    
	    return false;
	}	

	Inflection otherInflection = (Inflection)o;
	
	return getName().equals(otherInflection.getName()) &&
	    this.language == otherInflection.language;
    }


    @Override
    public int compareTo(Inflection i) {

	return getName().compareTo(i.getName());
    }
}
