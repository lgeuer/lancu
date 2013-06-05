/*
 * File: Transaction.java
 * Author: Lars Geuer
 * Date: 10.4.2007
 */

package de.lgeuer.lancu.util;


public interface Transaction {

    public void commit() throws Exception;

    public void rollback();
}