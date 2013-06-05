/*
 * File: InflectionState.java
 * Author: Lars Geuer
 * Date: 10.4.2007
 */

package de.lgeuer.lancu.core.entity;

import java.util.Collection;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import de.lgeuer.lancu.Environment;
import de.lgeuer.lancu.core.ItemAlreadyExistsException;
import de.lgeuer.lancu.util.id.IdFactory;
import de.lgeuer.lancu.util.id.UnknownIdException;




public class InflectionState extends Observable 
    implements LanguageItem,Observer,Comparable<InflectionState> {

    private static final long serialVersionUID = 1596272927558559775L;
    private int id = IdFactory.VOID;
    private String oldName;
    private String name;

    private Language language;

    private IdFactory regularityIdFactory = new IdFactory();
    private Map<Integer,Regularity> oldRegularities = new TreeMap<Integer,Regularity>();
    private Map<Integer,Regularity> regularities = new TreeMap<Integer,Regularity>();


    public InflectionState(String aName,Language aLanguage) {

	oldName = aName;
	name = aName;
	language = aLanguage;
	String defaultRegularityName = Environment.getEnvironment().getNames().defaultRegularity();
	Regularity defaultRegularity = new Regularity(defaultRegularityName,this,language);

	defaultRegularity.setId(regularityIdFactory.getId());
	regularities.put(defaultRegularity.getId(),defaultRegularity);

	for (Regularity regularity:regularities.values()) {

	    regularity.addObserver(this);
	}
    }


    @Override
    public void rollback() {

	name = oldName;

	if (!regularities.equals(oldRegularities)) {


	    for (Regularity regularity:regularities.values()) {
	    
		regularity.deleteObserver(this);
	    }

	    regularities.clear();
	    regularities.putAll(oldRegularities);

	    for (Regularity regularity:regularities.values()) {
	    
		regularity.addObserver(this);
	    }
	}

	for (Regularity regularity:regularities.values()) {

	    regularity.rollback();
	}

	setChanged();
	notifyObservers();
    }


    @Override
    public void commit() {

	oldName = name;

	if (!regularities.equals(oldRegularities)) {

	    oldRegularities.clear();
	    oldRegularities.putAll(regularities);
	}

	for (Regularity regularity:regularities.values()) {

	    regularity.commit();
	}

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


    @Override
    public void update(Observable observable,Object o) {

	setChanged();
	notifyObservers();
    }

    
    public String getName() {

	return name;
    }


    public void setName(String aName) {

	name = aName;
	setChanged();
	notifyObservers();
    }


    public int addRegularity(String regularityName) 
	throws ItemAlreadyExistsException {

	Regularity regularity = new Regularity(regularityName,this,language);

	if (getRegularities().contains(regularity)) {
	    
	    throw new ItemAlreadyExistsException("Reqularity state already exists: " 
						 + regularity);
	}

	regularity.setId(regularityIdFactory.getId());
	regularity.addObserver(this);

	regularities.put(regularity.getId(),regularity);

	setChanged();
	notifyObservers();

	return regularity.getId();
    } 


    public void removeRegularity(Regularity regularity) {

	if (regularities.containsValue(regularity)) {

	    regularity.deleteObserver(this);
	    int regularityId = regularity.getId();
	    regularities.remove(regularityId);

	    setChanged();
	    notifyObservers();
	}
    }


    public Regularity getRegularity(int regularityId) throws UnknownIdException {

	if (regularities.containsKey(regularityId)) {

	    return regularities.get(regularityId);
	}
	
	throw new UnknownIdException("Unknown regularity: " + regularityId,regularityId);
    }


    public Collection<Regularity> getRegularities()  {

	return regularities.values();
    }

    public boolean contains(Regularity regularity) {
	
	return getRegularities().contains(regularity);
    }

    @Override
    public boolean equals(Object o) {

	if (!(o instanceof InflectionState)) {
	    
	    return false;
	}	

	InflectionState otherInflectionState = (InflectionState)o;
	
	return getName().equals(otherInflectionState.getName()) &&
	    this.language == otherInflectionState.language;
    }


    @Override
    public int compareTo(InflectionState i) {

	return getName().compareTo(i.getName());
    }
}