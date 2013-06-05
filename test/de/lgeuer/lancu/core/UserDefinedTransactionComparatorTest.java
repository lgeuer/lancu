package de.lgeuer.lancu.core;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.lgeuer.lancu.core.entity.UserDefinedTransactionComparator;

public class UserDefinedTransactionComparatorTest {

    private UserDefinedTransactionComparator<Integer> comparator;


    @Before public void setUp() {
	
	comparator = new UserDefinedTransactionComparator<Integer>();
	comparator.add(0);
	comparator.add(1);
	comparator.add(2);
	comparator.commit();
    }


    @After public void tearDown() {
	
	comparator = null;
    }


    @Test public void naturalOrder() {

	//order: 0 1 2 

	Assert.assertEquals("copare(0,1)",-1,comparator.compare(0,1));
	Assert.assertEquals("copare(1,2)",-1,comparator.compare(1,2));
	Assert.assertEquals("copare(2,0)",2,comparator.compare(2,0));
	Assert.assertEquals("copare(2,1)",1,comparator.compare(2,1));	
	Assert.assertEquals("copare(2,2)",0,comparator.compare(2,2));	
	Assert.assertEquals("copare(3,2)",-1,comparator.compare(3,2));	
    }
    
    
    @Test public void moveUp() {
    
	comparator.moveUp(1); //order: 1 0 2

	Assert.assertEquals("copare(0,1)",1,comparator.compare(0,1));
	Assert.assertEquals("copare(1,2)",-2,comparator.compare(1,2));
	Assert.assertEquals("copare(2,0)",1,comparator.compare(2,0));
	Assert.assertEquals("copare(2,1)",2,comparator.compare(2,1));	
	Assert.assertEquals("copare(3,2)",-1,comparator.compare(3,2));	
    }

 
    @Test public void moveDown() {

	comparator.moveDown(1); //order: 0 2 1

	Assert.assertEquals("copare(0,1)",-2,comparator.compare(0,1));
	Assert.assertEquals("copare(2,1)",-1,comparator.compare(2,1));
    }    


    @Test public void remove() {

	comparator.remove(1); //order: 0 2 

	Assert.assertEquals("copare(2,0)",1,comparator.compare(2,0));
	Assert.assertEquals("copare(1,0)",-1,comparator.compare(1,0));
    }


    @Test public void rollback() {

	comparator.moveDown(1); //order: 0 2 1
	comparator.rollback(); //order 0 1 2
	

	Assert.assertEquals("copare(0,1)",-1,comparator.compare(0,1));
	Assert.assertEquals("copare(1,2)",-1,comparator.compare(1,2));
    }


    @Test public void commit() {

	comparator.moveDown(1); //order: 0 2 1
	comparator.commit(); 
	comparator.rollback(); //does nothing
	

	Assert.assertEquals("copare(0,1)",-2,comparator.compare(0,1));
	Assert.assertEquals("copare(1,2)",1,comparator.compare(1,2));
    }
}

