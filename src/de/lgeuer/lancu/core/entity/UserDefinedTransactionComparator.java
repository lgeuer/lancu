/*
 * File: UserDefinedTransactionComparator.java
 * Author: Lars Geuer
 * Date: 11.4.2007
 */

package de.lgeuer.lancu.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.lgeuer.lancu.util.Transaction;


/**
 * Note: this comparator imposes orderings that are inconsistent with equals.
 */
public class UserDefinedTransactionComparator<T> implements Comparator<T>,Transaction,Serializable {

    private static final long serialVersionUID = -6450099749571378556L;
    private Vector<T> changedVector = new Vector<T>();
    private Vector<T> commitedVector = new Vector<T>();

    private List<Map> maps = new ArrayList<Map>();

    private boolean hasChanged = false;


    public UserDefinedTransactionComparator() {
    }


    public UserDefinedTransactionComparator(UserDefinedTransactionComparator<T> comparator) {

	changedVector  = comparator.changedVector;
	commitedVector = comparator.commitedVector;
	hasChanged     = comparator.hasChanged;
    }


    @Override
    public void rollback() {

	changedVector.clear();
	changedVector.addAll(commitedVector);
	hasChanged = false;	
    }

    @Override
    public void commit() {

	commitedVector.clear();
	commitedVector.addAll(changedVector);
	hasChanged = false;
    }

    
    public boolean hasChanged() {

	return hasChanged;
    }


    public synchronized void register(Map map) {
	
	maps.add(map);
    }


    public synchronized void unregister(Map map) {
	
	maps.remove(map);
    }


    public synchronized void add(T o) {

	//changedVector.equals(commitedVector) if hasChanged == false
	if (!changedVector.contains(o)) {

	    List<Map> tmpMaps = new ArrayList<Map>();

	    //clear maps
	    for(Map map:maps) {

		tmpMaps.add(new HashMap(map));
		map.clear();
	    }
	    

	    changedVector.add(o);
	    hasChanged = true;


	    //refill maps
	    for(int i = 0;i < maps.size();i++) {

		maps.get(i).putAll(tmpMaps.get(i));
	    }
	}
    }


    public synchronized void remove(T o) {

	List<Map> tmpMaps = new ArrayList<Map>();
	
	//clear maps
	for(Map map:maps) {
	    
	    tmpMaps.add(new HashMap(map));
	    map.clear();
	}
	

	//changedVector.equals(commitedVector) if hasChanged == false
	changedVector.remove(o);
	hasChanged = true;


	//refill maps
	for(int i = 0;i < maps.size();i++) {
	    
	    maps.get(i).putAll(tmpMaps.get(i));
	}
    }


    public synchronized void moveUp(T o) {

	List<Map> tmpMaps = new ArrayList<Map>();

	//changedVector.equals(commitedVector) if hasChanged == false
	int index;
	
	if (!changedVector.contains(o)) {
	    
	    return;
	}

	index = changedVector.indexOf(o);

	if (index == 0) {

	    return;
	}

	//clear maps
	for(Map map:maps) {
	    
	    tmpMaps.add(new HashMap(map));
	    map.clear();
	}

	
	changedVector.remove(o);
	changedVector.add(index - 1,o);
	hasChanged = true;	


	//refill maps
	for(int i = 0;i < maps.size();i++) {
	    
	    maps.get(i).putAll(tmpMaps.get(i));
	}
    }


    public synchronized void moveDown(T o) {

	int index;
	List<Map> tmpMaps = new ArrayList<Map>();

	//changedVector.equals(commitedVector) if hasChanged == false
	if (!changedVector.contains(o)) {
	    
	    return;
	}

	index = changedVector.indexOf(o);

	if (changedVector.indexOf(o) == changedVector.size() - 1) {

	    return;
	}

	//clear maps
	for(Map map:maps) {
	    
	    tmpMaps.add(new HashMap(map));
	    map.clear();
	}


	changedVector.remove(o);
	changedVector.add(index + 1,o);
	hasChanged = true;	


	//refill maps
	for(int i = 0;i < maps.size();i++) {
	    
	    maps.get(i).putAll(tmpMaps.get(i));
	}
    }


    @Override
    public synchronized int compare(T o1,T o2) {


	//changedVector.equals(commitedVector) if hasChanged == false
	int index1 = changedVector.indexOf(o1);
	int index2 = changedVector.indexOf(o2);

	if (index1 == -1 && index2 == -1) {

	    return 0; 
	}

	if (index2 == -1) {

	    return 1;
	}

	if (index1 == -1) {

	    return -1;
	}

	//both are in vector
	return index1 - index2;
    }
    

    @Override
    public boolean equals(Object o) {
	
	return o instanceof UserDefinedTransactionComparator && 
	    commitedVector.equals(((UserDefinedTransactionComparator)o).commitedVector) &&
	    changedVector.equals(((UserDefinedTransactionComparator)o).changedVector);
    }
}
